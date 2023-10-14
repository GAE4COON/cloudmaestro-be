package com.gae4coon.cloudmaestro.domain.standard.dto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupData {
    private Boolean isGroup;
    private String type;
    private String key;
    private String stroke;
    private String group;
}
