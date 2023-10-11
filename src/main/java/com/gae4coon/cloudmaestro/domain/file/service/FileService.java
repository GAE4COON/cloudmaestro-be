package com.gae4coon.cloudmaestro.domain.file.service;

import com.google.gson.JsonObject;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface FileService {
    List<Map<String, String>> excelToJson(InputStream excelStream) throws Exception;

    String getCellValue(Cell cell);

    boolean isMergedRegion(Sheet sheet, Cell cell);

    CellAddress getMergedAddress(Sheet sheet, Cell cell);

    String dataToinput(List<Map<String, String>> inputData);
}
