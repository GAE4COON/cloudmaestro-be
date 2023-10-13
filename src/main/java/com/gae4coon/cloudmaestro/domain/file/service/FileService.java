package com.gae4coon.cloudmaestro.domain.file.service;


import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface FileService {
    List<Map<String, String>> excelToJson(InputStream excelStream) throws Exception;

    String dataToinput(List<Map<String, String>> inputData);

    void summaryFileParse(String file);

    }
