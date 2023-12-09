package com.gae4coon.cloudmaestro.domain.alert.service;

import com.gae4coon.cloudmaestro.domain.ssohost.dto.*;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DbAccessGuideAlertService {
    public String dbCheck(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData>linkDataList){
        Set<String> databaseGroups = new HashSet<>();
        String result = "";
        for (NodeData node : nodeDataList) {
            if ("Database".equals(node.getType())) {
                databaseGroups.add(node.getGroup());
            }
        }
        System.out.println("DB Group:"+databaseGroups);
        return result;
    }
}
