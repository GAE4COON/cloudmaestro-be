package com.gae4coon.cloudmaestro.domain.rehost.controller;

import com.gae4coon.cloudmaestro.domain.rehost.service.RehostService;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/api/v1/rehost-api")
@RestController
@RequiredArgsConstructor
public class rehostController {

    private final RehostService rehostService;
    @PostMapping("/rehost")
    public ResponseEntity<String> rehostpage(@RequestBody String request) throws ParseException {
        rehostService.parsing(request);
        return ResponseEntity.ok().body("haha");
    }
}
