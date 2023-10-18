package com.gae4coon.cloudmaestro.domain.ssohost.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.*;
import com.gae4coon.cloudmaestro.domain.ssohost.service.SecurityGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/v1/file-api/rehost")
public class RehostController {

    @Autowired
    private SecurityGroupService securityGroupService;

    @PostMapping("/ssohost")
    public ResponseEntity<HashMap<String, Object>> postNetworkData(@RequestBody(required = false) String postData){

        try{
            ObjectMapper mapper = new ObjectMapper();
            GraphLinksModel model = mapper.readValue(postData, GraphLinksModel.class);

        if (model == null) return null;

        List<NodeData> dataArray = model.getNodeDataArray();
        List<NodeData> nodeDataList = new ArrayList<>();
        List<GroupData> groupDataList = new ArrayList<>();

        List<LinkData> linkDataList = model.getLinkDataArray();

        for(NodeData data: dataArray){
            if(data.getIsGroup()!=null){
                GroupData groupData = new GroupData(data.getKey(), data.getText(), data.getIsGroup(), data.getGroup(),data.getType(), data.getStroke());
                groupDataList.add(groupData);
            }
            else{
                nodeDataList.add(data);
            }
        }

        // nodeDataArray에서 nodeData와 groupData 분류
        System.out.println("nodeDataList "+nodeDataList);
        System.out.println("groupDataList "+groupDataList);
        System.out.println("linkDataList "+linkDataList);

        // node rehost

        // create security group
        securityGroupService.addSecurityGroup(nodeDataList, groupDataList, linkDataList);

        System.out.println("nodeDataList "+nodeDataList);
        System.out.println("groupDataList "+groupDataList);
        System.out.println("linkDataList "+linkDataList);
        System.out.println("-------------------------------");


        securityGroupService.modifySecurityGroupLink(nodeDataList, groupDataList, linkDataList);
        System.out.println("nodeDataList "+nodeDataList);
        System.out.println("groupDataList "+groupDataList);
        System.out.println("linkDataList "+linkDataList);
        System.out.println("-------------------------------");
        linkDataList = unique(linkDataList);

        securityGroupService.excludeNode(nodeDataList, groupDataList, linkDataList);
        Map<String, Object> responseBody = new HashMap<>();

        List<Object> finalDataArray = new ArrayList<>();
        finalDataArray.addAll(nodeDataList);
        finalDataArray.addAll(groupDataList);

        System.out.println("final "+finalDataArray);
        finalDataArray.removeIf(Objects::isNull);
        System.out.println("remove final "+finalDataArray);


        responseBody.put("class", "GraphLinksModel");
        responseBody.put("linkKeyProperty", "key");
        responseBody.put("nodeDataArray", finalDataArray);  // 예시
        responseBody.put("linkDataArray", unique(linkDataList));  // 예시

        HashMap<String, Object> response = new HashMap<>();

        response.put("result",responseBody);

        return ResponseEntity.ok().body(response);

        }catch (Exception e){
            System.out.println("error"+e);
            return null;
        }

    }

    public List<LinkData> unique(List<LinkData> originalList) {
        Set<LinkData> linkDataSet = new HashSet<>();
        for (LinkData link1 : originalList) {
            linkDataSet.add(link1);
        }

        List<LinkData> setlist = new ArrayList<>();
        for (LinkData l : linkDataSet) {
            setlist.add(l);
        }
        System.out.println("linkDataSet "+linkDataSet);
        return setlist;
    }

}