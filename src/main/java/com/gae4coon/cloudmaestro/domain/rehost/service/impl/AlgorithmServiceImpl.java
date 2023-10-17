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

@Service
public class AlgorithmServiceImpl implements AlgorithmServiceInterface {
    // 방화벽 밑에 1단을 기준으로 security group을 묶는다.

    private static final Logger logger = LoggerFactory.getLogger(AlgorithmServiceImpl.class);

    @Override
    public Map<String, Object> algorithmDataList(Map<String, Object> nodesData, List<LinkData> linkData) {


        // 1. security group으로 묶기
        // nodeData로 캐스팅하기
        ObjectMapper mapper = new ObjectMapper();
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


        //어떤 그룹을 security group 묶을 건지
        Map<String, List<LinkData>> groupedByFrom = new HashMap<>();

        // 일단은 from 키를 묶어서 해결 -> 여기까지는 잘 들어가는 걸 확인
        for (LinkData link : linkData) {
            String fromKey = link.getFrom();
            groupedByFrom.putIfAbsent(fromKey, new ArrayList<>());
            groupedByFrom.get(fromKey).add(link);
        }

        // Security group 찾기 ( waf/ fw -> svr/ was ) 찾아서 security group으로 묶기
        // 일단 group을 null 이라고 해서 전체 데이터가 들어오게 하는 건 성공
        int index = 0;
        List<LinkData> addGroupList = new ArrayList<>();
        for (String key : groupedByFrom.keySet()) {
            if (key.contains("FW") || key.contains("WAF")) {
                List<LinkData> modifiedLinks = addGroup(groupedByFrom.get(key), index);
                addGroupList.addAll(modifiedLinks);
                index += 1;
            } else {
                addGroupList.addAll(groupedByFrom.get(key));
            }

        }


        // Step2 : linkDataArray를 순회하면서 해당 LinkData를 NodeData에 연결
        //addGroupList (즉, LinkData 리스트)의 각 항목마다 to 필드 값을 확인
        //해당 to 값이 nodesData (즉, NodeData 리스트)의 어떤 항목의 key 값과 일치하는 지 확인
        //만약 일치한다면, 해당 NodeData 항목의 group 값을 LinkData의 group 값으로 변경

        // NodesData - securityGroup도 여기서 묶자
        List<Object> nodesToAdd = new ArrayList<>();
        for (Object obj : nodeDataList) {
            if (obj instanceof NodeData) {
                NodeData fixGroupData = (NodeData) obj;
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
            } else {
                // Error handling: obj is not an instance of NodeData
                logger.error("Unexpected object type in nodeDataList: {}", obj.getClass().getName());
            }
        }

        nodeDataList.addAll(nodesToAdd);

        // 이제부터는 새로운 알고리즘 시작 ..하하 ( 연결고리 정보 건들기 )
        // 2. linkDataArray 정보를 활용해서 연결 고리 정보를 확인합시다

        // ips / ids 는 따로 아이콘으로 빼기
        //  fw / network_waf는 빼야 한다.

        // currentlist.to = nextlist.from 이 같은 거 연결
        System.out.println("addGroupList" + addGroupList);

        List<LinkData> link1 = generateLinksFromToWS2(addGroupList);
        System.out.println("link1 " + unique(link1));

        // group link 끊기
        List<LinkData> nonGroupLink = closeGroupLink(unique(link1), nodeDataList);
        System.out.println("nonGroupLink " + nonGroupLink);

        // node -> exclude되야할 node 연결되어있으면 nextnode로 연결
        List<LinkData> excludeLink = excludeInstance(nonGroupLink, nodeDataList, addGroupList);

        List<LinkData> unique = unique(excludeLink);

        List<Object> modifiedNodeList = addNodeLogic(nodeDataList);

        Map<String, Object> awsKey = fixKey(modifiedNodeList, unique);

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

            if(linkTo.contains("IPS")||linkTo.contains("IDS")) {

                for (Object nextFrom : addGroupList) {

                    if (nextFrom instanceof LinkData && ((LinkData) nextFrom).getFrom().equals(linkTo)){
                        System.out.println("from "+linkFrom+" to "+linkTo);
                        for (Object nextnextFrom : addGroupList) {
                            if (nextnextFrom instanceof LinkData && ((LinkData) nextnextFrom).getFrom().equals(((LinkData) nextFrom).getTo())) {
                                linkTo = ((LinkData) nextnextFrom).getGroup();
                                System.out.println("from "+linkFrom+" to "+linkTo);

                                result.add(new LinkData(linkFrom, linkTo, index -= 1, "-1"));
                            }
                        }

                    }
                }
            }

        }
        result.removeIf(linkData -> linkData.getFrom().contains("IPS")
                || linkData.getFrom().contains("IDS")
                || linkData.getTo().contains("IPS")
                || linkData.getTo().contains("IDS"));

        System.out.println("result "+result);
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
                            System.out.println("from " + ((NodeData) fromNode).getKey() + " to " + ((GroupData) toNode).getKey() + " from group " + ((NodeData) fromNode).getGroup() + " to group " + ((GroupData) toNode).getGroup());

                            // 그룹 안의 노드가 다른 그룹을 가르키는 것을 막기 위해
                            if (((NodeData) fromNode).getGroup() != null) {
                                System.out.println("if (((NodeData) fromNode).getGroup() != null && ((NodeData) toNode).getIsGroup().equals(\"true\")) {\n");
                                result.add(new LinkData(linkFrom, linkTo, index -= 1, "-1"));

                                break;
                            }
                            System.out.println("add " + new LinkData(linkFrom, linkTo, index -= 1, "-1"));
                        }

                    }

                }

                // fromNode is Group == true && toNode is Node
                else if (fromNode instanceof GroupData && ((GroupData) fromNode).getKey().equals(linkFrom)) {
                    for (Object toNode : NodeDataArray) {
                        if (toNode instanceof NodeData && ((NodeData) toNode).getKey().equals(linkTo)) {
                            System.out.println("!from " + ((GroupData) fromNode).getKey() + " to " + ((NodeData) toNode).getKey() + " from group " + ((GroupData) fromNode).getGroup() + " to group " + ((NodeData) toNode).getGroup());


                            if (((NodeData) toNode).getGroup().equals(((GroupData) fromNode).getKey())) {
                                System.out.println("else if(((NodeData) fromNode).getIsGroup().equals(\"true\")&&((NodeData) toNode).getGroup().equals(((NodeData) fromNode).getKey())){\n");
                                result.add(new LinkData(linkFrom, linkTo, index -= 1, "-1"));

                                break;
                            }
                            System.out.println("add " + new LinkData(linkFrom, linkTo, index -= 1, "-1"));
                        }

                    }
                }
            }
        }

        List<LinkData> difference = new ArrayList<>(LinkArray);
        difference.removeAll(result);

        System.out.println("final result2 " + LinkArray);
        System.out.println("final result " + result);
        System.out.println("diff " + difference);
        return difference;
    }


    @Override
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


    @Override
    public List<LinkData> generateLinksFromToWS(List<LinkData> originalList) {
        List<LinkData> resultList = new ArrayList<>();
        int index = 0;
        for (LinkData linkData : originalList) {

            String from = linkData.getFrom();
            String to = linkData.getTo();
            String nextFrom = to;

            // While the 'to' doesn't start with "WS", we try to find the next link
            while (!to.startsWith("WS") || !to.startsWith("SVR") || !to.startsWith("RDS")) {
                boolean linkFound = false;

                for (LinkData nextLink : originalList) {
                    if (nextLink.getFrom().equals(to)) {
                        to = nextLink.getTo(); // nextlink 다음으로 가기
                        nextFrom = nextLink.getFrom(); // nextlink 처음으로 가기
                        linkFound = true;
                        break;
                    }
                }

                if (!linkFound) break;
            }
            System.out.println("new LinkData(from, nextFrom, index -= 1)" + new LinkData(from, nextFrom, index -= 1, "-1"));
            resultList.add(new LinkData(from, nextFrom, index -= 1, "-1"));
        }


        return resultList;
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
                    }
                    else {
                        if (linkData.getGroup() != null && nextLink.getGroup() != null && !(linkData.getGroup().equals(nextLink.getGroup()))) {
                        } else
                            resultList.add(new LinkData(from, to, index -= 1, "-1"));

                    }

                }
            }
        }

        return resultList;
    }


    @Override
    public List<LinkData> addLogic(List<Object> nodeDataList, List<LinkData> originalList) {
        System.out.println("nodeDataList" + nodeDataList);
        for (var nodeData : nodeDataList) {
            if (nodeData instanceof NodeData) {
                ((NodeData) nodeData).getGroup();
            }
        }

        List<LinkData> resultList = new ArrayList<>();
        int index = 0;
        for (LinkData original : originalList) {
            String from = original.getFrom();
            String to = original.getTo();
            System.out.println("from " + from + " to " + to + " group " + original.getGroup());

            for (var nodeData : nodeDataList) {
                if (nodeData instanceof NodeData) {
                    if (((NodeData) nodeData).getKey().equals(to)) {
                        System.out.println("from " + from + " to " + to + " group " + ((NodeData) nodeData).getGroup());

                        if (((NodeData) nodeData).getGroup().equals(from)) {
                            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

                            break;
                        } else if (((NodeData) nodeData).getGroup().equals("-1")) break;

                        resultList.add(new LinkData(from, to, index -= 1, "-1"));
                    }
                }
            }


        }
        System.out.println("resultList" + resultList);
        return resultList;
    }


    @Override
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


    //    public List<LinkData> unique(List<LinkData> modifiedLinkDataList) {
//
//        Set<List<String>> set = new HashSet<>();
//        Map<String, LinkData> temp = new HashMap<>();
//        List<List> templist = new ArrayList<>();
//
//        List<LinkData> uniquelink = new ArrayList<>();
//        for (LinkData linkdata : modifiedLinkDataList) {
//            List<String> list = new LinkedList<>();
//            list.add(linkdata.getFrom());
//            list.add(linkdata.getTo());
//            templist.add(list);
//        }
//
//        for (List tempdata : templist) {
//            set.add(tempdata);
//        }
//        int i = 0;
//        for (List<String> data : set) {
//            uniquelink.add(new LinkData(data.get(0), data.get(1), i -= 1));
//
//        }
//
//        return uniquelink;
//
//    }
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
        Map<String, String> Awsnode = new HashedMap<>();
        int EC2_index = 0;
        int RDS_index = 0;
        int Shield_index = 0;
        int Rds_index = 0;

        for (Object node : modifiedNodeList) {

            if (node instanceof NodeData) {
                NodeData nodedata = (NodeData) node;
                String key = nodedata.getKey();
                if (key.contains("DB")) {
                    if (Awsnode.containsKey(key)) {
                        nodedata.setKey(Awsnode.get(key));
                    } else {
                        nodedata.setKey("RDS" + RDS_index);
                        RDS_index += 1;
                        Awsnode.put(key, nodedata.getKey());
                    }

                }
                if (key.contains("WS")) {
                    if (Awsnode.containsKey(key)) {
                        nodedata.setKey(Awsnode.get(key));
                    } else {
                        nodedata.setKey("EC2" + EC2_index);
                        EC2_index += 1;
                        Awsnode.put(key, nodedata.getKey());

                    }
                }
                if (key.contains("SVR")) {
                    if (Awsnode.containsKey(key)) {
                        nodedata.setKey(Awsnode.get(key));
                    } else {
                        nodedata.setKey("EC2" + EC2_index);
                        EC2_index += 1;
                        Awsnode.put(key, nodedata.getKey());

                    }
                }
                if (key.contains("AD")) {
                    if (Awsnode.containsKey(key)) {
                        nodedata.setKey(Awsnode.get(key));
                    } else {
                        nodedata.setKey("Shield" + Shield_index);
                        Shield_index += 1;
                        Awsnode.put(key, nodedata.getKey());

                    }
                }
                if (key.contains("DB")) {
                    if (Awsnode.containsKey(key)) {
                        nodedata.setKey(Awsnode.get(key));
                    } else {
                        nodedata.setKey("RDS" + Rds_index);
                        Rds_index += 1;
                        Awsnode.put(key, nodedata.getKey());

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
}









