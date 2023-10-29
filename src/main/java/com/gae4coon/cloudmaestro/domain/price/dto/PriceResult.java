package com.gae4coon.cloudmaestro.domain.price.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;
@Data
public class PriceResult {
    private Map<String, List<String>> details;
    private  Map<String, List<String>> insertEC2EntityDetails;

    // getters, setters, constructors 생략...

    // 두 인자를 받는 생성자
    public PriceResult(Map<String, List<String>> resultData, Map<String, List<String>> listOfDetails) {
        this.details = resultData;
        this.insertEC2EntityDetails = listOfDetails;
    }

}
