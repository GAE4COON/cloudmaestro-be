package com.gae4coon.cloudmaestro.domain.rehost.service;
import com.gae4coon.cloudmaestro.domain.rehost.dto.NodeData;
import java.util.List;
import java.util.Map;


public interface StandardServiceInterface {

     Map<String, Object> processNodeData(List<NodeData> data);
     void NetToAws(NodeData node);
     //List<LinkData> NetToAws(List<LinkData> linkdata);
     String NetToAws(String node, String nodeText);
     Map<String, Object> processKeyData(Map<String, Object> nodesData);


}
