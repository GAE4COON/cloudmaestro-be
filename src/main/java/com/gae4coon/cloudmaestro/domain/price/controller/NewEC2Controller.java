package com.gae4coon.cloudmaestro.domain.price.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.gae4coon.cloudmaestro.domain.price.dto.PriceResult;
import com.gae4coon.cloudmaestro.domain.price.dto.requestPrice;
import com.gae4coon.cloudmaestro.domain.price.service.NewEC2Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pricing-api")
public class NewEC2Controller {

    private NewEC2Service newEC2Service;
    @Autowired
    public NewEC2Controller(NewEC2Service newEC2Service) {
        this.newEC2Service = newEC2Service;
    }
    private final Logger logger = LoggerFactory.getLogger(NewEC2Controller.class);

    @PostMapping("/ec2")
    public PriceResult getEc2Pricing(@RequestBody requestPrice request){


        ObjectMapper mapper = new ObjectMapper();

        PriceResult ec2info= newEC2Service.getPrice(request.getPlatform());


        logger.info("price" + ec2info);

        return ec2info;


    }






}
