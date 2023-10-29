package com.gae4coon.cloudmaestro.domain.price.repository;

import com.gae4coon.cloudmaestro.domain.price.entity.MemberEntity;
import com.gae4coon.cloudmaestro.domain.price.entity.dbEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodeRepository extends JpaRepository<MemberEntity, Integer> {
}
