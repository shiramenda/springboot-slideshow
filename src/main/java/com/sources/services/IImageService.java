package com.sources.services;

import com.sources.dtos.ImageDTO;
import com.sources.entities.Image;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IImageService {
    ImageDTO addImage(ImageDTO image);
    void deleteImage(Long id);
    List<ImageDTO> searchImages(Optional<String> url, Optional<Integer> duration, Pageable pageable);
}
