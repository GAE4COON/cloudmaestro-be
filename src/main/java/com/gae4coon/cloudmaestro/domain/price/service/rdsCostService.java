package com.gae4coon.cloudmaestro.domain.price.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

@Service
public class rdsCostService {

    private final ObjectMapper objectMapper = new ObjectMapper();  // Jackson JSON 파서

    public String getRdsAwsPricing(String dbEngine, String instanceType) throws Exception {
        if(dbEngine.equals("SQLServer")){
            dbEngine="SQL Server";
        }
        if(dbEngine.equals("AuroraPostgresMySQL")){
            dbEngine="Aurora MySQL";
        }
        String commandTemplate = "aws pricing get-products --service-code AmazonRDS --filters \"Type=TERM_MATCH,Field=instanceType,Value=%s\" \"Type=TERM_MATCH,Field=databaseEngine,Value=%s\" \"Type=TERM_MATCH,Field=location,Value=Asia Pacific (Seoul)\" | jq -rc \".PriceList[]\" | jq -r \"select(.product.attributes.deploymentOption | contains(\\\"Single\\\"))\" | jq -rc \"{ databaseEngine: .product.attributes.databaseEngine, instanceType: .product.attributes.instanceType, deploymentOption: .product.attributes.deploymentOption, vcpu: .product.attributes.vcpu, memory: .product.attributes.memory, physicalProcessor: .product.attributes.physicalProcessor, storage: .product.attributes.storage, networkPerformance: .product.attributes.networkPerformance, priceUSD: .terms.OnDemand[]?.priceDimensions[]?.pricePerUnit.USD, description: .terms.OnDemand[]?.priceDimensions[]?.description }\"";
        String formattedCommand = String.format(commandTemplate, instanceType, dbEngine);

        String[] command = {
                "cmd.exe", "/c",
                formattedCommand
        };


        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();

        // 결과 가져오기
        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder output = new StringBuilder();

        // JSON 배열 형태로 결과를 반환하기 위한 수정
        output.append("["); // JSON 배열 시작
        String line;
        boolean isFirst = true;
        while ((line = reader.readLine()) != null) {
            if (!isFirst) {
                output.append(",");  // 다음 항목을 위한 쉼표 추가
            }
            output.append(line);
            isFirst = false;
        }

        output.append("]"); // JSON 배열 종료

        int exitCode = process.waitFor();
        System.out.println("Process exit code: " + exitCode);

        JsonNode jsonNode = objectMapper.readTree(output.toString());

        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode));
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
    }
}
