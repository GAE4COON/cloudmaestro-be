package com.gae4coon.cloudmaestro.domain.security.service.impl;

import com.gae4coon.cloudmaestro.domain.requirements.dto.RequireDTO;
import com.gae4coon.cloudmaestro.domain.requirements.dto.ZoneDTO;
import com.gae4coon.cloudmaestro.domain.security.service.SecurityService;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.GraphLinksModel;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import com.gae4coon.cloudmaestro.domain.ssohost.service.DiagramDTOService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    private final DiagramDTOService diagramDTOService;
    List<NodeData> nodeDataList;
    List<GroupData> groupDataList;
    List<LinkData> linkDataList;
    List<String> nodeKeys;

    // globalRequirements list service
    @Override
    public HashMap<String, Object> globalService(List<String> globalRequirements, Map<String, Object> responseArray){
        nodeDataList = (List<NodeData>) responseArray.get("nodeDataArray");
        groupDataList = (List<GroupData>) responseArray.get("groupDataArray");
        linkDataList = (List<LinkData>) responseArray.get("linkDataArray");

        // 노드 예외처리를 위해 nodeKey만 담긴 배열
        nodeKeys = new ArrayList<>();
        for (NodeData node : nodeDataList) {
            // Assuming 'getKey' is the method to get the key from NodeData
            String key = node.getKey();
            nodeKeys.add(key);
        }

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
                        setGlobalWAF();
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
        if(nodeKeys.contains("IAM Identity Center")) return;

        NodeData IAM = new NodeData();
        IAM.setKey("IAM Identity Center");
        IAM.setText("IAM Identity Center");
        IAM.setType("Security-Identity-Compliance");
        IAM.setGroup("Service");
        IAM.setSource("/img/AWS_icon/Arch_Security-Identity-Compliance/Arch_AWS-IAM-Identity-Center_48.svg");
        nodeDataList.add(IAM);
    }
    private void setSecurityHub() {
        if (nodeKeys.contains("Security Hub")) return;

        NodeData SecurityHub = new NodeData();
        SecurityHub.setKey("Security Hub");
        SecurityHub.setText("Security Hub");
        SecurityHub.setType("Security-Identity-Compliance");
        SecurityHub.setGroup("Service");
        SecurityHub.setSource("/img/AWS_icon/Arch_Security-Identity-Compliance/Arch_AWS-Security-Hub_48.svg");
        nodeDataList.add(SecurityHub);
    }

    private void setGuardDuty() {
        if(nodeKeys.contains("GuardDuty")) return;

        NodeData GuardDuty = new NodeData();
        GuardDuty.setKey("GuardDuty");
        GuardDuty.setText("GuardDuty");
        GuardDuty.setType("Security-Identity-Compliance");
        GuardDuty.setGroup("Service");
        GuardDuty.setSource("/img/AWS_icon/Arch_Security-Identity-Compliance/Arch_Amazon-GuardDuty_48.svg");
        nodeDataList.add(GuardDuty);
    }

    private void setDetective() {
        if(nodeKeys.contains("Detective")) return;

        NodeData Detective = new NodeData();
        Detective.setKey("Detective");
        Detective.setText("Detective");
        Detective.setType("Security-Identity-Compliance");
        Detective.setGroup("Service");
        Detective.setSource("/img/AWS_icon/Arch_Security-Identity-Compliance/Arch_Amazon-Detective_48.svg");
        nodeDataList.add(Detective);
    }

    private void setNetworkFW() {
        // network firewall있으면 endpoint만 검사
        if(!nodeKeys.contains("Network Firewall")){
            // add Network Firewall into Region
            NodeData NetworkFW = new NodeData();
            NetworkFW.setKey("Network Firewall");
            NetworkFW.setText("Network Firewall");
            NetworkFW.setType("Security-Identity-Compliance");
            NetworkFW.setGroup("Region");
            NetworkFW.setSource("/img/AWS_icon/Arch_Security-Identity-Compliance/Arch_AWS-Network-Firewall_48.svg");
            nodeDataList.add(NetworkFW);
        }

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
        if(nodeKeys.contains("Shield")) return;

        NodeData Shield = new NodeData();
        Shield.setKey("Shield");
        Shield.setText("Shield");
        Shield.setType("Security-Identity-Compliance");
        Shield.setGroup("Service");
        Shield.setSource("/img/AWS_icon/Arch_Security-Identity-Compliance/Arch_Amazon-Shield_48.svg");
        nodeDataList.add(Shield);
    }

    private void setGlobalWAF() {
        // CloudFront, API Gateway에 대해
        List<String> containResource = new ArrayList<>();
        if(nodeKeys.contains("CloudFront")){
            containResource.add("CloudFront");
        }
        if(nodeKeys.contains("API Gateway")){
            containResource.add("API Gateway");
        }
        if(containResource.isEmpty()) return;

        // 다이어그램에 WAF 없으면 예외처리
        if(!nodeKeys.contains("AWS_WAF")) {
            NodeData WAF = new NodeData();
            WAF.setKey("AWS_WAF");
            WAF.setText("AWS_WAF");
            WAF.setType("Security-Identity-Compliance");
            WAF.setGroup("Region");
            WAF.setSource("/img/AWS_icon/Arch_Security-Identity-Compliance/Arch_AWS-WAF_48.svg");
            nodeDataList.add(WAF);
        }

        // 이미 링크되어있는 경우 예외처리 필요
        for(var resource: containResource){
            LinkData WAFtoResource = new LinkData();
            WAFtoResource.setFrom("AWS_WAF");
            WAFtoResource.setTo(resource);
            linkDataList.add(WAFtoResource);
        }
    }

    private void setSecretsManager() {
        if(nodeKeys.contains("Secrets Manager")) return;

        NodeData SecretsManager = new NodeData();
        SecretsManager.setKey("Secrets Manager");
        SecretsManager.setText("Secrets Manager");
        SecretsManager.setType("Security-Identity-Compliance");
        SecretsManager.setGroup("Service");
        SecretsManager.setSource("/img/AWS_icon/Arch_Security-Identity-Compliance/Arch_Amazon-Secrets-Manager_48.svg");
        nodeDataList.add(SecretsManager);
    }
    private void setKMS( ) {
        if(nodeKeys.contains("Secrets Manager")) return;

        NodeData SecretsManager = new NodeData();
        SecretsManager.setKey("Secrets Manager");
        SecretsManager.setText("Secrets Manager");
        SecretsManager.setType("Security-Identity-Compliance");
        SecretsManager.setGroup("Service");
        SecretsManager.setSource("/img/AWS_icon/Arch_Security-Identity-Compliance/Arch_Amazon-Secrets-Manager_48.svg");
        nodeDataList.add(SecretsManager);
    }

    public HashMap<String, Object> zoneService(List<ZoneDTO> Zones, Map<String, Object> responseArray){
        nodeDataList = (List<NodeData>) responseArray.get("nodeDataArray");
        groupDataList = (List<GroupData>) responseArray.get("groupDataArray");
        linkDataList = (List<LinkData>) responseArray.get("linkDataArray");

        // Zone Requiremnts 처리
        for(ZoneDTO zone:Zones){
            for(String zoneRequirement: zone.getZoneRequirements()){
                switch (zoneRequirement){
                    case "웹 애플리케이션 보호":
                        setZoneWAF();
                        break;
                }
            }
        }

        HashMap<String, Object> response = diagramDTOService.dtoComplete(nodeDataList, groupDataList, linkDataList);
        System.out.println("response"+ response);
        return response;
    }

    private void setZoneWAF() {
    }


    // 망별로



}
