package com.gae4coon.cloudmaestro.domain.price.repository;

import com.gae4coon.cloudmaestro.domain.price.entity.MemberEntity;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;



@Qualifier("NodeMemberRepository")
@Repository
public interface NodeRepository extends JpaRepository<MemberEntity,Integer> {
    List<MemberEntity> findByAPIName(String APIName);

    @Query(value = "SELECT * FROM member_entity WHERE api_name LIKE ?1%", nativeQuery = true)
    List<MemberEntity> findByAPINameStartingWithNative(String prefix);

    List<MemberEntity> findByAPINameContaining(String keyword);



}
