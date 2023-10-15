package com.gae4coon.cloudmaestro.domain.rehost.dto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupData {
    private Boolean isGroup;
    private String type;
    private String key;
    private String stroke;
    private String group;
}
