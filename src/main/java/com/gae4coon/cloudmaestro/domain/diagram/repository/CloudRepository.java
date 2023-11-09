package com.gae4coon.cloudmaestro.domain.diagram.repository;

import com.gae4coon.cloudmaestro.domain.diagram.entity.Cloud;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CloudRepository extends JpaRepository<Cloud, Long> {
}
