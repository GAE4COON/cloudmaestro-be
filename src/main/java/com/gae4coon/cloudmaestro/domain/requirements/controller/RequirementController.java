package com.gae4coon.cloudmaestro.domain.requirements.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.gae4coon.cloudmaestro.domain.requirements.dto.RequireDiagramDTO;
import com.gae4coon.cloudmaestro.domain.requirements.service.AllRequirementService;
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
    private final AllRequirementService allRequirementService;
    private final DiagramDTOService diagramDtoService;


    @PostMapping(value = "/userRequirement")
    public ResponseEntity<?> userRequirement(@RequestBody RequireDiagramDTO requireDiagramDTO) throws JsonProcessingException {

        HashMap<String, Object> response = allRequirementService.requirement(requireDiagramDTO);

        return ResponseEntity.ok(response);
    }
}