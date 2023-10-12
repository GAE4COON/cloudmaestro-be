package com.gae4coon.cloudmaestro.domain.rds.controller;

import com.gae4coon.cloudmaestro.domain.rds.dto.rdsCostRequest;
import com.gae4coon.cloudmaestro.domain.rds.service.rdsCostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@RestController
public class costController {
    @Autowired
    private rdsCostService rdsPricingService;

    @PostMapping("/pricing-api/rds")
    public ResponseEntity<String> handleRdsRequest(@RequestBody rdsCostRequest request) throws Exception {
        //System.out.println("Received dbEngine: " + request.getDbEngine()+"\nReceived instanceType: " + request.getInstanceType());

        String dbPrice = rdsPricingService.getRdsAwsPricing(request.getDbEngine(), request.getInstanceType());

        return new ResponseEntity<>(dbPrice, HttpStatus.OK);
    }
}