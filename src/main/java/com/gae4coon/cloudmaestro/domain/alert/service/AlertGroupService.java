package com.gae4coon.cloudmaestro.domain.alert.service;

import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


@Service
public class AlertGroupService {
    public List<String> groupSearch(String option, List<GroupData> groupDataList) { //그룹 다 퉁쳐서 나오는 함수

        HashSet<String> BPSet = new HashSet<>();
        if(groupDataList != null) {
            for (GroupData group : groupDataList) {
                if (group.getText().equals(option)) {
                    BPSet.add(group.getKey());
                }
            }

            int previousSize = 0;
            int currentSize = BPSet.size();

            while (previousSize != currentSize) {
                previousSize = currentSize;

                for (GroupData groupItem : groupDataList) {
                    if (groupItem.getGroup()!= null && groupItem.getGroup() instanceof String) {
                        for (String setItem : BPSet) {
                            if (groupItem.getGroup().equals(setItem)) {
                                BPSet.add(groupItem.getKey());
                            }
                        }
                    }
                }
                currentSize = BPSet.size();
            }
        }
        return new ArrayList<>(BPSet);
    }

    public List<String> groupSearch2(String option, List<GroupData> groupDataList) {
        HashSet<String> BPSet = new HashSet<>();
        HashSet<String> tempSet = new HashSet<>();

        BPSet.add(option);

        int previousSize = 0;
        int currentSize = BPSet.size();

        while (previousSize != currentSize) {
            previousSize = currentSize;

            for (GroupData groupItem : groupDataList) {
                if (groupItem.getGroup() instanceof String) {
                    for (String setItem : BPSet) {
                        if (groupItem.getGroup().equals(setItem)) {
                            tempSet.add(groupItem.getKey());
                        }
                    }
                }
            }

            BPSet.addAll(tempSet); // 여기에서 tempSet의 모든 항목을 BPSet에 추가합니다.
            tempSet.clear(); // tempSet을 비웁니다.

            currentSize = BPSet.size();
        }
        return new ArrayList<>(BPSet);
    }


    public List<String> groupSearch3(NodeData option, List<GroupData> groupDataList) { //nodegroup 분할되어 나올 때 쓸 함수



        HashSet<String> BPSet = new HashSet<>();
        boolean flag = false;
        try {
        if (option.getGroup()!=null) {
            BPSet.add(option.getGroup());
            System.out.println("===================================================================");


            int previousSize = 0;
            int currentSize = BPSet.size();

            while (previousSize != currentSize) {
                if(flag==true) break;
                previousSize = currentSize;

                for (GroupData groupItem : groupDataList) {
//                    System.out.println("groupItem"+groupItem);
                    if (groupItem == null) continue;
                    if(flag==true) break;
                    if (groupItem.getKey() instanceof String) {
                        for (String setItem : BPSet) {
//                            System.out.println("setItem"+setItem);
                            if (groupItem.getGroup() != null && groupItem.getKey().equals(setItem)) {
                                BPSet.add(groupItem.getGroup());

                                if (groupItem.getGroup().contains("Private subnet")) {
                                    flag = true;
                                    break;
                                }
                            }
                        }
                    }
                }
                currentSize = BPSet.size();
//                System.out .println(BPSet);
//                System.out .println(currentSize + " " +previousSize);
            }
        }
        }catch (Exception e){
            System.out.println(e);
        }
        return new ArrayList<>(BPSet);

    }


}
