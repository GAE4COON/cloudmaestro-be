package com.gae4coon.cloudmaestro.domain.ssohost.service.impl;

import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import com.gae4coon.cloudmaestro.domain.ssohost.service.DiagramDTOService;
import com.gae4coon.cloudmaestro.domain.ssohost.service.NetworkToAWS;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class NetworkToAWSImpl implements NetworkToAWS {
    private final DiagramDTOService diagramDTOService;
    @Override
    public void deleteServiceDuplicatedNode(List<NodeData> nodeDataList){
        List<NodeData> serviceNodeList = new ArrayList<>();
        List<NodeData> removeNode = new ArrayList<>();
        Set<String> seenTexts = new HashSet<>();

        System.out.println("duplicate nodeDataList" + nodeDataList);
        for(NodeData node: nodeDataList){
            System.out.println(node.getGroup());
            if(node.getGroup().equals("Service")){
                System.out.println("node" + node);
                removeNode.add(node);
                if(seenTexts.add(node.getSource())){
                    serviceNodeList.add(node);
                }
            }
        }

        for(NodeData node: removeNode){
            nodeDataList.remove(node);
        }
        nodeDataList.addAll(serviceNodeList);

        System.out.println("serviceNodeList" + serviceNodeList);

    }


    @Override
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

            } else if (node.contains("IDS")){

                String nodeKey = nodeData.getKey();
                nodeKey = nodeKey.replace("IDS", "CloudTrail");
                nodeData.setKey(nodeKey);
                nodeData.setText("CloudTrail");
                nodeData.setSource("/img/AWS_icon/Arch_Management-Governance/Arch_AWS-CloudTrail_48.svg");
                nodeData.setType("Management-Governance");
                nodeData.setGroup("Service");

            }else if (node.contains("Database")) {
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

    @Override
    public void changeGroupSource(List<NodeData> nodeDataList, List<GroupData> groupDataList) {
        Set<String> groupKey = new HashSet<>();

        for(GroupData groupData:groupDataList){
            String group = groupData.getKey();
            groupKey.add(group);
        }
        int count = 1;

        for(String key:groupKey){
            if(key.contains("Security Group")) continue;
            for(GroupData groupData:groupDataList) {
                if(!groupData.getKey().equals(key)) continue;
                groupData.setKey(key+" Virtual private cloud (VPC) " + count);
                groupData.setText(key+" Virtual private cloud (VPC) " + count);
                groupData.setStroke("rgb(140,79,255)");

                for(NodeData node: nodeDataList) {
                    if (!node.getGroup().equals(key)) continue;
                    node.setGroup(key + " Virtual private cloud (VPC) " + count);
                }
                for(GroupData group: groupDataList) {
                    if(group.getGroup()==null) continue;
                    if (!group.getGroup().equals(key)) continue;
                    group.setGroup(key + " Virtual private cloud (VPC) " + count);
                }
            }
            count++;
        }

        Map<List<NodeData>, List<GroupData>> result = new HashMap<>();
        result.put(nodeDataList, groupDataList);
    }


    public void changeGroupSource2(List<NodeData> nodeDataList,List<GroupData> groupDataList) {
        Set<String> groupKey = new HashSet<>();

        for(GroupData groupData:groupDataList){
            String group = groupData.getKey();
            groupKey.add(group);
        }

        int count = 1;
        List<GroupData> newGroupDataList = new ArrayList<>();
        for(String key:groupKey){
            if(key.contains("Security Group")) continue;
            for(GroupData groupData:groupDataList) {
                if(!groupData.getKey().equals(key)) continue;
                // public subnet 생성
                GroupData publicSubnet = new GroupData();
                publicSubnet.setKey(key+" Public subnet " + count);
                publicSubnet.setGroup(key+" Public subnet " + count);
                publicSubnet.setStroke("rgb(122,161,22)");
                publicSubnet.setType("AWS_Groups");
                publicSubnet.setText(key+" Public subnet " + count);
                publicSubnet.setIsGroup(true);
                newGroupDataList.add(publicSubnet);

                //private subnet 생성
                groupData.setKey(key+" Private subnet " + count);
                groupData.setText(key+" Private subnet " + count);
                groupData.setStroke("rgb(0,164,166)");


                for(NodeData node: nodeDataList) {
                    if (!node.getGroup().equals(key)) continue;
                    node.setGroup(key + " Private subnet " + count);
                }
                for(GroupData group: groupDataList) {
                    if(group.getGroup()==null) continue;
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
    @Override

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
            } else if(node.contains("IDS")){
                value = node.replace("IDS","CloudTrail");
                linkData.setFrom(value);
            } else if (node.contains("Database")) {
                value = node.replace("Database","RDS");
                linkData.setFrom(value);
            }
        }
    }


    public void changeRegionandVpc(List<GroupData> groupDataList) {


        Set<String> groupKey = new HashSet<>();

        for(GroupData groupData:groupDataList) {
            String group = groupData.getKey();
            groupKey.add(group);
        }

        // Temporary list to store new GroupData objects
        for(GroupData groupData:groupDataList) {
            if(groupData.getKey().contains("Private subnet") || groupData.getKey().contains("Public subnet") ){
//                groupData.setStroke("rgb(0,164,166)");
                groupData.setIsGroup(true);
                groupData.setGroup("Availability Zone");
                groupData.setType("AWS_Groups");
            }

        }
        // Add all new GroupData objects to the original list
        GroupData availableSubnet = new GroupData();
        availableSubnet.setIsGroup(true);
        availableSubnet.setGroup("VPC");
        availableSubnet.setType("AWS_Groups");
        availableSubnet.setKey("Availability Zone");
        availableSubnet.setText("Availability Zone");
        availableSubnet.setStroke("rgb(0,164,166)");
        groupDataList.add(availableSubnet);

        GroupData vpcSubnet = new GroupData();
        vpcSubnet.setIsGroup(true);
        vpcSubnet.setGroup("Region");
        vpcSubnet.setType("AWS_Groups");
        vpcSubnet.setKey("VPC");
        vpcSubnet.setText("VPC");
        vpcSubnet.setStroke("rgb(140,79,255)");
        groupDataList.add(vpcSubnet);

        GroupData regionSubnet = new GroupData();
        regionSubnet.setIsGroup(true);
//        regionSubnet.setGroup("AWS Cloud");
        regionSubnet.setType("AWS_Groups");
        regionSubnet.setKey("Region");
        regionSubnet.setText("Region");
        regionSubnet.setStroke("rgb(0,164,166)");
        groupDataList.add(regionSubnet);

//        GroupData AWSCloud = new GroupData();
//        AWSCloud.setIsGroup(true);
//        AWSCloud.setType("AWS_Groups");
//        AWSCloud.setKey("AWS Cloud");
//        AWSCloud.setText("AWS Cloud");
//        AWSCloud.setStroke("rgb(0,0,0)");
//        groupDataList.add(AWSCloud);
    }

    public void moveNodeToRegion(List<NodeData> nodeDataList){
        for(NodeData nodeData : nodeDataList){
            if(nodeData.getKey().contains("Shield")){
//                nodeData.setGroup("Region");
            }
            if(nodeData.getKey().contains("CloudTrail")){
//                nodeData.setGroup("Region");
            }
            else if(nodeData.getKey().contains("CloudFront")){
                nodeData.setGroup("Region");
            }
            else if(nodeData.getKey().contains("CodeDeploy")){
                nodeData.setGroup("Region");
            }
            else if(nodeData.getKey().contains("CloudWatch")){
                nodeData.setGroup("Region");
            }
            else if(nodeData.getKey().contains("Simple Storage Service")){
                nodeData.setGroup("Region");
            }
            else if(nodeData.getKey().contains("Internet")){
                nodeData.setGroup("Region");
            }
            else if(nodeData.getKey().contains("NACL")){
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

        if(publicSubnetGroupKeys.isEmpty()){
            return;
        }
        //TODO 여기에 NACL+natCount의 loc를 찾아 해당 y축의 위치에 +를 엄청해주면 된다
        String newLoc = "";
        for (String publicSubnetGroupKey : publicSubnetGroupKeys) {

            for (NodeData nodeData : nodeDataList){
                if(nodeData.getKey().equals("NACL"+natCount)){
                    String location = nodeData.getLoc();
                    String[] locParts = location.split(" ");

                    double x = Double.parseDouble(locParts[0]);
                    double y = Double.parseDouble(locParts[1]);
                    newLoc = x + " " + (y-250);
                }
            }

            NodeData natNode = new NodeData();
            natNode.setKey("NAT Gateway"+natCount);
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

    public void addNacl(List<NodeData> nodeDataList, List<GroupData> groupDataList){
        int naclCount = 1;

        List<String> privateSubnetGroupKeys = new ArrayList<>();
        for (GroupData group : groupDataList) {
            if (group.getKey().contains("Private subnet")) {
                privateSubnetGroupKeys.add(group.getKey());
            }
        }
        if (privateSubnetGroupKeys.isEmpty()){
            return;
        }

        NodeData naclNode = new NodeData();
        naclNode.setKey("Network Access Control List (NACL)"); // NAT 키를 고유하게 만듦
        naclNode.setText("Network Access Control List (NACL)");
        naclNode.setLoc("200 -400"); // 계산된 위치 설정
        naclNode.setSource("/img/AWS_icon/Arch_Networking-Content-Delivery/Arch_Amazon-VPC_Network-Access-Control-List_48.svg");
        naclNode.setType("Networking-Content-Delivery");
        naclNode.setGroup("VPC");

        // NAT 노드를 리스트에 추가
        nodeDataList.add(naclNode);
    }
    public void addInternet(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList){
        if(groupDataList.isEmpty()){
            return;
        }
        NodeData internetNode = new NodeData();
        internetNode.setKey("Internet Gateway");
        internetNode.setText("Internet Gateway");
        internetNode.setSource("/img/AWS_icon/Arch_Networking-Content-Delivery/Arch_Amazon-VPC_Internet-Gateway_48.svg");
        internetNode.setType("Networking-Content-Delivery");
        internetNode.setGroup("VPC");
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


    @Override
    public void changeAll(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList) {
        changeNodeSource(nodeDataList);
        changeLinkSource(linkDataList);
        changeGroupSource(nodeDataList, groupDataList);
    }

    public void changeAll2(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList) {
//        diagramDTOService.addServiceGroup(groupDataList);

        changeNodeSource(nodeDataList);
        changeLinkSource(linkDataList);
        changeGroupSource2(nodeDataList, groupDataList);
    }


    public void setRegionAndVpcData(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList){
        changeRegionandVpc(groupDataList);
        // Region에 옮기기
        moveNodeToRegion(nodeDataList);
        // 기본 옵션들 추가하기




    }

    public void addNetwork(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList){
        addNat(nodeDataList, groupDataList);
        addNacl(nodeDataList,groupDataList);
        addInternet(nodeDataList, groupDataList, linkDataList);
    }



    @Override
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

        while(iterator.hasNext()){
            LinkData linkData = iterator.next();
            if(linkData.getFrom().contains("Shield")){
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

        List<String> Except = new ArrayList<>(Arrays.asList("Internet Gateway", "Public subnet", "Private subnet"));

        // 해당 subnet에 해당하는 거 먼저 할게 ..
        List<LinkData> visitedLinks = new ArrayList<>();
        List<LinkData> setNewLinkList = new ArrayList<>();

        for(LinkData linkdata : linkDataList){
            LinkData nextLink = findNextLink(linkdata, linkDataList, visitedLinks);
            if (nextLink != null && !visitedLinks.contains(nextLink)) {
                visitedLinks.add(nextLink);
                setNewLinkList.add(nextLink);
            }

        }

        for (String public_subnet : count_public_subnet) {

            double[] updatedCoordinates = processPublicSubnet(nodeDataList, public_subnet, nat_x, nat_y);
            nat_x = updatedCoordinates[0];
            nat_y = updatedCoordinates[1];
            String[] parts = public_subnet.split(" ");
            String netName = parts[0];
            node_x = nat_x + 430;
            node_y = nat_y - 85;
            List<String> visitedNode = new ArrayList<>();
            for (LinkData currentLink : setNewLinkList) {
                // 처음 방문한 linkdata에 대해서
                    for (NodeData nodedata : nodeDataList) {
                        if(currentLink.getFrom() == null) {
                            System.out.println("link" + currentLink);
                            break;
                        }
                         //linkdata에 node 관련 정보는 없고 private subnet에 달랑 한개만 있을 때
//                        if(currentLink.getTo().contains("Private subnet")){
//                            double[] newCoordinates = processFromPrivateSubnet(currentLink, nodedata, nodeDataList,linkDataList,groupDataList, netName, Except, node_x, node_y,visitedNode);
//                            node_x = newCoordinates[0];
//                            node_y = newCoordinates[1];
//                        }
                        if (currentLink.getFrom().contains("Group")) {
                            double[] newCoordinates = processFromGroupData(currentLink, nodedata, groupDataList, netName, Except, node_x, node_y,visitedNode);
                            node_x = newCoordinates[0];
                            node_y = newCoordinates[1];
                        }
                        if (currentLink.getTo().contains("Group")) {
                            double[] newCoordinates = processToGroupData(currentLink, nodedata, groupDataList, netName, Except, node_x, node_y,visitedNode);
                            node_x = newCoordinates[0];
                            node_y = newCoordinates[1];
                        }
                        String group_name2 = "";
                        if (currentLink.getFrom().contains(nodedata.getKey()) &&
                                !Except.contains(nodedata.getKey())) {
                            //System.out.println("currentLink" + currentLink);
                            if(nodedata.getGroup() != null){
                                String[] group_name = nodedata.getGroup().split(" ");
                                group_name2 = group_name[0];
                            }
                            if(group_name2.equals(netName)){
                                node_x += 20;
                                String newLoc = (node_x) + " " + (node_y);
                                nodedata.setLoc(newLoc);
                                visitedNode.add(currentLink.getFrom());
                            }

                        }
                        if (currentLink.getTo().contains(nodedata.getKey()) &&
                                !Except.contains(nodedata.getKey())) {
                            if(nodedata.getGroup() != null){
                                String[] group_name = nodedata.getGroup().split(" ");
                                group_name2 = group_name[0];
                            }
                            if(group_name2.equals(netName)){
                                node_x += 20;
                                String newLoc = (node_x) + " " + (node_y);
                                nodedata.setLoc(newLoc);
                                visitedNode.add(currentLink.getTo());
                            }

                        }
                    }
                }
            }

        // 마지막 전처리
        // private subnet에 아무것도 포함되지 않은 노드가 있다면 없애버리자
        }

    public double[] processFromPrivateSubnet(LinkData currentLink, NodeData nodedata, List<NodeData> nodeDataList, List<LinkData> linkDataList, List<GroupData> groupDataList, String netName, List<String> Except, double node_x, double node_y, List<String> visitedNode) {
        // 만약 해당 linkdata 중에서 prod23 private subnet이 포함되는 게 한개라도 없다면 .. ?
        List<String> from = new ArrayList<>();
        List<String> to = new ArrayList<>();
        for(LinkData linkdata : linkDataList){
            from.add(linkdata.getFrom());
            to.add(linkdata.getTo());
        }
        // 다시 정렬
        // 중복을 제거하기 위해 Set 사용
        Set<String> uniqueFrom = new HashSet<>(from);
        Set<String> uniqueTo = new HashSet<>(to);

        // 필요한 경우, 다시 List로 변환
        List<String> distinctFrom = new ArrayList<>(uniqueFrom);
        List<String> distinctTo = new ArrayList<>(uniqueTo);

        System.out.println("distinctFrom" + distinctFrom);
        System.out.println("distinctTo" + distinctTo);



        for(GroupData group : groupDataList){
            String group_name2 = group.getGroup();
            if(group.getGroup() != null){
                String[] group_name = group.getGroup().split(" ");
                group_name2 = group_name[0] + " " + "Private subnet";

            }
            if( group_name2 != null &&
                group_name2.contains(netName) &&
                !distinctFrom.contains(group.getKey()) &&
                !distinctTo.contains(group.getKey())

            ){
                System.out.println("NoLinkCurrentLinkData" + currentLink + group + nodedata);
                double[] newCoordinates = processFromGroupData(currentLink,nodedata, groupDataList, netName, Except, node_x, node_y,visitedNode);
                node_x = newCoordinates[0];
                node_y = newCoordinates[1];
                return new double[]{node_x,node_y};
            }

        }



        return null;
    }




    private LinkData findNextLink(LinkData currentLink, List<LinkData> linkDataList, List<LinkData> visitedLinks) {

        // 그냥 단순한 순서 정렬을 하자고 ..
        for (LinkData link : linkDataList) {
            if(currentLink.getFrom().equals(link.getTo()) &&
                !visitedLinks.contains(link)){
                return link;
            }
            if(currentLink.getTo().equals(link.getFrom()) &&
                    !visitedLinks.contains(link)){
                return link;
            }


        }
        return currentLink; // No next link found
    }


    public double[]  processPublicSubnet(List<NodeData> nodeDataList, String publicSubnet, double nat_x, double nat_y) {
        double x = 0.0;
        double y = 0.0;
        for (NodeData nodeData : nodeDataList) {
            if (nodeData.getGroup().contains(publicSubnet)) {
                String location = nodeData.getLoc();
                String[] locParts = location.split(" ");
                System.out.println("public Subnet" + publicSubnet);
                x = nat_x -1;
                y = nat_y + 260;
                String newLoc = (x) + " " + (y);
                System.out.println("newLoc" + newLoc);
                nat_x -= 1;
                nat_y += 260;
                nodeData.setLoc(newLoc);
                break;
            }
        }
        return new double[]{nat_x,nat_y};
    }

    private double[] processFromGroupData(LinkData linkdata, NodeData nodedata, List<GroupData> groupDataList, String netName, List<String> Except, double node_x, double node_y , List<String> visitedNodes) {
        // 해당 group이 prod private subnet에 포함됐는 지 확인
        String security_group = linkdata.getFrom();
        for(GroupData group : groupDataList){
            String group_name2 = group.getGroup();
            if(group.getGroup() != null){
                String[] group_name = group.getGroup().split(" ");
                group_name2 = group_name[0];
            }

            if(group.getKey().equals(security_group) &&
                    group_name2.equals(netName) &&
                    !Except.contains(security_group) &&
                    !visitedNodes.contains(nodedata.getKey())
                // 새로운 security 요소여야 함 &&
            ){
                // 포함되는 게 확인됐다면, 그룹 내의 요소들 가져오기
                if(nodedata.getGroup().equals(security_group)){
                    System.out.println("Group에 있는 NodeData" + nodedata + security_group + group_name2);node_x += 150;
                    String newLoc = (node_x) + " " + (node_y);
                    nodedata.setLoc(newLoc);
                    visitedNodes.add(nodedata.getKey());

                }

            }
        }
        return new double[]{node_x, node_y};
    }

    private  double[] processToGroupData(LinkData linkdata, NodeData nodedata, List<GroupData> groupDataList, String netName,  List<String> Except, double node_x, double node_y, List<String> visitedNodes) {

        // 해당 group이 prod private subnet에 포함됐는 지 확인
        String security_group = linkdata.getTo();
        System.out.println("securityGroup v19191919" + security_group);
        for(GroupData group : groupDataList){
            String group_name2 = group.getGroup();
            if(group.getGroup() != null){
                String[] group_name = group.getGroup().split(" ");
                group_name2 = group_name[0];
            }

            if(group.getKey().equals(security_group) &&
                    group_name2.equals(netName) &&
                    !Except.contains(security_group) &&
                    !visitedNodes.contains(nodedata.getKey())
                // 새로운 security 요소여야 함 &&
            )
                //visitGroup.add(security_group);
                // 포함되는 게 확인됐다면, 그룹 내의 요소들 가져오기
                if(nodedata.getGroup().equals(security_group)){
                    System.out.println("group include nodedata2 : "+nodedata);
                    node_x += 150;
                    String newLoc = (node_x) + " " + (node_y);
                    nodedata.setLoc(newLoc);
                    visitedNodes.add(nodedata.getKey());

                }

            }

        return new double[]{node_x, node_y};
    }



}