package com.gae4coon.cloudmaestro.domain.ssohost.service;

import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import io.swagger.v3.oas.models.links.Link;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.util.*;

@Service
public class ModifyLink{

    
    public Map<List<GroupData>, List<NodeData>> excludeNode(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList) {

        for(LinkData link: linkDataList){
            String rootTo = link.getTo();
            if(isExclude(rootTo)){
                String normalTo = findNormalNode(rootTo, linkDataList);
                link.setTo(normalTo);
            }
        }

        unique(linkDataList);

        Map<List<GroupData>, List<NodeData>> resultMap = new HashMap<>();
        resultMap.put(groupDataList, nodeDataList);

        return resultMap;

    }

    private String findNormalNode(String from, List<LinkData> linkDataList){
        String destination = from;

        for(LinkData link: linkDataList){
            if(link.getFrom().equals(destination)) {
                if (isExclude(link.getTo())){
                    return findNormalNode(link.getTo(), linkDataList);
                } else {
                    return link.getTo();
                }
            }
        }
        return destination;
    }


    private Boolean isExclude(String node){
        List<String> exclude = new ArrayList<>(Arrays.asList("Anti DDoS","IPS", "IDS", "Firewall"));

        for (String excludeItem : exclude) {
            if (node.contains(excludeItem)){
                return true;
            }
        }
        return false;
    }

    private Boolean isMaintain(String node){
        List<String> exclude = new ArrayList<>(Arrays.asList("Network Firewall Endpoint", "Network Firewall"));

        for (String excludeItem : exclude) {
            if (node.contains(excludeItem)){
                return true;
            }
        }
        return false;
    }

    private List<LinkData> unique(List<LinkData> originalList) {
        Set<LinkData> linkDataSet = new HashSet<>();
        for (LinkData link1 : originalList) {
            linkDataSet.add(link1);
        }

        List<LinkData> setlist = new ArrayList<>();
        for (LinkData l : linkDataSet) {
            setlist.add(l);
        }
        return setlist;
    }

    public void deleteNode(List<NodeData> nodeDataList, List<LinkData> linkDataList){
        List<NodeData> newnodeDataList = new ArrayList<>(nodeDataList);
        List<LinkData> newlinkDataList = new ArrayList<>(linkDataList);

        for (NodeData node: newnodeDataList){
            // delete node
            if(!node.getKey().startsWith("Firewall")) continue;
            nodeDataList.remove(node);

            // delete link
            for (LinkData link: newlinkDataList){
                if(link.getFrom().equals(node.getKey()) || link.getTo().equals(node.getKey())){
                    linkDataList.remove(link);
                    System.out.println(link);
                }
            }

        }
    }
}
