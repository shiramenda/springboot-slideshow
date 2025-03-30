package com.sources.controllers;

import com.sources.api.IImageAPI;
import com.sources.api.ISlidesShowAPI;
import com.sources.api.ImageAPI;
import com.sources.api.SlidesShowAPI;
import com.sources.dtos.ImageDTO;
import com.sources.dtos.SlidesShowDTO;
import com.sources.entities.Image;
import com.sources.entities.SlidesShow;
import com.sources.services.ImageService;
import com.sources.services.SlidesShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ImageSlideshowController {

    @Autowired
    private ImageAPI imageApi;

    @Autowired
    private SlidesShowAPI slidesShowApi;

    @PostMapping("/addImage")
    public ResponseEntity<ImageDTO> addImage(@RequestBody ImageDTO image) {
        ImageDTO savedImage = imageApi.addImage(image);
        return ResponseEntity.ok(savedImage);
    }
    @DeleteMapping("/deleteImage/{id}")
    public ResponseEntity<String> deleteImage(@PathVariable Long id) {
        imageApi.deleteImage(id);
        return ResponseEntity.ok("Image deleted successfully");
    }
    @PostMapping("/addSlideshow")
    public ResponseEntity<SlidesShowDTO> addSlideshow(@RequestBody SlidesShowDTO slideshow) {
        SlidesShowDTO savedSlideshow = slidesShowApi.addSlideshow(slideshow);
        return ResponseEntity.ok(savedSlideshow);
    }
    @DeleteMapping("/deleteSlideshow/{id}")
    public ResponseEntity<String> deleteSlideshow(@PathVariable Long id) {
        slidesShowApi.deleteSlideshow(id);
        return ResponseEntity.ok("Slideshow deleted successfully");
    }
    @GetMapping("/images/search")
    public ResponseEntity<List<ImageDTO>> searchImages(
            @RequestParam Optional<String> url,
            @RequestParam Optional<Integer> duration,
            @PageableDefault(size = 10) Pageable pageable) {

        List<ImageDTO> images = imageApi.searchImages(url, duration, pageable);
        return ResponseEntity.ok(images);
    }
    @GetMapping("/slideShow/{id}/slidesShowOrder")
    public ResponseEntity<List<ImageDTO>> getSlideshowOrder(@PathVariable Long id,
                                                            @PageableDefault(size = 10) Pageable pageable) {
        List<ImageDTO> images = slidesShowApi.getSlideshowOrder(id, pageable);
        return ResponseEntity.ok(images);
    }
    @PostMapping("/slideShow/{id}/proof-of-play/{imageId}")
    public ResponseEntity<String> proofOfPlay(@PathVariable Long id, @PathVariable Long imageId) {
        slidesShowApi.recordProofOfPlay(id, imageId);
        return ResponseEntity.ok("Proof of play recorded successfully");
    }
}
