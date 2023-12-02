package com.gae4coon.cloudmaestro.domain.file.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.GraphLinksModel;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import com.gae4coon.cloudmaestro.domain.ssohost.service.DiagramDTOService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3Client amazonS3Client;
    private final DiagramDTOService diagramDtoService;


    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public boolean uploadS3File(String fileName, String jsonContent) {
        try {
            // 버킷 내에 동일한 이름의 파일이 있는지 확인
            if (amazonS3Client.doesObjectExist(bucket, fileName)) {
                System.out.println(fileName + " 파일이 이미 존재합니다.");
                return false;
            }

            try (InputStream inputStream = new ByteArrayInputStream(jsonContent.getBytes(StandardCharsets.UTF_8))) {
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentType("application/json");
                metadata.setContentLength(jsonContent.length());
                amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, inputStream, metadata));
                System.out.println(fileName + " 저장");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(fileName + " 저장 실패");
            return false;
        }
    }

    public GraphLinksModel getS3File(String fileName) {
        try {
            // S3에서 파일을 가져옴
            S3Object s3Object = amazonS3Client.getObject(bucket, fileName);
            S3ObjectInputStream objectInputStream = s3Object.getObjectContent();
            ObjectMapper objectMapper = new ObjectMapper();

//            testDTO parsedObject = objectMapper.readValue(objectInputStream, testDTO.class);

            GraphLinksModel diagramData = objectMapper.readValue(objectInputStream, GraphLinksModel.class);

            return diagramData;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
