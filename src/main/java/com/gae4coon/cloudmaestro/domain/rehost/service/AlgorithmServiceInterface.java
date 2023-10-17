package com.gae4coon.cloudmaestro.domain.rehost.service;

import com.gae4coon.cloudmaestro.domain.rehost.dto.LinkData;

import java.util.List;
import java.util.Map;

public interface AlgorithmServiceInterface {

    Map<String, Object> algorithmDataList( Map<String, Object> nodesData , List<LinkData> linkData);


}