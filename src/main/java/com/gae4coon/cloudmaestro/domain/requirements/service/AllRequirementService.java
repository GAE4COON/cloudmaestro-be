package com.gae4coon.cloudmaestro.domain.requirements.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gae4coon.cloudmaestro.domain.available.service.AvailableService;
import com.gae4coon.cloudmaestro.domain.logging.service.LoggingService;
import com.gae4coon.cloudmaestro.domain.naindae.service.DnsService;
import com.gae4coon.cloudmaestro.domain.refactor.service.BackupService;
import com.gae4coon.cloudmaestro.domain.requirements.dto.RequireDTO;
import com.gae4coon.cloudmaestro.domain.requirements.dto.RequireDiagramDTO;
import com.gae4coon.cloudmaestro.domain.security.service.SecurityService;
import com.gae4coon.cloudmaestro.domain.naindae.service.DnsMultiService;
import com.gae4coon.cloudmaestro.domain.naindae.service.RegionService;
import com.gae4coon.cloudmaestro.domain.naindae.service.DbReplication;
import com.gae4coon.cloudmaestro.domain.naindae.service.DbCache;
import com.gae4coon.cloudmaestro.domain.naindae.service.CloudFrontDistribution;
import com.gae4coon.cloudmaestro.domain.naindae.service.DbCache;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.GraphLinksModel;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import com.gae4coon.cloudmaestro.domain.ssohost.service.DiagramDTOService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AllRequirementService {
    private final DiagramDTOService diagramDTOService;
    private final SecurityService securityService;
    private final LoggingService loggingService;
    private final AvailableService availableService;
    private final BackupService backupService;
    private final DnsMultiService dnsMultiService;
    private final RegionService regionService;
    private final DbReplication dbReplication;
    private final DnsService dnsService;
    private final DbCache dbCache;
    private final CloudFrontDistribution cloudFrontDistribution;
    public HashMap<String, Object> requirement(RequireDiagramDTO requireDiagramDTO) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        GraphLinksModel diagramData = mapper.readValue(requireDiagramDTO.getDiagramData(), GraphLinksModel.class);
        RequireDTO requirementData = requireDiagramDTO.getRequirementData();

        System.out.println("requirement: " + requirementData);
        System.out.println("diagramData:"+diagramData);

        // diagramData formatter
        Map<String, Object> responseArray = diagramDTOService.dtoGenerator(diagramData);

        List<NodeData> nodeDataList = (List<NodeData>) responseArray.get("nodeDataArray");
        List<GroupData> groupDataList = (List<GroupData>) responseArray.get("groupDataArray");
        List<LinkData> linkDataList = (List<LinkData>) responseArray.get("linkDataArray");
        Map<String, Object> cost = (Map<String, Object>) responseArray.get("cost");

        loggingService.logging(requirementData, nodeDataList, groupDataList, linkDataList);
        availableService.availalbeService(requireDiagramDTO,nodeDataList,groupDataList,linkDataList);
        dnsMultiService.getRequirementDns(requireDiagramDTO, nodeDataList, linkDataList, groupDataList);
        regionService.getRegion(requireDiagramDTO, nodeDataList, linkDataList, groupDataList);
        dbReplication.getRequirementAvailable(requireDiagramDTO, nodeDataList, linkDataList, groupDataList);
        dnsService.createDns(requireDiagramDTO, nodeDataList, linkDataList, groupDataList);
        dbCache.createNode(requireDiagramDTO, nodeDataList, linkDataList, groupDataList);
        cloudFrontDistribution.createNode(requireDiagramDTO, nodeDataList, linkDataList, groupDataList);
        securityService.security(requirementData, nodeDataList, groupDataList, linkDataList);
        loggingService.logging2(requirementData, nodeDataList, groupDataList, linkDataList);
        backupService.requirementParsing(requireDiagramDTO, nodeDataList, linkDataList, groupDataList);



        //HashMap<String, Object> available = availableService.availalbeService(requirementData.getZones(),nodeDataList,groupDataList,linkDataList);

        // Service 데이터 임시 위치 할당
        for (NodeData node: nodeDataList){
            if(node.getGroup()!=null && node.getGroup().equals("Service")){
                node.setLoc("-200 0");
            }
        }


        //System.out.println("requriement data : "x + available);
        System.out.println("ReuqireDiagramDTO : " + requireDiagramDTO);

        HashMap<String, Object> response = diagramDTOService.dtoComplete(nodeDataList, groupDataList, linkDataList, cost);

        return response;
    }
}