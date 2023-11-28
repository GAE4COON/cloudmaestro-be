package com.gae4coon.cloudmaestro.domain.mypage.controller;

import com.gae4coon.cloudmaestro.domain.mypage.dto.MyNetworkDTO;
import com.gae4coon.cloudmaestro.domain.mypage.service.NetworkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mypage-api/network")
@RequiredArgsConstructor
public class MyNetworkController {
    private final NetworkService networkService;

    @PostMapping("/user/{userId}")
    public ResponseEntity<List<MyNetworkDTO>> getNetworkFilesByUserId(@PathVariable String userId) {
        // userId 기반으로 network db에 저장된 파일 목록 불러오기
        List<MyNetworkDTO> networkFiles = networkService.getNetworksByUserId(userId);

        System.out.println("networkFiles "+networkFiles);
        // network db에 저장된 file을 s3에 쿼리

        return ResponseEntity.ok(networkFiles);
    }
}
