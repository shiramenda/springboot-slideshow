package com.sources.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Setter
@Getter
public class SlidesShowDTO {
    private Long id;
    private String name;
    private List<Long> imageIds; // List of image IDs instead of full Image objects
    public SlidesShowDTO() {}


    public SlidesShowDTO(Long id, String name, List<Long> imageIds) {
        this.id = id;
        this.name = name;
        this.imageIds = imageIds;
    }


}
