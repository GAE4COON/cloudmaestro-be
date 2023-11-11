package com.gae4coon.cloudmaestro.domain.mypage.repository;

import com.gae4coon.cloudmaestro.domain.mypage.entity.Network;
import com.gae4coon.cloudmaestro.domain.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NetworkRepository extends JpaRepository<Network, String> {
//    List<Network> findByUserId(Member userId);
    @Query("SELECT n FROM Network n WHERE n.userId.userId = :userId")
    List<Network> findByUserId(@Param("userId") String userId);
}
