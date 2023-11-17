package com.gae4coon.cloudmaestro.domain.refactor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gae4coon.cloudmaestro.domain.refactor.service.BackupService;
import com.gae4coon.cloudmaestro.domain.requirements.dto.RequireDiagramDTO;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.GraphLinksModel;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import com.gae4coon.cloudmaestro.domain.ssohost.service.DiagramDTOService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@Log4j2
@RequestMapping("/api/v1/refactor-api")
public class backupController {

    private final BackupService backupService;
    private final DiagramDTOService diagramDtoService;

    @PostMapping("/backup")
    public ResponseEntity<HashMap<String, Object>> postNetworkData(@RequestBody RequireDiagramDTO requireDiagramDTO) {


        try {
            ObjectMapper mapper = new ObjectMapper();
            GraphLinksModel diagramData = mapper.readValue(requireDiagramDTO.getDiagramData(), GraphLinksModel.class);

            if (diagramData == null) return null;

            Map<String, Object> responseArray = diagramDtoService.dtoGenerator(diagramData);
            // rehost에서 받은 DTO와 동일하게 작동

//            List<NodeData> dataArray = diagramData.getNodeDataArray();
//            List<NodeData> nodeDataList = new ArrayList<>();
//            List<GroupData> groupDataList = new ArrayList<>();
//
//            List<LinkData> linkDataList = diagramData.getLinkDataArray();

            backupService.requirementParsing(requireDiagramDTO, responseArray);



        } catch (Exception e) {
            System.out.println("error" + e);
            return null;
        }
        return null;

    }
}
