package com.gae4coon.cloudmaestro.domain.rds.repository;

import com.gae4coon.cloudmaestro.domain.rds.entity.dbEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface dbRepository extends JpaRepository<dbEntity, Integer> {
}

