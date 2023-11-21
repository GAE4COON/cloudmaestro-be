package com.gae4coon.cloudmaestro.domain.available.service;

import com.gae4coon.cloudmaestro.domain.requirements.dto.ZoneDTO;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import com.gae4coon.cloudmaestro.domain.ssohost.service.impl.DiagramDTOServiceImpl;
import org.springframework.stereotype.Service;

import java.time.zone.ZoneRules;
import java.util.*;

@Service
public class AvailableService {
    List<NodeData> nodeDataList;
    List<GroupData> groupDataList;
    List<LinkData> linkDataList;
    DiagramDTOServiceImpl diagramDTOService;
    public HashMap<String, Object> availalbeService(List<ZoneDTO> zoneRequirements, Map<String, Object> responseArray) {
        nodeDataList = (List<NodeData>) responseArray.get("nodeDataArray");
        groupDataList = (List<GroupData>) responseArray.get("groupDataArray");
        linkDataList = (List<LinkData>) responseArray.get("linkDataArray");

        HashMap<String, Object> result = new HashMap<>();

        // 위치 정보가 제일 높은 Y의 Node를 선택을 함
        NodeData highestNode = null;
        highestNode = findNodeWithHighestYCoordinate(nodeDataList);

        System.out.println("highestNode: "+highestNode);

        // nacl2 = -764.9202380643841 336.05824133430997
        // alb = -300 ,540
        // alb2 = -80 ,540
        // alb + 220 = alb2

        String location = highestNode.getLoc();
        String[] locParts = location.split(" ");
        double node_x = Double.parseDouble(locParts[0]);
        double node_y = Double.parseDouble(locParts[1]);
        node_x += 460;
        node_y += 220;
        int index = 0;
        for(int i = 0; i < zoneRequirements.size(); i++){
            NodeData AlbNode = new NodeData();
            AlbNode.setText("Application Load Balancer(ALB)");
            AlbNode.setKey("Application Load Balancer(ALB)" + index);
            AlbNode.setFigure("Rectangle");
            AlbNode.setSource("/img/AWS_icon/Resource_icon/Res_Networking-Content-Delivery/Res_Elastic-Load-Balancing_Application-Load-Balancer_48.svg");
            AlbNode.setType("Networking-Content-Delivery");
            String newLoc = (node_x) + " " + (node_y);
            AlbNode.setLoc(newLoc);
            AlbNode.setGroup("VPC");
            node_x += 220;
            System.out.println("ALBNode: "+AlbNode);
            nodeDataList.add(AlbNode);
        }


        //// ========================== Final =====================================
        List<Object> finalDataArray = new ArrayList<>();
        finalDataArray.addAll(nodeDataList);
        finalDataArray.addAll(groupDataList);

        finalDataArray.removeIf(Objects::isNull);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("class", "GraphLinksModel");
        responseBody.put("linkKeyProperty", "key");
        responseBody.put("nodeDataArray", finalDataArray);
        responseBody.put("linkDataArray", linkDataList);

        HashMap<String, Object> response = new HashMap<>();
        response.put("result", responseBody);
        //HashMap<String, Object> response = diagramDTOService.dtoComplete(nodeDataList, groupDataList, linkDataList);
        System.out.println("response"+ response);

        return response;

    }

    public NodeData findNodeWithHighestYCoordinate(List<NodeData> nodeDataList) {
        NodeData highestYNode = null;
        double highestY = Double.NEGATIVE_INFINITY;

        for (NodeData nodedata : nodeDataList) {
            String location = nodedata.getLoc();
            String[] locParts = location.split(" ");

            double y = Double.parseDouble(locParts[1]);

            if (y > highestY) {
                highestY = y;
                highestYNode = nodedata;
            }
        }

        return highestYNode;
    }

}
