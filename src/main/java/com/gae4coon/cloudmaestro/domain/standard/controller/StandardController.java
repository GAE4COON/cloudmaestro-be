package com.gae4coon.cloudmaestro.domain.standard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gae4coon.cloudmaestro.domain.standard.dto.GraphLinksModel;
import com.gae4coon.cloudmaestro.domain.standard.dto.LinkData;
import com.gae4coon.cloudmaestro.domain.standard.dto.NodeData;
//import com.gae4coon.cloudmaestro.domain.standard.service.AlgorithmServiceInterface;
import com.gae4coon.cloudmaestro.domain.standard.service.StandardServiceInterface;
import com.gae4coon.cloudmaestro.domain.standard.service.impl.AlgorithmServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/file-api/")
@RequiredArgsConstructor
@Log4j2
public class StandardController {
    private final StandardServiceInterface standardService;
    private final AlgorithmServiceImpl algorithmService;
//    public ResponseEntity<HashMap<String, Object> > postNetworkData(@RequestBody(required = false) String postData) {
    @PostMapping("/network")
    public ResponseEntity postNetworkData(@RequestBody(required = false) String postData) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            //System.out.println("postData: "+postData);
            GraphLinksModel graphData = mapper.readValue(postData, GraphLinksModel.class);
            List<NodeData> nodesData = graphData.getNodeDataArray();
            List<LinkData> linkData = graphData.getLinkDataArray();



            // 정말 단순한 일대일 대응
            Map<String, Object> hoho= standardService.processNodeData(nodesData, linkData);


            Map<String, Object> result = algorithmService.algorithmDataList(hoho.get("nodeDataArray"), hoho.get("linkDataArray"));

            result.put("class","GraphLinksModel");
            result.put("linkKeyProperty","key");
//
//          Map<String, Object> responseBody = new HashMap<>();
//
//            responseBody.put("class","GraphLinksModel");
//            responseBody.put("linkKeyProperty","key");
//            responseBody.put("nodeDataArray",finalData.get("nodeDataArray"));
//            responseBody.put("linkDataArray", finalData.get("linkDataArray"));
//
//            HashMap<String, Object> finalBody = new HashMap<>();
//            finalBody.put("result",responseBody);
//            System.out.println("finalBody: "+finalBody);

            return ResponseEntity.ok().body(result);//.body(finalBody);

        } catch (Exception e) {
            ObjectNode errorNode = mapper.createObjectNode();
            errorNode.put("error", e.getMessage());
            System.out.println("error:"+ e.getMessage());

            HashMap<String, Object> ErrorBody = new HashMap<>();
            ErrorBody.put("result","fail");
            return ResponseEntity.ok().body(ErrorBody);
        }
    }


}



