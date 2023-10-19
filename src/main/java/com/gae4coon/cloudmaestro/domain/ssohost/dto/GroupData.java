package com.gae4coon.cloudmaestro.domain.ssohost.dto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupData{
    private String key;
    private String text;
    private Boolean isGroup = true;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String group;

    private String type;
    private String stroke;
}
