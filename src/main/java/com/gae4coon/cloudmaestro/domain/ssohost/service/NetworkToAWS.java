package com.gae4coon.cloudmaestro.domain.ssohost.service;

import com.gae4coon.cloudmaestro.domain.resource.service.AddResourceService;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.analysis.function.Add;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;

import javax.swing.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class NetworkToAWS {
    private final DiagramDTOService diagramDTOService;
    private final AddResourceService addResourceService;
    private final Location2Service locationService2;

    public void managedAllNode(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList){
        // 서비스 노드 관리 (lift & shift 시 변경되는 노드들 (ips, ids, anti ddos..)
        managedReplaceNode(nodeDataList, groupDataList, linkDataList);
        // 위치 정보 수정
        locationService2.setNodeLocation(nodeDataList, groupDataList,linkDataList);
        // nodeData에 없는 link정보 삭제
        diagramDTOService.removeNullLink(nodeDataList, groupDataList, linkDataList);
    }

    public void managedReplaceNode(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList){
        groupDataList.add(addResourceService.addService());

        List<NodeData> replaceNodeList = new ArrayList<>();
        replaceNodeList.addAll(diagramDTOService.getNodeListByText(nodeDataList, "IPS"));
        replaceNodeList.addAll(diagramDTOService.getNodeListByText(nodeDataList, "IDS"));
        replaceNodeList.addAll(diagramDTOService.getNodeListByText(nodeDataList, "Anti DDoS"));

        for (NodeData nodeData: replaceNodeList){
            String node = nodeData.getKey();

            if (node.contains("Anti DDoS")) {
                if(diagramDTOService.isNodeDataContains(nodeDataList, "Shield")){
                    nodeDataList.remove(nodeData);
                    continue;
                }

                String nodeKey = nodeData.getKey();
                nodeKey = nodeKey.replace("Anti DDoS", "Shield");
                nodeData.setKey(nodeKey);
                nodeData.setText("Shield");
                nodeData.setSource("/img/AWS_icon/Arch_Security-Identity-Compliance/Arch_AWS-Shield_48.svg");
                nodeData.setType("Security-Identity-Compliance");
                nodeData.setGroup("Service");
            }
            if (node.contains("IPS")) {

                if(diagramDTOService.isNodeDataContains(nodeDataList, "Network Firewall")){
                    nodeDataList.remove(nodeData);
                    continue;
                }
                String nodeKey = nodeData.getKey();
                nodeKey = nodeKey.replace("IPS", "Network Firewall");
                nodeData.setKey(nodeKey);
                nodeData.setText("Network Firewall");
                nodeData.setSource("/img/AWS_icon/Arch_Security-Identity-Compliance/Arch_AWS-Network-Firewall_48.svg");
                nodeData.setType("Security-Identity-Compliance");
                nodeData.setGroup("Region");
                nodeData.setLoc("0 -300");

                // endpoint 생성
                List<GroupData> vpcGroupList = diagramDTOService.getGroupListByText(groupDataList, "VPC");
                for(GroupData vpcGroup: vpcGroupList){
                    GroupData fps = addResourceService.addPublicsubnet();
                    fps.setKey("Firewall Public Subnet"+diagramDTOService.getGroupNumber(groupDataList, "Firewall Public Subnet"));
                    fps.setText("Firewall Public Subnet");
                    fps.setGroup(vpcGroup.getKey());
                    groupDataList.add(fps);

                    NodeData fpend = addResourceService.addNetworkFirewallEndpoints(nodeDataList);
                    fpend.setGroup(fps.getKey());
                    fpend.setLoc("200 -200");
                    nodeDataList.add(fpend);

                    LinkData link = new LinkData();
                    link.setFrom(nodeKey);
                    link.setTo(fpend.getKey());
                    linkDataList.add(link);
                }

            }
            if (node.contains("IDS")) {
                if (diagramDTOService.isNodeDataContains(nodeDataList, "CloudTrail")){
                    nodeDataList.remove(nodeData);
                    continue;
                }
                String nodeKey = nodeData.getKey();
                nodeKey = nodeKey.replace("IDS", "CloudTrail");
                nodeData.setKey(nodeKey);
                nodeData.setText("CloudTrail");
                nodeData.setSource("/img/AWS_icon/Arch_Management-Governance/Arch_AWS-CloudTrail_48.svg");
                nodeData.setType("Management-Governance");
                nodeData.setGroup("Service");
            }
        }
        serviceLocation(nodeDataList);

    }

    public void serviceLocation(List<NodeData> nodeDataList){
        List<NodeData> serviceNodeDataList = diagramDTOService.getNodeListByGroup(nodeDataList, "Service");
        int x = 0;
        for (NodeData nodeData: serviceNodeDataList){
            nodeData.setLoc(x+" -700");
            x+=70;
        }

    }

    public void changeNodeSource(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList) {

        for (NodeData nodeData : nodeDataList) {
            String node = nodeData.getKey();
            // server, web server
            if (node.contains("Server")) {
                String nodeKey = nodeData.getKey();
                nodeKey = nodeKey.replace("Server", "EC2");
                nodeData.setKey(nodeKey);
                nodeData.setText("EC2");
                nodeData.setSource("/img/AWS_icon/Arch_Compute/Arch_Amazon-EC2_48.svg");
                nodeData.setType("Compute");
            } else if (node.contains("Database")) {
                String nodeKey = nodeData.getKey();
                nodeKey = nodeKey.replace("Database", "RDS");
                nodeData.setKey(nodeKey);
                nodeData.setText("RDS");
                nodeData.setSource("/img/AWS_icon/Arch_Database/Arch_Amazon-RDS_48.svg");
                nodeData.setType("Database");
            }

        }
    }




    public void changeGroupSource2(List<NodeData> nodeDataList, List<GroupData> groupDataList) {
        Set<String> groupKey = new HashSet<>();

        for (GroupData groupData : groupDataList) {
            String group = groupData.getKey();
            groupKey.add(group);
        }

        int count = 1;
        List<GroupData> newGroupDataList = new ArrayList<>();

        List<String> excludeGroup = List.of("Security Group", "VPC", "Firewall Public Subnet", "Service", "-");

        for (String key : groupKey) {
            boolean skip = false;
            for (String exclude : excludeGroup) {
                if (key.contains(exclude)) {
                    skip = true;
                    break;
                }
            }
            if (skip) continue;
            for (GroupData groupData : groupDataList) {
                if (!groupData.getKey().equals(key)) continue;
                // public subnet 생성
                GroupData publicSubnet = new GroupData();
                publicSubnet.setKey(key + " Public subnet " + count);
                publicSubnet.setStroke("rgb(122,161,22)");
                publicSubnet.setType("AWS_Groups");
                publicSubnet.setText(key + " Public subnet " + count);
                publicSubnet.setIsGroup(true);
                publicSubnet.setGroup(groupData.getGroup());
                newGroupDataList.add(publicSubnet);

                //private subnet 생성
                groupData.setKey(key + " Private subnet " + count);
                groupData.setText(key + " Private subnet " + count);
                groupData.setStroke("rgb(0,164,166)");


                for (NodeData node : nodeDataList) {
                    if(node.getGroup()==null) continue;
                    if (!node.getGroup().equals(key)) continue;
                    node.setGroup(key + " Private subnet " + count);
                }
                for (GroupData group : groupDataList) {
                    if (group.getGroup() == null) continue;
                    if (!group.getGroup().equals(key)) continue;
                    group.setGroup(key + " Private subnet " + count);
                }
            }
            count++;
        }
        groupDataList.addAll(newGroupDataList);
        Map<List<NodeData>, List<GroupData>> result = new HashMap<>();
        result.put(nodeDataList, groupDataList);

        return;
    }


    public void changeLinkSource(List<LinkData> linkDataList) {
        for (LinkData linkData : linkDataList) {
            String node = linkData.getFrom();
            String value;
            // server, web server
            if (node.contains("Server")) {
                value = node.replace("Server", "EC2");
                linkData.setFrom(value);
            } else if (node.contains("Database")) {
                value = node.replace("Database", "RDS");
                linkData.setFrom(value);
            }
        }
        for (LinkData linkData : linkDataList) {
            String node = linkData.getTo();
            String value;
            // server, web server
            if (node.contains("Server")) {
                value = node.replace("Server", "EC2");
                linkData.setTo(value);
            }else if (node.contains("Database")) {
                value = node.replace("Database", "RDS");
                linkData.setTo(value);
            }
        }
    }


    public void changeRegionandVpc(List<GroupData> groupDataList) {


        Set<String> groupKey = new HashSet<>();

        boolean containsVPC = false;
        for (GroupData groupData : groupDataList) {
            String group = groupData.getKey();
            groupKey.add(group);

            if (group.toLowerCase().contains("vpc")) {
                containsVPC = true;
                break;
            }
        }


        // VPC 없는 그룹을 VPC로 묶기
        if (!containsVPC) {
            GroupData vpc = addResourceService.addVPC(groupDataList);
            vpc.setGroup("Region");
            groupDataList.add(vpc);

            for (GroupData groupData : groupDataList) {
                if (groupData.getGroup() == null) {
                    groupData.setGroup(vpc.getKey());
                }
            }
        }

        // VPC 별로 탐색
        List<GroupData> vpcGroup = diagramDTOService.getGroupListByText(groupDataList, "VPC");
        for (GroupData vpc : vpcGroup) {
            String vpcKey = vpc.getKey();
            // groupdata를 vpc안의 groupdata만 뽑기
            List<GroupData> vpcGroupDataList = diagramDTOService.getNestedGroupDataList(groupDataList, vpcKey);

            GroupData az = addResourceService.addAvailabilityZone(groupDataList);
            az.setGroup(vpcKey);
            groupDataList.add(az);

            for (GroupData groupData : vpcGroupDataList) {
                if (groupData.getKey().contains("Private subnet") || groupData.getKey().contains("Public subnet")) {
//                groupData.setStroke("rgb(0,164,166)");
//                    groupData.setIsGroup(true);
                    groupData.setGroup(az.getKey());
//                    groupData.setType("AWS_Groups");
                }

            }

            if(!diagramDTOService.isGroupDataContains(groupDataList, "AWS Cloud")) {
                GroupData AWSCloud = addResourceService.addAWSCloud();
                groupDataList.add(AWSCloud);
            }

            if(!diagramDTOService.isGroupDataContains(groupDataList, "Region")){
                GroupData region = addResourceService.addRegion();
                region.setGroup("AWS Cloud");
                groupDataList.add(region);
            }

            vpc.setGroup("Region");

        }
    }

    public void moveNodeToRegion(List<NodeData> nodeDataList) {
        for (NodeData nodeData : nodeDataList) {
            if (nodeData.getKey().contains("Shield")) {
//                nodeData.setGroup("Region");
            }
            if (nodeData.getKey().contains("CloudTrail")) {
//                nodeData.setGroup("Region");
            } else if (nodeData.getKey().contains("CloudFront")) {
                nodeData.setGroup("Region");
            } else if (nodeData.getKey().contains("CodeDeploy")) {
                nodeData.setGroup("Region");
            } else if (nodeData.getKey().contains("CloudWatch")) {
                nodeData.setGroup("Region");
            } else if (nodeData.getKey().contains("Simple Storage Service")) {
                nodeData.setGroup("Region");
            } else if (nodeData.getKey().contains("Internet")) {
                nodeData.setGroup("Region");
            } else if (nodeData.getKey().contains("NACL")) {
                nodeData.setGroup("VPC");
            }
        }
    }

    public void addNat(List<NodeData> nodeDataList, List<GroupData> groupDataList) {
        // 모든 public subnet 그룹을 찾기
        int natCount = 1;

        List<String> publicSubnetGroupKeys = new ArrayList<>();
        for (GroupData group : groupDataList) {
            if (group.getKey().contains("Public subnet")) {
                publicSubnetGroupKeys.add(group.getKey());
            }
        }

        if (publicSubnetGroupKeys.isEmpty()) {
            return;
        }
        //TODO 여기에 NACL+natCount의 loc를 찾아 해당 y축의 위치에 +를 엄청해주면 된다
        String newLoc = "";
        for (String publicSubnetGroupKey : publicSubnetGroupKeys) {

            for (NodeData nodeData : nodeDataList) {
                if (nodeData.getKey().equals("NACL" + natCount)) {
                    String location = nodeData.getLoc();
                    String[] locParts = location.split(" ");

                    double x = Double.parseDouble(locParts[0]);
                    double y = Double.parseDouble(locParts[1]);
                    newLoc = x + " " + (y - 250);
                }
            }

            NodeData natNode = new NodeData();
            natNode.setKey("NAT Gateway" + natCount);
            natNode.setText("NAT Gateway");
            natNode.setLoc(newLoc);
            natNode.setSource("/img/AWS_icon/Arch_Networking-Content-Delivery/Arch_Amazon-VPC_NAT-Gateway_48.svg");
            natNode.setType("Networking-Content-Delivery");
            natNode.setGroup(publicSubnetGroupKey);

            // NAT 노드를 리스트에 추가
            nodeDataList.add(natNode);
            natCount++;
        }

    }

    public void addNacl(List<NodeData> nodeDataList, List<GroupData> groupDataList) {
        int naclCount = 1;

        List<String> privateSubnetGroupKeys = new ArrayList<>();
        for (GroupData group : groupDataList) {
            if (group.getKey().contains("Private subnet")) {
                privateSubnetGroupKeys.add(group.getKey());
            }
        }
        if (privateSubnetGroupKeys.isEmpty()) {
            return;
        }

        NodeData naclNode = new NodeData();
        naclNode.setKey("Network Access Control List (NACL)"); // NAT 키를 고유하게 만듦
        naclNode.setText("Network Access Control List (NACL)");
        naclNode.setLoc("200 -400"); // 계산된 위치 설정
        naclNode.setSource("/img/AWS_icon/Arch_Networking-Content-Delivery/Arch_Amazon-VPC_Network-Access-Control-List_48.svg");
        naclNode.setType("Networking-Content-Delivery");
        naclNode.setGroup("Region");

        // NAT 노드를 리스트에 추가
        nodeDataList.add(naclNode);
    }

    public void addInternet(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList) {
        if (groupDataList.isEmpty()) {
            return;
        }
        NodeData internetNode = new NodeData();
        internetNode.setKey("Internet Gateway");
        internetNode.setText("Internet Gateway");
        internetNode.setSource("/img/AWS_icon/Arch_Networking-Content-Delivery/Arch_Amazon-VPC_Internet-Gateway_48.svg");
        internetNode.setType("Networking-Content-Delivery");
        internetNode.setGroup("Region");
        internetNode.setLoc("0 0");
        nodeDataList.add(internetNode);

        for (GroupData group : groupDataList) {
            if (group.getKey().contains("Public subnet")) {
                LinkData link = new LinkData();
                link.setFrom("Internet Gateway");
                link.setTo(group.getKey()); //여기에 public subnet이 와야함
                linkDataList.add(link);
            }
        }

        for (GroupData group : groupDataList) {
            if (group.getKey().contains("Public subnet")) {
                String privateSubnetKey = group.getKey().replace("Public", "Private");
                LinkData link = new LinkData();
                link.setFrom(group.getKey());
                link.setTo(privateSubnetKey);
                linkDataList.add(link);
            }
        }

    }

    public void changeAll2(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList) {
//        diagramDTOService.addServiceGroup(groupDataList);

        changeNodeSource(nodeDataList, groupDataList, linkDataList);
        changeLinkSource(linkDataList);
        changeGroupSource2(nodeDataList, groupDataList);
    }


    public void setRegionAndVpcData(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList) {
        changeRegionandVpc(groupDataList);
        // Region에 옮기기
//        moveNodeToRegion(nodeDataList);
        // 기본 옵션들 추가하기
    }

    public void addNetwork(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList) {
        addNat(nodeDataList, groupDataList);
        addNacl(nodeDataList, groupDataList);
        addInternet(nodeDataList, groupDataList, linkDataList);
    }



}
