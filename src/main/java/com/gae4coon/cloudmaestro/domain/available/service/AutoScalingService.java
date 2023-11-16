package com.gae4coon.cloudmaestro.domain.available.service;

import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AutoScalingService {


    private final ALBService albservice;
    @Autowired
    AutoScalingService (ALBService albservice){
        this.albservice = albservice;
    }


    public void addAutoScaling(int autogroupcount, List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList) {
        String name ="PROD";

        // public 망 - available zone 연결 고리 삭제
        List<LinkData> tempLinkDataList = new ArrayList<>();

        // Auto Scaling Group 추가하기
        // Security Group 안에 Ec2 포함 or Security Group없는 ec2 추가
        List<String> includeEc2Security = albservice.processSecurityGroups(nodeDataList, groupDataList, name);

        // ec2 일경우 같은 목적지를 바라보고 있는 지 확인
        Map<String, List<LinkData>>  ec2link = addAutoScale(includeEc2Security,nodeDataList,groupDataList,linkDataList);

        // 목적지가 같은 ec2를 하나의 그룹으로 묶어서 연결을 한다.
        addLinkData(ec2link,includeEc2Security,nodeDataList, groupDataList,linkDataList);


    }

    public Map<String, List<LinkData>> addAutoScale(List<String> includeEc2Security, List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList) {
        // Node 끼리 같은 목적지를 바로보고 있는 지를 확인
        Map<String, List<LinkData>> linkGroups = new HashMap<>();
        for (LinkData linkdata : linkDataList) {
            String link = linkdata.getFrom();
            // computeIfAbsent : key 값에 해당하는 Value가 존재하면 가져와서 사용하고 싶으면
            if (includeEc2Security.contains(link)) {
                linkGroups.computeIfAbsent(linkdata.getTo(), k -> new ArrayList<>()).add(linkdata);
            }
        }

        System.out.println("linkGroups" + linkGroups);


        return linkGroups;
    }
    public void addLinkData(Map<String, List<LinkData>> ec2link, List<String> includeEc2Security,List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList)
    {
        GroupData groupdata = new GroupData();
        // AutoScalingGroup 추가하기
        String name ="PROD";
        int i = 1;
        for(String key : ec2link.keySet()) {
            //node data가 그룹 조건일 때 추가
            String AutoScaling = "Auto Scaling group" + i;

            List<LinkData> linklist2 = ec2link.get(key);
            System.out.println("LINKDATA3"  + linklist2);
             //Security Group일 때 AutoScaling Group 내에 Security Group을 추가
            for(LinkData securitylink: linklist2){
                System.out.println("Securitygroup333" + securitylink);
                if(securitylink.getFrom().contains("Security Group")){
                    for(GroupData securitygroup : groupDataList){
                        System.out.println("Securitygroup" + securitylink);
                        System.out.println("i am nodedata2" + securitygroup);
                        if(securitygroup.getKey().equals(securitylink.getFrom())){
                            System.out.println("i am nodedata" + securitygroup);
                            securitygroup.setGroup(AutoScaling); // 여기서 "AutoScaling"이 문자열이라고 가정합니다.
                        }
                    }
                }
            }

            NodeData nodedata = new NodeData(); // Assuming GroupData is the correct type
            nodedata.setIsGroup(true);
            nodedata.setStroke("rgb(237,113,0)");
            nodedata.setText("Auto Scaling group");
            nodedata.setKey(AutoScaling);
            nodedata.setSource("/img/AWS_icon/AWS_Groups/Auto_Scaling_group.svg");
            nodedata.setType("AWS_Groups");
            nodedata.setGroup("PROD Private subnet 1");

            nodeDataList.add(nodedata);


            // security group에 있는 node가 아닌 ec2에 있는 group을 auto scaling group으로 바꾸기
            //node 에 있는 group을 Auto Scaling group으로 바꾸기
            for (NodeData node : nodeDataList) {
                for (String key2 : ec2link.keySet()) {
                        List<LinkData> netListLink = ec2link.get(key2);
                        for (LinkData linkData : netListLink) {
                            if (node.getKey().contains(linkData.getFrom())) {
                                node.setGroup(AutoScaling);
                                break; // 일치하는 첫 번째 linkData에서 그룹을 설정한 후 반복 중단
                            }
                        }
                }
            }

            // LinkData 바꾸기
            for(LinkData linkdata : linkDataList){
                if(linkdata.getFrom().contains("Elastic Load Balancing")){
                    linkdata.setTo(AutoScaling);
                }
                for (String key2 : ec2link.keySet()) {
                    List<LinkData> netListLink = ec2link.get(key2);
                        for (LinkData netlinkdata : netListLink) {
                            if (linkdata.getTo().contains(netlinkdata.getFrom())) {
                                linkdata.setTo(AutoScaling);
                            }
                            if (linkdata.getFrom().contains(netlinkdata.getFrom())) {
                                linkdata.setFrom(AutoScaling);
                            }

                            // Elastic Load Balancing이 없을 조건 추가
                        }
                    }
                }
            }

            // You might want to do something with groupdata here, like adding it to a list or processing it further
        }




    }

