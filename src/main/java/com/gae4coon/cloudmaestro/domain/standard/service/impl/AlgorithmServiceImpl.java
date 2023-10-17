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
                    if (link.getTo().equals(item.getKey())) {
                        link.setTo(groupNode.getKey());
                        newLinks.add(link);
                        iterator.remove();  // Remove it from the original list to avoid processing it again
                    } else if (link.getFrom().equals(item.getKey())) {
                        nodes.stream()
                                .filter(searchNode -> searchNode.getKey().equals(link.getTo()))
                                .forEach(searchNode -> {
                                    NodeData tempNode = new NodeData(searchNode);  // 복제 생성자를 사용
                                    tempNode.setGroup(groupNode.getKey());
                                    newNodes.add(tempNode);  // 새로운 노드 목록에 추가
                                });
                        iterator.remove();  // Remove it from the original list to avoid processing it again
                    }
                }
            }
        }

        // Remaining links that are not related to FW nodes
        newLinks.addAll(links);

        for (NodeData node : nodes) {
            if (!node.getKey().contains("FW")) {
                newNodes.add(node);
            }
        }

        combinedList.addAll(newNodes);
        combinedList.addAll(groups);
        HashMap<String, Object> result = new HashMap<String, Object>();

        result.put("nodeDataArray", combinedList);
        result.put("linkDataArray", newLinks);
        return result;
    }

}










