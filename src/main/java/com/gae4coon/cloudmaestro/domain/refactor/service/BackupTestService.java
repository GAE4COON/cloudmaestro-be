package com.gae4coon.cloudmaestro.domain.refactor.service;

import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BackupTestService {
    private final BackupService backupService;
    private final BPService bpService;


    public Map<String, Object> testBackup(Map<String, Object> responseArray) {

        List<NodeData> nodeDataList = (List<NodeData>) responseArray.get("nodeDataArray");
        List<GroupData> groupDataList = (List<GroupData>) responseArray.get("groupDataArray");
        List<LinkData> linkDataList = (List<LinkData>) responseArray.get("linkDataArray");

        List<String> centralManage = new ArrayList<>();
        List<String> generalManage = new ArrayList<>();

        backupService.generalBackup(nodeDataList, groupDataList);
        backupService.centralBackup(nodeDataList, linkDataList, groupDataList);

        int cnt = 0;

        bpService.bpsearch("비동기", nodeDataList, linkDataList, groupDataList, 0);

        List<Object> finalDataArray = new ArrayList<>();
        finalDataArray.addAll(nodeDataList);
        finalDataArray.addAll(groupDataList);

        Map<String, Object> responseBody = new HashMap<>();

        responseBody.put("class", "GraphLinksModel");
        responseBody.put("linkKeyProperty", "key");
        responseBody.put("nodeDataArray", finalDataArray);  // 예시
        responseBody.put("linkDataArray", linkDataList);  // 예시

        return responseBody;

    }
}
