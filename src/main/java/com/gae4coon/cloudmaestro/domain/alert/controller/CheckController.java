package com.gae4coon.cloudmaestro.domain.alert.controller;

import com.gae4coon.cloudmaestro.domain.alert.service.DiagramCheckService;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/alert-api")
@RequiredArgsConstructor
public class CheckController {

    private final DiagramCheckService diagramCheckService;

    @PostMapping("/alert-check")
    public ResponseEntity<HashMap<String, Object>> alertCheck(@RequestBody LinkData postData) {
//        if(bindingResult.hasErrors()){
//            return new ResponseEntity(HttpStatus.BAD_REQUEST);
//        }
        HashMap result = new HashMap<>();
        try {

        HashMap ResponseMap = diagramCheckService.linkCheck(postData);
        result.put("result", ResponseMap);
        return ResponseEntity.ok().body(result);

        }catch (Exception e){
            result.put("result", "error");
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.ok().body(result);
        }
    }

}
