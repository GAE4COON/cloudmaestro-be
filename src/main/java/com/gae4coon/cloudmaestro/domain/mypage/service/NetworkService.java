package com.gae4coon.cloudmaestro.domain.mypage.service;

import com.gae4coon.cloudmaestro.domain.mypage.dto.MyNetworkDTO;
import com.gae4coon.cloudmaestro.domain.mypage.entity.Diagram;
import com.gae4coon.cloudmaestro.domain.mypage.entity.Network;
import com.gae4coon.cloudmaestro.domain.user.entity.Member;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface NetworkService {
    void addNetwork(Member userId, String networkFile, Set<Diagram> diagrams);
    List<MyNetworkDTO> getNetworksByUserId(String userId);
    Optional<String> getNetworkFileById(String networkId) ;
}
