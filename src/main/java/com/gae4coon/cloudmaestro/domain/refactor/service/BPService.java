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
    public void bpsearch(String id, List<NodeData> nodeDataList, List<LinkData> linkDataList, List<GroupData> groupDataList){
        BpModule bp = moduleRepository.findBpModuleById(id);
        bploc(bp, nodeDataList, linkDataList, groupDataList);

        System.out.println("Test: BP????"+bp.getJsonData());
    }


    public void bploc(BpModule bp, List<NodeData> nodeDataList, List<LinkData> linkDataList, List<GroupData> groupDataList){

        double maxY = Double.MIN_VALUE;
        double maxX = Double.MIN_VALUE;

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
        maxX+=500;
        try {

        ObjectMapper mapper = new ObjectMapper();
        GraphLinksModel diagramData = mapper.readValue(bp.getJsonData(), GraphLinksModel.class);
        Map<String, Object> responseArray = diagramDtoService.dtoGenerator(diagramData);

        List<NodeData> BPnodeDataList = (List<NodeData>) responseArray.get("nodeDataArray");
        List<GroupData> BPgroupDataList = (List<GroupData>) responseArray.get("groupDataArray");
        List<LinkData> BPlinkDataList = (List<LinkData>) responseArray.get("linkDataArray");

        System.out.println("---------------");
        System.out.println(BPnodeDataList);

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

        for (GroupData group: BPgroupDataList) {
            groupDataList.add(group);
        }


        } catch (Exception e) {
            System.out.println("Bp error: " + e);
        }




    }
}
