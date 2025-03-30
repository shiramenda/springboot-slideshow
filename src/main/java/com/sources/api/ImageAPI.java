package com.sources.api;

import com.sources.dtos.ImageDTO;
import com.sources.entities.Image;
import com.sources.services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ImageAPI implements IImageAPI {
    @Autowired
    private ImageService imageService;
    @Override
    public ImageDTO addImage(ImageDTO image) {
        return imageService.addImage(image);
    }

    @Override
    public void deleteImage(Long id) {
        imageService.deleteImage(id);
    }
    @Override
    public List<ImageDTO> searchImages(Optional<String> url, Optional<Integer> duration, Pageable pageable) {
       return imageService.searchImages(url, duration, pageable);
    }
}
