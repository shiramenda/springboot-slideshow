package com.sources.repositories;

import com.sources.entities.Image;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    Image findByUrlAndDuration(String url, int duration);
    Page<Image> findByUrlContainingAndDuration(String url, int duration, Pageable pageable);
    Page<Image> findByUrlContaining(String url, Pageable pageable);
    Page<Image> findByDuration(Integer duration, Pageable pageable);
    Page<Image> findBySlideshowIdOrderByAddedDateAsc(Long slideshowId , Pageable pageable);
}
