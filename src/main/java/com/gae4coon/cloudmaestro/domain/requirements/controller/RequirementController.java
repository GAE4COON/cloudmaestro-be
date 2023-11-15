package com.gae4coon.cloudmaestro.domain.requirements.controller;


import com.gae4coon.cloudmaestro.domain.requirements.dto.RequireDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/request-api")
@RequiredArgsConstructor
public class RequirementController {

    @PostMapping(value = "/userRequirement")
    public ResponseEntity<?> uploadFile(@RequestBody RequireDTO requireDTO){
        System.out.println("requirement: " + requireDTO);
        return ResponseEntity.ok().body("Received successfully");
    }
}
