package com.sources.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Pattern(regexp = "(https?:\\/\\/.*\\.(?:png|jpg|jpeg|webp))", message = "URL must point to a valid image")
    private String url;
    private int duration;
    private LocalDateTime addedDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slides_show_id") // Foreign key to Slideshow table
    private SlidesShow slideshow; // Many Images belong to one Slideshow
}