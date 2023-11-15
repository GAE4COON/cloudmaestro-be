package com.gae4coon.cloudmaestro.domain.available.dto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupData{
    private String key;
    private String text;
    private Boolean isGroup = true;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String group;

    private String type;
    private String stroke;

    public void addKey(String Key) {
        this.key = Key;
    }
}
