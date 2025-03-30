package com.sources.services;

import com.sources.dtos.ImageDTO;
import com.sources.dtos.SlidesShowDTO;
import com.sources.entities.Image;
import com.sources.entities.SlidesShow;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ISlidesShowService {
    SlidesShowDTO addSlideshow(SlidesShowDTO slideshow);
    void deleteSlideshow(Long id);
    List<ImageDTO> getSlideshowOrder(Long slideshowId, Pageable pageable);
    void recordProofOfPlay(Long slideshowId, Long imageId);
}
