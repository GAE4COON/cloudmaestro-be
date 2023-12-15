package com.gae4coon.cloudmaestro.domain.user.service;

import com.gae4coon.cloudmaestro.domain.user.dto.UserJoinRequestDto;
import com.gae4coon.cloudmaestro.domain.user.entity.Member;
import com.gae4coon.cloudmaestro.domain.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;
    public List<Member> getList(){
        return this.memberRepository.findAll();
    }

    public HashMap<String, Object> idOverlap(String userId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("result", memberRepository.existsById(userId));
        return map;
    }

    public boolean emailOverlap(String email) {
        return memberRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public Member findById(String userId){
        return memberRepository.findByUserId(userId);
    }


    public String join(@NotNull UserJoinRequestDto dto){
        Member existingUser = this.memberRepository.findByUserId(dto.getUser_id());
        if(existingUser != null) {
            throw new RuntimeException(dto.getUser_id() + " 는 이미 있습니다.");
        }

        if(!dto.getUser_pw().equals(dto.getUser_check_pw())){
            throw new RuntimeException(dto.getUser_pw()+"가 다릅니다.");
        }

        if(!dto.getEmailCheck()){
            throw new RuntimeException("이메일 인증이 필요합니다.");
        }


        var encodedUserPw = passwordEncoder.encode(dto.getUser_pw());

        Member member = Member.builder()
            .userId(dto.getUser_id())
            .userPw(encodedUserPw)
            .userName(dto.getUser_name())
            .email(dto.getEmail())
            .role(Member.UserRole.member)
            .build();

        this.memberRepository.save(member);
        return "success";
    }

    public String userNameModify(String userid, String username){
        Member existingUser = this.memberRepository.findByUserId(userid);
        if(existingUser == null) {
            throw new RuntimeException(userid + "가 없습니다.");
        }
        if(existingUser.getUserName().equals(username)) {
            throw new RuntimeException(username + "동일한 이름으로 변경할 수 없습니다.");
        }

        Member member = Member.builder()
                .userId(existingUser.getUserId())
                .userPw(existingUser.getUserPw())
                .userName(username)
                .email(existingUser.getEmail())
                .role(Member.UserRole.member)
                .build();

        this.memberRepository.save(member);
        return "success";
    }

    public String userPWModify(String userid, String userpw){
        Member existingUser = this.memberRepository.findByUserId(userid);
        if(existingUser == null) {
            throw new RuntimeException(userid + "가 없습니다.");
        }

        if(passwordEncoder.matches(userpw, existingUser.getUserPw())){
            throw new RuntimeException("새로운 비밀번호는 현재 비밀번호와 달라야 합니다.");
        }



        var encodedUserPw = passwordEncoder.encode(userpw);
        System.out.println(userpw+"변경되었습니다");


        Member member = Member.builder()
                .userId(existingUser.getUserId())
                .userPw(encodedUserPw)
                .userName(existingUser.getUserName())
                .email(existingUser.getEmail())
                .role(Member.UserRole.member)
                .build();

        this.memberRepository.save(member);
        return "success";
    }




    public String userPwCheck(String userid, String userpw){
        Member existingUser = this.memberRepository.findByUserId(userid);
        if(existingUser == null) {
            throw new RuntimeException(userid + "가 없습니다.");
        }


        if(!passwordEncoder.matches(userpw, existingUser.getUserPw())){
            throw new RuntimeException(existingUser.getUserPw()+"가 다릅니다.");
        }

        return "success";
    }

}
