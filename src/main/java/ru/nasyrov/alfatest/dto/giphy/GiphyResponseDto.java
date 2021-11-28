package ru.nasyrov.alfatest.dto.giphy;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GiphyResponseDto {

    @JsonProperty("data")
    private DataDto[] data;
    @JsonProperty("pagination")
    private PaginationDto pagination;
    @JsonProperty("meta")
    private MetaDto meta;
}
