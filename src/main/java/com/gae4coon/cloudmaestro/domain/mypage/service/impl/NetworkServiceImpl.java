package com.gae4coon.cloudmaestro.domain.mypage.service.impl;

import com.gae4coon.cloudmaestro.domain.mypage.dto.MyNetworkDTO;
import com.gae4coon.cloudmaestro.domain.mypage.entity.Diagram;
import com.gae4coon.cloudmaestro.domain.mypage.entity.Network;
import com.gae4coon.cloudmaestro.domain.mypage.repository.NetworkRepository;
import com.gae4coon.cloudmaestro.domain.mypage.service.NetworkService;
import com.gae4coon.cloudmaestro.domain.user.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NetworkServiceImpl implements NetworkService {

    private final NetworkRepository networkRepository;
    @Override
    public void addNetwork(Member userId, String networkFile, Set<Diagram> diagrams){
        Network network = Network.builder()
                .userId(userId)
                .networkFile(networkFile)
                .diagrams(diagrams)
                .build();

        networkRepository.save(network);
    }

    @Override
    public Optional<String> getNetworkFileById(String networkId) {
        return networkRepository.findById(networkId)
                .map(Network::getNetworkFile);
    }

    @Override
    public List<MyNetworkDTO> getNetworksByUserId(String userId) {
        AtomicLong idCounter = new AtomicLong(1); // 순차 ID 생성기

        List<MyNetworkDTO> networkFiles = networkRepository.findByUserId(userId).stream()
                .map(network -> new MyNetworkDTO(
                        idCounter.getAndIncrement(),
                        network.getNetworkFile(),
                        "/assets/img/Cloud-architecture.png"
                ))
            .collect(Collectors.toList());
        return networkFiles;
    }

}
