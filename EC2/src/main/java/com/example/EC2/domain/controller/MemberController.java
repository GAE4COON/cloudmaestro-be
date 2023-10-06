package com.example.EC2.controller;

import com.example.EC2.dto.ApiNameRequest;
import com.example.EC2.dto.MemberDTO;
import com.example.EC2.dto.SplitApiNRequest;
import com.example.EC2.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ec2")
@CrossOrigin(origins="http://localhost:3000")
public class MemberController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    private MemberService memberService;



    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }
//똥
    @GetMapping("/all")
    public List<MemberDTO> getAllMembers() {
        List<MemberDTO> members = memberService.getAllMembers();
        logger.info("Fetched members: {}", members);
        return members;
    }

    public List<MemberDTO> getMembersByAPIName(@PathVariable("APIName") String APIName) {
        List<MemberDTO> members = memberService.getMembersByAPIName(APIName);
        logger.info("Fetched members by name '{}': {}", APIName, members);
        return members;
    }

    @PostMapping(value = "/platform", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<MemberDTO> getMembersByAPIName(@RequestBody ApiNameRequest request) {
        List<MemberDTO> allMembers;

        if(request.getInstanceType() == null) {
            // Only platform provided
            allMembers = memberService.getMembersByAPIName(request.getPlatform());
            logger.info("Fetched members by platform '{}': {}", request.getPlatform(), allMembers);
        } else {
            // Both platform and instanceType provided
            allMembers = memberService.getMembersByAPINameAndInstanceType(request.getPlatform(), request.getInstanceType());
            logger.info("Fetched members by platform '{}' and instanceType '{}': {}", request.getPlatform(), request.getInstanceType(), allMembers);
        }

        return allMembers;
    }

    @PostMapping(value = "/apiname", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getMembersByAPIName(@RequestBody SplitApiNRequest request) {
        List<String> splitAPIname = null;

        if(request.getInstanceType() == null) {
            // Only platform provided
            splitAPIname = memberService.getSplitByAPIName(request.getPlatform());
            logger.info("Fetched members by platform '{}': {}", request.getPlatform(), splitAPIname);
        } else {
            splitAPIname = memberService.getSplitByAPINameAndInstanceType(request.getPlatform(), request.getInstanceType());
            logger.info("Fetched members by platform '{}': {}", request.getPlatform(), splitAPIname);
        }



        return splitAPIname;
    }



}
