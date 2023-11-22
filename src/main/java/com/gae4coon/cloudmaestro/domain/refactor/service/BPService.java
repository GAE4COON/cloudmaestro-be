package com.gae4coon.cloudmaestro.domain.refactor.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.gae4coon.cloudmaestro.domain.refactor.entity.BpModule;
import com.gae4coon.cloudmaestro.domain.refactor.repository.ModuleRepository;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.GraphLinksModel;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import com.gae4coon.cloudmaestro.domain.ssohost.service.DiagramDTOService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class BPService {

    private final DiagramDTOService diagramDtoService;
    private final ModuleRepository moduleRepository;
    public void bpsearch(String id, List<NodeData> nodeDataList, List<LinkData> linkDataList, List<GroupData> groupDataList, int cnt){
        BpModule bp = moduleRepository.findBpModuleById(id);
        if (nodeDataList == null) {
            nodeDataList = new ArrayList<>();
        }

        bploc(bp, nodeDataList, linkDataList, groupDataList, cnt);

        System.out.println("Test: BP????"+bp.getJsonData());
    }


    public void bploc(BpModule bp, List<NodeData> nodeDataList, List<LinkData> linkDataList, List<GroupData> groupDataList, int cnt){

        double maxY = Double.MIN_VALUE;
        double maxX = Double.MIN_VALUE;

        if (nodeDataList == null) {
            System.out.println("nodeDataList is null");
            return;
        }

        for (NodeData node:nodeDataList) {
            String location =node.getLoc();
            String[] locParts = location.split(" ");

            double x = Double.parseDouble(locParts[0]);
            double y = Double.parseDouble(locParts[1]);

            if (x > maxX) {
                maxX = x;
            }
            if (y > maxY) {
                maxY = y;
            }
        }

        //maxY+=200;
        maxX+=700;
        try {

        ObjectMapper mapper = new ObjectMapper();
        GraphLinksModel diagramData = mapper.readValue(bp.getJsonData(), GraphLinksModel.class);
        Map<String, Object> responseArray = diagramDtoService.dtoGenerator(diagramData);

        List<NodeData> BPnodeDataList = (List<NodeData>) responseArray.get("nodeDataArray");
        List<GroupData> BPgroupDataList = (List<GroupData>) responseArray.get("groupDataArray");
        List<LinkData> BPlinkDataList = (List<LinkData>) responseArray.get("linkDataArray");

        System.out.println("---------------");
        System.out.println(BPnodeDataList);


        for (GroupData group: BPgroupDataList) {

            for (NodeData node:BPnodeDataList){
                if(node.getGroup().equals(group.getKey())){
                    node.setGroup(node.getGroup()+" BP"+cnt);
                }
            }

            for (LinkData link:BPlinkDataList){
                if(link.getTo().equals(group.getKey())){
                    link.setTo(link.getTo()+" BP"+cnt);
                }
                if(link.getFrom().equals(group.getKey())){
                    link.setFrom(link.getFrom()+" BP"+cnt);
                }
            }

            for (GroupData groupitem:BPgroupDataList){
                if (groupitem.getGroup() != null && groupitem.getGroup().equals(group.getKey())) {
                    groupitem.setGroup(groupitem.getGroup() + " BP" + cnt);
                }
            }

            group.setKey(group.getKey()+" BP"+cnt);
            groupDataList.add(group);
        }

        for (NodeData node:BPnodeDataList) {
            String location =node.getLoc();
            String[] locParts = location.split(" ");
            double x = Double.parseDouble(locParts[0]);
            double y = Double.parseDouble(locParts[1]);

            node.setLoc(""+(x+maxX)+" "+y);
            nodeDataList.add(node);

        }

        for (LinkData link: BPlinkDataList) {
            linkDataList.add(link);
        }




        } catch (Exception e) {
            System.out.println("Bp error: " + e);
        }




    }
}
