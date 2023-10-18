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
    private Boolean isGroup = true;
    private String text;
    private String key;
    private String type;
    
    // 부가적
//    @JsonIgnore
    private String group;
//    @JsonIgnore
    private String stroke;


    public GroupData(String key, String text, Boolean isGroup, String group,  String type, String stroke) {
        this.key = key;
        this.text = text;
        this.isGroup = isGroup;
        this.type = type;
        this.group = group;
        this.stroke = stroke;
    }

    // getters and setters
}
