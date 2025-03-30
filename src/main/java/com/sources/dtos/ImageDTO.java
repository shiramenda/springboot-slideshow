package com.sources.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ImageDTO {
    private Long id;
    private String url;
    private Integer duration;

    public ImageDTO() {}

    public ImageDTO(Long id, String url, Integer duration) {
        this.id = id;
        this.url = url;
        this.duration = duration;
    }

}
