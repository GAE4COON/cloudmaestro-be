package com.gae4coon.cloudmaestro.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class EmailAuthRequestDto {
    @JsonProperty("email")
    public String email;
}
