package com.gae4coon.cloudmaestro.domain.ssohost.service;

import com.gae4coon.cloudmaestro.domain.resource.service.AddResourceService;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.analysis.function.Add;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class NetworkToAWS {
    private final DiagramDTOService diagramDTOService;
    private final AddResourceService addResourceService;

    public void deleteServiceDuplicatedNode(List<NodeData> nodeDataList, List<LinkData> linkDataList) {
        List<NodeData> serviceNodeList = new ArrayList<>();
        List<NodeData> removeNode = new ArrayList<>();
        Set<String> seenTexts = new HashSet<>();

        System.out.println("duplicate nodeDataList" + nodeDataList);
        for (NodeData node : nodeDataList) {
            System.out.println(node.getGroup());
            if (node.getGroup().equals("Service")) {
                System.out.println("node" + node);
                removeNode.add(node);
                if (seenTexts.add(node.getSource())) {
                    serviceNodeList.add(node);
                }

            }
        }

        for (NodeData node : removeNode) {
            nodeDataList.remove(node);
            LinkData slink = diagramDTOService.getLinkDataByFrom(linkDataList, node.getKey());
            if (slink != null) {
                linkDataList.remove(slink);
            }
            slink = diagramDTOService.getLinkDataByTo(linkDataList, node.getKey());
            if (slink != null) {
                linkDataList.remove(slink);
            }
        }
        nodeDataList.addAll(serviceNodeList);

    }

    public void changeNodeSource(List<NodeData> nodeDataList) {

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
            } else if (node.contains("Anti DDoS")) {
                String nodeKey = nodeData.getKey();
                nodeKey = nodeKey.replace("Anti DDoS", "Shield");
                nodeData.setKey(nodeKey);
                nodeData.setText("Shield");
                nodeData.setSource("/img/AWS_icon/Arch_Security-Identity-Compliance/Arch_AWS-Shield_48.svg");
                nodeData.setType("Security-Identity-Compliance");
                nodeData.setGroup("Service");
            } else if (node.contains("IPS")) {

                String nodeKey = nodeData.getKey();
                nodeKey = nodeKey.replace("IPS", "CloudTrail");
                nodeData.setKey(nodeKey);
                nodeData.setText("CloudTrail");
                nodeData.setSource("/img/AWS_icon/Arch_Management-Governance/Arch_AWS-CloudTrail_48.svg");
                nodeData.setType("Management-Governance");
                nodeData.setGroup("Service");

            } else if (node.contains("IDS")) {

                String nodeKey = nodeData.getKey();
                nodeKey = nodeKey.replace("IDS", "CloudTrail");
                nodeData.setKey(nodeKey);
                nodeData.setText("CloudTrail");
                nodeData.setSource("/img/AWS_icon/Arch_Management-Governance/Arch_AWS-CloudTrail_48.svg");
                nodeData.setType("Management-Governance");
                nodeData.setGroup("Service");

            } else if (node.contains("Database")) {
                String nodeKey = nodeData.getKey();
                nodeKey = nodeKey.replace("Database", "RDS");
                nodeData.setKey(nodeKey);
                nodeData.setText("RDS");
                nodeData.setSource("/img/AWS_icon/Arch_Database/Arch_Amazon-RDS_48.svg");
                nodeData.setType("Database");
            }

        }
        return;
    }


//    public void changeGroupSource(List<NodeData> nodeDataList, List<GroupData> groupDataList) {
//        Set<String> groupKey = new HashSet<>();
//
//        for(GroupData groupData:groupDataList){
//            String group = groupData.getKey();
//            groupKey.add(group);
//        }
//        int count = 1;
//
//        for(String key:groupKey){
//            if(key.contains("Security Group")||key.contains("VPC")||key.contains("Firewall Subnet")) continue;
//            for(GroupData groupData:groupDataList) {
//                if(!groupData.getKey().equals(key)) continue;
//                if(groupData.getGroup().contains("VPC")) continue;
//
//                groupData.setKey(key+" Virtual private cloud (VPC) " + count);
//                groupData.setText(key+" Virtual private cloud (VPC) " + count);
//                groupData.setStroke("rgb(140,79,255)");
//
//                for(NodeData node: nodeDataList) {
//                    if (!node.getGroup().equals(key)) continue;
//                    node.setGroup(key + " Virtual private cloud (VPC) " + count);
//                }
//                for(GroupData group: groupDataList) {
//                    if(group.getGroup()==null) continue;
//                    if (!group.getGroup().equals(key)) continue;
//                    group.setGroup(key + " Virtual private cloud (VPC) " + count);
//                }
//            }
//            GroupData vpc = addResourceService.addVPC();
//            vpc.setKey(key+" Virtual private cloud (VPC) " + count);
//            vpc.setText("VPC");
//
//            count++;
//        }
//
//        Map<List<NodeData>, List<GroupData>> result = new HashMap<>();
//        result.put(nodeDataList, groupDataList);
//    }


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
            System.out.println("linkData" + linkData);
            String value;
            // server, web server
            if (node.contains("Server")) {
                value = node.replace("Server", "EC2");
                linkData.setFrom(value);
            } else if (node.contains("Anti DDoS")) {
                value = node.replace("Anti DDoS", "Shield");
                linkData.setFrom(value);
            } else if (node.contains("IPS")) {
                value = node.replace("IPS", "CloudTrail");
                linkData.setFrom(value);
            } else if (node.contains("IDS")) {
                value = node.replace("IDS", "CloudTrail");
                linkData.setFrom(value);
            } else if (node.contains("Database")) {
                value = node.replace("Database", "RDS");
                linkData.setFrom(value);
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

            System.out.println("vpcGroupData" + vpcGroupDataList);

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

        changeNodeSource(nodeDataList);
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


    public void setNodeLocation(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList) {

        // Group 정보에서 public subnet이 몇 개인지 확인
        List<String> count_public_subnets = new ArrayList<>();
        for (GroupData groupdata : groupDataList) {
            if (groupdata.getKey().contains("Public subnet")) {
                count_public_subnets.add(groupdata.getKey());
            }
        }

        // LinkData Public Subnet 별로 순서 정하기

        // LinkData 정렬
        linkDataList.sort(Comparator.comparing(LinkData::getFrom).thenComparing(LinkData::getTo));

        Iterator<LinkData> iterator = linkDataList.iterator();
        while (iterator.hasNext()) {
            LinkData linkData = iterator.next();
            System.out.println("sortedlinkData" + linkData);
            if (linkData.getFrom().contains("Shield")) {
                iterator.remove();
            }
        }

        // public subnet을 일단 internet gateway를 기반으로 위치 정하기
        addPublicLocation(nodeDataList, groupDataList, linkDataList, count_public_subnets);


    }


    public void addPublicLocation(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList, List<String> count_public_subnet) {

        double nat_x = 400;
        double nat_y = -400;

        double node_x;
        double node_y;

        // Except 해야 하는 리스트
        List<String> Except = new ArrayList<>(Arrays.asList("Internet Gateway", "Public subnet", "Private subnet"));

        //NAT 정보 옮기기
        for (String public_subnet : count_public_subnet) {


            // Public Subnet에 있는 NAT 정하기
            double[] updatedCoordinates = processPublicSubnet(nodeDataList, public_subnet, nat_x, nat_y);

            nat_x = updatedCoordinates[0];
            nat_y = updatedCoordinates[1];

            // 해당 prod private subnet에 포함된 링크 연결된 정보를 탐색해서 그에 맞게 위치 정보넣기
            String[] parts = public_subnet.split(" ");
            String netName = parts[0];

            node_x = nat_x + 430;
            node_y = nat_y - 85;


            for (LinkData linkdata : linkDataList) {
                for (NodeData nodedata : nodeDataList) {
                    // Group Data
                    List<String> visitGroup = new ArrayList<>();

                    if (linkdata.getFrom().contains("Group")) {
                        double[] newCoordinates = processFromGroupData(linkdata, nodedata, groupDataList, netName, visitGroup, Except, node_x, node_y);
                        node_x = newCoordinates[0];
                        node_y = newCoordinates[1];
                    }
                    if (linkdata.getTo().contains("Group")) {
                        System.out.println("GetTo" + linkdata.getTo());
                        double[] newCoordinates = processToGroupData(linkdata, nodedata, groupDataList, netName, visitGroup, Except, node_x, node_y);
                        node_x = newCoordinates[0];
                        node_y = newCoordinates[1];

                    }
                    // group에 없는 ec2일 경우
                    if (linkdata.getFrom().contains(nodedata.getKey()) &&
                            !Except.contains(nodedata.getKey()) &&
                            nodedata.getGroup().contains(netName)
                    ) {
                        System.out.println("Ec2 Comeon" + nodedata.getKey());
                        node_x += 20;
                        String newLoc = (node_x) + " " + (node_y);
                        nodedata.setLoc(newLoc);

                    }

                }


            }


        }


    }

    public double[] processPublicSubnet(List<NodeData> nodeDataList, String publicSubnet, double nat_x, double nat_y) {
        double x = 0.0;
        double y = 0.0;
        for (NodeData nodeData : nodeDataList) {
            if (nodeData.getGroup().contains(publicSubnet)) {
                String location = nodeData.getLoc();
                String[] locParts = location.split(" ");
                System.out.println("public Subnet" + publicSubnet);
                x = nat_x - 1;
                y = nat_y + 260;
                String newLoc = (x) + " " + (y);
                System.out.println("newLoc" + newLoc);
                nat_x -= 1;
                nat_y += 260;
                nodeData.setLoc(newLoc);
                break;
            }
        }
        return new double[]{nat_x, nat_y};
    }

    private double[] processFromGroupData(LinkData linkdata, NodeData nodedata, List<GroupData> groupDataList, String netName, List<String> visitGroup, List<String> Except, double node_x, double node_y) {
        // 해당 group이 prod private subnet에 포함됐는 지 확인
        String security_group = linkdata.getFrom();
        for (GroupData group : groupDataList) {
            if (group.getKey().contains(security_group) &&
                    group.getGroup().contains(netName) &&
                    !visitGroup.contains(nodedata.getKey()) &&
                    !Except.contains(security_group)
                // 새로운 security 요소여야 함 &&

            ) {
                // 포함되는 게 확인됐다면, 그룹 내의 요소들 가져오기
                if (nodedata.getGroup().contains(security_group)) {
                    visitGroup.add(nodedata.getKey());
                    node_x += 150;
                    String newLoc = (node_x) + " " + (node_y);
                    nodedata.setLoc(newLoc);

                }

            }
        }
        return new double[]{node_x, node_y};
    }

    private double[] processToGroupData(LinkData linkdata, NodeData nodedata, List<GroupData> groupDataList, String netName, List<String> visitGroup, List<String> Except, double node_x, double node_y) {

        // 해당 group이 prod private subnet에 포함됐는 지 확인
        String security_group = linkdata.getTo();
        for (GroupData group : groupDataList) {
            if (group.getKey().contains(security_group) &&
                    group.getGroup().contains(netName) &&
                    !visitGroup.contains(nodedata.getKey()) &&
                    !Except.contains(security_group)// 새로운 security 요소여야 함

            ) {
                //visitGroup.add(security_group);
                // 포함되는 게 확인됐다면, 그룹 내의 요소들 가져오기
                if (nodedata.getGroup().contains(security_group)) {
                    visitGroup.add(nodedata.getKey());
                    System.out.println("group include nodedata2 : " + nodedata);
                    node_x += 150;
                    String newLoc = (node_x) + " " + (node_y);
                    nodedata.setLoc(newLoc);

                }

            }
        }
        return new double[]{node_x, node_y};
    }


}

