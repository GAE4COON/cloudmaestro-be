package com.gae4coon.cloudmaestro.domain.rehost.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;


import com.gae4coon.cloudmaestro.domain.rehost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.rehost.dto.LinkData;
import com.gae4coon.cloudmaestro.domain.rehost.dto.NodeData;
import com.gae4coon.cloudmaestro.domain.rehost.service.AlgorithmServiceInterface;


import org.apache.commons.collections4.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.util.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlgorithmServiceImpl implements AlgorithmServiceInterface {
    // 방화벽 밑에 1단을 기준으로 security group을 묶는다.

    private static final Logger logger = LoggerFactory.getLogger(AlgorithmServiceImpl.class);

    @Override
    public Map<String, Object> algorithmDataList(Map<String, Object> nodesData, List<LinkData> linkData) {

        // 1. security group으로 묶기
        // nodeData로 캐스팅하기
        Object rawNodeData = nodesData.get("nodeDataArray");
        Map<String, Object> result = new HashMap<>();

        List<Object> nodeDataList = null;
        if (rawNodeData instanceof List) {
            nodeDataList = (List<Object>) rawNodeData;
        } else {
            logger.error("Unexpected type for nodeDataArray. Expected List but got: {}", rawNodeData.getClass().getName());
            return Collections.emptyMap();
        }

        logger.info("nodeDataList2: {}", nodeDataList);
        List<LinkData> vpcLinkData = vpcLink(nodeDataList, linkData);

        //어떤 그룹을 security group 묶을 건지
        Map<String, List<LinkData>> groupedByFrom = new HashMap<>();

        // 일단은 from 키를 묶어서 해결 -> 여기까지는 잘 들어가는 걸 확인
        for (LinkData link : vpcLinkData) {
            String fromKey = link.getFrom();
            groupedByFrom.putIfAbsent(fromKey, new ArrayList<>());
            // groupedByFrom에 From key에 해당하는 key 값이 있으면 연결
            if (link.getFrom().equals(fromKey)) {
                groupedByFrom.get(fromKey).add(link);
            }
        }
        // Security group 찾기 ( waf/ fw -> svr/ was ) 찾아서 security group으로 묶기
        // 일단 group을 null 이라고 해서 전체 데이터가 들어오게 하는 건 성공
        int index = 0;
        List<LinkData> addGroupList = new ArrayList<>();
        for (String key : groupedByFrom.keySet()) {
            if (key.contains("FW")) {
                List<LinkData> modifiedLinks = addGroup(groupedByFrom.get(key), index);
                addGroupList.addAll(modifiedLinks);
                index += 1;
            } else {
                addGroupList.addAll(groupedByFrom.get(key));
            }

        }
//        addGroupList = unique(addGroupList);

        List<Object> nodesToAdd = new ArrayList<>();
        for (Object node : nodeDataList) {
            // node
            if (node instanceof NodeData) {
                NodeData fixGroupData = (NodeData) node;
                for (LinkData linkdata : addGroupList) {
                    if (fixGroupData.getKey().equals(linkdata.getTo())
                            && linkdata.getGroup() != null) {

                        // security group 생생
                        GroupData groupNode = new GroupData();
                        groupNode.setIsGroup(true);
                        groupNode.setKey(linkdata.getGroup());
                        groupNode.setStroke("rgb(221,52,76)");
                        groupNode.setType("group");
                        groupNode.setGroup(fixGroupData.getGroup());

                        boolean nodeExistsInToAddList = nodesToAdd.stream()
                                .filter(n -> n instanceof GroupData)
                                .map(n -> (GroupData) n)
                                .anyMatch(existingNode -> existingNode.getKey().equals(groupNode.getKey()));
                        if (!nodeExistsInToAddList) {
                            nodesToAdd.add(groupNode);
                        }
                        fixGroupData.setGroup(linkdata.getGroup());

                        break;

                    }
                }
            } else if (node instanceof GroupData) {
                System.out.println("i am group " + ((GroupData) node).getKey());
            } else {
                logger.error("Unexpected object type in nodeDataList: {}", node.getClass().getName());
            }
        }
        nodeDataList.addAll(nodesToAdd);
        System.out.println("nodeDataList " + nodeDataList);
        System.out.println("addGroupList " + addGroupList);

        Map<String, List<LinkData>> groupedByVpc = new HashMap<>();

        for (LinkData link : addGroupList) {
            String vpcKey = link.getVpcgroup();
            groupedByVpc.putIfAbsent(vpcKey, new ArrayList<>());
            if (link.getVpcgroup().equals(vpcKey)) {
                groupedByVpc.get(vpcKey).add(link);
            }
        }

        List<LinkData> entireLogicList = new ArrayList<>();

        for (Map.Entry<String, List<LinkData>> entry : groupedByVpc.entrySet()) {
            List<LinkData> addLogicList = generateLinksFromToWS2(entry.getValue());
            entireLogicList.addAll(addLogicList);
        }
        System.out.println("entireLogicList "+entireLogicList);

        List<LinkData> nonGroupLink = unique(closeGroupLink(entireLogicList, nodeDataList));
        System.out.println("nonGroupLink " + nonGroupLink);

        List<LinkData> excludeLink = unique(excludeInstance(nonGroupLink, nodeDataList, addGroupList));
        System.out.println("excludeLink " + excludeLink);

        List<Object> modifiedNodeList = addNodeLogic(nodeDataList);
        System.out.println("modifiedNodeList " + modifiedNodeList);

        Map<String, Object> awsKey = fixKey(modifiedNodeList, excludeLink);

        result.put("nodeDataArray", awsKey.get("nodeDataArray"));
        result.put("linkDataArray", awsKey.get("linkDataArray"));
        result.put("addGroupList", addGroupList);
        return result;
    }

    private List<LinkData> excludeInstance(List<LinkData> LinkArray, List<Object> NodeDataArray, List<LinkData> addGroupList) {
        int index = 0;

        List<LinkData> result = new ArrayList<>();
        result.addAll(LinkArray);

        for (LinkData link : LinkArray) {
            String linkFrom = link.getFrom();
            String linkTo = link.getTo();

            if (linkTo.contains("IPS") || linkTo.contains("IDS")) {
                for (Object nextFrom : addGroupList) {
                    if (nextFrom instanceof LinkData && ((LinkData) nextFrom).getFrom().equals(linkTo)) {
                        for (Object nextnextFrom : addGroupList) {
                            if (nextnextFrom instanceof LinkData && ((LinkData) nextnextFrom).getFrom().equals(((LinkData) nextFrom).getTo())) {
                                linkTo = ((LinkData) nextnextFrom).getGroup();
                                if (linkTo == null) {
                                    continue;
                                } else {
                                    result.add(new LinkData(linkFrom, linkTo, index -= 1, "-1"));
                                }
                            }
                        }

                    }
                }
            } else {
                result.add(new LinkData(linkFrom, linkTo, index -= 1, "-1"));
            }

        }
        result.removeIf(linkData -> linkData.getFrom().contains("IPS")
                || linkData.getFrom().contains("IDS")
                || linkData.getTo().contains("IPS")
                || linkData.getTo().contains("IDS"));

        return result;
    }


    private List<LinkData> closeGroupLink(List<LinkData> LinkArray, List<Object> NodeDataArray) {
        int index = 0;

        List<LinkData> result = new ArrayList<>();
        for (LinkData link : LinkArray) {
            String linkFrom = link.getFrom();
            String linkTo = link.getTo();

            for (Object fromNode : NodeDataArray) {

                // fromNode == Node && toNode == Group
                if (fromNode instanceof NodeData && ((NodeData) fromNode).getKey().equals(linkFrom)) {
                    for (Object toNode : NodeDataArray) {
                        if (toNode instanceof GroupData && ((GroupData) toNode).getKey().equals(linkTo)) {
                            // 그룹 안의 노드가 다른 그룹을 가르키는 것을 막기 위해
                            if (((NodeData) fromNode).getGroup() != null) {
                                System.out.println("node to group "+new LinkData(linkFrom, linkTo, index -= 1, "-1"));
                                result.add(new LinkData(linkFrom, linkTo, index -= 1, "-1"));

                                break;
                            }
                        }
                    }
                }

                // fromNode is Group == true && toNode is Node
                else if (fromNode instanceof GroupData && ((GroupData) fromNode).getKey().equals(linkFrom)) {
                    for (Object toNode : NodeDataArray) {
                        if (toNode instanceof NodeData && ((NodeData) toNode).getKey().equals(linkTo)) {
                            if (((NodeData) toNode).getGroup().equals(((GroupData) fromNode).getKey())) {
                                System.out.println("group to node "+new LinkData(linkFrom, linkTo, index -= 1, "-1"));
                                result.add(new LinkData(linkFrom, linkTo, index -= 1, "-1"));
                                break;
                            }
                        }

                    }
                }

                // fromNode is Group && toNode is Group
                else if (fromNode instanceof GroupData && ((GroupData) fromNode).getKey().equals(linkFrom)) {
                    for (Object toNode : NodeDataArray) {
                        if (toNode instanceof GroupData && ((GroupData) toNode).getKey().equals(linkTo)) {

                        }
                    }
                }
            }
        }

        System.out.println("LinkArray "+LinkArray);
        List<LinkData> difference = new ArrayList<>(LinkArray);
        difference.removeAll(result);

        return difference;
    }

    // security group으로 무엇을 묶을 것인지 결정을 한다.
    // 일단 group을 null 이라고 해서 이렇게 값이 들어오게 하는건 성공
    public List<LinkData> addGroup(List<LinkData> linkData, int index) {
        for (LinkData data : linkData) {
            if (data.getTo().toLowerCase().startsWith("ws") || data.getTo().toLowerCase().startsWith("svr") || data.getTo().toLowerCase().startsWith("db")) {
                data.setGroup("SecurityGroup" + index);
            }
        }
        return linkData;  // 이제 linkData는 수정된 리스트입니다.
    }


    public List<LinkData> generateLinksFromToWS2(List<LinkData> originalList) {
        List<LinkData> resultList = new ArrayList<>();
        int index = 0;
        for (LinkData linkData : originalList) {

            String from = linkData.getFrom();
            String to = linkData.getTo();
            String nextTo = null;

            if (!to.startsWith("WS") || !to.startsWith("SVR") || !to.startsWith("RDS")) {
                for (LinkData nextLink : originalList) {
                    if (nextLink == linkData) continue;

                    if (linkData.getGroup() != null) {
                        from = linkData.getGroup();

                        if (!(linkData.getGroup().equals(nextLink.getGroup())) && nextLink.getGroup() != null) {
                            nextTo = nextLink.getGroup();
                            resultList.add(new LinkData(from, nextTo, index -= 1, "-1"));
                            break;
                        }
                    }

                    if (to.startsWith("FW") && nextLink.getFrom().equals(to)) {

                        if (nextLink.getGroup() != null) {
                            nextTo = nextLink.getGroup();
                        } else {
                            nextTo = nextLink.getTo(); // nextlink 다음으로 가기
                        }
                        if (linkData.getGroup() != null && nextLink.getGroup() != null) {
                            nextTo = null;
                        }
                    }
                    if (nextTo != null) {
                        resultList.add(new LinkData(from, nextTo, index -= 1, "-1"));
                    } else {
                        if (linkData.getGroup() != null && nextLink.getGroup() != null && !(linkData.getGroup().equals(nextLink.getGroup()))) {
                        } else
                            resultList.add(new LinkData(from, to, index -= 1, "-1"));

                    }

                }
            }
        }
        return resultList;
    }

    private boolean isIgnoredPrefix(String to) {
        return to.startsWith("WS") || to.startsWith("SVR") || to.startsWith("RDS");
    }

    private void addLink(List<LinkData> list, String from, String to, int index) {
        list.add(new LinkData(from, to, index--, "-1"));
    }

    public List<Object> addNodeLogic(List<Object> nodeDataList) {

        List<Object> NewNodeDataList = new ArrayList<>();
        for (Object data : nodeDataList) {
            if (data instanceof NodeData) {
                NodeData nodedata = (NodeData) data;
                String key = nodedata.getKey();
                if (!key.startsWith("Network") && !key.startsWith("FW") && !key.startsWith("WAF")) {
                    NewNodeDataList.add(nodedata);
                }
            }
            if (data instanceof GroupData) {
                NewNodeDataList.add(data);
            }
        }
        return NewNodeDataList;
    }

    public List<LinkData> unique(List<LinkData> modifiedLinkDataList) {
        Set<Pair> seen = new HashSet<>();
        List<LinkData> uniquelink = new ArrayList<>();
        int i = 0;
        for (LinkData linkdata : modifiedLinkDataList) {
            Pair pair = new Pair(linkdata.getFrom(), linkdata.getTo());
            if (!seen.contains(pair)) {
                uniquelink.add(new LinkData(linkdata.getFrom(), linkdata.getTo(), i -= 1, "-1"));
                seen.add(pair);

            }
        }
        return uniquelink;
    }

    // Inner Pair class to represent a pair of from and to strings
    private static class Pair {
        private final String from;
        private final String to;

        public Pair(String from, String to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pair pair = (Pair) o;
            return Objects.equals(from, pair.from) &&
                    Objects.equals(to, pair.to);
        }

        @Override
        public int hashCode() {
            return Objects.hash(from, to);
        }

    }

    public Map<String, Object> fixKey(List<Object> modifiedNodeList, List<LinkData> unique) {
        Map<String, Object> fixkey = new HashMap<>();
        Map<String, String> Awsnode = new HashMap<>();
        int EC2_index = 0;
        int RDS_index = 0;
        int Shield_index = 0;

        for (Object node : modifiedNodeList) {
            if (node instanceof NodeData) {
                NodeData nodedata = (NodeData) node;
                String key = nodedata.getKey();

                String newKey = null;
                if (key.contains("DB")) {
                    newKey = "RDS" + RDS_index++;
                } else if (key.contains("WS") || key.contains("SVR")) {
                    newKey = "EC2" + EC2_index++;
                } else if (key.contains("AD")) {
                    newKey = "Shield" + Shield_index++;
                }

                if (newKey != null) {
                    if (Awsnode.containsKey(key)) {
                        nodedata.setKey(Awsnode.get(key));
                    } else {
                        nodedata.setKey(newKey);
                        Awsnode.put(key, newKey);
                    }
                }
            }
        }

        logger.info("nodeDataList444: {}", modifiedNodeList);

        for (LinkData link : unique) {
            if (Awsnode.containsKey(link.getFrom())) {
                link.setFrom(Awsnode.get(link.getFrom()));
            }
            if (Awsnode.containsKey(link.getTo())) {
                link.setTo(Awsnode.get(link.getTo()));
            }
        }

        fixkey.put("linkDataArray", unique);
        fixkey.put("nodeDataArray", modifiedNodeList);

        return fixkey;
    }

    public List<LinkData> vpcLink(List<Object> nodeDataList, List<LinkData> linkData) {

        List<LinkData> vpcLinkData = new ArrayList<>();
        for (Object node : nodeDataList) {
            for (LinkData link : linkData) {
                String from = link.getFrom();
                String to = link.getTo();
                int key = link.getKey();
                if (node instanceof NodeData) {
                    if (link.getFrom().equals(((NodeData) node).getKey())) {
                        LinkData newLink = new LinkData(link.getFrom(), link.getTo(), ((NodeData) node).getGroup(), link.getKey());
                        vpcLinkData.add(newLink);
                    }
                    if (link.getTo().equals(((NodeData) node).getKey())) {
                        LinkData newLink = new LinkData(link.getFrom(), link.getTo(), ((NodeData) node).getGroup(), link.getKey());
                        vpcLinkData.add(newLink);
                    }
                }
            }


        }

        return vpcLinkData;

    }
}
