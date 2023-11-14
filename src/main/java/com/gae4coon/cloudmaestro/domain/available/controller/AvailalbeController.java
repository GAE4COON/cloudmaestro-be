package com.gae4coon.cloudmaestro.domain.available.controller;

import com.gae4coon.cloudmaestro.domain.alert.service.DiagramCheckService;
import com.gae4coon.cloudmaestro.domain.available.service.AvailableService;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/v1/requirement-api")
@RequiredArgsConstructor
public class AvailalbeController {

    private final AvailableService availableService;

    @PostMapping("/available")
    public ResponseEntity <Map<String, Object>> addALB(@RequestBody Map<String, Object> requestData){
        Map<String, Object> result = new HashMap<>();

        try{
            Map<String, Object> resultData = (Map<String, Object>) requestData.get("result");
            List<Map<String, Object>> nodeDataArray = (List<Map<String, Object>>) resultData.get("nodeDataArray");
            List<Map<String, Object>> linkDataList = (List<Map<String, Object>>) resultData.get("linkDataArray");

            List<GroupData> groupDataList = new ArrayList<>();
            List<NodeData> nodeDataList = new ArrayList<>();

            // Group Data와 Node Data 의 변환
            for( Map<String, Object> data : nodeDataArray){
                if(data.containsKey("loc")){
                    NodeData nodeData = availableService.convertMapToNodeData(data);
                    nodeDataList.add(nodeData);
                }else{
                    GroupData groupData = availableService.convertMapToGroupData(data);
                    groupDataList.add(groupData);
                }
            }


            System.out.println("============================");
            System.out.println("groupData"+groupDataList);
            System.out.println("nodeData"+nodeDataList);
            System.out.println("link"+linkDataList);


            result.put("message","ALB Data processed successfully");


            Map<String, Object> responseBody = new HashMap<>();

            List<Object> finalDataArray = new ArrayList<>();
            finalDataArray.addAll(nodeDataList);
            finalDataArray.addAll(groupDataList);

            //finalDataArray.removeIf(Objects::isNull);

            responseBody.put("class", "GraphLinksModel");
            responseBody.put("linkKeyProperty", "key");
            responseBody.put("nodeDataArray", finalDataArray);  // 예시
            responseBody.put("linkDataArray", linkDataList);  // 예시

            HashMap<String, Object> response = new HashMap<>();

            response.put("result", responseBody);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 예외 처리 로직
            result.put("error", "An error occurred: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }



}
