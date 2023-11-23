/*package com.gae4coon.cloudmaestro.domain.naindae.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gae4coon.cloudmaestro.domain.naindae.service.RegionService;
import com.gae4coon.cloudmaestro.domain.naindae.service.RequirementService;
import com.gae4coon.cloudmaestro.domain.naindae.service.DnsMultiService;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1/naindae-api")
@RequiredArgsConstructor
public class AvailabilityRequirementController {
    private final RequirementService requirementService;
    private final RegionService regionService;
    private final DnsMultiService dnsMultiService;

    @PostMapping("/available")
    public ResponseEntity<HashMap<String, Object>> multiAz(@RequestBody String diagram){

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

    @PostMapping("/multiregion")
    public ResponseEntity<HashMap<String, Object>> multiRegion(@RequestBody String diagram){
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

            Map<String, Object> result1 = regionService.getRegion(nodeDataList,groupDataList,linkDataList);
            Map<String, Object> result2 = regionService.getOriginalRegion(nodeDataList,groupDataList,linkDataList);

            List<NodeData> newNodeDataList = new ArrayList<>();
            List<GroupData> newGroupDataList = new ArrayList<>();
            List<LinkData> newLinkDataList = new ArrayList<>();
            List<NodeData> newNodeDataList2 = new ArrayList<>();

            newNodeDataList = (List<NodeData>) result1.get("nodes");
            newGroupDataList = (List<GroupData>) result1.get("groups");
            newLinkDataList = (List<LinkData>) result1.get("links");

            nodeDataList = (List<NodeData>) result2.get("nodes");
            linkDataList = (List<LinkData>) result2.get("links");

            System.out.println("------------require--------------");
            System.out.println("nodeDataList " + nodeDataList);
            System.out.println("groupDataList " + groupDataList);
            System.out.println("linkDataList " + linkDataList);
            System.out.println("linkDataList2 " + newLinkDataList);

            Map<String, Object> responseBody = new HashMap<>();

            List<Object> finalDataArray = new ArrayList<>();

            finalDataArray.addAll(nodeDataList);
            finalDataArray.addAll(newNodeDataList);

            linkDataList=regionService.regionLink(nodeDataList,newNodeDataList,linkDataList);

            finalDataArray.addAll(groupDataList);
            finalDataArray.addAll(newGroupDataList);

            finalDataArray.removeIf(Objects::isNull);

            responseBody.put("class", "GraphLinksModel");
            responseBody.put("linkKeyProperty", "key");
            responseBody.put("nodeDataArray", finalDataArray);
            responseBody.put("linkDataArray", Stream.concat(linkDataList.stream(), newLinkDataList.stream()).collect(Collectors.toList()));

            HashMap<String, Object> response = new HashMap<>();

            response.put("result", responseBody);

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            System.out.println("error" + e);
            return null;
        }
    }

    @PostMapping("/dnsmultiregion")
    public ResponseEntity<HashMap<String, Object>> dnsMultiRegion(@RequestBody String diagram){

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


            List<NodeData> newNodeDataList = new ArrayList<>();
            List<GroupData> newGroupDataList = new ArrayList<>();
            List<LinkData> newLinkDataList = new ArrayList<>();

            Map<String, Object> result = dnsMultiService.getRequirementDns(nodeDataList,groupDataList,linkDataList);

            newNodeDataList = (List<NodeData>) result.get("nodes");
            newGroupDataList = (List<GroupData>) result.get("groups");
            newLinkDataList = (List<LinkData>) result.get("links");


            System.out.println("------------require--------------");
            System.out.println("nodeDataList " + nodeDataList);
            System.out.println("groupDataList " + groupDataList);
            System.out.println("linkDataList " + linkDataList);

            Map<String, Object> responseBody = new HashMap<>();

            List<Object> finalDataArray = new ArrayList<>();

            finalDataArray.addAll(nodeDataList);
            finalDataArray.addAll(newNodeDataList);
            finalDataArray.addAll(groupDataList);
            finalDataArray.addAll(newGroupDataList);

            finalDataArray.removeIf(Objects::isNull);

            responseBody.put("class", "GraphLinksModel");
            responseBody.put("linkKeyProperty", "key");
            responseBody.put("nodeDataArray", finalDataArray);
            responseBody.put("linkDataArray", Stream.concat(linkDataList.stream(), newLinkDataList.stream()).collect(Collectors.toList()));

            HashMap<String, Object> response = new HashMap<>();

            response.put("result", responseBody);

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            System.out.println("error" + e);
            return null;
        }
    }

}*/
