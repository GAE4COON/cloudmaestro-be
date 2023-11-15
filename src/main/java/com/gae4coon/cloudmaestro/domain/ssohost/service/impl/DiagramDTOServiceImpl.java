package com.gae4coon.cloudmaestro.domain.ssohost.service.impl;

import com.gae4coon.cloudmaestro.domain.ssohost.dto.*;
import com.gae4coon.cloudmaestro.domain.ssohost.service.DiagramDTOService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service

public class DiagramDTOServiceImpl implements DiagramDTOService {
    @Override
    public Map<String, Object> dtoGenerator(GraphLinksModel graphLinksModel){
        List<NodeData> dataArray = graphLinksModel.getNodeDataArray();
        List<NodeData> nodeDataList = new ArrayList<>();
        List<GroupData> groupDataList = new ArrayList<>();

        List<LinkData> linkDataList = graphLinksModel.getLinkDataArray();

        for (NodeData data : dataArray) {
            if (data.getIsGroup() != null) {
                GroupData groupData = new GroupData(data.getKey(), data.getText(), data.getIsGroup(), data.getGroup(), data.getType(), data.getStroke());
                groupDataList.add(groupData);
            } else {
                nodeDataList.add(data);
            }
        }

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("nodeDataArray", nodeDataList);
        responseBody.put("groupDataArray", groupDataList);
        responseBody.put("linkDataArray", linkDataList);

        return responseBody;
    }

    @Override
    public HashMap<String, Object> dtoComplete(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList){
        Map<String, Object> responseBody = new HashMap<>();

        List<Object> finalDataArray = new ArrayList<>();
        finalDataArray.add(nodeDataList);
        finalDataArray.add(groupDataList);

        finalDataArray.removeIf(Objects::isNull);

        responseBody.put("class", "GraphLinksModel");
        responseBody.put("linkKeyProperty", "key");
        responseBody.put("nodeDataArray", finalDataArray);  // 예시
        responseBody.put("linkDataArray", linkDataList);  // 예시

        HashMap<String, Object> response = new HashMap<>();

        response.put("result", responseBody);

        return response;
    }
}
