package com.gae4coon.cloudmaestro.domain.resourceguide.repository;

import com.gae4coon.cloudmaestro.domain.resourceguide.entity.ResourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceRepository extends JpaRepository<ResourceEntity, String> {
    ResourceEntity findByTitle(String title);
}

