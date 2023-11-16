package com.gae4coon.cloudmaestro.domain.security.service.impl;

import com.gae4coon.cloudmaestro.domain.security.service.SecurityService;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.GraphLinksModel;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import com.gae4coon.cloudmaestro.domain.ssohost.service.DiagramDTOService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    private final DiagramDTOService diagramDTOService;
    List<NodeData> nodeDataList;
    List<GroupData> groupDataList;
    List<LinkData> linkDataList;
    // globalRequirements list service
    @Override
    public HashMap<String, Object> globalService(List<String> globalRequirements, Map<String, Object> responseArray){
        nodeDataList = (List<NodeData>) responseArray.get("nodeDataArray");
        groupDataList = (List<GroupData>) responseArray.get("groupDataArray");
        linkDataList = (List<LinkData>) responseArray.get("linkDataArray");

        GroupData groupData = new GroupData();
        groupData.setKey("Service");
        groupData.setText("Service");
        groupData.setStroke("rgb(158, 224, 255)");
        groupDataList.add(groupData);

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
        HashMap<String, Object> response = diagramDTOService.dtoComplete(nodeDataList, groupDataList, linkDataList);
        System.out.println("response"+ response);
        return response;
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
        // add Network Firewall into Region
        NodeData NetworkFW = new NodeData();
        NetworkFW.setKey("Network Firewall");
        NetworkFW.setText("Network Firewall");
        NetworkFW.setType("Security-Identity-Compliance");
        NetworkFW.setGroup("Region");
        NetworkFW.setSource("/img/AWS_icon/Arch_Security-Identity-Compliance/Arch_AWS-Network-Firewall_48.svg");
        nodeDataList.add(NetworkFW);

        // create Network Firewall Group in VPC
        GroupData groupData = new GroupData();
        groupData.setKey("Firewall Public Subnet");
        groupData.setText("Firewall Subnet");
        groupData.setStroke("rgb(122,161,22)");
        groupData.setGroup("VPC");
        groupDataList.add(groupData);

        // add Network Firewall endpoint into Firewall Public Subnet
        NodeData nodeData = new NodeData();
        nodeData.setKey("Network Firewall Endpoints");
        nodeData.setText("Network Firewall Endpoints");
        nodeData.setType("Security-Identity-Compliance");
        nodeData.setGroup("Firewall Public Subnet");
        nodeData.setSource("/img/AWS_icon/Resource_icon/Res_Security-Identity-Compliance/Res_AWS-Network-Firewall_Endpoints_48.svg");
        nodeDataList.add(nodeData);

        LinkData netFWtoendpoint = new LinkData();
        netFWtoendpoint.setFrom("Network Firewall");
        netFWtoendpoint.setTo("Network Firewall Endpoints");
        linkDataList.add(netFWtoendpoint);
    }

    private void setShield() {
        NodeData Shield = new NodeData();
        Shield.setKey("Shield");
        Shield.setText("Shield");
        Shield.setType("Security-Identity-Compliance");
        Shield.setGroup("Service");
        Shield.setSource("/img/AWS_icon/Arch_Security-Identity-Compliance/Arch_Amazon-Shield_48.svg");
        nodeDataList.add(Shield);
    }

    private void setWAF() {
        List<String> nodeKeys = new ArrayList<>();
        for (NodeData node : nodeDataList) {
            // Assuming 'getKey' is the method to get the key from NodeData
            String key = node.getKey();
            nodeKeys.add(key);
        }

        List<String> containResource = new ArrayList<>();
        if(nodeKeys.contains("CloudFront")){
            containResource.add("CloudFront");
        }
        if(nodeKeys.contains("API Gateway")){
            containResource.add("API Gateway");
        }
        if(containResource.isEmpty()) return;

        NodeData WAF = new NodeData();
        WAF.setKey("AWS_WAF");
        WAF.setText("AWS_WAF");
        WAF.setType("Security-Identity-Compliance");
        WAF.setGroup("Region");
        WAF.setSource("/img/AWS_icon/Arch_Security-Identity-Compliance/Arch_AWS-WAF_48.svg");
        nodeDataList.add(WAF);


        for(var resource: containResource){
            LinkData WAFtoResource = new LinkData();
            WAFtoResource.setFrom("AWS_WAF");
            WAFtoResource.setTo(resource);
            linkDataList.add(WAFtoResource);
        }
    }

    private void setSecretsManager() {
        NodeData SecretsManager = new NodeData();
        SecretsManager.setKey("Secrets Manager");
        SecretsManager.setText("Secrets Manager");
        SecretsManager.setType("Security-Identity-Compliance");
        SecretsManager.setGroup("Service");
        SecretsManager.setSource("/img/AWS_icon/Arch_Security-Identity-Compliance/Arch_Amazon-Secrets-Manager_48.svg");
        nodeDataList.add(SecretsManager);
    }

    // 망별로
    private void setKMS() {

    }


}
