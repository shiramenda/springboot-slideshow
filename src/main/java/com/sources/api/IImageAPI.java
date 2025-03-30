package com.sources.api;

import com.sources.dtos.ImageDTO;
import com.sources.entities.Image;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
@Component
public interface IImageAPI {
    ImageDTO addImage(ImageDTO image);

    void deleteImage(Long id);

    List<ImageDTO> searchImages(Optional<String> url, Optional<Integer> duration, Pageable pageable);
}
