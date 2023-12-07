package com.gae4coon.cloudmaestro.domain.alert.service;

import com.amazonaws.services.ec2.model.Vpc;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class DiagramCheckService {

    private final AlertGroupService alertGroupService;
    public HashMap<String, String> linkCheck(LinkData linkData) {
        HashMap<String, String> check = new HashMap<>();
        Pattern pattern = Pattern.compile("^Firewall(\\d*)");
        Matcher matcher = pattern.matcher(linkData.getFrom());
        if (matcher.find()) {
            System.out.println(matcher.group(0));
            if (linkData.getTo().contains("IDS") || linkData.getTo().contains("IPS") && matcher.group(0).contains("Firewall")) {
                check.put("status", "fail");
                check.put("message", "Firewall 다음에는 IPS, IDS가 올 수 없습니다.");
            } else {
                check.put("status", "success");
            }
            return check;
        }else {
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
//            System.out.println("vpcCheck BPgroup: "+BPgroup);


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
                        if (region.getKey().equals(group.getGroup()) && group.getText().equals("VPC") ) {
                            vpcKeys.add(group.getKey());
                        }
                    }
//                    System.out.println("vpcCheck vpcKeys: "+vpcKeys);
                    regionVPC.put(region.getKey(), vpcKeys);
                }

                for (Map.Entry<String, List<String>> entry : regionVPC.entrySet()) {
                    String key = entry.getKey(); // 키를 얻음
                    List<String> vpcs = entry.getValue(); // VPC 리스트

                    if (vpcs.size()>5){
                        if (vpcs.contains(newData.getKey())) {
                            flag = true;
                            check.put("message", "VPC는 한 Region당 최대 5개까지 가능합니다.");
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
        System.out.println("APICheck BPgroup"+ BPgroup);

        if(groupDataList != null) {
            if(newData.getGroup().contains("VPC")) {
                List<String> hhgroup = alertGroupService.groupSearch2(newData.getGroup(), groupDataList);
                System.out.println("APICheck hhgroup" + hhgroup);

                for (NodeData item : nodeDataList) {
                    if (!(item.getGroup() == null) && hhgroup.contains(item.getGroup()) && item.getText().equals("API Gateway")) {
                        checkgr.add(item.getKey());
                    }
                }
                System.out.println("APICheck checkgr" + checkgr);
                System.out.println("APICheck NewData" + newData.getKey());

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

        System.out.println("check: " +check);
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
                List<String> hhgroup = alertGroupService.groupSearch3(newData, groupDataList);


                boolean containsPrivateSubnet = false;
                for(String group : hhgroup) {
                    if(group.contains("Private subnet")) {
                        containsPrivateSubnet = true;
                        break;
                    }
                }
                System.out.println("hhgroup"+hhgroup);
                if (!containsPrivateSubnet && !BPgroup.contains(newData.getGroup())) {
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

    public HashMap<String, String> Loggingcheck(List<NodeData> nodedatalist, NodeData newData) {
        boolean flag = false;
        HashMap<String, String> check = new HashMap<>();
        List<String> checknode = new ArrayList<>();

        for (NodeData item: nodedatalist) {
            if (item.getText().equals("S3") || item.getText().equals("CloudTrail") || item.getText().equals("CloudWatch")){
                checknode.add(item.getKey());
            }
        }

        if (checknode.size()==1 && checknode.get(0).equals(newData.getKey())){
            flag = true;
            check.put("message", "클라우드 서비스 고객은 이벤트 로깅을위한 요구 사항을 정의하고 클라우드 서비스가 이러한 요구 사항을 충족하는지 확인해야합니다.");
        }

        if (flag) {
            check.put("status", "fail");
        }else{
            check.put("status", "success");
        }
        return check;
    }

    public HashMap<String, String> Backupcheck(List<NodeData> nodedatalist, NodeData newData) {
        boolean flag = false;
        HashMap<String, String> check = new HashMap<>();
        List<String> checknode = new ArrayList<>();

        for (NodeData item: nodedatalist) {
            if (item.getText().equals("Backup") || item.getText().equals("S3")){
                checknode.add(item.getKey());
            }
        }
        if (checknode.size()==1 && checknode.get(0).equals(newData.getKey())){
            flag = true;
            check.put("message", "클라우드 서비스 공급자가 클라우드 서비스의 일부로 백업 기능을 제공하는 경우 클라우드 서비스 고객은 클라우드 서비스 공급자에게 백업 기능의 사양을 요청해야합니다. 클라우드 서비스 고객은 백업 요구 사항을 충족하는지 확인하여야 합니다.");
        }

        if (flag) {
            check.put("status", "fail");
        }else{
            check.put("status", "success");
        }
        return check;
    }
}
