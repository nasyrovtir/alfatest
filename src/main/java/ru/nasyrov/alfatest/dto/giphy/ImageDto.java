package ru.nasyrov.alfatest.dto.giphy;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageDto {

    @JsonProperty("height")
    private String height;
    @JsonProperty("size")
    private String size;
    @JsonProperty("url")
    private String url;
    @JsonProperty("width")
    private String width;
    @JsonProperty("mp4")
    private String mp4;
    @JsonProperty("mp4_size")
    private String mp4Size;
    @JsonProperty("webp")
    private String webp;
    @JsonProperty("webp_size")
    private String webpSize;
    @JsonProperty("frames")
    private Integer frames;
    @JsonProperty("hash")
    private String hash;
}
