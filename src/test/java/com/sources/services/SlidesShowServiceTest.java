package com.sources.services;

import com.sources.dtos.ImageDTO;
import com.sources.dtos.SlidesShowDTO;
import com.sources.entities.*;
import com.sources.events.AppActionEvent;
import com.sources.exceptions.*;
import com.sources.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SlidesShowServiceTest {

    @Mock
    private SlidesShowRepository slidesShowRepository;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private AuditEventRepository auditEventRepository;
    @Mock
    private SlideshowImageRepository slideshowImageRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private SlidesShowService slidesShowService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        slidesShowService.slideshowRepository = slidesShowRepository;
        slidesShowService.imageRepository = imageRepository;
        slidesShowService.auditEventRepository = auditEventRepository;
        slidesShowService.slideshowImageRepository = slideshowImageRepository;
        slidesShowService.eventPublisher = eventPublisher;
    }

    @Test
    void addSlideshow_valid_shouldSaveAndPublishEvent() {
        SlidesShowDTO dto = new SlidesShowDTO(null, "Spring", List.of());
        SlidesShow saved = new SlidesShow();
        saved.setId(1L); saved.setName("Spring");

        when(slidesShowRepository.findByName("Spring")).thenReturn(Collections.emptyList());
        when(slidesShowRepository.save(any())).thenReturn(saved);

        SlidesShowDTO result = slidesShowService.addSlideshow(dto);

        assertEquals(saved.getId(), result.getId());
        verify(eventPublisher).publishEvent(any(AppActionEvent.class));
    }

    @Test
    void addSlideshow_duplicateName_shouldThrowException() {
        SlidesShow existing = new SlidesShow();
        existing.setName("Holiday");
        when(slidesShowRepository.findByName("Holiday")).thenReturn(List.of(existing));

        SlidesShowDTO dto = new SlidesShowDTO(null, "Holiday", List.of());
        assertThrows(DuplicateSlideshowNameException.class, () -> slidesShowService.addSlideshow(dto));
    }

    @Test
    void deleteSlideshow_valid_shouldDeleteAndPublishEvent() {
        SlidesShow slideshow = new SlidesShow();
        slideshow.setId(1L); slideshow.setName("Promo");

        when(slidesShowRepository.findById(1L)).thenReturn(Optional.of(slideshow));

        slidesShowService.deleteSlideshow(1L);

        verify(slidesShowRepository).deleteById(1L);
        verify(eventPublisher).publishEvent(any(AppActionEvent.class));
    }

    @Test
    void deleteSlideshow_notFound_shouldThrowException() {
        when(slidesShowRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> slidesShowService.deleteSlideshow(1L));
    }
    @Test
    void getSlideshowOrder_shouldReturnImagesInCorrectOrder() {
        SlidesShow slideshow = new SlidesShow();
        slideshow.setId(1L);
        when(slidesShowRepository.findById(1L)).thenReturn(Optional.of(slideshow));

        // All images have different addedDates
        Image img1 = new Image(); img1.setId(1L); img1.setAddedDate(LocalDateTime.of(2024, 1, 1, 0, 0));
        Image img2 = new Image(); img2.setId(2L); img2.setAddedDate(LocalDateTime.of(2024, 1, 2, 0, 0));
        Image img3 = new Image(); img3.setId(3L); img3.setAddedDate(LocalDateTime.of(2024, 1, 3, 0, 0));

        Pageable pageable = PageRequest.of(0, 10);

        // Simulate images being added at specific timestamps
        List<SlideshowImage> slideshowImages = List.of(
                createSlideshowImage(img3, slideshow, LocalDateTime.of(2024, 1, 4, 0, 0)), // img3 added last
                createSlideshowImage(img1, slideshow, LocalDateTime.of(2024, 1, 1, 0, 0)), // img1 added first
                createSlideshowImage(img2, slideshow, LocalDateTime.of(2024, 1, 2, 0, 0)), // img2 added second
                createSlideshowImage(img1, slideshow, LocalDateTime.of(2024, 1, 1, 0, 0))  // img1 again
        );

        // Expected order by SlideshowImage.createdDate
        Page<SlideshowImage> page = new PageImpl<>(slideshowImages.stream()
                .sorted(Comparator.comparing(SlideshowImage::getCreatedDate))
                .toList());

        when(slideshowImageRepository.findBySlidesShowIdOrderByCreatedDateAsc(1L, pageable)).thenReturn(page);

        List<ImageDTO> result = slidesShowService.getSlideshowOrder(1L, pageable);
        List<Long> expectedOrder = List.of(1L, 1L, 2L, 3L); // Ordered by SlideshowImage.createdDate
        List<Long> actualOrder = result.stream().map(ImageDTO::getId).collect(Collectors.toList());

        assertEquals(expectedOrder, actualOrder);
    }


    @Test
    void getSlideshowOrder_noImages_shouldThrowException() {
        SlidesShow slideshow = new SlidesShow();
        slideshow.setId(1L);
        when(slidesShowRepository.findById(1L)).thenReturn(Optional.of(slideshow));

        Pageable pageable = PageRequest.of(0, 10);
        when(slideshowImageRepository.findBySlidesShowIdOrderByCreatedDateAsc(1L, pageable)).thenReturn(Page.empty());

        assertThrows(NoImagesFoundException.class, () -> slidesShowService.getSlideshowOrder(1L, pageable));
    }

    @Test
    void getSlideshowOrder_notFound_shouldThrowException() {
        when(slidesShowRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> slidesShowService.getSlideshowOrder(99L, PageRequest.of(0, 5)));
    }

    @Test
    void recordProofOfPlay_valid_shouldSaveAudit() {
        SlidesShow show = new SlidesShow(); show.setId(1L);
        Image image = new Image(); image.setId(2L);

        when(slidesShowRepository.findById(1L)).thenReturn(Optional.of(show));
        when(imageRepository.findById(2L)).thenReturn(Optional.of(image));

        slidesShowService.recordProofOfPlay(1L, 2L);

        verify(auditEventRepository).save(any(AuditEvent.class));
    }

    @Test
    void recordProofOfPlay_slideshowNotFound_shouldThrow() {
        when(slidesShowRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> slidesShowService.recordProofOfPlay(99L, 1L));
    }

    @Test
    void recordProofOfPlay_imageNotFound_shouldThrow() {
        SlidesShow show = new SlidesShow(); show.setId(1L);
        when(slidesShowRepository.findById(1L)).thenReturn(Optional.of(show));
        when(imageRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> slidesShowService.recordProofOfPlay(1L, 999L));
    }

    private SlideshowImage createSlideshowImage(Image img, SlidesShow show, LocalDateTime createdDate) {
        SlideshowImage si = new SlideshowImage();
        si.setSlidesShow(show);
        si.setImage(img);
        si.setCreatedDate(createdDate);
        return si;
    }
}
