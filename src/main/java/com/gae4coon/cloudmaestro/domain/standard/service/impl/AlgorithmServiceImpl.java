package com.gae4coon.cloudmaestro.domain.standard.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;


import com.gae4coon.cloudmaestro.domain.standard.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.standard.dto.LinkData;
import com.gae4coon.cloudmaestro.domain.standard.dto.NodeData;
//import com.gae4coon.cloudmaestro.domain.standard.service.AlgorithmServiceInterface;
import com.gae4coon.cloudmaestro.domain.standard.service.StandardServiceInterface;
import io.micrometer.observation.annotation.Observed;
import io.swagger.v3.oas.models.links.Link;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.FileWriter;
import java.security.Key;
import java.sql.Array;
import java.util.*;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
public class AlgorithmServiceImpl {

    public Map<String, Object> algorithmDataList(Object nodesData, Object linkData) {
        List<NodeData> nodes = new ArrayList<>();
        List<LinkData> links = new ArrayList<>();
        List<GroupData> groups = new ArrayList<>();
        List<Object> combinedList = new ArrayList<Object>();

        if (nodesData instanceof List<?>) {
            for (Object obj : (List<?>) nodesData) {
                if (obj instanceof NodeData) {
                    nodes.add((NodeData) obj);
                } else if (obj instanceof GroupData) {
                    groups.add((GroupData) obj);
                } else {
                    log.error("nodesData 리스트에 예상치 못한 객체 타입 발견: " + obj.getClass().getName());
                }
            }
        }

        if (linkData instanceof List<?>) {
            for (Object obj : (List<?>) linkData) {
                if (obj instanceof LinkData) {
                    links.add((LinkData) obj);
                } else {
                    log.error("linkData 리스트에 예상치 못한 객체 타입 발견: " + obj.getClass().getName());
                }
            }
        }

        List<NodeData> newNodes = new ArrayList<>();  // 초기 노드로 초기화
        List<LinkData> newLinks = new ArrayList<>();  // 초기 링크로 초기화
        int count = 0; // count 변수를 외부로 이동

        for (NodeData item : nodes) {
            if (item.getKey().contains("FW")) {
                GroupData groupNode = new GroupData();
                groupNode.setIsGroup(true);
                groupNode.setKey("Security Group" + count++);
                groupNode.setStroke("rgba(0,0,198,0.3)");
                groupNode.setType("AWS_Groups");
                groupNode.setGroup(item.getGroup());
                groups.add(groupNode);

                Iterator<LinkData> iterator = links.iterator();
                while (iterator.hasNext()) {
                    LinkData link = iterator.next();
                    if (link.getTo().equals(item.getKey())) {//현재 to가 FWn인 Node를 찾는다.
                        link.setTo(groupNode.getKey()); //현재 보안 그룹으로 to를 링크
                        newLinks.add(link);
                        iterator.remove();  // Remove it from the original list to avoid processing it again
                    } else if (link.getFrom().equals(item.getKey())) {
                        nodes.stream()
                                .filter(searchNode -> searchNode.getKey().equals(link.getTo()))
                                .forEach(searchNode -> {
                                    NodeData tempNode = new NodeData(searchNode);  // 복제 생성자를 사용
                                    if (!newNodes.contains(tempNode)) {
                                        tempNode.setGroup(groupNode.getKey());
                                        newNodes.add(tempNode);  // 새로운 노드 목록에 추가
                                    }
                                });
                        iterator.remove();  // Remove it from the original list to avoid processing it again
                    }
                }
            }
        }

        HashSet<NodeData> uniqueNodes = new HashSet<>();
        uniqueNodes.addAll(newNodes);

        for (NodeData item: nodes) { //node에서 PW와 관련이 있는데 보안 그룹이 없는 노드들 추출
            boolean nodeExists = newNodes.stream().anyMatch(newNode -> newNode.getKey().equals(item.getKey()));
            if(!nodeExists){
                uniqueNodes.add(item);
            }
        }

        System.out.println("======================================");

        //newNode, newLink는 보안그룹과 연관있는 node와 link들
        
        //newLinks, newNodes를 가지고 위와 같은 방식으로 Security group이 to link일때
        Set<LinkData> uniqueLinks = new HashSet<>();  // 중복 링크 제거를 위한 Set

        for (NodeData item : uniqueNodes) { // PW 링크 됐었던 노드들 탐색
            for (LinkData link : newLinks) {
                if (link.getFrom().equals(item.getKey())) { // 현재 노드와 대응되는 link from
                    if (item.getGroup() != null && item.getGroup().contains("Security Group") && ! item.getGroup().equals(link.getTo())) { // 보안 그룹이 있는 노드지만.. 똑같으면 안됨
                        LinkData newLink = new LinkData();
                        newLink.setFrom(item.getGroup()); // 보안 그룹으로 'from' 설정
                        newLink.setTo(link.getTo()); // 'to'는 그대로 유지
                        newLink.setKey(link.getKey());
                        uniqueLinks.add(newLink); // 생성된 링크 추가
                        System.out.println("1: "+ newLink);
                        System.out.println("group:" + item.getGroup());
                    } else { // 보안 그룹이 없는 노드
                        System.out.println("2: "+ link);
                        uniqueLinks.add(link); // 기존 링크 그대로 추가
                    }
                }
            }
        }


        for (LinkData item:uniqueLinks
             ) {
            System.out.println(item);
        }

        // Remaining links that are not related to FW nodes
        uniqueLinks.addAll(links);
        //uniqueLinks.addAll(newLinks);

        for (NodeData node : nodes) { // FW와 관련 없던 노드들 모두 추가
            if (!node.getKey().contains("FW")) {
                newNodes.add(node);
            }
        }

        combinedList.addAll(newNodes);
        combinedList.addAll(groups);
        HashMap<String, Object> result = new HashMap<String, Object>();

        result.put("nodeDataArray", combinedList);
        result.put("linkDataArray", uniqueLinks);
        return result;
    }

}










