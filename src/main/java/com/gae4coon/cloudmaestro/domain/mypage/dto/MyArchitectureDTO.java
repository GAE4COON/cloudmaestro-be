package com.gae4coon.cloudmaestro.domain.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyArchitectureDTO{
    private Long key;
    private String title;
}
