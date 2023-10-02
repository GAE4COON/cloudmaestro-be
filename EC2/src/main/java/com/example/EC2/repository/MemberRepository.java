package com.example.EC2.repository;

import com.example.EC2.dto.MemberDTO;
import com.example.EC2.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MemberRepository extends JpaRepository<MemberEntity,Integer> {

}
