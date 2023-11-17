package com.gae4coon.cloudmaestro.domain.requirements.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gae4coon.cloudmaestro.domain.requirements.dto.RequireDiagramDTO;
import com.gae4coon.cloudmaestro.domain.security.service.SecurityService;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.GraphLinksModel;
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
    public ResponseEntity<?> userRequirement(@RequestBody RequireDiagramDTO requireDiagramDTO) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        GraphLinksModel diagramData = mapper.readValue(requireDiagramDTO.getDiagramData(), GraphLinksModel.class);

        System.out.println("requirement: " + requireDiagramDTO.getRequirementData());
        System.out.println("reqwqrojasepfo:"+diagramData);
//
        Map<String, Object> responseArray = diagramDtoService.dtoGenerator(diagramData);
        // rehost에서 받은 DTO와 동일하게 작동



        HashMap<String, Object> responseDiagram = securityService.globalService(requireDiagramDTO.getRequirementData().getGlobalRequirements(), responseArray);


        //        return ResponseEntity.ok("success");
        return ResponseEntity.ok(responseDiagram);
    }
}
