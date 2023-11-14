package com.gae4coon.cloudmaestro.domain.resource.service;

import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SidebarService {
    public List<String> getResourceFilter(List<NodeData> nodeDataList){
        List<String> result = new ArrayList<>();
        for (NodeData node: nodeDataList){
            String type = node.getType();
            boolean isGroup;
            try {
                isGroup = node.getIsGroup();
            } catch (NullPointerException e) {
                isGroup = false; // 예외가 발생한 경우 기본값으로 false 설정
            }
            if (type != null &&
                !type.equals("Compute") &&
                !type.equals("Database") &&
                !type.equals("Storage") &&
                !type.equals("Networking-Content-Delivery") &&
                !type.equals("Group") &&
                !type.equals("Network_icon") &&
                !isGroup &&
                !result.contains(node.getText())){
                result.add(node.getText());
            }
        }

        return result;
    }
}
