package com.gae4coon.cloudmaestro.domain.alert.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gae4coon.cloudmaestro.domain.alert.dto.inputDto;
import com.gae4coon.cloudmaestro.domain.alert.dto.inputNodeDto;
import com.gae4coon.cloudmaestro.domain.alert.service.DiagramCheckService;
import com.gae4coon.cloudmaestro.domain.requirements.dto.RequireDTO;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.GraphLinksModel;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import com.gae4coon.cloudmaestro.domain.ssohost.service.DiagramDTOService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/alert-api")
@RequiredArgsConstructor
public class CheckController {

    private final DiagramCheckService diagramCheckService;
    private final DiagramDTOService diagramDTOService;

    @PostMapping("/alert-check")
    public ResponseEntity<HashMap<String, Object>> alertCheck(@RequestBody LinkData postData) {
//        if(bindingResult.hasErrors()){
//            return new ResponseEntity(HttpStatus.BAD_REQUEST);
//        }
        HashMap result = new HashMap<>();
        try {

            HashMap ResponseMap = diagramCheckService.linkCheck(postData);
            result.put("result", ResponseMap);
            return ResponseEntity.ok().body(result);

        } catch (Exception e) {
            result.put("result", "error");
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.ok().body(result);
        }
    }
    @PostMapping("/group-check")
    public ResponseEntity<Object> groupCheck(@RequestBody inputDto inputData) throws JsonProcessingException {
        HashMap result = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("inputData: "+inputData);
        try {
            if (inputData.getDiagramData()!=null) {
                GraphLinksModel diagramData = mapper.readValue(inputData.getDiagramData(), GraphLinksModel.class);
                System.out.println("diagramData:" + diagramData);
                // diagramData formatter
                Map<String, Object> responseArray = diagramDTOService.dtoGenerator(diagramData);

                List<NodeData> nodeDataList = (List<NodeData>) responseArray.get("nodeDataArray");
                List<GroupData> groupDataList = (List<GroupData>) responseArray.get("groupDataArray");
                List<LinkData> linkDataList = (List<LinkData>) responseArray.get("linkDataArray");

                if (inputData.getCheckOption().equals("VPC")) {
                    HashMap ResponseMap = diagramCheckService.vpcCheck(groupDataList, inputData.getNewData());
                    result.put("result", ResponseMap);
                } else {
                    HashMap<String, String> check = new HashMap<>();
                    check.put("status", "success");
                    result.put("result", check);
                }
            }else {
                HashMap<String, String> check = new HashMap<>();
                check.put("status", "success");
                result.put("result", check);
            }

            return ResponseEntity.ok().body(result);
        } catch (Exception e) {
            result.put("result", "error");
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.ok().body(result);
        }
    }

    @PostMapping("/node-check")
    public ResponseEntity<Object> nodeCheck(@RequestBody inputNodeDto inputData) throws JsonProcessingException {
        HashMap result = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("inputData: "+inputData);
        try {
            if (inputData.getDiagramData()!=null) {
                GraphLinksModel diagramData = mapper.readValue(inputData.getDiagramData(), GraphLinksModel.class);
                System.out.println("diagramData:" + diagramData);
                // diagramData formatter
                Map<String, Object> responseArray = diagramDTOService.dtoGenerator(diagramData);

                List<NodeData> nodeDataList = (List<NodeData>) responseArray.get("nodeDataArray");
                List<GroupData> groupDataList = (List<GroupData>) responseArray.get("groupDataArray");
                List<LinkData> linkDataList = (List<LinkData>) responseArray.get("linkDataArray");

                if (inputData.getCheckOption().equals("API Gateway")) {
                    HashMap ResponseMap = diagramCheckService.APICheck(nodeDataList, groupDataList, inputData.getNewData());
                    result.put("result", ResponseMap);
                } else {
                    HashMap<String, String> check = new HashMap<>();
                    check.put("status", "success");
                    result.put("result", check);
                }
            }else {
                HashMap<String, String> check = new HashMap<>();
                check.put("status", "success");
                result.put("result", check);
            }

            return ResponseEntity.ok().body(result);
        } catch (Exception e) {
            result.put("result", "error");
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.ok().body(result);
        }
    }

}
