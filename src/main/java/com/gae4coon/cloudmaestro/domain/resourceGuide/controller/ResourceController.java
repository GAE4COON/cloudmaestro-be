package com.gae4coon.cloudmaestro.domain.resourceguide.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gae4coon.cloudmaestro.domain.resourceguide.dto.ResourceDto;
import com.gae4coon.cloudmaestro.domain.resourceguide.dto.ResourceResponseDto;
import com.gae4coon.cloudmaestro.domain.resourceguide.service.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RequiredArgsConstructor
@RestController
@Log4j2
@RequestMapping("/api/v1/resource-api")
public class ResourceController
{
    private final ResourceService resourceService;

    @PostMapping("/resource")
    public HashMap<String, Object> member(@RequestBody ResourceDto title) {
        HashMap<String, Object> resourceEntity  = null;
        try {
            resourceEntity = resourceService.resourceSearch(title.getTitle());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return resourceEntity;
    }

}
