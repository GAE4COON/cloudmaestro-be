package com.gae4coon.cloudmaestro.domain.user.repository;

import com.gae4coon.cloudmaestro.domain.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
    Member findByUserId(String user_id);
    boolean existsByUserId(String user_id);
    boolean existsByUserPw(String user_pw);
    boolean existsByEmail(String email);


}
