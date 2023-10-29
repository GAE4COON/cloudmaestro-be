package com.gae4coon.cloudmaestro.domain.file.controller;

import com.gae4coon.cloudmaestro.domain.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


@RestController
@RequestMapping("/api/v1/file-api")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping(value = "/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        String fileType = file.getContentType();
        System.out.println("upload file type"+fileType);

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
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty.");
        }
        return ResponseEntity.ok(fileService.summaryFileParse(file));
    }
}
