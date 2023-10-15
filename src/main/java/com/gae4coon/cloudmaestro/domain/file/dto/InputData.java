package com.gae4coon.cloudmaestro.domain.file.dto;

import lombok.Data;

import java.util.*;

@Data
public class InputData {
    private String class2; //예약어때문에 class:GraphicLinks 처리 필요
    private String linkKeyProperty;
    private List<NodeData> nodeDataArray;
    private List<?> linkDataArray;
    private List<Map<String, Object>> cost;
}


