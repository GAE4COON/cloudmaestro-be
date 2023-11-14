package com.gae4coon.cloudmaestro.domain.resource.controller;

import com.gae4coon.cloudmaestro.domain.resource.service.SidebarService;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sidebar-api")
@RequiredArgsConstructor
public class SidebarController {

    private final SidebarService sidebarService;
    // NodeData의 배열 또는 리스트를 받을 수 있도록 수정
    @PostMapping("/resource")
    public ResponseEntity<List<String>> getNodeArray(@RequestBody List<NodeData> nodeDataList){
        //System.out.println("\nReceived nodedata: " + nodeDataList);
        // 여기에 각 nodeData에 대한 처리 로직 추가
        List<String> result = sidebarService.getResourceFilter(nodeDataList);
        return ResponseEntity.ok(result);
    }
}
