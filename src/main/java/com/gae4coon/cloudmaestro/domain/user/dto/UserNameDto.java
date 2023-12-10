package com.gae4coon.cloudmaestro.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class UserNameDto {
    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("name")
    private String userName;
}
