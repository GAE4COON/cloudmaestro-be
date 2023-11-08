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
            if (!type.equals("Compute") &&
                !type.equals("Database") &&
                !type.equals("Storage") &&
                !type.equals("Networking-Content-Delivery") &&
                !result.contains(node.getText())){
                result.add(node.getText());
            }
        }

        return result;
    }
}
