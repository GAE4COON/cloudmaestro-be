package com.gae4coon.cloudmaestro.domain.ssohost.dto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.mail.internet.InternetAddress;
import lombok.*;

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

    @Builder
    GroupData(String key, String text, Boolean isGroup, String type, String stroke) {
        this.key = key;
        this.text = text;
        this.isGroup = isGroup;
        this.type = type;
        this.stroke = stroke;
    }

    public void addKey(String Key) {
        this.key = Key;
    }
}
