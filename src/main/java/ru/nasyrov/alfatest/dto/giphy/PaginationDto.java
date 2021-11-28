package ru.nasyrov.alfatest.dto.giphy;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PaginationDto {

    @JsonProperty("offset")
    Integer offset;
    @JsonProperty("total_count")
    Integer totalCount;
    @JsonProperty("count")
    Integer count;
}
