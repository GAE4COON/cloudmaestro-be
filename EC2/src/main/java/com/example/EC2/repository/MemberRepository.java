package com.example.EC2.repository;

import com.example.EC2.dto.MemberDTO;
import com.example.EC2.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface MemberRepository extends JpaRepository<MemberEntity,Integer> {
    List<MemberEntity> findByAPIName(String APIName);

}
