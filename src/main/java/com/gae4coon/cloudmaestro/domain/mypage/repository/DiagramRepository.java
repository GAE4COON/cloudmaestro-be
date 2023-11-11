package com.gae4coon.cloudmaestro.domain.mypage.repository;

import com.gae4coon.cloudmaestro.domain.mypage.entity.Diagram;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiagramRepository extends JpaRepository<Diagram, Long> {
}
