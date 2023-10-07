package com.example.EC2.domain.controller;

import com.example.EC2.domain.dto.requestPrice;
import com.example.EC2.domain.service.EC2Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vantage")
public class EC2Controller {

    private final EC2Service ec2Service;

    @Autowired
    public EC2Controller(EC2Service ec2Service) {
        this.ec2Service = ec2Service;
    }

    @PostMapping("/ec2")
    public ResponseEntity<JsonNode> getVantagePricing(@RequestBody requestPrice request){
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode response = ec2Service.getVantagePricing(request.getPlatform(), request.getInstance(), request.getLifeCycle());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {

            ObjectNode errorNode = mapper.createObjectNode();
            errorNode.put("error", e.getMessage());

            return new ResponseEntity<>(errorNode, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
