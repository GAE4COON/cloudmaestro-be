package com.gae4coon.cloudmaestro.domain.resourceguide.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
<<<<<<< HEAD
=======
import com.gae4coon.cloudmaestro.domain.resourceguide.dto.DrawResourceDto;
>>>>>>> 5a2bb9df2979355e6fbda10ae00d61913594f1ca
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
import java.util.List;

@RequiredArgsConstructor
@RestController
@Log4j2
@RequestMapping("/api/v1/resource-api")
public class ResourceController
{
    private final ResourceService resourceService;

    @PostMapping("/resource")
    public HashMap<String, Object> member(@RequestBody ResourceDto titleArray) {
        HashMap<String, Object> resourceEntity  = null;
        try {
                resourceEntity = resourceService.resourceSearch(titleArray);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return resourceEntity;
    }

<<<<<<< HEAD
=======
    @PostMapping("/draw-resource")
    public HashMap<String, Object> member(@RequestBody DrawResourceDto title) {
        HashMap<String, Object> resourceEntity  = null;
        try {
            resourceEntity = resourceService.drawResource(title);
            System.out.println(resourceEntity);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return resourceEntity;
    }


>>>>>>> 5a2bb9df2979355e6fbda10ae00d61913594f1ca
}
