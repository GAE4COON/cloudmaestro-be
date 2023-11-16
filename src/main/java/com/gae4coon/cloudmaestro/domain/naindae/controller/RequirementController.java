package com.gae4coon.cloudmaestro.domain.naindae.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gae4coon.cloudmaestro.domain.naindae.service.RequirementService;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.GraphLinksModel;
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
public class RequirementController {
    private final RequirementService requirementService;

    @PostMapping("/available")
    public ResponseEntity<HashMap<String, Object>> available(@RequestBody String diagram){

        try {
            ObjectMapper mapper = new ObjectMapper();
            GraphLinksModel model = mapper.readValue(diagram, GraphLinksModel.class);

            if (model == null) return null;

            List<NodeData> dataArray = model.getNodeDataArray();
            List<NodeData> nodeDataList = new ArrayList<>();
            List<GroupData> groupDataList = new ArrayList<>();

            List<LinkData> linkDataList = model.getLinkDataArray();

            for (NodeData data : dataArray) {
                if (data.getIsGroup() != null) {
                    GroupData groupData = new GroupData(data.getKey(), data.getText(), data.getIsGroup(), data.getGroup(), data.getType(), data.getStroke());
                    groupDataList.add(groupData);
                } else {
                    nodeDataList.add(data);
                }
            }

            Map<String, Object> result = requirementService.getRequirementAvailable(nodeDataList,groupDataList,linkDataList);
            nodeDataList = (List<NodeData>) result.get("nodes");
            groupDataList = (List<GroupData>) result.get("groups");

            System.out.println("------------require--------------");
            System.out.println("nodeDataList " + nodeDataList);
            System.out.println("groupDataList " + groupDataList);
            System.out.println("linkDataList " + linkDataList);

            Map<String, Object> responseBody = new HashMap<>();

            List<Object> finalDataArray = new ArrayList<>();

            finalDataArray.addAll(nodeDataList);
            finalDataArray.addAll(groupDataList);

            finalDataArray.removeIf(Objects::isNull);

            responseBody.put("class", "GraphLinksModel");
            responseBody.put("linkKeyProperty", "key");
            responseBody.put("nodeDataArray", finalDataArray);  // 예시
            responseBody.put("linkDataArray", linkDataList);  // 예시

            HashMap<String, Object> response = new HashMap<>();

            response.put("result", responseBody);

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            System.out.println("error" + e);
            return null;
        }

    }
}
