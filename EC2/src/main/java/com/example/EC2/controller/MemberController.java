package com.example.EC2.controller;

import com.example.EC2.dto.ApiNameRequest;
import com.example.EC2.dto.MemberDTO;
import com.example.EC2.entity.MemberEntity;
import com.example.EC2.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import lombok.Data;

@RestController
@RequestMapping("/")
public class MemberController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    private MemberService memberService;



    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping(value = "/entities",produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MemberDTO> getAllMembers() {
        List<MemberDTO> members = memberService.getAllMembers();
        logger.info("Fetched members: {}", members);
        return members;
    }

    @GetMapping(value = "/entities/{APIName}",produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MemberDTO> getMembersByAPIName(@PathVariable("APIName") String APIName) {
        List<MemberDTO> members = memberService.getMembersByAPIName(APIName);
        logger.info("Fetched members by name '{}': {}", APIName, members);
        return members;
    }

    @PostMapping(value = "/entities", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<MemberDTO> getMembersByAPIName(@RequestBody ApiNameRequest request) {
        List<MemberDTO> members = memberService.getMembersByAPIName(request.getAPIName());
        logger.info("Fetched members by APIName '{}': {}", request.getAPIName(), members);
        return members;
    }

}
