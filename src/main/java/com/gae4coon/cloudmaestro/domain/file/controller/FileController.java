package com.gae4coon.cloudmaestro.domain.file.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gae4coon.cloudmaestro.domain.file.dto.InputData;
import com.gae4coon.cloudmaestro.domain.file.dto.NodeData;
import com.gae4coon.cloudmaestro.domain.file.dto.OutputData;
import com.gae4coon.cloudmaestro.domain.file.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


@RestController
@RequestMapping("/api/v1/file-api")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping(value = "/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        String fileType = file.getContentType();

        if (fileType.equals("application/json")) {
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            return ResponseEntity.ok(content);
        }
        // excel to json
        List<Map<String, String>> data = fileService.excelToJson(file.getInputStream());

        // json to input data
        return ResponseEntity.ok(fileService.dataToinput(data));
    }

    @GetMapping(value = "/example/{order}")
    public ResponseEntity<?> exampleFile(@PathVariable String order) {
        String fileName = "example" + order + ".json";
        Resource resource = new ClassPathResource("static/" + fileName);
        return ResponseEntity.ok(resource);
    }

    @PostMapping(value = "/summary")
    public ResponseEntity<?> summaryFile(@RequestParam("file") MultipartFile file) throws IOException {
        System.out.println(file);
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty.");
        }

        try {
            // Read the content of the uploaded file into a string
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);

            // Parse the JSON string into a Map<String, Object>
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> jsonData = objectMapper.readValue(content, new TypeReference<Map<String, Object>>() {
            });

            // 변환된 데이터를 담을 결과 맵
            Map<String, Object> response = new HashMap<>();
            Map<String, Object> costMap = (Map<String, Object>) jsonData.get("cost");

            // "compute" 부분 변환
            Map<String, Object> compute = new HashMap<>();
            System.out.println("key!!!!!!!!"+costMap.keySet());
            for (String key : costMap.keySet()) {
                if (key.startsWith("EC2")) {
                    compute.put(key, costMap.get(key));
                }
            }
            response.put("compute", compute);

            // "database" 부분 변환
            Map<String, Object> database = new HashMap<>();
            for (String key : costMap.keySet()) {
                if (key.startsWith("RDS")) {
                    database.put(key, costMap.get(key));
                }
            }
            response.put("database", database);

            // "storage" 부분 변환
            Map<String, Object> storage = new HashMap<>();
            for (String key : costMap.keySet()) {
                if (key.startsWith("Simple Storage Service")) {
                    storage.put(key, costMap.get(key));
                }
            }
            response.put("storage", storage);

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            // Handle exceptions such as IO errors or JSON parsing errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the file: " + e.getMessage());
        }
    }


    private OutputData transformData(InputData inputData) {
        // 변환 로직 구현
        Map<String, Object> compute = new HashMap<>();
        Map<String, Object> database = new HashMap<>();
        Map<String, Object> storage = new HashMap<>();

        for (NodeData nodeData : inputData.getNodeDataArray()) {
            Map<String, Object> matchedCost = null;
            for (Map<String, Object> costItem : inputData.getCost()) {
                if (costItem.containsKey(nodeData.getKey())) {
                    matchedCost = (Map<String, Object>) costItem.get(nodeData.getKey());
                    break;
                }
            }

            if (matchedCost == null) {
                continue;
            }

            if ("Compute".equals(nodeData.getType())) {
                Map<String, Object> comDetails = new HashMap<>();
                if (matchedCost.containsKey("platform")) {
                    comDetails.put("platform", matchedCost.get("platform"));
                }
                if (matchedCost.containsKey("instancetype")) {
                    comDetails.put("instancetype", matchedCost.get("instancetype"));
                }
                if (matchedCost.containsKey("size")) {
                    comDetails.put("size", matchedCost.get("size"));
                }
                if (matchedCost.containsKey("billing")) {
                    comDetails.put("billing", matchedCost.get("billing"));
                }
                if (matchedCost.containsKey("cost")) {
                    comDetails.put("cost", matchedCost.get("cost"));
                }
                compute.put(nodeData.getKey(), comDetails);

            } else if ("Database".equals(nodeData.getType())) {
                Map<String, Object> dbDetails = new HashMap<>();
                if (matchedCost.containsKey("engine")) {
                    dbDetails.put("engine", matchedCost.get("engine"));
                }
                if (matchedCost.containsKey("instancetype")) {
                    dbDetails.put("instancetype", matchedCost.get("instancetype"));
                }
                if (matchedCost.containsKey("size")) {
                    dbDetails.put("size", matchedCost.get("size"));
                }
                if (matchedCost.containsKey("cost")) {
                    dbDetails.put("cost", matchedCost.get("cost"));
                }
                // Add other attributes if needed...

                database.put(nodeData.getKey(), dbDetails);

            } else if ("Storage".equals(nodeData.getType())) {
                storage.put(nodeData.getKey(), matchedCost);
            }
        }

        OutputData outputData = new OutputData();
        outputData.setCompute(compute);
        outputData.setDatabase(database);
        outputData.setStorage(storage);

        return outputData;

    }


}
