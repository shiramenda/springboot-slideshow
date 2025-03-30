// CHANGED: Order by createdDate instead of displayOrder.
package com.sources.repositories;

import com.sources.entities.SlideshowImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlideshowImageRepository extends JpaRepository<SlideshowImage, Long> {
    Page<SlideshowImage> findBySlidesShowIdOrderByCreatedDateAsc(Long slidesShowId, Pageable pageable);
    void deleteAllBySlidesShowId(Long slidesShowId);
    void deleteAllByImageId(Long imageId);
}
