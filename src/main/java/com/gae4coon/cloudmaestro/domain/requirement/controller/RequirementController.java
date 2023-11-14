package com.gae4coon.cloudmaestro.domain.requirement.controller;

import com.gae4coon.cloudmaestro.domain.requirement.service.RequirementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/requirement-api")
@RequiredArgsConstructor
public class RequirementController {
    private final RequirementService requirementService;

    @PostMapping("/available")
    public List<String> available(@RequestBody String postData){
        System.out.println("available: "+postData);
        List<String> result = requirementService.getRequirement(postData);

        return result;
    }
}
