package com.gae4coon.cloudmaestro.domain.file.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;

public interface S3Service {


    boolean uploadS3File(String fileName, String jsonContent);

}
