package ru.nasyrov.alfatest.dto.giphy;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MetaDto {

    @JsonProperty("msg")
    private String msg;
    @JsonProperty("status")
    private Integer status;
    @JsonProperty("response_id")
    private String responseId;

}
