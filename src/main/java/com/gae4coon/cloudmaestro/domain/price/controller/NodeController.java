package com.gae4coon.cloudmaestro.domain.price.controller;

import com.gae4coon.cloudmaestro.domain.price.dto.ApiNameRequest;
import com.gae4coon.cloudmaestro.domain.price.dto.MemberDTO;

import com.gae4coon.cloudmaestro.domain.price.service.NodeService;
import com.gae4coon.cloudmaestro.domain.price.service.dbService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/db-api")
@RequiredArgsConstructor

public class NodeController {

    private NodeService nodeService;

    @Autowired
    private dbService dbConnectService;


    @GetMapping("/all")
    public List<MemberDTO> getAllMembers() {
        List<MemberDTO> members = nodeService.getAllMembers();
        return members;
    }

    @PostMapping(value = "/ec2", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getMembersByAPIName(@RequestBody ApiNameRequest request) {
        List<String> splitAPIname = null;
        if(request.getInstanceType() == null) {
            // Only platform provided
            splitAPIname = nodeService.getSplitByAPIName(request.getPlatform());
        } else {
            splitAPIname = nodeService.getSplitByAPINameAndInstanceType(request.getPlatform(), request.getInstanceType());
        }
        return splitAPIname;
    }
    @PostMapping("/rds")
    public Map<String, List<String>> getDbData(@RequestBody Map<String, String> payload) {
        String engine = payload.get("engine");
        //System.out.println("엔진" + engine);
        Map<String, List<String>> dbinfo = dbConnectService.getAvailableApiNamesForField(engine);
        //System.out.println(result)11;
        return dbinfo;
    }

}
