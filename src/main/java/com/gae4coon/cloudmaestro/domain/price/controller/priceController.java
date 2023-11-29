package com.gae4coon.cloudmaestro.domain.price.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.gae4coon.cloudmaestro.domain.price.dto.PriceResult;
import com.gae4coon.cloudmaestro.domain.price.dto.rdsCostRequest;
import com.gae4coon.cloudmaestro.domain.price.dto.requestPrice;
import com.gae4coon.cloudmaestro.domain.price.service.NewEC2Service;
import com.gae4coon.cloudmaestro.domain.price.service.rdsCostService;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pricing-api")
@RequiredArgsConstructor

public class priceController {

    private final NewEC2Service newEC2Service;
    private final Logger logger = LoggerFactory.getLogger(priceController.class);

    @PostMapping("/ec2")
    public PriceResult getEc2Pricing(@RequestBody requestPrice request){


        ObjectMapper mapper = new ObjectMapper();

        PriceResult ec2info= newEC2Service.getPrice(request.getPlatform());


        logger.info("price" + ec2info);

        return ec2info;


    }


    @Autowired
    private rdsCostService rdsPricingService;

    @PostMapping("/rds")
    public ResponseEntity<String> handleRdsRequest(@RequestBody rdsCostRequest request) throws Exception {
        //System.out.println("Received dbEngine: " + request.getDbEngine()+"\nReceived instanceType: " + request.getInstanceType());

        String dbPrice = rdsPricingService.getRdsAwsPricing(request.getDbEngine(), request.getInstanceType());

        return new ResponseEntity<>(dbPrice, HttpStatus.OK);
    }
}
