//package com.gae4coon.cloudmaestro.domain.ssohost.service;
//import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
//import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
//import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.LinkedHashSet;
//import java.util.List;
//import java.util.Set;
//
//@Service
//public class SetNoLinkLocation {
//    public void noLinkGroup(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList) {
//
//        // linkData에 있는 Group 정보가 없을 때
//        List<String> linkdataFrom = new ArrayList<>();
//
//        for(LinkData linkdata : linkDataList){
//            linkdataFrom.add(linkdata.getFrom());
//            linkdataFrom.add(linkdata.getTo());
//        }
//
//        Set<String> link_set = new LinkedHashSet<>(linkdataFrom);
//        for(GroupData groupData : groupDataList){
//            if(!linkdataFrom.contains(groupData.getKey())) )
//            {
//
//            }
//        }
//
//
//
//    }
//}
