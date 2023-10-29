package com.gae4coon.cloudmaestro.domain.price.service;

import com.gae4coon.cloudmaestro.domain.price.dto.PriceResult;
import com.gae4coon.cloudmaestro.domain.price.entity.EC2Entity;
import com.gae4coon.cloudmaestro.domain.price.repository.EC2Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Service
public class NewEC2Service {
    @Autowired
    private EC2Repository ec2Repository;

    public PriceResult getPrice(String platform) {
        platform = formatPlatform(platform);

        List<EC2Entity> dataList = ec2Repository.findAll();
        // ec2 한 데이터에 대한 전체 데이터 가져오기
        Map<String, List<String>> detailsMap = collectEC2Details(platform, dataList);

        List<String> apiNames = new ArrayList<>(detailsMap.keySet());

        // instance type, instance size , 한 데이터에 대한 전체데이터 묶기
        Map<String, List<String>> result = splitInstanceDetails(apiNames);

        result =combineInstanceDetails(result);


        return new PriceResult(result,detailsMap);
    }

    private String formatPlatform(String platform) {
        switch (platform.toLowerCase()) {
            case "linux":
                return "Linux";
            case "windows":
                return "Windows";
            default:
                return platform;
        }
    }

    private Map<String, List<String>> collectEC2Details(String platform, List<EC2Entity> dataList) {
        Map<String, List<String>> detailsMap = new HashMap<>();

        for (EC2Entity entity : dataList) {
            try {
                Method method = EC2Entity.class.getMethod("get" + platform + "OnDemand");
                String value = (String) method.invoke(entity);

                if (value != null && !value.contains("unavailable")) {
                    List<String> details = detailsMap.computeIfAbsent(entity.getAPIName(), k -> new ArrayList<>());
                    details.addAll(Arrays.asList(
                            entity.getInstanceMemory(),
                            entity.getNetwork(),
                            entity.getInstanceStorage(),
                            entity.getVcpu(),
                            entity.getLinuxOnDemand()
                    ));
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return detailsMap;
    }

    private Map<String, List<String>> splitInstanceDetails(List<String> apiNames) {
        List<String> instanceType = new ArrayList<>();
        List<String> instanceSize = new ArrayList<>();

        for (String instance : apiNames) {
            int lastDotIndex = instance.lastIndexOf(".");
            if (lastDotIndex != -1) {
                instanceType.add(instance.substring(0, lastDotIndex));
                instanceSize.add(instance.substring(lastDotIndex + 1));
            }
        }
        // Set<String> uniqueInstanceType = new HashSet<>(instanceType);
//        instanceType = new ArrayList<>(uniqueInstanceType);

        Map<String, List<String>> result = new HashMap<>();
        System.out.println("instanceSize" +instanceSize);
        result.put("instanceType", instanceType);
        result.put("instanceSize", instanceSize);

        return result;
    }


    public Map<String, List<String>> combineInstanceDetails(Map<String, List<String>> ec2info) {

        List<String> instanceType = ec2info.get("instanceType");
        List<String> instanceSize = ec2info.get("instanceSize");

        Map<String, List<String>> resultMap = new HashMap<>();

        for (int i = 0; i < instanceType.size(); i++) {
            String type = instanceType.get(i);
            if (!resultMap.containsKey(type)) {
                resultMap.put(type, new ArrayList<>());
            }
            resultMap.get(type).add(instanceSize.get(i));
        }

        Set<String> uniqueInstanceType = new HashSet<>(instanceType);
        instanceType = new ArrayList<>(uniqueInstanceType);
        resultMap.put("instanceType",instanceType);


        return resultMap;


    }
}
