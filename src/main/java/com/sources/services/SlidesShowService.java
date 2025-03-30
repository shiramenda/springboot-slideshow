package com.sources.services;
import com.sources.dtos.ImageDTO;
import com.sources.dtos.SlidesShowDTO;
import com.sources.entities.AuditEvent;
import com.sources.entities.Image;
import com.sources.entities.SlidesShow;
import com.sources.entities.SlideshowImage;
import com.sources.events.AppActionEvent;
import com.sources.exceptions.DatabaseOperationException;
import com.sources.exceptions.DuplicateSlideshowNameException;
import com.sources.exceptions.NoImagesFoundException;
import com.sources.exceptions.ResourceNotFoundException;
import com.sources.repositories.AuditEventRepository;
import com.sources.repositories.ImageRepository;
import com.sources.repositories.SlidesShowRepository;
import com.sources.repositories.SlideshowImageRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class SlidesShowService implements ISlidesShowService{

    @Autowired
    private SlidesShowRepository slideshowRepository;
    @Autowired
    ImageRepository imageRepository;
    @Autowired
    AuditEventRepository auditEventRepository;
    @Autowired
    SlideshowImageRepository slideshowImageRepository;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public SlidesShowDTO addSlideshow(SlidesShowDTO slideshowDto) {
        try {

            if(slideshowDto.getName() != null){
                List<SlidesShow> slideshows = slideshowRepository.findByName(slideshowDto.getName());
                if(slideshows != null && !slideshows.isEmpty()){
                    throw new DuplicateSlideshowNameException("Slideshow with Name " + slideshowDto.getName() + " is already exists.");
                }

            }
            SlidesShow slideshow = new SlidesShow();
            slideshow.setName(slideshowDto.getName());
            slideshow = slideshowRepository.save(slideshow);
            attachImagesToSlideshow(slideshow, slideshowDto.getImageIds());
            slideshowDto.setId(slideshow.getId());
            eventPublisher.publishEvent(new AppActionEvent(
                    AppActionEvent.ActionType.ADD_SLIDESHOW,
                    slideshow.getId(),
                    slideshow.getName()
            ));
            return slideshowDto;
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("Database error while adding slideshow.", ex);
        }
    }

    public void deleteSlideshow(Long id) {
        SlidesShow slideshow = slideshowRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Slideshow with ID " + id + " not found."));

        try {
            slideshowRepository.deleteById(id);
            // Fire event
            eventPublisher.publishEvent(new AppActionEvent(
                    AppActionEvent.ActionType.DELETE_SLIDESHOW,
                    slideshow.getId(),
                    slideshow.getName()
            ));
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("Database error while deleting slideshow with ID: " + id, ex);
        }
    }

    public List<ImageDTO> getSlideshowOrder(Long slideshowId, Pageable pageable) {
        // Check that the slideshow exists.
        SlidesShow slideshow = slideshowRepository.findById(slideshowId)
                .orElseThrow(() -> new ResourceNotFoundException("Slideshow with ID " + slideshowId + " not found."));

        // Get the join table entries ordered by createdDate.
        Page<SlideshowImage> slideshowImagePage = slideshowImageRepository.findBySlidesShowIdOrderByCreatedDateAsc(slideshowId, pageable);

        if (slideshowImagePage.isEmpty()) {
            throw new NoImagesFoundException("No images found for slideshow ID: " + slideshowId);
        }

        // Convert to DTOs by mapping through the associated image.
        return slideshowImagePage.getContent().stream()
                .map(slideshowImage -> convertToDTO(slideshowImage.getImage()))
                .collect(Collectors.toList());
    }


    public void recordProofOfPlay(Long slideshowId, Long imageId) {
        try {
            SlidesShow slideshow = slideshowRepository.findById(slideshowId)
                    .orElseThrow(() -> new ResourceNotFoundException("Slideshow with ID " + slideshowId + " not found."));

            Image image = imageRepository.findById(imageId)
                    .orElseThrow(() -> new ResourceNotFoundException("Image with ID " + imageId + " not found."));
            AuditEvent auditEvent = new AuditEvent();
            auditEvent.setCode("PROOF_OF_PLAY");
            auditEvent.setMessage("Image " + imageId + " was played in slideshow " + slideshowId);
            auditEventRepository.save(auditEvent);

        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("Database error while recording proof of play.", ex);
        }
    }

    private void attachImagesToSlideshow(SlidesShow slideshow, List<Long> imageIds) {
        for (Long id : imageIds) {
            Image image = imageRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Image with ID " + id + " not found."));
            // Create a new join entity to support duplicate entries and order by created date.
            SlideshowImage slideshowImage = new SlideshowImage();
            slideshowImage.setSlidesShow(slideshow);
            slideshowImage.setImage(image);
            slideshowImage.setCreatedDate(image.getAddedDate()); // Set the creation timestamp.
            slideshowImageRepository.save(slideshowImage);
        }
    }
    // Helper method to convert Image to ImageDTO
    private ImageDTO convertToDTO(Image image) {
        return new ImageDTO(image.getId(), image.getUrl(), image.getDuration());
    }
}

