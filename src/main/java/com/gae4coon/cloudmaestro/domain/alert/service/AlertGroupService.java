package com.gae4coon.cloudmaestro.domain.alert.service;

import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
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

    public List<String> groupSearch2(String option,List<GroupData> groupDataList) { //nodegroup 분할되어 나올 때 쓸 함수
        HashSet<String> BPSet = new HashSet<>();

        BPSet.add(option);


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

    public List<String> groupSearch3(String option,List<GroupData> groupDataList) { //nodegroup 분할되어 나올 때 쓸 함수
        HashSet<String> BPSet = new HashSet<>();

        BPSet.add(option);


        int previousSize = 0;
        int currentSize = BPSet.size();

        while (previousSize != currentSize) {
            previousSize = currentSize;

            for (GroupData groupItem : groupDataList) {
                if (groupItem.getKey() instanceof String) {
                    for (String setItem : BPSet) {
                        if (groupItem.getGroup() !=null && groupItem.getGroup().equals(setItem)) {
                            BPSet.add(groupItem.getGroup());
                            if (groupItem.getGroup().contains("Private subnet")) break;
                        }
                    }
                }
            }
            currentSize = BPSet.size();
        }
        return new ArrayList<>(BPSet);
    }
}
