package com.example.EC2.controller;

import com.example.EC2.dto.MemberDTO;
import com.example.EC2.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
