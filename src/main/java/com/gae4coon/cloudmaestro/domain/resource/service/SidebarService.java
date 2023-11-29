package com.gae4coon.cloudmaestro.domain.resource.service;

import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SidebarService {
    public List<String> getResourceFilter(List<NodeData> nodeDataList){
        List<String> service = new ArrayList<>();
        for (NodeData node: nodeDataList){
            String type = node.getType();
            boolean isGroup;
            String group = node.getGroup();

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
                !type.equals("Arch_Database")&&
                !isGroup &&
                !service.contains(node.getText())){
                service.add(node.getText());
            }
            if(group!=null && group.equals("Service") && !service.contains(node.getText())) {
                service.add(node.getText());
                // 서비스 노드 임시 할당
            }
        }

        return service;
    }


}
