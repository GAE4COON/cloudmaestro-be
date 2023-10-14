package com.gae4coon.cloudmaestro.domain.rds.service;

import com.gae4coon.cloudmaestro.domain.rds.entity.dbEntity;
import com.gae4coon.cloudmaestro.domain.rds.repository.dbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class dbService {
    @Autowired
    private dbRepository repository;

    public Map<String, List<String>> getAvailableApiNamesForField(String inputField) {
        List<dbEntity> dataList = repository.findAll();
        List<String> apiNames = new ArrayList<>();
        List<String> instanceType = new ArrayList<>();
        List<String> instanceSize = new ArrayList<>();

        try {
            for (dbEntity entity : dataList) {
                Method method = dbEntity.class.getMethod("get" + inputField); // Get the method

                String value = (String) method.invoke(entity); // Invoke the method
                if (value!=null && !value.contains("unavailable")) {
                    apiNames.add(entity.getApiName());
                    //System.out.println(entity.getApiName());
                }
            }
        } catch (Exception e) { // Here, catching a general exception for simplicity
            e.printStackTrace();
        }
        //System.out.println(apiNames.get(1));

        for (String instance : apiNames) {
            int lastDotIndex = instance.lastIndexOf(".");
            if (lastDotIndex != -1) {
                String type = instance.substring(0, lastDotIndex);
                String size = instance.substring(lastDotIndex + 1);

                instanceType.add(type);
                instanceSize.add(size);
            }
        }
        Map<String, List<String>> result = new HashMap<>();
        result.put("instanceType", instanceType);
        result.put("instanceSize", instanceSize);
        // 확인을 위한 출력
       // System.out.println("Instance: " + result);


        return result;
    }
}