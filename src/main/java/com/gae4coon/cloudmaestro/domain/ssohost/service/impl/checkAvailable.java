package com.gae4coon.cloudmaestro.domain.ssohost.service.impl;

import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;

import java.util.ArrayList;
import java.util.List;

public class checkAvailable {

    public void addALB(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList){

        String name = "PROD";

        // Public Subnet에 해당되는 영역에 넣기
        int ALB = 2;
        int albCount = 0;
        for (int i = 0; i <= ALB; i++ ){
            NodeData albNode = new NodeData();
            albNode.setKey("ALB"+albCount);
            albNode.setText("ALB");
            albNode.setType("Networking-Content-Delivery");
            albNode.setSource("/img/AWS_icon/Arch_Networking-Content-Delivery/Res_Elastic-Load-Balancing_Application-Load-Balancer_48.svg");
            nodeDataList.add(albNode);
        }


        // Link 정보 연결 Public Subnet과 Application Balancer 연결

        for (GroupData group : groupDataList) {
            if (group.getKey().contains(name + "Public subnet")) {
                String privateSubnetKey = group.getKey().replace("Public", "Private");
                LinkData link = new LinkData();
                link.setFrom(group.getKey());
                link.setTo(privateSubnetKey);
                linkDataList.add(link);
            }
        }



    }

    public void addALBLink(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList) {


    }
}
