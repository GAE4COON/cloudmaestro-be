package com.gae4coon.cloudmaestro.domain.security.service;

import com.gae4coon.cloudmaestro.domain.requirements.dto.RequireDTO;
import com.gae4coon.cloudmaestro.domain.requirements.dto.ZoneDTO;
import com.gae4coon.cloudmaestro.domain.resource.service.AddResourceService;
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

    private final DiagramDTOService diagramDTOService;
    private final AddResourceService addResourceService;

    List<NodeData> nodeDataList;
    List<GroupData> groupDataList;
    List<LinkData> linkDataList;

    // globalRequirements list service

    public HashMap<String, Object> security(RequireDTO requireDTO,List<NodeData> originNodeDataList, List<GroupData> originGroupDataList, List<LinkData> originLinkDataList) {
        List<String> globalRequirements = requireDTO.getGlobalRequirements();
        List<ZoneDTO> Zones = requireDTO.getZones();

        this.nodeDataList = originNodeDataList;
        this.groupDataList = originGroupDataList;
        this.linkDataList = originLinkDataList;

        globalService(globalRequirements);
        zoneService(Zones);

        HashMap<String, Object> response = diagramDTOService.dtoComplete(nodeDataList, groupDataList, linkDataList);

        return response;
    }



    private void globalService(List<String> globalRequirements) {
        for (var global : globalRequirements) {
            switch (global) {
                case "보안":
                    setSecurity();
                    break;
                case "탐지 및 대응":
                    setDetect();
                    break;
                case "네트워크 보호":
                    setNetworkSecure();
                    break;
                case "데이터 보호":
                    setDataSecure();
                    break;
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

    private void setDataSecure() {
        setKMS();
        setSecretsManager();
    }

    private void setNetworkSecure() {
        setNetworkFW();
        setShield();
    }

    private void setDetect() {
        setDetective();
        setGuardDuty();
        setSecurityHub();
    }

    private void setSecurity() {
        setIAM();
        setDetect();
        setNetworkSecure();
        setGlobalWAF();
        setDataSecure();
    }

    public void zoneService(List<ZoneDTO> Zones) {
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

        NodeData IAM = addResourceService.addIdentityandAccessManagement();
        IAM.setGroup("Service");
        nodeDataList.add(IAM);
    }

    private void setSecurityHub() {
        if (diagramDTOService.isNodeDataContains(nodeDataList, "Security Hub")) return;
        diagramDTOService.addServiceGroup(groupDataList);

        NodeData SecurityHub = addResourceService.addSecurityHub();
        SecurityHub.setGroup("Service");
        nodeDataList.add(SecurityHub);
    }

    private void setGuardDuty() {
        if (diagramDTOService.isNodeDataContains(nodeDataList, "GuardDuty")) return;
        diagramDTOService.addServiceGroup(groupDataList);

        NodeData GuardDuty = addResourceService.addGuardDuty();
        GuardDuty.setGroup("Service");
        nodeDataList.add(GuardDuty);
    }

    private void setDetective() {
        if (diagramDTOService.isNodeDataContains(nodeDataList, "Detective")) return;
        diagramDTOService.addServiceGroup(groupDataList);

        NodeData Detective = addResourceService.addDetective();
        Detective.setGroup("Service");
        nodeDataList.add(Detective);
    }

    private void setNetworkFW() {
        // network firewall있으면 endpoint만 검사
        diagramDTOService.addServiceGroup(groupDataList);

        if (!diagramDTOService.isNodeDataContains(nodeDataList, "Network Firewall")) {
            // add Network Firewall into Region
            NodeData NetworkFW = addResourceService.addNetworkFirewall();
            NetworkFW.setGroup("Region");
            nodeDataList.add(NetworkFW);
        }

        // create Network Firewall Group in VPC
        GroupData groupData = addResourceService.addPublicsubnet();
        groupData.setKey("Firewall Public Subnet");
        groupData.setText("Firewall Public Subnet");
        groupData.setGroup("VPC");
        groupDataList.add(groupData);

        // add Network Firewall endpoint into Firewall Public Subnet
        NodeData nodeData = addResourceService.addNetworkFirewallEndpoints();
        nodeData.setGroup("Firewall Public Subnet");
        nodeDataList.add(nodeData);

        LinkData netFWtoendpoint = new LinkData();
        netFWtoendpoint.setFrom("Network Firewall");
        netFWtoendpoint.setTo("Network Firewall Endpoints");
        linkDataList.add(netFWtoendpoint);
    }

    private void setShield() {
        if (diagramDTOService.isNodeDataContains(nodeDataList, "Shield")) return;
        diagramDTOService.addServiceGroup(groupDataList);
        NodeData Shield = addResourceService.addShield();
        Shield.setGroup("Service");
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
            NodeData WAF = addResourceService.addAWS_WAF();
            WAF.setGroup("Region");
            WAF.setLoc("0 -200");
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

        NodeData SecretsManager = addResourceService.addSecretsManager();
        SecretsManager.setGroup("Service");
        nodeDataList.add(SecretsManager);
    }

    private void setKMS() {
        diagramDTOService.addServiceGroup(groupDataList);
        if (diagramDTOService.isNodeDataContains(nodeDataList, "Key Management Service")) return;

        NodeData KMS = addResourceService.addKeyManagementService();
        KMS.setGroup("Service");
        nodeDataList.add(KMS);
    }


    private void setZoneWAF(String zoneName) {
        List<NodeData> zoneConnectedALB = new ArrayList<>();
        List<NodeData> ALBList = diagramDTOService.getNodeListByText(nodeDataList, "Application Load Balancer(ALB)");

        if (ALBList.isEmpty()) return;

        // ALB 노드 모음
        for (NodeData alb : ALBList) {
            // ALB가 아니면 continue
            LinkData ALBLink = diagramDTOService.getLinkDataByFrom(linkDataList, alb.getKey());
            if (ALBLink == null) continue;

            // ALB와 연결된 from 데이터의 group확인
            NodeData ALBToNode = diagramDTOService.getNodeDataByKey(nodeDataList, ALBLink.getTo());
            if (ALBToNode != null) {
                String group = ALBToNode.getGroup();
                // 해당 망이 ALB와 연결되지 않았음
                if (!group.contains(zoneName)) continue;
            }

            GroupData ALBToGroup = diagramDTOService.getGroupDataByKey(groupDataList, ALBLink.getTo());
            if (ALBToGroup != null) {
                String group = ALBToGroup.getGroup();
                // 해당 망이 ALB와 연결되지 않았음
                if (!group.contains(zoneName)) continue;
            }


            zoneConnectedALB.add(alb);
        }

        // 다이어그램에 WAF 없으면 생성
        if (!diagramDTOService.isNodeDataContains(nodeDataList, "AWS_WAF")) {
            NodeData WAF = addResourceService.addAWS_WAF();
            WAF.setGroup("Region");
            WAF.setLoc("0 -200");
            nodeDataList.add(WAF);
        }

        // ALB마다 WAF 연결
        for (NodeData alb : zoneConnectedALB) {
            LinkData WAFtoResource = new LinkData();
            WAFtoResource.setFrom("AWS_WAF");
            WAFtoResource.setTo(alb.getKey());
            linkDataList.add(WAFtoResource);
        }
    }
}

