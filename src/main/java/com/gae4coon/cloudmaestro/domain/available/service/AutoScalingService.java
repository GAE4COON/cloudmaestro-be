package com.gae4coon.cloudmaestro.domain.available.service;

import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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


    }
}
