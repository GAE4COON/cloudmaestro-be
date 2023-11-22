package com.gae4coon.cloudmaestro.domain.logging.service;

import com.gae4coon.cloudmaestro.domain.requirements.dto.RequireDTO;
import com.gae4coon.cloudmaestro.domain.requirements.dto.ZoneDTO;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import com.gae4coon.cloudmaestro.domain.ssohost.service.DiagramDTOService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LoggingService {

    private final DiagramDTOService diagramDTOService;

    List<NodeData> nodeDataList;
    List<GroupData> groupDataList;
    List<LinkData> linkDataList;

    // globalRequirements list service

    public HashMap<String, Object> logging(RequireDTO requireDTO,List<NodeData> originNodeDataList, List<GroupData> originGroupDataList, List<LinkData> originLinkDataList) {
        List<String> globalRequirements = requireDTO.getGlobalRequirements();
        List<ZoneDTO> Zones = requireDTO.getZones();

        this.nodeDataList = originNodeDataList;
        this.groupDataList = originGroupDataList;
        this.linkDataList = originLinkDataList;

        globalService(globalRequirements);

        HashMap<String, Object> response = diagramDTOService.dtoComplete(nodeDataList, groupDataList, linkDataList);

        return response;
    }

    private void globalService(List<String> globalRequirements) {
        for (var global : globalRequirements) {
            switch (global) {
                case "API 로그 수집":
                    setCloudTrail();
                    break;
                case "리소스 및 애플리케이션 모니터링":
                    setCloudWatch();
                    break;
                case "로그 수집/분석/시각화 통합 (OpenSearch)":
                    setOpenSearch();
                    break;
                case "로그 분석 (SQL 쿼리)":
                    setAthena();
                    break;
                case "로그 시각화":
                    setQuickSight();
                    break;
            }
        }
    }

    private void setCloudTrail() {
        NodeData cloudTrail = NodeData.builder()
                .key("CloudTrail_logging")
                .text("CloudTrail")
                .group("Region")
                .type("Management-Governance")
                .source("/img/AWS_icon/Arch_Management-Governance/Arch_AWS-CloudTrail_48.svg")
                .build();
        nodeDataList.add(cloudTrail);

        NodeData s3 = NodeData.builder()
                .key("Simple Storage Service_logging")
                .text("Simple Storage Service")
                .group("Region")
                .type("Storage")
                .source("/img/AWS_icon/Arch_Storage/Arch_Amazon-Simple-Storage-Service_48.svg")
                .build();
        nodeDataList.add(s3);

        LinkData link = LinkData.builder()
                .from("CloudTrail_logging")
                .to("Simple Storage Service_logging")
                .build();
        linkDataList.add(link);

    }

    private void setCloudWatch() {
    }

    private void setOpenSearch() {
    }

    private void setAthena() {
    }

    private void setQuickSight() {
    }
}