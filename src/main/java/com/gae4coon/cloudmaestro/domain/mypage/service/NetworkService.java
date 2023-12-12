package com.gae4coon.cloudmaestro.domain.mypage.service;

import com.gae4coon.cloudmaestro.domain.mypage.dto.MyArchitectureDTO;
import com.gae4coon.cloudmaestro.domain.mypage.entity.Diagram;
import com.gae4coon.cloudmaestro.domain.mypage.entity.Require;
import com.gae4coon.cloudmaestro.domain.mypage.repository.DiagramRepository;
import com.gae4coon.cloudmaestro.domain.user.entity.Member;
import com.gae4coon.cloudmaestro.domain.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NetworkService {

    private final DiagramRepository diagramRepository;
    private final MemberRepository memberRepository;

    public void addDiagram(String userId, String fileName){
        Member user = memberRepository.getReferenceById(userId);
        Diagram diagram = Diagram.builder()
                .userId(user)
                .diagramFile(fileName)
                .build();

        diagramRepository.save(diagram);
    }

    public Optional<String> getNetworkFileById(Long diagramId) {
        return diagramRepository.findById(diagramId)
                .map(Diagram::getDiagramFile);
    }

    public List<MyArchitectureDTO> getNetworksByUserId(String userId) {

        List<MyArchitectureDTO> diagramFiles = diagramRepository.findByUserId(userId).stream()
                .map(diagram -> new MyArchitectureDTO(
                        diagram.getDiagramId(),
                        diagram.getDiagramFile(),
                        diagram.getCreatedDate(),
                        diagram.getModifiedDate()
                ))
            .collect(Collectors.toList());

        return diagramFiles;
    }

}
