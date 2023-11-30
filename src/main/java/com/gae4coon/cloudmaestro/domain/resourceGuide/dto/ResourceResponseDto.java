package com.gae4coon.cloudmaestro.domain.resourceGuide.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class ResourceResponseDto {
    private String title;

    private String imgPath;

    private List<String> tag;

    private String guide1;

    private String guide2;

    private String guide3;

    private String guide4;
}