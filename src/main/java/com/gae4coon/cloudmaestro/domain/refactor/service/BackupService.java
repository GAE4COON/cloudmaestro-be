package com.gae4coon.cloudmaestro.domain.refactor.service;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gae4coon.cloudmaestro.domain.requirements.dto.RequireDTO;
import com.gae4coon.cloudmaestro.domain.requirements.dto.RequireDiagramDTO;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import jakarta.mail.Flags;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BackupService {


    public void requirementParsing(RequireDiagramDTO requireDiagramDTO, Map<String, Object>  responseArray){

        List<NodeData> nodeDataList = (List<NodeData>) responseArray.get("nodeDataArray");
        List<GroupData> groupDataList = (List<GroupData>) responseArray.get("groupDataArray");
        List<LinkData> linkDataList = (List<LinkData>) responseArray.get("linkDataArray");

        List<String> centralManage= new ArrayList<>();
        List<String> generalManage= new ArrayList<>();

        GroupData newGroup;

        if(requireDiagramDTO.getRequirementData().getBackup().size()!= 0){
            if(requireDiagramDTO.getRequirementData().getBackup().contains("중앙관리")){
                centralBackup(nodeDataList,linkDataList, groupDataList);
            };

            if(requireDiagramDTO.getRequirementData().getBackup().contains("일반")){
                generalBackup(nodeDataList);
            };
        }

//        //사이즈 체크 후, 백업 옵션 여부 확인.
//        for (RequireDTO.Zone zone: requireDiagramDTO.getRequirementData().getZones()) {
//            if(zone.getDynamicBackup().size() == 0 && zone.getStaticBackup().size() == 0){
//                continue;
//            }
//            backupFlag = true;
//        }
//
//        // 백업 플래그가 있다면? CRR 용 백업 리전 그룹 추가
//        if(backupFlag){
//             newGroup= addRegionGroup(groupDataList);
//        }
//        else{//없다면? 바로 리턴
//            System.out.println("return 해야됨");
//            return;
//        }

//        //이후부터 백업작업
//        for (RequireDTO.Zone zone: requireDiagramDTO.getRequirementData().getZones()) {
//            //중앙관리백업
//            if(zone.getDynamicBackup().size() != 0){
//                centralManage = zone.getDynamicBackup();
//                centralBackup(centralManage, newGroup, nodeDataList, linkDataList, groupDataList);
//            }
//
//            //일반백업
//            if(zone.getStaticBackup().size() != 0){
//                generalManage = zone.getStaticBackup();
//
//            }
//        }

    }

    public GroupData addRegionGroup (List<GroupData> groupDataList){

        int number=0;


        for (GroupData groupitem : groupDataList) {
            if(groupitem.getKey().contains("Region")){

                Pattern pattern = Pattern.compile("Region(\\d*)");
                Matcher matcher = pattern.matcher(groupitem.getKey());
                if (matcher.find()) {
                    String numberStr = matcher.group(1); // Region 다음의 숫자 부분
                    int tempnum = 1; // 기본값을 1로 설정

                    // 숫자가 있으면 그 숫자를 사용, 없으면 기본값 사용
                    if (!numberStr.isEmpty()) {
                        tempnum = Integer.parseInt(numberStr)+1;
                    }

                    if(number<tempnum) number=tempnum;

                }

            }
        }

        GroupData newGroup=new GroupData("Region" + number,"Region"+number,true,null,"AWS_Groups","rgb(0,164,166)");
        System.out.println("newGroup: " +newGroup);
        System.out.println();

        groupDataList.add(newGroup);
        System.out.println("test: "+groupDataList);

        return newGroup;

    }

    public void centralBackup (List<NodeData> nodeDataList, List<LinkData> linkDataList,List<GroupData> groupDataList){

        List<GroupData> regionList = new ArrayList<>();
        for (GroupData grd:groupDataList) {
            if(grd.getKey().contains("Region")){
                regionList.add(grd);
            }
        }

        if(regionList.size()<2){
            GroupData newGroup = addRegionGroup(groupDataList);
            regionList.add(newGroup);
            groupDataList.add(newGroup);
        };

        int number=0;

        //기존에 backup 버킷이 있다면 카운트
        for (NodeData groupitem : nodeDataList) {
            if(groupitem.getKey().contains("Backup")){

                Pattern pattern = Pattern.compile("Backup(\\d*)");
                Matcher matcher = pattern.matcher(groupitem.getKey());
                if (matcher.find()) {
                    String numberStr = matcher.group(1); // Region 다음의 숫자 부분
                    int tempnum = 1; // 기본값을 1로 설정

                    // 숫자가 있으면 그 숫자를 사용, 없으면 기본값 사용
                    if (!numberStr.isEmpty()) {
                        tempnum = Integer.parseInt(numberStr)+1;
                    }

                    if(number<tempnum) number=tempnum;

                }

            }
        }
        NodeData newNode=new NodeData();
        newNode.setKey("Backup" + number);
        newNode.setType("Storage");
        newNode.setText("Backup"+number);
        newNode.setSource("/img/AWS_icon/Arch_Storage/Arch_AWS-Backup_48.svg");
        newNode.setLoc("0 0");
        newNode.setStroke(null);
        newNode.setGroup(null);
        newNode.setFigure(null);
        newNode.setGroup(null);

        NodeData backupNode=new NodeData();
        backupNode.setKey("Backup" + number+1);
        backupNode.setType("Storage");
        backupNode.setText("Backup"+number+1);
        backupNode.setSource("/img/AWS_icon/Arch_Storage/Arch_AWS-Backup_48.svg");
        backupNode.setLoc("0 0");
        backupNode.setStroke(null);
        backupNode.setGroup(null);
        backupNode.setFigure(null);
        backupNode.setGroup(null);


    }


    public void generalBackup (List<NodeData> nodeDataList){
        int number=0;

        //기존에 s3 버킷이 있다면 카운트
        for (NodeData groupitem : nodeDataList) {
            if(groupitem.getKey().startsWith("S3")){

                Pattern pattern = Pattern.compile("^S3(\\d*)");
                Matcher matcher = pattern.matcher(groupitem.getKey());
                if (matcher.find()) {
                    String numberStr = matcher.group(1); // Region 다음의 숫자 부분
                    int tempnum = 1; // 기본값을 1로 설정

                    // 숫자가 있으면 그 숫자를 사용, 없으면 기본값 사용
                    if (!numberStr.isEmpty()) {
                        tempnum = Integer.parseInt(numberStr)+1;
                    }

                    if(number<tempnum) number=tempnum;

                }

            }
        }

        NodeData newNode=new NodeData();
        newNode.setKey("S3" + number);
        newNode.setType("Storage");
        newNode.setText("Backup"+number);
        newNode.setSource("/img/AWS_icon/Arch_Storage/Res_Amazon-Simple-Storage-Service_S3-Standard_48.svg");
        newNode.setLoc("0 0");
        newNode.setStroke(null);
        newNode.setGroup(null);
        newNode.setFigure(null);
        newNode.setGroup(null);

        nodeDataList.add(newNode);
    }

}
