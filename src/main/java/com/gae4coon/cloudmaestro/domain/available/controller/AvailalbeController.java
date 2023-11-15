package com.gae4coon.cloudmaestro.domain.available.controller;

import com.gae4coon.cloudmaestro.domain.alert.service.DiagramCheckService;
import com.gae4coon.cloudmaestro.domain.available.service.ALBService;
import com.gae4coon.cloudmaestro.domain.available.service.AutoScalingService;
import com.gae4coon.cloudmaestro.domain.available.service.DTOTransfer;
import com.gae4coon.cloudmaestro.domain.file.service.FileService;
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
@RequestMapping("/api/v1/available-require")
@RequiredArgsConstructor
public class AvailalbeController {
    private final DTOTransfer dtotransfer;
    private final ALBService albservice;
    private final AutoScalingService autoScalingService;

    @PostMapping("/available")
    public ResponseEntity <Map<String, Object>> addALB(@RequestBody Map<String, Object> requestData){
        Map<String, Object> result = new HashMap<String, Object> ();
        int albCount = 0;
        int autogroupcount = 0;
        try{

                Map<String, Object> resultData = (Map<String, Object>) requestData.get("result");
                List<Map<String, Object>> nodeDataArray = (List<Map<String, Object>>) resultData.get("nodeDataArray");
                List<Map<String, Object>> linkDataArray = (List<Map<String, Object>>) resultData.get("linkDataArray");

                List<GroupData> groupDataList = new ArrayList<>();
                List<NodeData> nodeDataList = new ArrayList<>();
                List<LinkData> linkDataList = new ArrayList<>();

                // DTO의 형식에 맞게 변환
                dtotransfer.converMapToData(groupDataList, nodeDataList, linkDataList, linkDataArray, nodeDataArray);





                // ALB 에 넣기
                albservice.addALB(albCount,nodeDataList,groupDataList,linkDataList);
                linkDataList = albservice.unique(linkDataList);

                // Auto Scaling Group에 넣기
                autoScalingService.addAutoScaling(autogroupcount,nodeDataList,groupDataList,linkDataList);
                linkDataList = albservice.unique(linkDataList);




                /// 전체 데이터 넣기
                Map<String, Object> responseBody = new HashMap<>();

                List<Object> finalDataArray = new ArrayList<>();
                finalDataArray.addAll(nodeDataList);
                finalDataArray.addAll(groupDataList);

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
