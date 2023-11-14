package com.gae4coon.cloudmaestro.domain.resourceguide.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.gae4coon.cloudmaestro.domain.resourceguide.dto.DrawResourceDto;

import com.gae4coon.cloudmaestro.domain.resourceguide.dto.ResourceDto;
import com.gae4coon.cloudmaestro.domain.resourceguide.dto.ResourceResponseDto;
import com.gae4coon.cloudmaestro.domain.resourceguide.entity.ResourceEntity;
import com.gae4coon.cloudmaestro.domain.resourceguide.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final ObjectMapper objectMapper;

    public HashMap<String, Object> resourceSearch(ResourceDto titleArray) throws JsonProcessingException {
        HashMap<String, Object> tag = new HashMap<>();
        HashMap<String, Object> map = new HashMap<>();
        List<ResourceResponseDto> resourcelist = new ArrayList<>();

        for (String title: titleArray.getTitle()) {
            ResourceEntity resourceEntity =resourceRepository.findByTitle(title);
            String tagString = resourceEntity.getTag();
            List<String> tags = objectMapper.readValue(tagString, new TypeReference<List<String>>() {});

            resourcelist.add(new ResourceResponseDto(resourceEntity.getTitle(), resourceEntity.getImgPath(), tags,
                    resourceEntity.getGuide1(),resourceEntity.getGuide2(), resourceEntity.getGuide3(), resourceEntity.getGuide4()));
        }

        map.put("result", resourcelist);
        return map;
    }

    public HashMap<String, Object> drawResource(DrawResourceDto title) throws JsonProcessingException {
        HashMap<String, Object> map = new HashMap<>();

        ResourceEntity resourceEntity =resourceRepository.findByTitle(title.getTitle());
        System.out.println(resourceEntity.getTitle() + resourceEntity.getGuide1());

        map.put("result", resourceEntity.getGuide1());
        return map;
    }

}