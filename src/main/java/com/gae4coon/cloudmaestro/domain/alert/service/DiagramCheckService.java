package com.gae4coon.cloudmaestro.domain.alert.service;

import com.amazonaws.services.ec2.model.Vpc;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DiagramCheckService {
    public HashMap<String, String> linkCheck(LinkData linkData) {
        HashMap<String, String> check = new HashMap<>();
        if (linkData.getTo().contains("IDS") || linkData.getTo().contains("IPS") && linkData.getFrom().contains("Firewall")) {
            check.put("status", "fail");
            check.put("message", "Firewall 다음에는 IPS, IDS가 올 수 없습니다.");
        } else {
            check.put("status", "success");
        }

        return check;
    }


    public List<String> BPgroupSearch(List<GroupData> groupDataList) {
        HashSet<String> BPSet = new HashSet<>();
        for (GroupData group : groupDataList) {
            if(group.getText().equals("Module")){
                BPSet.add(group.getKey());
            }
        }

        int previousSize = 0;
        int currentSize = BPSet.size();

        while (previousSize != currentSize) {
            previousSize = currentSize;

            for (GroupData groupItem : groupDataList) {
                if (groupItem.getGroup() instanceof String) {
                    for (String setItem : BPSet) {
                        if (groupItem.getGroup().equals(setItem)) {
                            BPSet.add(groupItem.getKey());
                        }
                    }
                }
            }
            currentSize = BPSet.size();
        }
        return new ArrayList<>(BPSet);
    }




    public HashMap<String, String> vpcCheck(List<GroupData> groupDataList, GroupData newData) {
        boolean flag = false;
        HashMap<String, String> check = new HashMap<>();
        List<GroupData> checkgr = new ArrayList<>();
        Map<String, List<String>> regionVPC = new HashMap<>();


        if(groupDataList != null) {
            //BP 찾기
            List<String> BPgroup=BPgroupSearch(groupDataList);
            System.out.println(BPgroup);


            for (GroupData group : groupDataList) {
                //그룹 체크
                if (group.getGroup() != null) {
                    if (group.getText().equals("VPC") && (!group.getGroup().contains("Region") && !BPgroup.contains(group.getKey()))) {
                        if (group.getKey().equals(newData.getKey())) {
                            flag = true;
                            check.put("message", "VPC는 Region 그룹 안에 있어야 합니다.");
                            break;
                        }
                    }
                }
                //그룹에 잘 있다면? 그리고 Region 거르기
                else{
                    if (group.getText().equals("Region")) {
                        checkgr.add(group);
                    }
                }
            }
            if(flag != true) {
                for (GroupData region : checkgr) {
                    List<String> vpcKeys = new ArrayList<>();
                    for (GroupData group : groupDataList) {
                        if (region.getKey().equals(group.getGroup())) {
                            vpcKeys.add(group.getKey());
                        }
                    }
                    regionVPC.put(region.getKey(), vpcKeys);
                }

                for (Map.Entry<String, List<String>> entry : regionVPC.entrySet()) {
                    String key = entry.getKey(); // 키를 얻음
                    List<String> vpcs = entry.getValue(); // VPC 리스트

                    if (vpcs.size()>5){
                        if (vpcs.contains(newData.getKey())) {
                            flag = true;
                            check.put("message", "VPC는 한 Region당 5개를 초과할 수 없습니다.");
                            break;
                        }
                    }
                }
            }

        }

        if (flag) {
            check.put("status", "fail");
        }else{
            check.put("status", "success");
        }
        return check;
    }
}
