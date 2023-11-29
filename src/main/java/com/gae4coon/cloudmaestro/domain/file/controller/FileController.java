package com.gae4coon.cloudmaestro.domain.file.controller;

import com.gae4coon.cloudmaestro.domain.file.dto.SaveDiagramDTO;
import com.gae4coon.cloudmaestro.domain.file.service.FileService;
import com.gae4coon.cloudmaestro.domain.file.service.S3Service;
import com.gae4coon.cloudmaestro.domain.mypage.service.NetworkService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.Principal;


@RestController
@RequestMapping("/api/v1/file-api")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final NetworkService networkService;
    private final S3Service s3Service;

    @PostMapping(value = "/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        // convert file to input format
        String inputData = fileService.convertFileFormat(file);

        // json to input data
        return ResponseEntity.ok(inputData);
    }
    @GetMapping(value = "/example/{order}")
    public ResponseEntity<?> exampleFile(@PathVariable String order) {
        String fileName = "example" + order + ".json";
        Resource resource = new ClassPathResource("static/" + fileName);
        return ResponseEntity.ok(resource);
    }

    // S3 파일 다운로드
//    @GetMapping(value = "/download")
//    public ResponseEntity<?> downloadFile(@RequestParam("fileName") String fileName) throws Exception {
//
//        try {
//            S3Object s3object = amazonS3Client.getObject(bucket, fileName);
//            S3ObjectInputStream inputStream = s3object.getObjectContent();
//            byte[] bytes = IOUtils.toByteArray(inputStream);
//
//            return ResponseEntity.ok()
//                    .contentType(MediaType.parseMediaType(s3object.getObjectMetadata().getContentType()))
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
//                    .body(new ByteArrayResource(bytes));
//        } catch (IOException e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }

//    @GetMapping(value = "/s3/parseTest")
//    public ResponseEntity<?> parseJsonFileFromS3(@RequestParam("fileName") String fileName) {
//        try {
//            // S3에서 파일을 가져옴
//            S3Object s3Object = amazonS3Client.getObject(bucket, fileName);
//            S3ObjectInputStream objectInputStream = s3Object.getObjectContent();
//            ObjectMapper objectMapper = new ObjectMapper();
//            testDTO parsedObject = objectMapper.readValue(objectInputStream, testDTO.class);
//
//            return ResponseEntity.ok(parsedObject);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }

    @PostMapping(value = "/summary")
    public ResponseEntity<?> summaryFile(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty.");
        }
        return ResponseEntity.ok(fileService.summaryFileParse(file));
    }


    @PostMapping("/save-diagram")
    public ResponseEntity<?> postNetworkData(@RequestBody(required = false) SaveDiagramDTO request,  Principal principal) {
        String diagramData = request.getDiagramData();
        System.out.println("diagramData "+diagramData);
        String fileName = request.getFileName();
        // put s3
//        String fileName = "NetworkData_" + System.currentTimeMillis() + ".json";
        s3Service.uploadS3File(fileName, diagramData);

        // userId, diagramFileName
        networkService.addDiagram(principal.getName(), fileName);

        return ResponseEntity.ok().build();
    }

}