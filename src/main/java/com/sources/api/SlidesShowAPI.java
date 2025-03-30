package com.sources.api;

import com.sources.dtos.ImageDTO;
import com.sources.dtos.SlidesShowDTO;
import com.sources.services.IImageService;
import com.sources.services.ISlidesShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SlidesShowAPI implements ISlidesShowAPI {
    @Autowired
    IImageService imageService;
    @Autowired
    ISlidesShowService slidesShowService;
    @Override
    public SlidesShowDTO addSlideshow(SlidesShowDTO slideshow) {
        return slidesShowService.addSlideshow(slideshow);
    }

    @Override
    public void deleteSlideshow(Long id) {
        slidesShowService.deleteSlideshow(id);
    }

    @Override
    public List<ImageDTO> getSlideshowOrder(Long id, Pageable pageable) {
        return slidesShowService.getSlideshowOrder(id, pageable);
    }

    @Override
    public void recordProofOfPlay(Long id, Long imageId) {
        slidesShowService.recordProofOfPlay(id, imageId);
    }
}
