package com.gae4coon.cloudmaestro.domain.standard.service;
import com.gae4coon.cloudmaestro.domain.standard.dto.LinkData;
import com.gae4coon.cloudmaestro.domain.standard.dto.NodeData;
import java.util.List;
import java.util.Map;


public interface StandardServiceInterface {

     Map<String, Object>   processNodeData(List<NodeData> node, List<LinkData> link);
     void NetToAws(NodeData node);
     //List<LinkData> NetToAws(List<LinkData> linkdata);
     String NetToAws(String node, String nodeText);

     void LinkRehost(LinkData link);

     String StringRehost(String linkObj);



     Map<String, Object> processKeyData(Map<String, Object> nodesData);


}
