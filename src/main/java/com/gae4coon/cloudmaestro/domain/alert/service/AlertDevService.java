package com.gae4coon.cloudmaestro.domain.alert.service;

import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import org.springframework.stereotype.Service;

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
}