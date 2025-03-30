package com.sources.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Setter
@Getter
@Entity
@Table(name = "slideshow_images")
//New join entity to support duplicate images and ordering
public class SlideshowImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many-to-one to SlidesShow
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slides_show_id", nullable = false)
    private SlidesShow slidesShow;

    // Many-to-one to Image
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id", nullable = false)
    private Image image;

    private LocalDateTime createdDate;
}