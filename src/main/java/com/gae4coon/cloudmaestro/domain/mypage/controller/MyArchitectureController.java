package com.gae4coon.cloudmaestro.domain.mypage.controller;

import com.gae4coon.cloudmaestro.domain.file.service.S3Service;
import com.gae4coon.cloudmaestro.domain.mypage.dto.MyArchitectureDTO;
import com.gae4coon.cloudmaestro.domain.mypage.repository.DiagramRepository;
import com.gae4coon.cloudmaestro.domain.mypage.service.NetworkService;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.GraphLinksModel;
import com.gae4coon.cloudmaestro.domain.ssohost.service.DiagramDTOService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/mypage-api/architecture")
@RequiredArgsConstructor
public class MyArchitectureController {
    private final NetworkService networkService;
    private final DiagramRepository diagramRepository;
    private final DiagramDTOService diagramDTOService;
    private final S3Service s3service;

    @PostMapping("/history/list")
    public ResponseEntity<List<MyArchitectureDTO>> getDiagramFileListByUserId(Principal principal) {
        String userId = principal.getName();
        System.out.println("userId "+userId);

        // userId 기반으로 network db에 저장된 파일 목록 불러오기
        List<MyArchitectureDTO> networkFiles = networkService.getNetworksByUserId(userId);

        System.out.println("networkFiles "+networkFiles);
        // network db에 저장된 file을 s3에 쿼리

        return ResponseEntity.ok(networkFiles);
    }

    @PostMapping("/history/diagram")
    public ResponseEntity<?> getDiagramFileByDiagramId(@RequestBody Long diagramId) {
        String diagramFile = diagramRepository.findByDiagramId(diagramId).getDiagramFile();
        GraphLinksModel graphLinksModel = s3service.getS3File(diagramFile);
        HashMap<String, Object> response = diagramDTOService.DiagramDTOtoResponse(graphLinksModel);
        System.out.println("response "+response);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/history/delete")
    public ResponseEntity<?> deleteDiagramData(@RequestBody Long diagramId) {
        diagramRepository.deleteById(diagramId);
        return ResponseEntity.ok("success");
    }
}
