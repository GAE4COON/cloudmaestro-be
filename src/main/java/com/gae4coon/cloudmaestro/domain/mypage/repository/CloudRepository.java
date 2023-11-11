package com.gae4coon.cloudmaestro.domain.mypage.repository;

import com.gae4coon.cloudmaestro.domain.mypage.entity.Cloud;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CloudRepository extends JpaRepository<Cloud, Long> {
}
