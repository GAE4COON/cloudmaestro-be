package com.gae4coon.cloudmaestro.domain.file.service.impl;

import com.gae4coon.cloudmaestro.domain.file.service.FileService;
import com.google.gson.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

@Service
public class FileServiceImpl implements FileService {
    @Override
    public List<Map<String, String>> excelToJson(InputStream excelStream) throws Exception {
        List<Map<String, String>> jsonList = new ArrayList<>();

        Workbook workbook = new XSSFWorkbook(excelStream);
        Sheet sheet = workbook.getSheetAt(0);

        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            workbook.close();
            throw new Exception("No header row found.");
        }

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            Map<String, String> jsonMap = new LinkedHashMap<>();

            for (Cell cell : row) {
                String header;
                try {
                    header = headerRow.getCell(cell.getColumnIndex()).getStringCellValue();
                }catch (Exception ignored){continue;}
                if (isMergedRegion(sheet, cell)) {
                    CellAddress mergedAddress = getMergedAddress(sheet, cell);
                    cell = sheet.getRow(mergedAddress.getRow()).getCell(mergedAddress.getColumn());
                }
                jsonMap.put(header, getCellValue(cell));
            }
            jsonList.add(jsonMap);
        }

        workbook.close();
        System.out.println(jsonList);
        return jsonList;
    }

    @Override
    public String dataToinput(List<Map<String, String>> inputData) {
        JsonObject root = new JsonObject();
        JsonArray nodeDataArray = new JsonArray();
        Set<String> group = new HashSet<>();


        inputData.forEach(map -> {
            JsonObject node = new JsonObject();

            List<String> keys = new ArrayList<>(map.keySet());

            int count = 1; // Default count

            for (String key : keys) {
                String newKey = key;
                String newValue = map.get(key);

                if ("망".equals(newKey)) {
                    newKey = "group";
                    group.add(newValue);
                } else if ("장비명".equals(newKey)) {
                    newKey = "text";
                } else if ("개수".equals(newKey)) {
                    try {
                        count = (int) Double.parseDouble(newValue); // Convert the value to integer count

                    } catch (Exception e) {
                        count = 0;
                        map.remove(key); // Remove '개수' entry
                        continue;
                    }
                    map.remove(key); // Remove '개수' entry
                    continue; // Skip putting '개수' back into map
                }

                node.addProperty(newKey, newValue);

                if (!newKey.equals(key)) {
                    map.remove(key);
                }
            }

            // Add the transformed map 'count' number of times
            for (int i = 0; i < count; i++) {
                JsonObject tempNode = node.deepCopy(); // 반복마다 새로운 JsonObject 인스턴스를 생성
                tempNode.addProperty("type", "Network_icon");

                String textValue = tempNode.get("text").getAsString();
                String imgFile=null;
                tempNode.addProperty("key", textValue);

                switch (textValue){
                    case "FW": imgFile="firewall"; break;
                    case "WAF": imgFile = "firewall"; break;
                    case "AD": imgFile = "Anti_DDoS"; break;
                    case "DB": imgFile = "database"; break;
                    case "IPS": imgFile = "ips"; break;
                    case "IDS": imgFile = "ips"; break;
                    case "SVR": imgFile = "server"; break;
                    case "WS": imgFile = "server"; break;
                }

                tempNode.addProperty("source", "/img/Network_icon/" +imgFile+ ".png");
                nodeDataArray.add(tempNode);
            }
        });

        for(var g:group){
            JsonObject groupNode = new JsonObject();
            groupNode.addProperty("text", g);
            groupNode.addProperty("isGroup", true);
            groupNode.addProperty("type", "group");
            groupNode.addProperty("key", g);

            nodeDataArray.add(groupNode);
        }

        root.addProperty("class", "GraphLinksModel");
        root.addProperty("linkKeyProperty", "key");
        root.add("nodeDataArray", nodeDataArray);

        // put link
        JsonArray linkDataArray = new JsonArray();
        root.add("linkDataArray",linkDataArray);

        return root.toString();
    }

    public void summaryFileParse(String file){
        Gson gson = new Gson();


    }



    public String getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return Double.toString(cell.getNumericCellValue());
            case BOOLEAN:
                return Boolean.toString(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

    public boolean isMergedRegion(Sheet sheet, Cell cell) {
        for (CellRangeAddress mergedRegion : sheet.getMergedRegions()) {
            if (mergedRegion.isInRange(cell.getRowIndex(), cell.getColumnIndex())) {
                return true;
            }
        }
        return false;
    }

    public CellAddress getMergedAddress(Sheet sheet, Cell cell) {
        for (CellRangeAddress mergedRegion : sheet.getMergedRegions()) {
            if (mergedRegion.isInRange(cell.getRowIndex(), cell.getColumnIndex())) {
                return new CellAddress(mergedRegion.getFirstRow(), mergedRegion.getFirstColumn());
            }
        }
        return null;
    }


}
