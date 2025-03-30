package com.sources.api;

import com.sources.dtos.ImageDTO;
import com.sources.dtos.SlidesShowDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public interface ISlidesShowAPI {
    SlidesShowDTO addSlideshow(SlidesShowDTO slideshow);

    void deleteSlideshow(Long id);

    List<ImageDTO> getSlideshowOrder(Long id, Pageable pageable);

    void recordProofOfPlay(Long id, Long imageId);
}
