package com.gae4coon.cloudmaestro.domain.refactor.service;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BackupService {

    public void addRegionGroup(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList) {

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

    }

}
