package com.gae4coon.cloudmaestro.domain.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyArchitectureDTO{
    private Long key;
    private String title;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}
