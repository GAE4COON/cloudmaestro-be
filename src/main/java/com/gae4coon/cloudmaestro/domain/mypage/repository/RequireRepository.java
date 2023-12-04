package com.gae4coon.cloudmaestro.domain.mypage.repository;

import com.gae4coon.cloudmaestro.domain.mypage.entity.Require;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequireRepository extends JpaRepository<Require, Long> {
    Require findByFileName(String fileName);
}
