package com.gae4coon.cloudmaestro.domain.rds.controller;

import com.gae4coon.cloudmaestro.domain.rds.service.dbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class dbController {

    @Autowired
    private dbService dbConnectService;
    @PostMapping("/db-api/rds")
    public Map<String, List<String>> getDbData(@RequestBody Map<String, String> payload) {
        String engine = payload.get("engine");
        //System.out.println("엔진" + engine);
        Map<String, List<String>> dbinfo = dbConnectService.getAvailableApiNamesForField(engine);
        //System.out.println(result);
        return dbinfo;
    }
}