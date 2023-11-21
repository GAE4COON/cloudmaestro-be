package com.gae4coon.cloudmaestro.domain.security.service;

import com.gae4coon.cloudmaestro.domain.requirements.dto.RequireDTO;
import com.gae4coon.cloudmaestro.domain.requirements.dto.ZoneDTO;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import com.gae4coon.cloudmaestro.domain.ssohost.service.DiagramDTOService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SecurityService{

    private DiagramDTOService diagramDTOService;

    List<NodeData> nodeDataList;
    List<GroupData> groupDataList;
    List<LinkData> linkDataList;

    // globalRequirements list service

    public HashMap<String, Object> security(RequireDTO requireDTO, Map<String, Object> responseArray) {
        nodeDataList = (List<NodeData>) responseArray.get("nodeDataArray");
        groupDataList = (List<GroupData>) responseArray.get("groupDataArray");
        linkDataList = (List<LinkData>) responseArray.get("linkDataArray");

        globalService(requireDTO.getGlobalRequirements(), responseArray);
        zoneService(requireDTO.getZones(), responseArray);

        HashMap<String, Object> response = diagramDTOService.dtoComplete(nodeDataList, groupDataList, linkDataList);
        System.out.println("response" + response);
        return response;
    }


    public void globalService(List<String> globalRequirements, Map<String, Object> responseArray) {
        for (var global : globalRequirements) {
            switch (global) {
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
    }

    public void zoneService(List<ZoneDTO> Zones, Map<String, Object> responseArray) {
        // Zone Requiremnts 처리
        for (ZoneDTO zone : Zones) {
            for (String zoneRequirement : zone.getZoneRequirements()) {
                switch (zoneRequirement) {
                    case "망별_웹 애플리케이션 보호":
                        setZoneWAF(zone.getName());
                        break;
                }
            }
        }
    }

    private void setIAM() {
        if (diagramDTOService.isNodeDataContains(nodeDataList, "Identity and Access Management")) return;
        diagramDTOService.addServiceGroup(groupDataList);

        NodeData IAM = new NodeData();
        IAM.setKey("Identity and Access Management");
        IAM.setText("Identity and Access Management");
        IAM.setType("Security-Identity-Compliance");
        IAM.setGroup("Service");
        IAM.setSource("/img/AWS_icon/Arch_Security-Identity-Compliance/Arch_AWS-Identity-and-Access-Management_48.svg");
        nodeDataList.add(IAM);
    }

    private void setSecurityHub() {
        if (diagramDTOService.isNodeDataContains(nodeDataList, "Security Hub")) return;
        diagramDTOService.addServiceGroup(groupDataList);

        NodeData SecurityHub = new NodeData();
        SecurityHub.setKey("Security Hub");
        SecurityHub.setText("Security Hub");
        SecurityHub.setType("Security-Identity-Compliance");
        SecurityHub.setGroup("Service");
        SecurityHub.setSource("/img/AWS_icon/Arch_Security-Identity-Compliance/Arch_AWS-Security-Hub_48.svg");
        nodeDataList.add(SecurityHub);
    }

    private void setGuardDuty() {
        if (diagramDTOService.isNodeDataContains(nodeDataList, "GuardDuty")) return;
        diagramDTOService.addServiceGroup(groupDataList);

        NodeData GuardDuty = new NodeData();
        GuardDuty.setKey("GuardDuty");
        GuardDuty.setText("GuardDuty");
        GuardDuty.setType("Security-Identity-Compliance");
        GuardDuty.setGroup("Service");
        GuardDuty.setSource("/img/AWS_icon/Arch_Security-Identity-Compliance/Arch_Amazon-GuardDuty_48.svg");
        nodeDataList.add(GuardDuty);
    }

    private void setDetective() {
        if (diagramDTOService.isNodeDataContains(nodeDataList, "Detective")) return;
        diagramDTOService.addServiceGroup(groupDataList);

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
        diagramDTOService.addServiceGroup(groupDataList);

        if (!diagramDTOService.isNodeDataContains(nodeDataList, "Network Firewall")) {
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
        if (diagramDTOService.isNodeDataContains(nodeDataList, "Shield")) return;
        diagramDTOService.addServiceGroup(groupDataList);
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
        if (diagramDTOService.isNodeDataContains(nodeDataList, "CloudFront")) {
            containResource.add("CloudFront");
        }
        if (diagramDTOService.isNodeDataContains(nodeDataList, "API Gateway")) {
            containResource.add("API Gateway");
        }
        if (containResource.isEmpty()) return;

        // 다이어그램에 WAF 없으면 예외처리
        if (!diagramDTOService.isNodeDataContains(nodeDataList, "AWS_WAF")) {
            NodeData WAF = new NodeData();
            WAF.setKey("AWS_WAF");
            WAF.setText("AWS_WAF");
            WAF.setType("Security-Identity-Compliance");
            WAF.setGroup("Region");
            WAF.setSource("/img/AWS_icon/Arch_Security-Identity-Compliance/Arch_AWS-WAF_48.svg");
            nodeDataList.add(WAF);
        }

        // 이미 링크되어있는 경우 예외처리 필요
        for (var resource : containResource) {
            LinkData WAFtoResource = new LinkData();
            WAFtoResource.setFrom("AWS_WAF");
            WAFtoResource.setTo(resource);
            linkDataList.add(WAFtoResource);
        }
    }

    private void setSecretsManager() {
        if (diagramDTOService.isNodeDataContains(nodeDataList, "Secrets Manager")) return;
        diagramDTOService.addServiceGroup(groupDataList);

        NodeData SecretsManager = new NodeData();
        SecretsManager.setKey("Secrets Manager");
        SecretsManager.setText("Secrets Manager");
        SecretsManager.setType("Security-Identity-Compliance");
        SecretsManager.setGroup("Service");
        SecretsManager.setSource("/img/AWS_icon/Arch_Security-Identity-Compliance/Arch_Amazon-Secrets-Manager_48.svg");
        nodeDataList.add(SecretsManager);
    }

    private void setKMS() {
        diagramDTOService.addServiceGroup(groupDataList);
        if (diagramDTOService.isNodeDataContains(nodeDataList, "Secrets Manager")) return;

        NodeData SecretsManager = new NodeData();
        SecretsManager.setKey("Secrets Manager");
        SecretsManager.setText("Secrets Manager");
        SecretsManager.setType("Security-Identity-Compliance");
        SecretsManager.setGroup("Service");
        SecretsManager.setSource("/img/AWS_icon/Arch_Security-Identity-Compliance/Arch_Amazon-Secrets-Manager_48.svg");
        nodeDataList.add(SecretsManager);
    }


    private void setZoneWAF(String zoneName) {
        NodeData zoneConnectedALB = null;
        List<NodeData> ALBList = diagramDTOService.getNodeListByText(nodeDataList, "Application Load Balancer(ALB)");

        if (ALBList.isEmpty()) return;

        // ALB 노드 모음
        for (NodeData alb : ALBList) {
            // ALB가 아니면 continue
            LinkData ALBLink = diagramDTOService.getLinkDataByTo(linkDataList, alb.getKey());
            if (ALBLink == null) continue;

            NodeData ALBToNode = diagramDTOService.getNodeDataByKey(nodeDataList, ALBLink.getTo());
            GroupData ALBToGroup = diagramDTOService.getGroupDataByKey(groupDataList, ALBLink.getTo());

            // ALB와 연결된 from 데이터의 group확인
            if (ALBToNode != null) {
                String group = ALBToNode.getGroup();
                // 해당 망이 ALB와 연결되지 않았음
                if (!group.contains(zoneName)) continue;
            }
            if (ALBToGroup != null) {
                String group = ALBToGroup.getGroup();
                // 해당 망이 ALB와 연결되지 않았음
                if (!group.contains(zoneName)) continue;
            }

            zoneConnectedALB = alb;
            break;
        }

        if (zoneConnectedALB == null) return;

        // 다이어그램에 WAF 없으면 생성
        if (!diagramDTOService.isNodeDataContains(nodeDataList, "AWS_WAF")) {
            NodeData WAF = new NodeData();
            WAF.setKey("AWS_WAF");
            WAF.setText("AWS_WAF");
            WAF.setType("Security-Identity-Compliance");
            WAF.setGroup("Region");
            WAF.setSource("/img/AWS_icon/Arch_Security-Identity-Compliance/Arch_AWS-WAF_48.svg");
            nodeDataList.add(WAF);
        }

        // ALB마다 WAF 연결
        LinkData WAFtoResource = new LinkData();
        WAFtoResource.setFrom("AWS_WAF");
        WAFtoResource.setTo(zoneConnectedALB.getKey());
        linkDataList.add(WAFtoResource);
    }


}

