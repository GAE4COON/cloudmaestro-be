package com.gae4coon.cloudmaestro.domain.logging.service;

import com.gae4coon.cloudmaestro.domain.requirements.dto.RequireDTO;
import com.gae4coon.cloudmaestro.domain.requirements.dto.ZoneDTO;
import com.gae4coon.cloudmaestro.domain.resource.service.AddResourceService;
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
    private final AddResourceService addResourceService;

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
                case "로깅":
                    setLogging();
                    break;
                case "로그 수집 및 저장":
                    setLogCollectStore();
                    break;
                case "분석 및 시각화":
                    setLogAnalyze();
                    break;
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

    private void setLogging(){
        setLogCollectStore();
        setLogAnalyze();
    }

    private void setLogAnalyze() {
        setOpenSearch();
        setAthena();
        setQuickSight();
    }

    private void setLogCollectStore() {
        setCloudTrail();
        setCloudWatch();
    }

    private void setCloudTrail() {
        NodeData cloudTrail = addResourceService.addCloudTrail();
        cloudTrail.setKey(cloudTrail.getKey()+diagramDTOService.getNodeNumber(nodeDataList, cloudTrail.getText()));
        cloudTrail.setGroup("Region");
        nodeDataList.add(cloudTrail);

        NodeData s3 = addResourceService.addSimpleStorageService();
        s3.setKey(s3.getKey()+diagramDTOService.getNodeNumber(nodeDataList, s3.getText()));
        s3.setGroup("Region");
        nodeDataList.add(s3);

        LinkData link = LinkData.builder()
                .from(cloudTrail.getKey())
                .to(s3.getKey())
                .build();
        linkDataList.add(link);
    }

    private void setCloudWatch() {
        NodeData cloudWatch = addResourceService.addCloudWatch();
        cloudWatch.setKey(cloudWatch.getKey()+diagramDTOService.getNodeNumber(nodeDataList, cloudWatch.getText()));
        cloudWatch.setGroup("Region");
        nodeDataList.add(cloudWatch);

        NodeData s3 = addResourceService.addSimpleStorageService();
        s3.setKey(s3.getKey()+diagramDTOService.getNodeNumber(nodeDataList, s3.getText()));
        s3.setGroup("Region");
        nodeDataList.add(s3);

        LinkData link = LinkData.builder()
                .from(s3.getKey())
                .to(cloudWatch.getKey())
                .build();
        linkDataList.add(link);
    }

    private void setOpenSearch() {
        NodeData cloudWatchNode = diagramDTOService.getNodeDataByKey(nodeDataList, "CloudWatch");
        if(cloudWatchNode==null){
            cloudWatchNode = addResourceService.addCloudWatch();
            cloudWatchNode.setGroup("Region");
            nodeDataList.add(cloudWatchNode);
        }

        NodeData OpenSearch = addResourceService.addOpenSearchService();
        OpenSearch.setKey(OpenSearch.getKey()+diagramDTOService.getNodeNumber(nodeDataList, OpenSearch.getText()));
        OpenSearch.setGroup("Region");
        nodeDataList.add(OpenSearch);

        NodeData lambda = addResourceService.addLambdaLambdaFunction();
        lambda.setKey(lambda.getKey()+diagramDTOService.getNodeNumber(nodeDataList, lambda.getText()));
        lambda.setGroup("Region");
        nodeDataList.add(lambda);

        LinkData link = LinkData.builder()
                .from(cloudWatchNode.getKey())
                .to(lambda.getKey())
                .build();
        linkDataList.add(link);

        LinkData link2 = LinkData.builder()
                .from(lambda.getKey())
                .to(OpenSearch.getKey())
                .build();
        linkDataList.add(link2);
    }

    private void setAthena() {
        NodeData athena = addResourceService.addAthena();
        athena.setKey(athena.getKey()+diagramDTOService.getNodeNumber(nodeDataList, athena.getText()));
        athena.setGroup("Region");
        nodeDataList.add(athena);

        NodeData s3 = addResourceService.addSimpleStorageService();
        s3.setKey(s3.getKey()+diagramDTOService.getNodeNumber(nodeDataList, s3.getText()));
        s3.setGroup("Region");
        nodeDataList.add(s3);

        LinkData link = LinkData.builder()
                .from(s3.getKey())
                .to(athena.getKey())
                .build();
        linkDataList.add(link);
    }

    private void setQuickSight() {
        NodeData athenaNode = diagramDTOService.getNodeDataByKey(nodeDataList, "Athena");
        if(athenaNode==null){
            athenaNode = addResourceService.addAthena();
            athenaNode.setGroup("Region");
            nodeDataList.add(athenaNode);
        }

        NodeData quickSight = addResourceService.addQuickSight();
        quickSight.setKey(quickSight.getKey()+diagramDTOService.getNodeNumber(nodeDataList, quickSight.getText()));
        quickSight.setGroup("Region");
        nodeDataList.add(quickSight);

        LinkData link = LinkData.builder()
                .from(athenaNode.getKey())
                .to(quickSight.getKey())
                .build();
        linkDataList.add(link);

    }
}