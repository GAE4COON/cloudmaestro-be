package com.gae4coon.cloudmaestro.domain.ssohost.service;

import com.gae4coon.cloudmaestro.domain.resource.service.AddResourceService;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
@Service
@RequiredArgsConstructor
public class SecurityGroupService{
    private final DiagramDTOService diagramDTOService;
    private final AddResourceService addResourceService;

    public void addSecurityGroup(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList) {
        List<NodeData> fwNodeData = diagramDTOService.getNodeListByText(nodeDataList, "Firewall");

        for(NodeData fw: fwNodeData) {
            String fwVPC = null;
            List<LinkData> fwLinkData = diagramDTOService.getLinkDataListByFrom(linkDataList, fw.getKey());
            System.out.println("fwLinkData" + fwLinkData);
            GroupData sg = addResourceService.addSecurityGroup(groupDataList);

            for(LinkData fwLink: fwLinkData) {
                System.out.println("fwLink" + fwLink);

                // FW -> Node
                NodeData toNode = diagramDTOService.getNodeDataByKey(nodeDataList, fwLink.getTo());
                // server, database인 경우 Security Group 처리

                if(toNode!=null && (toNode.getText().contains("Server") || toNode.getText().contains("Database"))) {
                    // sg group 한번만 만들기
                    if(!diagramDTOService.isGroupDataEquals(groupDataList, sg.getKey())) {
                        sg.setGroup(toNode.getGroup());
                        groupDataList.add(sg);
                    }
                    dfs(sg.getKey(), toNode, nodeDataList, linkDataList);
                }

                // FW -> Group
                GroupData toGroup = diagramDTOService.getGroupDataByKey(groupDataList, fwLink.getTo());
                // Network Firewall 처리
                if(toGroup!=null) {
                    System.out.println("toGroup" + toGroup);
                    if(fwVPC!=null){
                        toGroup.setGroup(fwVPC);
                        continue;
                    };

                    GroupData vpc = addResourceService.addVPC(groupDataList);
                    vpc.setGroup("Region");
                    groupDataList.add(vpc);
                    fwVPC = vpc.getKey();

                    toGroup.setGroup(vpc.getKey());

                    NodeData NetworkFW = diagramDTOService.getNodeDataByText(nodeDataList, "Network Firewall");
                    // NF 없으면 생성
                    if (NetworkFW == null) {
                        // add Network Firewall into Region
                        NetworkFW = addResourceService.addNetworkFirewall();
                        NetworkFW.setGroup("Region");
                        NetworkFW.setLoc("0 -300");
                        nodeDataList.add(NetworkFW);
                    }

                    // create Network Firewall Group in VPC
                    GroupData fps = addResourceService.addPublicsubnet();
                    fps.setKey("Firewall Public Subnet"+diagramDTOService.getGroupNumber(groupDataList, "Firewall Public Subnet"));
                    fps.setText("Firewall Public Subnet");
                    fps.setGroup(vpc.getKey());
                    groupDataList.add(fps);

                    // add Network Firewall endpoint into Firewall Public Subnet
                    NodeData fwend = addResourceService.addNetworkFirewallEndpoints(nodeDataList);
                    fwend.setGroup(fps.getKey());
                    fwend.setLoc("200 -200");
                    nodeDataList.add(fwend);

                    LinkData netFWtoendpoint = new LinkData();
                    netFWtoendpoint.setFrom(NetworkFW.getKey());
                    netFWtoendpoint.setTo(fwend.getKey());
                    linkDataList.add(netFWtoendpoint);

                    System.out.println("netFWtoendpoint" + netFWtoendpoint);

                }
            }
        }
    }

    private void dfs(String securityGroup, NodeData node, List<NodeData> nodeDataList, List<LinkData> linkDataList){
        List<String> visited = new ArrayList<>();
        if(node.getKey().startsWith("Firewall")) return;
        visited.add(node.getKey());
        node.setGroup(securityGroup);
        System.out.println("dfs"+node.getKey());

        List<LinkData> nodeLinkData = diagramDTOService.getLinkDataListByFrom(linkDataList, node.getKey());

        for (LinkData link:nodeLinkData){
            if(!visited.contains(link.getTo())){
                dfs(securityGroup, diagramDTOService.getNodeDataByKey(nodeDataList, link.getTo()), nodeDataList, linkDataList);
            }
        }
    }


    public void modifySecurityGroupLink(List<NodeData> nodeDataList, List<LinkData> linkDataList) {
        for (NodeData nodeData : nodeDataList) {
            if (nodeData.getGroup()!=null&&!nodeData.getGroup().contains("Security Group")) continue;

            for (LinkData linkData : linkDataList) {
                NodeData linkFrom = diagramDTOService.getNodeDataByKey(nodeDataList, linkData.getFrom());
                NodeData linkTo = diagramDTOService.getNodeDataByKey(nodeDataList, linkData.getTo());
                if(linkFrom==null||linkTo==null) continue;
                if(linkFrom.getGroup()!=null&&linkTo.getGroup()!=null&&linkFrom.getGroup().equals(linkTo.getGroup())) continue;

                if (linkData.getFrom()!=null && linkData.getFrom().equals(nodeData.getKey())) {
                    linkData.setFrom(nodeData.getGroup());
                } else if (linkData.getTo()!=null && linkData.getTo().equals(nodeData.getKey())) {
                    linkData.setTo((nodeData.getGroup()));

                }
            }
        }
    }


    // from 이 server이고, to가 같은 목적지라면 ?
    public void addEc2Group(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList){

        List<NodeData> newNodeDataList = new ArrayList<>();
        List<GroupData> newGroupDataList = new ArrayList<>();

        // Server에서 같은 to를 향한다면 같은 그룹으로 묶기
        for (NodeData nodedata : nodeDataList) {
            System.out.println("NodeData" + nodedata);
            for(LinkData linkdata : linkDataList){
                if(nodedata.getText().contains("Server")){


                }
            }
        }


    }
}
