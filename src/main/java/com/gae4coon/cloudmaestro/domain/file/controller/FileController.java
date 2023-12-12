package com.gae4coon.cloudmaestro.domain.file.controller;

import com.gae4coon.cloudmaestro.domain.file.dto.SaveDiagramDTO;
import com.gae4coon.cloudmaestro.domain.file.service.FileService;
import com.gae4coon.cloudmaestro.domain.file.service.S3Service;
import com.gae4coon.cloudmaestro.domain.mypage.entity.Diagram;
import com.gae4coon.cloudmaestro.domain.mypage.repository.DiagramRepository;
import com.gae4coon.cloudmaestro.domain.mypage.service.NetworkService;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.GraphLinksModel;
import com.gae4coon.cloudmaestro.domain.user.entity.Member;
import com.gae4coon.cloudmaestro.domain.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.Principal;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/file-api")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final NetworkService networkService;
    private final S3Service s3Service;
    private final DiagramRepository diagramRepository;
    private final MemberRepository memberRepository;

    @PostMapping(value = "/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        // convert file to input format
        String inputData = fileService.convertFileFormat(file);

        // json to input data
        return ResponseEntity.ok(inputData);
    }
    @GetMapping(value = "/example/{order}")
    public ResponseEntity<?> exampleFile(@PathVariable String order) {
        String fileName = "example" + order + ".json";
        Resource resource = new ClassPathResource("static/" + fileName);
        return ResponseEntity.ok(resource);
    }

    @PostMapping(value = "/summary")
    public ResponseEntity<?> requestSummary(@RequestBody Map<String, Object> cost){

        fileService.summaryFileParse(cost);
        return ResponseEntity.ok(fileService.summaryFileParse(cost));
    }

    @PostMapping("/save-diagram")
    public ResponseEntity<?> postNetworkData(@RequestBody(required = false) SaveDiagramDTO request,  Principal principal) {
        String diagramData = request.getDiagramData();
        String fileImg = request.getFileImg();

        String fileName = request.getFileName()+"_"+principal.getName();

        // put s3
        boolean isUnique = s3Service.uploadS3File(fileName, diagramData, fileImg);
        if(!isUnique) return ResponseEntity.ok("false");

        // userId, diagramFileName
        networkService.addDiagram(principal.getName(), request.getFileName());

        return ResponseEntity.ok("true");
    }

    @PostMapping("/update-diagram")
    public ResponseEntity<?> updateNetworkData(@RequestBody(required = false) SaveDiagramDTO request,  Principal principal) {
        String diagramData = request.getDiagramData();
        String fileImg = request.getFileImg();

        Long diagramId = diagramRepository.findByDiagramFile(request.getFileName()).getDiagramId();

        String fileName = request.getFileName()+"_"+principal.getName();

        // put s3
        boolean isUnique = s3Service.updateS3File(fileName, diagramData, fileImg);

        Member user = memberRepository.getReferenceById(principal.getName());
        Diagram diagram = Diagram.builder()
                .diagramId(diagramId)
                .userId(user)
                .diagramFile(request.getFileName())
                .build();

        System.out.println("update diagram"+ diagramId+ fileName);
        diagramRepository.save(diagram);

        if(!isUnique) return ResponseEntity.ok("false");

        return ResponseEntity.ok("true");
    }

}