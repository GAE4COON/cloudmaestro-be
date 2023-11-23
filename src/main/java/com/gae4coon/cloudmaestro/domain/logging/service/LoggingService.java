package com.gae4coon.cloudmaestro.domain.logging.service;

import com.gae4coon.cloudmaestro.domain.requirements.dto.RequireDTO;
import com.gae4coon.cloudmaestro.domain.requirements.dto.ZoneDTO;
import com.gae4coon.cloudmaestro.domain.resource.service.AddResourceService;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import com.gae4coon.cloudmaestro.domain.ssohost.service.DiagramDTOService;
import io.swagger.v3.oas.models.links.Link;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;

import java.util.ArrayList;
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

    private void setLogCollectStore() {
        List<NodeData> cloudTrailList = diagramDTOService.getNodeListByText(nodeDataList, "CloudTrail");
        List<NodeData> cloudWatchList = diagramDTOService.getNodeListByText(nodeDataList, "CloudWatch");
        List<NodeData> cloudTrails3List = new ArrayList<>();

        // 노드 없으면 추가
        if(cloudTrailList.isEmpty()){
            NodeData cloudTrail = addResourceService.addCloudTrail(nodeDataList);
            cloudTrail.setKey(cloudTrail.getKey()+diagramDTOService.getNodeNumber(nodeDataList, cloudTrail.getText()));
            cloudTrail.setGroup("Region");
            cloudTrail.setLoc("400 -600");
            nodeDataList.add(cloudTrail);
            cloudTrailList.add(cloudTrail);
        }
        if(cloudWatchList.isEmpty()){
            NodeData cloudWatch = addResourceService.addCloudWatch(nodeDataList);
            cloudWatch.setKey(cloudWatch.getKey()+diagramDTOService.getNodeNumber(nodeDataList, cloudWatch.getText()));
            cloudWatch.setGroup("Region");
            cloudWatch.setLoc("600, -600");
            nodeDataList.add(cloudWatch);
            cloudWatchList.add(cloudWatch);
        }
        else{
            // cloudWatch가 있다면 cloudWatch와 연결된 s3있는지 검사
            List<NodeData> s3List = diagramDTOService.getNodeListByText(nodeDataList, "Simple Storage Service");

            for(NodeData s3: s3List){
                LinkData link = diagramDTOService.getLinkDataByTo(linkDataList, s3.getKey());
                System.out.println(link);
                if(link!=null && link.getFrom().contains("CloudWatch")){
                    cloudTrails3List.add(s3);
                }

                LinkData link2 = diagramDTOService.getLinkDataByFrom(linkDataList, s3.getKey());
                System.out.println(link2);
                if(link2!=null && link2.getTo().contains("CloudWatch")){
                    cloudTrails3List.add(s3);
                }
            }
        }
        // 연결된 s3 없으면 생성
        if(cloudTrails3List.isEmpty()){
            System.out.println("new");
            NodeData s3 = addResourceService.addSimpleStorageService(nodeDataList);
            s3.setGroup("Region");
            s3.setLoc("600, -750");
            nodeDataList.add(s3);
            cloudTrails3List.add(s3);
        }

        for(NodeData cloudWatch : cloudWatchList){
            // cloudtrail -> cloudwatch
            for (NodeData cloudTrail : cloudTrailList) {
                LinkData link = LinkData.builder()
                        .from(cloudTrail.getKey())
                        .to(cloudWatch.getKey())
                        .build();
                linkDataList.add(link);
            }

            for(NodeData s3: cloudTrails3List){
                // cloudwatch -> s3
                LinkData link = LinkData.builder()
                        .from(cloudWatch.getKey())
                        .to(s3.getKey())
                        .build();
                linkDataList.add(link);
                // s3 -> cloudwatch
                LinkData link2 = LinkData.builder()
                        .from(s3.getKey())
                        .to(cloudWatch.getKey())
                        .build();
                linkDataList.add(link2);

            }
        }

        // unique link
        List<LinkData> uniqueLink = LinkData.uniqueLink(linkDataList);
        linkDataList.clear();
        linkDataList.addAll(uniqueLink);
    }

    private void setLogAnalyze() {
        setOpenSearch();
        setAthena();
        setQuickSight();
    }

    private void setCloudTrail() {
        NodeData cloudTrail = addResourceService.addCloudTrail();
        cloudTrail.setKey(cloudTrail.getKey()+diagramDTOService.getNodeNumber(nodeDataList, cloudTrail.getText()));
        cloudTrail.setGroup("Region");
        cloudTrail.setLoc("400 -600");
        nodeDataList.add(cloudTrail);

        NodeData s3 = addResourceService.addSimpleStorageService();
        s3.setKey(s3.getKey()+diagramDTOService.getNodeNumber(nodeDataList, s3.getText()));
        s3.setGroup("Region");
        s3.setLoc("400 -750");
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
        cloudWatch.setLoc("600, -600");
        nodeDataList.add(cloudWatch);

        NodeData s3 = addResourceService.addSimpleStorageService();
        s3.setKey(s3.getKey()+diagramDTOService.getNodeNumber(nodeDataList, s3.getText()));
        s3.setGroup("Region");
        s3.setLoc("450, -600");
        nodeDataList.add(s3);

        LinkData link = LinkData.builder()
                .from(s3.getKey())
                .to(cloudWatch.getKey())
                .build();
        linkDataList.add(link);

        LinkData link2 = LinkData.builder()
                .from(cloudWatch.getKey())
                .to(s3.getKey())
                .build();
        linkDataList.add(link2);
    }

    private void setOpenSearch() {
        NodeData cloudWatchNode = diagramDTOService.getNodeDataByText(nodeDataList, "CloudWatch");
        if(cloudWatchNode==null){
            cloudWatchNode = addResourceService.addCloudWatch();
            cloudWatchNode.setGroup("Region");
            cloudWatchNode.setLoc("1200 -700");
            nodeDataList.add(cloudWatchNode);
        }

        NodeData OpenSearch = addResourceService.addOpenSearchService();
        OpenSearch.setKey(OpenSearch.getKey()+diagramDTOService.getNodeNumber(nodeDataList, OpenSearch.getText()));
        OpenSearch.setGroup("Region");
        OpenSearch.setLoc("1000 -800");
        nodeDataList.add(OpenSearch);

        NodeData lambda = addResourceService.addLambdaLambdaFunction();
        lambda.setKey(lambda.getKey()+diagramDTOService.getNodeNumber(nodeDataList, lambda.getText()));
        lambda.setGroup("Region");
        lambda.setLoc("1000 -600");
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
        athena.setLoc("700 -700");
        nodeDataList.add(athena);

        NodeData s3 = addResourceService.addSimpleStorageService();
        s3.setKey(s3.getKey()+diagramDTOService.getNodeNumber(nodeDataList, s3.getText()));
        s3.setGroup("Region");
        s3.setLoc("700 -900");
        nodeDataList.add(s3);

        LinkData link = LinkData.builder()
                .from(s3.getKey())
                .to(athena.getKey())
                .build();
        linkDataList.add(link);
    }

    private void setQuickSight() {
        List<NodeData> athenaNodeList = diagramDTOService.getNodeListByText(nodeDataList, "Athena");
        if(athenaNodeList.isEmpty()){
            NodeData athenaNode = addResourceService.addAthena();
            athenaNode.setGroup("Region");
            athenaNode.setLoc("700 -700");
            nodeDataList.add(athenaNode);

            athenaNodeList.add(athenaNode);
        }

        NodeData quickSight = addResourceService.addQuickSight();
        quickSight.setKey(quickSight.getKey()+diagramDTOService.getNodeNumber(nodeDataList, quickSight.getText()));
        quickSight.setGroup("Region");
        quickSight.setLoc("900 -700");
        nodeDataList.add(quickSight);

        for(NodeData athenaNode: athenaNodeList) {
            LinkData link = LinkData.builder()
                    .from(athenaNode.getKey())
                    .to(quickSight.getKey())
                    .build();
            linkDataList.add(link);
        }
    }
}