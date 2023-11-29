package com.gae4coon.cloudmaestro.domain.alert.service;

import com.amazonaws.services.ec2.model.Vpc;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DiagramCheckService {

    private final AlertGroupService alertGroupService;
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

    public HashMap<String, String> vpcCheck(List<GroupData> groupDataList, GroupData newData) {
        boolean flag = false;
        HashMap<String, String> check = new HashMap<>();
        List<GroupData> checkgr = new ArrayList<>();
        Map<String, List<String>> regionVPC = new HashMap<>();


        if(groupDataList != null) {
            //BP 찾기
            List<String> BPgroup=alertGroupService.groupSearch("Module", groupDataList);
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
                //그룹이 없다면? 그리고 Region 거르기
                if (group.getText().equals("Region"))
                 {
                    checkgr.add(group);
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

    public HashMap<String, String> APICheck(List<NodeData> nodeDataList, List<GroupData> groupDataList, NodeData newData) {
        boolean flag = false;
        HashMap<String, String> check = new HashMap<>();
        List<String> checkgr = new ArrayList<>();

        List<String> BPgroup=alertGroupService.groupSearch("Module", groupDataList);
        System.out.println("BPgroup"+ BPgroup);

        if(groupDataList != null) {
            if(newData.getGroup().contains("VPC")) {
                List<String> hhgroup = alertGroupService.groupSearch2(newData.getGroup(), groupDataList);
                System.out.println("hhgroup" + hhgroup);

                for (NodeData item : nodeDataList) {
                    if (!(item.getGroup() == null) && hhgroup.contains(item.getGroup()) && item.getText().equals("API Gateway")) {
                        checkgr.add(item.getKey());
                    }
                }
                System.out.println("checkgr" + checkgr);
                System.out.println("NewData" + newData.getKey());

                if (checkgr.size() > 1) {
                    if (checkgr.contains(newData.getKey()) && !BPgroup.contains(newData.getGroup())) {
                        flag = true;
                        check.put("message", "VPC는 당 API GateWay는 한 개를 초과할 수 없습니다.");
                        System.out.println("??check");
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

    public HashMap<String, String> DBcheck(List<GroupData> groupDataList,NodeData newData) {
        boolean flag = false;
        HashMap<String, String> check = new HashMap<>();
        List<String> checkgr = new ArrayList<>();

        List<String> BPgroup=alertGroupService.groupSearch("Module", groupDataList);
        System.out.println("BPgroup"+ BPgroup);

        if(groupDataList != null) {
            if (newData.getGroup() != null) {

                List<String> hhgroup = alertGroupService.groupSearch3(newData.getKey(), groupDataList);
                if (!hhgroup.contains("Private subnet") && !BPgroup.contains(newData.getGroup())) {
                    flag = true;
                    check.put("message", "인터넷에 연결할 필요가 없는 VPC내의 DB는 클러스터는 Private Subnet에 배치해야합니다.");
                    System.out.println("??check");
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
