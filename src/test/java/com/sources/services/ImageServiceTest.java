package com.sources.services;

import com.sources.dtos.ImageDTO;
import com.sources.entities.Image;
import com.sources.events.AppActionEvent;
import com.sources.exceptions.DatabaseOperationException;
import com.sources.exceptions.InvalidImageURLException;
import com.sources.exceptions.NoResultsFoundException;
import com.sources.exceptions.ResourceNotFoundException;
import com.sources.repositories.ImageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ImageServiceTest {

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ImageService imageService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        imageService = new ImageService();
        imageService.imageRepository = imageRepository;
        imageService.eventPublisher = eventPublisher;
    }

    @Test
    void addImage_valid_shouldCreateAndPublishEvent() {
        ImageDTO dto = new ImageDTO(null, "https://example.com/test.jpg", 5);
        Image image = new Image();
        image.setId(1L);
        image.setUrl(dto.getUrl());
        image.setDuration(dto.getDuration());

        when(imageRepository.findByUrlAndDuration(dto.getUrl(), dto.getDuration())).thenReturn(null);
        when(imageRepository.save(any(Image.class))).thenReturn(image);

        ImageDTO result = imageService.addImage(dto);

        assertEquals(dto.getUrl(), result.getUrl());
        verify(imageRepository).save(any(Image.class));
        verify(eventPublisher).publishEvent(any(AppActionEvent.class));
    }

    @Test
    void addImage_sameUrlAndDuration_shouldUpdateImage() {
        ImageDTO dto = new ImageDTO(null, "https://example.com/test.jpg", 5);
        Image existing = new Image();
        existing.setId(1L);
        existing.setUrl(dto.getUrl());
        existing.setDuration(dto.getDuration());

        when(imageRepository.findByUrlAndDuration(dto.getUrl(), dto.getDuration())).thenReturn(existing);
        when(imageRepository.save(any())).thenReturn(existing); // <-- this is what was missing

        ImageDTO result = imageService.addImage(dto);

        assertEquals(existing.getUrl(), result.getUrl());
        verify(imageRepository).save(any()); // still gets saved
    }


    @Test
    void addImage_invalidUrl_shouldThrowException() {
        ImageDTO dto = new ImageDTO(null, "invalid_url", 5);
        assertThrows(InvalidImageURLException.class, () -> imageService.addImage(dto));
    }

    @Test
    void addImage_databaseError_shouldThrowException() {
        ImageDTO dto = new ImageDTO(null, "https://example.com/test.jpg", 5);
        when(imageRepository.findByUrlAndDuration(dto.getUrl(), dto.getDuration())).thenReturn(null);
        when(imageRepository.save(any())).thenThrow(new DataAccessException("DB error") {});

        assertThrows(DatabaseOperationException.class, () -> imageService.addImage(dto));
    }

    @Test
    void deleteImage_valid_shouldDeleteAndPublishEvent() {
        Image image = new Image();
        image.setId(1L);
        image.setUrl("https://example.com/test.jpg");

        when(imageRepository.findById(1L)).thenReturn(Optional.of(image));

        imageService.deleteImage(1L);

        verify(imageRepository).deleteById(1L);
        verify(eventPublisher).publishEvent(any(AppActionEvent.class));
    }

    @Test
    void deleteImage_notFound_shouldThrowException() {
        when(imageRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> imageService.deleteImage(1L));
    }

    @Test
    void deleteImage_databaseError_shouldThrowException() {
        Image image = new Image();
        image.setId(1L);
        image.setUrl("https://example.com/test.jpg");

        when(imageRepository.findById(1L)).thenReturn(Optional.of(image));
        doThrow(new DataAccessException("DB error") {}).when(imageRepository).deleteById(1L);

        assertThrows(DatabaseOperationException.class, () -> imageService.deleteImage(1L));
    }

    @Test
    void searchImages_byUrlAndDuration_shouldReturnResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Image image = new Image();
        image.setId(1L);
        image.setUrl("https://example.com");
        image.setDuration(5);

        Page<Image> page = new PageImpl<>(List.of(image));
        when(imageRepository.findByUrlContainingAndDuration("test", 5, pageable)).thenReturn(page);

        List<ImageDTO> result = imageService.searchImages(Optional.of("test"), Optional.of(5), pageable);
        assertEquals(1, result.size());
    }

    @Test
    void searchImages_byUrlOnly_shouldReturnResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Image image = new Image();
        image.setUrl("https://example.com");

        Page<Image> page = new PageImpl<>(List.of(image));
        when(imageRepository.findByUrlContaining("test", pageable)).thenReturn(page);

        List<ImageDTO> result = imageService.searchImages(Optional.of("test"), Optional.empty(), pageable);
        assertEquals(1, result.size());
    }

    @Test
    void searchImages_byDurationOnly_shouldReturnResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Image image = new Image();
        image.setDuration(5);

        Page<Image> page = new PageImpl<>(List.of(image));
        when(imageRepository.findByDuration(5, pageable)).thenReturn(page);

        List<ImageDTO> result = imageService.searchImages(Optional.empty(), Optional.of(5), pageable);
        assertEquals(1, result.size());
    }

    @Test
    void searchImages_all_shouldReturnResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Image> page = new PageImpl<>(List.of(new Image()));
        when(imageRepository.findAll(pageable)).thenReturn(page);

        List<ImageDTO> result = imageService.searchImages(Optional.empty(), Optional.empty(), pageable);
        assertEquals(1, result.size());
    }

    @Test
    void searchImages_noResults_shouldThrowException() {
        Pageable pageable = PageRequest.of(0, 10);
        when(imageRepository.findAll(pageable)).thenReturn(Page.empty());
        assertThrows(NoResultsFoundException.class, () -> imageService.searchImages(Optional.empty(), Optional.empty(), pageable));
    }
}
