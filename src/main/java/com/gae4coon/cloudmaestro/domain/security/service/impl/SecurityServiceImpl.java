package com.gae4coon.cloudmaestro.domain.security.service.impl;

import com.gae4coon.cloudmaestro.domain.security.service.SecurityService;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.GraphLinksModel;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {
    List<NodeData> nodeDataList;
    List<GroupData> groupDataList;
    List<LinkData> linkDataList;
    // globalRequirements list service
    @Override
    public void globalService(List<String> globalRequirements, Map<String, Object> responseArray){
        nodeDataList = (List<NodeData>) responseArray.get("nodeDataArray");
        groupDataList = (List<GroupData>) responseArray.get("groupDataArray");
        linkDataList = (List<LinkData>) responseArray.get("linkDataArray");

        GroupData groupData = new GroupData();
        groupData.setKey("Service");
        groupData.setText("Service");
        groupData.setStroke("rgb(158, 224, 255)");

        for(var global: globalRequirements){
                switch (global){
                    case "접근 권한 관리":
                        setIAM();
                        break;
                    case "보안 로그 통합":
                        setSecurityHub();
                        break;
                    case "랜섬웨어 탐지":
                        setGuardDuty();
                        break;
                    case "보안 사고 조사":
                        setDetective();
                        break;
                    case "네트워크 방화벽":
                        setNetworkFW();
                        break;
                    case "안티 디도스":
                        setShield();
                        break;
                    case "웹 애플리케이션 보호":
                        setWAF();
                        break;
                    case "키관리":
                        setSecretsManager();
                        break;
                    case "키생성 및 암호화":
                        setKMS();
                        break;
                }
            }

    }

    private void setIAM() {
        NodeData IAM = new NodeData();
        IAM.setKey("IAM Identity Center");
        IAM.setText("IAM Identity Center");
        IAM.setType("Security-Identity-Compliance");
        IAM.setGroup("Service");
        IAM.setSource("/img/AWS_icon/Arch_Security-Identity-Compliance/Arch_AWS-IAM-Identity-Center_48.svg");
        nodeDataList.add(IAM);
    }
    private void setSecurityHub() {
        NodeData SecurityHub = new NodeData();
        SecurityHub.setKey("Security Hub");
        SecurityHub.setText("Security Hub");
        SecurityHub.setType("Security-Identity-Compliance");
        SecurityHub.setGroup("Service");
        SecurityHub.setSource("/img/AWS_icon/Arch_Security-Identity-Compliance/Arch_AWS-Security-Hub_48.svg");
        nodeDataList.add(SecurityHub);
    }

    private void setGuardDuty() {
        NodeData GuardDuty = new NodeData();
        GuardDuty.setKey("GuardDuty");
        GuardDuty.setText("GuardDuty");
        GuardDuty.setType("Security-Identity-Compliance");
        GuardDuty.setGroup("Service");
        GuardDuty.setSource("/img/AWS_icon/Arch_Security-Identity-Compliance/Arch_Amazon-GuardDuty_48.svg");
        nodeDataList.add(GuardDuty);
    }

    private void setDetective() {
        NodeData Detective = new NodeData();
        Detective.setKey("Detective");
        Detective.setText("Detective");
        Detective.setType("Security-Identity-Compliance");
        Detective.setGroup("Service");
        Detective.setSource("/img/AWS_icon/Arch_Security-Identity-Compliance/Arch_Amazon-Detective_48.svg");
        nodeDataList.add(Detective);
    }

    private void setNetworkFW() {
    }

    private void setShield() {
    }

    private void setWAF() {
    }

    private void setSecretsManager() {
    }

    private void setKMS() {
    }


}
