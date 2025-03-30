package com.sources.services;
import com.sources.dtos.ImageDTO;
import com.sources.entities.Image;
import com.sources.events.AppActionEvent;
import com.sources.exceptions.DatabaseOperationException;
import com.sources.exceptions.InvalidImageURLException;
import com.sources.exceptions.NoResultsFoundException;
import com.sources.exceptions.ResourceNotFoundException;
import com.sources.repositories.ImageRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ImageService implements IImageService{

    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public ImageDTO addImage(ImageDTO imageDto) {
        if (!isValidImageURL(imageDto.getUrl())) {
            throw new InvalidImageURLException("Invalid image URL: " + imageDto.getUrl());
        }
        try{
            Image image;
            Optional<Image> imageOptional = Optional.ofNullable(imageRepository.findByUrlAndDuration(imageDto.getUrl(), imageDto.getDuration()));
            image = imageOptional.orElseGet(Image::new);
            image.setUrl(imageDto.getUrl());
            image.setDuration(imageDto.getDuration());
            image.setAddedDate(LocalDateTime.now());
            Image savedImage = imageRepository.save(image);
            // Fire event
            eventPublisher.publishEvent(new AppActionEvent(
                    AppActionEvent.ActionType.ADD_OR_UPDATE_IMAGE,
                    savedImage.getId(),
                    savedImage.getUrl()
            ));
            return convertToDTO(savedImage);
        }catch(DataAccessException e){
            throw new DatabaseOperationException("Database error while saving the image.", e);
        }

    }

    public void deleteImage(Long id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found"));
        try{
            imageRepository.deleteById(id);
            // Fire event
            eventPublisher.publishEvent(new AppActionEvent(
                    AppActionEvent.ActionType.DELETE_IMAGE,
                    image.getId(),
                    image.getUrl()
            ));
        }catch(DataAccessException e){
            throw new DatabaseOperationException("Database error while deleting the image with ID: " + id, e);
        }

    }
    public List<ImageDTO> searchImages(Optional<String> url, Optional<Integer> duration, Pageable pageable) {
        Page<Image> imagePage;

        if(url.isPresent() && duration.isPresent()) {
            imagePage = imageRepository.findByUrlContainingAndDuration(url.get(), duration.get(), pageable);
        }
        else if (url.isPresent()) {
            imagePage = imageRepository.findByUrlContaining(url.get(), pageable);
        } else if (duration.isPresent()) {
            imagePage = imageRepository.findByDuration(duration.get(), pageable);
        } else {
            imagePage = imageRepository.findAll(pageable);
        }
        if (imagePage.isEmpty()) {
            throw new NoResultsFoundException("No images found for the given criteria.");
        }
        // Convert Page<Image> to List<ImageDTO>
        return imagePage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private  Page<Image> handleImageToPage(Optional<String> url, Optional<Integer> duration, Pageable pageable, Image image) {
        Page<Image> imagePage;
        if (image != null) {
            imagePage = new PageImpl<>(Collections.singletonList(image), pageable, 1);
        } else {
            throw new ResourceNotFoundException("No images found with URL: " + url.orElse("") + " and duration: " + duration.orElse(0));
        }
        return imagePage;
    }

    // Helper method to convert Image to ImageDTO
    private ImageDTO convertToDTO(Image image) {
        return new ImageDTO(image.getId(), image.getUrl(), image.getDuration());
    }

    private boolean isValidImageURL(String url) {
        // Implement URL validation logic here
        return url.matches("https?://.*\\.(jpg|jpeg|png|webp)");
    }
}

