package com.gae4coon.cloudmaestro.domain.alert.service;

import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import io.swagger.v3.oas.models.links.Link;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AlertDevService {
    public boolean alertDev(List<GroupData> groupDataList) {
        for(GroupData groupData : groupDataList){
            if(groupData.getText().contains("dev") ||
                    groupData.getText().contains("DEV")) {
                System.out.println("Dev groupData" + groupData);
                return true;
            }
        }
        System.out.println("Dev groupDataalse" + groupDataList);
        return false;
    }

    public boolean alertGateway(List<LinkData> linkDataList, List<NodeData> nodeDataList) {
        List<String> fromapi = new ArrayList<>();
        for (NodeData nodeData : nodeDataList) {
            if (nodeData.getText().equals("API Gateway")) {
                for (LinkData linkData : linkDataList) {
                    if(linkData.getFrom().equals(nodeData.getKey())){
                        fromapi.add(linkData.getTo());
                    }
                }
            }
        }
        boolean existLambda = false;
        if(fromapi.size() > 0){
            for(int i = 0; i < fromapi.size(); i++){
                if(fromapi.get(i).contains("Lambda")){
                    existLambda = false;
                }
            }
            if(!existLambda){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }

    }
}