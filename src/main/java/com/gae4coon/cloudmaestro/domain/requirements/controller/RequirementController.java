package com.gae4coon.cloudmaestro.domain.requirements.controller;


import com.gae4coon.cloudmaestro.domain.requirements.dto.RequireDiagramDTO;
import com.gae4coon.cloudmaestro.domain.security.service.SecurityService;
import com.gae4coon.cloudmaestro.domain.ssohost.service.DiagramDTOService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/request-api")
@RequiredArgsConstructor
public class RequirementController {
    private final SecurityService securityService;
    private final DiagramDTOService diagramDtoService;

    @PostMapping(value = "/userRequirement")
    public ResponseEntity<?> userRequirement(@RequestBody RequireDiagramDTO requireDiagramDTO){
        System.out.println("requirement: " + requireDiagramDTO);

        Map<String, Object> responseArray = diagramDtoService.dtoGenerator(requireDiagramDTO.getGraphLinksModel());
        securityService.globalService(requireDiagramDTO.getRequireDTO().getGlobalRequirements(), responseArray);

        return ResponseEntity.ok().body("Received successfully");
    }
}
