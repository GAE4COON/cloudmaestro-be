package com.gae4coon.cloudmaestro.domain.ssohost.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.*;
import com.gae4coon.cloudmaestro.domain.ssohost.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/v1/file-api/rehost")
@RequiredArgsConstructor
public class RehostController {
    private final SecurityGroupService securityGroupService;
    private final ModifyLink modifyLink;
    private final NetworkToAWS networkToAWS;
    private final DiagramDTOService diagramDtoService;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @PostMapping("/ssohost")
    public ResponseEntity<HashMap<String, Object>> postNetworkData(@RequestBody(required = false) String postData) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            GraphLinksModel model = mapper.readValue(postData, GraphLinksModel.class);

            Map<String, Object> responseArray = diagramDtoService.dtoGenerator(model);
            List<NodeData> nodeDataList = (List<NodeData>) responseArray.get("nodeDataArray");
            List<GroupData> groupDataList = (List<GroupData>) responseArray.get("groupDataArray");
            List<LinkData> linkDataList = (List<LinkData>) responseArray.get("linkDataArray");
            Map<String, Object> cost = (Map<String, Object>) responseArray.get("cost");

            addGroup(groupDataList, nodeDataList, linkDataList);

            // sg 추가 -> 관문 fw 있는 경우 nfw로 변경 후 vpc 추가
            securityGroupService.addSecurityGroup(nodeDataList, groupDataList, linkDataList);
            System.out.println("linkData" + linkDataList);
            securityGroupService.modifySecurityGroupLink(nodeDataList, linkDataList);

            // fw 건너서 link 연결
            modifyLink.excludeNode(nodeDataList, groupDataList, unique(linkDataList));
            // fw와 관련된 link 제외
            modifyLink.deleteNode(nodeDataList, unique(linkDataList));

            //node, group, link 정보 변경 (network node to aws)
            networkToAWS.changeAll2(nodeDataList, groupDataList, linkDataList);

            // igw, nacl 등 넣기
            networkToAWS.addNetwork(nodeDataList, groupDataList, linkDataList);

            // Region, vpc, available zone 넣기
            networkToAWS.setRegionAndVpcData(nodeDataList, groupDataList, linkDataList);
            // 전체 노드 관리 & location 위치 정보
            networkToAWS.managedAllNode(nodeDataList, groupDataList, linkDataList);

            // 위치 정보 수정 ,,, ,하하


            //setNoLinkLocation.noLinkGroup(nodeDataList, groupDataList,linkDataList);


            HashMap<String, Object> response = diagramDtoService.dtoComplete(nodeDataList, groupDataList, unique(linkDataList), cost);
            System.out.println("response"+ response);

            return ResponseEntity.ok().body(response);

        } catch (Exception e) {
            System.out.println("error" + e);
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());

            return null;
        }

    }

    // 전체를 그룹으로 감싸기
    public void addGroup(List<GroupData> groupDataList, List<NodeData> nodeDataList, List<LinkData> linkDataList) {
        //find groupdata that has no parent and has no group node

        boolean isNoGroupNode = false;
        for(NodeData node: nodeDataList){
            if(node.getGroup()==null){
                isNoGroupNode = true;
                break;
            }
        }

        List<GroupData> noParentGroup = new ArrayList<>();
        for (GroupData group : groupDataList) {
            if (group.getGroup() == null) {
                noParentGroup.add(group);
            }
        }

//        if(!noParentGroup.isEmpty()&&isNoGroupNode){
//            GroupData group = GroupData.builder()
//                    .key("TEMPGROUP")
//                    .text("TEMPGROUP")
//                    .isGroup(true)
//                    .stroke("rgb(122,161,22)")
//                    .build();
//
//            groupDataList.add(group);
//            for (GroupData noParent : noParentGroup) {
//                noParent.setGroup("TEMPGROUP");
//                System.out.println("noParent" + noParent);
//            }
//        }



        //if groupdata==null, add all node group
        if(groupDataList.isEmpty()){
            GroupData group = GroupData.builder()
                    .key("group")
                    .text("group")
                    .isGroup(true)
                    .stroke("rgb(122,161,22)")
                    .build();

            groupDataList.add(group);
            for (NodeData node : nodeDataList) {
                node.setGroup("group");
            }
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
        return setlist;
    }

}