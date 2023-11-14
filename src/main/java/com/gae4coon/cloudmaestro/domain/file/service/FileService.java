package com.gae4coon.cloudmaestro.domain.file.service;


import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface FileService {

    String convertFileFormat(MultipartFile file) throws Exception;

    Map<String, Object> summaryFileParse(MultipartFile file);

}
