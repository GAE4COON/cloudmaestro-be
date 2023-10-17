package com.gae4coon.cloudmaestro.domain.ssohost.controller;

import com.gae4coon.cloudmaestro.domain.file.service.FileService;
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
    public ResponseEntity<HashMap<String, Object>> postNetworkData(@RequestBody(required = false) GraphLinksModel model) {
        if (model == null) return (ResponseEntity<HashMap<String, Object>>) ResponseEntity.badRequest();

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
        // delete all link from/to sg node + sg안에서 연결되어있던 가장 상단노드의 from을 sg와 연결

        securityGroupService.modifySecurityGroupLink(nodeDataList, groupDataList, linkDataList);
        System.out.println("nodeDataList "+nodeDataList);
        System.out.println("groupDataList "+groupDataList);
        System.out.println("linkDataList "+linkDataList);
        // link rehost

        Map<String, Object> responseBody = new HashMap<>();

        List<Object> finalDataArray = new ArrayList<>();
        finalDataArray.addAll(nodeDataList);
        finalDataArray.addAll(groupDataList);


        // 이 부분에서 finalData를 적절하게 설정하세요.
        responseBody.put("class", "GraphLinksModel");
        responseBody.put("linkKeyProperty", "key");
        responseBody.put("nodeDataArray", finalDataArray);  // 예시
        responseBody.put("linkDataArray", unique(linkDataList));  // 예시


        return ResponseEntity.ok().body((HashMap<String, Object>) responseBody);
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