package com.gae4coon.cloudmaestro.domain.ec2.controller;

import com.gae4coon.cloudmaestro.domain.ec2.dto.ApiNameRequest;
import com.gae4coon.cloudmaestro.domain.ec2.dto.MemberDTO;

import com.gae4coon.cloudmaestro.domain.ec2.service.NodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/db-api")
public class NodeController {

    private static final Logger logger = LoggerFactory.getLogger(NodeController.class);

    private NodeService nodeService;



    @Autowired
    public NodeController(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @GetMapping("/all")
    public List<MemberDTO> getAllMembers() {
        List<MemberDTO> members = nodeService.getAllMembers();
        logger.info("Fetched members: {}", members);
        return members;
    }

    public List<MemberDTO> getMembersByAPIName(@PathVariable("APIName") String APIName) {
        List<MemberDTO> members = nodeService.getMembersByAPIName(APIName);
        logger.info("Fetched members by name '{}': {}", APIName, members);
        return members;
    }


    @PostMapping(value = "/ec2", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getMembersByAPIName(@RequestBody ApiNameRequest request) {
        List<String> splitAPIname = null;
        logger.info("hello world '{}': {}", request.getPlatform(), request.getInstanceType());
        if(request.getInstanceType() == null) {
            // Only platform provided
            splitAPIname = nodeService.getSplitByAPIName(request.getPlatform());
            logger.info("Fetched members by platform '{}': {}", request.getPlatform(), splitAPIname);
        } else {
            splitAPIname = nodeService.getSplitByAPINameAndInstanceType(request.getPlatform(), request.getInstanceType());
            logger.info("Fetched members by instance '{}': {}", request.getInstanceType(), splitAPIname);
        }



        return splitAPIname;
    }


}
