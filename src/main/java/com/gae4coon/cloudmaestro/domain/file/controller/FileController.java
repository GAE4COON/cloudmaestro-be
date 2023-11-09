package com.gae4coon.cloudmaestro.domain.file.controller;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gae4coon.cloudmaestro.domain.file.service.FileService;
import com.gae4coon.cloudmaestro.domain.file.service.S3Service;
import com.gae4coon.cloudmaestro.domain.file.testDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.utils.IOUtils;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    private final AmazonS3Client amazonS3Client;
    private final S3Service s3Service;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @PostMapping(value = "/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        // 파일 저장
        try {
            String fileName=file.getOriginalFilename();
            String fileUrl= "https://" + bucket + "/test" +fileName;
            ObjectMetadata metadata= new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());
            amazonS3Client.putObject(bucket,fileName,file.getInputStream(),metadata);
            System.out.println(fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        System.out.println("파일 저장");


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

    // S3 파일 다운로드
    @GetMapping(value = "/download")
    public ResponseEntity<?> downloadFile(@RequestParam("fileName") String fileName) throws Exception {

        try {
            S3Object s3object = amazonS3Client.getObject(bucket, fileName);
            S3ObjectInputStream inputStream = s3object.getObjectContent();
            byte[] bytes = IOUtils.toByteArray(inputStream);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(s3object.getObjectMetadata().getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(new ByteArrayResource(bytes));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/s3/parseTest")
    public ResponseEntity<?> parseJsonFileFromS3(@RequestParam("fileName") String fileName) {
        try {
            // S3에서 파일을 가져옴
            S3Object s3Object = amazonS3Client.getObject(bucket, fileName);
            S3ObjectInputStream objectInputStream = s3Object.getObjectContent();
            ObjectMapper objectMapper = new ObjectMapper();
            testDTO parsedObject = objectMapper.readValue(objectInputStream, testDTO.class);

            return ResponseEntity.ok(parsedObject);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/summary")
    public ResponseEntity<?> summaryFile(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty.");
        }
        return ResponseEntity.ok(fileService.summaryFileParse(file));
    }
}
