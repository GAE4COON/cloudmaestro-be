package com.gae4coon.cloudmaestro.domain.price.repository;

import com.gae4coon.cloudmaestro.domain.price.entity.dbEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface dbRepository extends JpaRepository<dbEntity, Integer> {
}

