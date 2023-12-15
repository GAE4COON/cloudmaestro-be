package com.gae4coon.cloudmaestro.domain.user.controller;


import com.gae4coon.cloudmaestro.domain.user.dto.*;
import com.gae4coon.cloudmaestro.domain.user.entity.Member;
import com.gae4coon.cloudmaestro.domain.user.repository.MemberRepository;
import com.gae4coon.cloudmaestro.domain.user.service.EmailService;
import com.gae4coon.cloudmaestro.domain.user.service.MemberService;
import com.gae4coon.cloudmaestro.domain.user.service.UserDetailsServiceImpl;
import com.gae4coon.cloudmaestro.global.config.JwtTokenProvider;
import com.gae4coon.cloudmaestro.global.config.TokenInfo;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Validated
@RequiredArgsConstructor
@RestController
@Log4j2
@RequestMapping("/api/v1/users-api")
public class MemberController {
    private final MemberService memberService;
    private final EmailService emailService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsServiceImpl userDetailsService;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @PostMapping("/id-dup-check")
    public ResponseEntity<?> idCheck(@RequestBody UserIdDupDto dto) {
        try {
            HashMap<String, Object> result = memberService.idOverlap(dto.getUserId());
            return ResponseEntity.ok().body(result);
        } catch (RuntimeException e) {
            HashMap<String, Object> errorResult = new HashMap<>();
            errorResult.put("message", "fail");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }

    @PostMapping("/mailConfirm")
    public ResponseEntity<?> mailConfirm(@RequestBody EmailAuthRequestDto emailDto) throws MessagingException, UnsupportedEncodingException {
        HashMap<String, Object> codeResult = new HashMap<>();
        try {

            if (memberService.emailOverlap(emailDto.getEmail())) {
                codeResult.put("result", false);
            } else {
                String authCode = emailService.sendAuthMail(emailDto.getEmail());
                codeResult.put("result", true);

            }
            return ResponseEntity.ok().body(codeResult);

        }catch (Exception e){
            codeResult.put("result", "error");
            return ResponseEntity.ok().body(codeResult);
        }
    }

    @PostMapping("/authCode")
    public ResponseEntity<?> sendEmailAndCode(@RequestBody EmailCodeDto dto) throws NoSuchAlgorithmException {
        if (emailService.verifyEmailCode(dto.getEmail(), dto.getCode())) {
            HashMap<String, Object> codeResult = new HashMap<>();
            codeResult.put("result", true);
            return ResponseEntity.ok().body(codeResult);
        }
        else {
            HashMap<String, Object> errorResult = new HashMap<>();
            errorResult.put("result", false);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResult);
        }
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUpProcess(@RequestBody @Valid UserJoinRequestDto dto, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
           return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        try {
        this.memberService.join(dto);
        HashMap<String, String> map = new HashMap<>();
        map.put("result", "success");
        return ResponseEntity.ok().body(map);

        } catch (RuntimeException e) {
            HashMap<String, String> map = new HashMap<>();
            map.put("result", "fail");
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.ok().body(map);
        }
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> signInProcess(@RequestBody @Valid UserLoginRequestDto dto){
        HashMap<String, Object> result = new HashMap<>();
        try {
            Member user1 = memberService.findById(dto.getUser_id());
            System.out.println("dtd:" + dto);

            System.out.println("user : " + user1);

            if (!passwordEncoder.matches(dto.getUser_pw(), user1.getUserPw())) {
                return new ResponseEntity(HttpStatus.UNAUTHORIZED);
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(dto.getUser_id());
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(dto.getUser_id(), dto.getUser_pw());
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

            Member user = memberService.findById(dto.getUser_id());
            System.out.println("test: " + user.getUserId());

            TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication, user.getUserId());
            result.put("result", tokenInfo);
            result.put("status", "success");
            System.out.println(tokenInfo);
            return ResponseEntity.ok().body(result);
        }catch (Exception e) {
            System.out.println("error:"+ e);
            result.put("status", "error");
            result.put("result", e);
            return ResponseEntity.ok().body(result);
    }
}

    @PostMapping("/my-user")
    public ResponseEntity<?> myUser(@RequestBody UserIdDupDto dto){
        try {
            HashMap<String, String> result = new HashMap<>();
            System.out.println("check??? "+dto.getUserId());

            Member user1 = memberService.findById(dto.getUserId());
            if (user1.getUserId()!=null){
                result.put("user_id", user1.getUserId());
                result.put("name", user1.getUserName());
                result.put("email", user1.getEmail());
            }
            return ResponseEntity.ok().body(result);
        }catch (Exception e) {
            System.out.println("error:"+ e);
            return ResponseEntity.ok().body("error");
        }
    }

    @PostMapping("/my-modify-name")
    public ResponseEntity<?> myNameFix(@RequestBody @Valid UserNameDto dto){
        HashMap<String, String> result = new HashMap<>();
        try {
            String res=memberService.userNameModify(dto.getUserId(), dto.getUserName());
            result.put("result", res);

            return ResponseEntity.ok().body(result);
        }catch (Exception e) {
            System.out.println("error:"+ e);
            result.put("result", "error");
            return ResponseEntity.ok().body(result);
        }
    }

    @PostMapping("/my-modify-pw")
    public ResponseEntity<?> myPWFix(@RequestBody @Valid UserPWDto dto){
        try {
            HashMap<String, String> result = new HashMap<>();
            String res=memberService.userPWModify(dto.getUserId(), dto.getUserPw());
            result.put("result", res);

            return ResponseEntity.ok().body(result);
        }catch (Exception e) {
            System.out.println("error:"+ e);
            return ResponseEntity.ok().body("error");
        }
    }
    @PostMapping("/my-check-pw")
    public ResponseEntity<?> myPWcheck(@RequestBody @Valid UserPWDto dto){
        try {
            HashMap<String, String> result = new HashMap<>();
            String res=memberService.userPwCheck(dto.getUserId(), dto.getUserPw());
            result.put("result", res);

            return ResponseEntity.ok().body(result);
        }catch (Exception e) {
            System.out.println("error:"+ e);
            return ResponseEntity.ok().body("error");
        }
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
