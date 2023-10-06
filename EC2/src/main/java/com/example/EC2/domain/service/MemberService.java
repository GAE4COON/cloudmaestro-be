package com.example.EC2.service;

import com.example.EC2.controller.MemberController;
import com.example.EC2.dto.MemberDTO;

import com.example.EC2.entity.MemberEntity;
import com.example.EC2.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private static final Logger logger = LoggerFactory.getLogger(MemberService.class);

    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public List<MemberDTO> getAllMembers() {
        List<MemberEntity> memberEntities = memberRepository.findAll();
        return memberEntities.stream()
                .map(entity -> new MemberDTO(
                        entity.getId(),
                        entity.getName(),
                        entity.getAPIName(),
                        entity.getInstanceMemory(),
                        entity.getNetwork(),
                        entity.getLinuxOnDemand(),
                        entity.getLinuxReserved(),
                        entity.getLinuxSpot(),
                        entity.getWindowsOnDemand(),
                        entity.getWindowsReserved()
                ))
                .collect(Collectors.toList());
    }

    public List<MemberDTO> getMembersByAPIName(String platform) {
        List<MemberEntity> memberEntities = memberRepository.findAll();

        if(platform.equalsIgnoreCase("Linux")) {
            return memberEntities.stream()
                    .map(entity -> new MemberDTO(
                            entity.getId(),
                            entity.getName(),
                            entity.getAPIName(),
                            entity.getInstanceMemory(),
                            entity.getNetwork(),
                            entity.getLinuxOnDemand(),
                            entity.getLinuxReserved(),
                            entity.getLinuxSpot(),
                            null, // Assuming no Windows OnDemand for Linux
                            null  // Assuming no Windows Reserved for Linux
                    ))
                    .filter(member -> member.getAPIName() != null)
                    .filter(member -> !member.getLinuxOnDemand().equalsIgnoreCase("unavailable"))
                    .filter(member -> !member.getLinuxReserved().equalsIgnoreCase("unavailable"))
                    .filter(member -> !member.getLinuxSpot().equalsIgnoreCase("unavailable"))
                    .filter(member -> member.getName() != null)
                    .filter(member -> member.getNetwork() != null)
                    .filter(member -> member.getInstanceMemory() != null)
                    .collect(Collectors.toList());
        } else if (platform.equalsIgnoreCase("Windows")) {
            return memberEntities.stream()
                    .map(entity -> new MemberDTO(
                            entity.getId(),
                            entity.getName(),
                            entity.getAPIName(),
                            entity.getInstanceMemory(),
                            entity.getNetwork(),
                            null,  // Assuming no Linux OnDemand for Windows
                            null,  // Assuming no Linux Reserved for Windows
                            null,  // Assuming no Linux Spot for Windows
                            entity.getWindowsOnDemand(),
                            entity.getWindowsReserved()
                    ))
                    .filter(member -> member.getAPIName() != null)
                    .filter(member -> !member.getWindowsOnDemand().equalsIgnoreCase("unavailable"))
                    .filter(member -> !member.getWindowsReserved().equalsIgnoreCase("unavailable"))
                    .filter(member -> member.getName() != null)
                    .filter(member -> member.getNetwork() != null)
                    .filter(member -> member.getInstanceMemory() != null)
                    .collect(Collectors.toList());
        } else {
            // Return an empty list or throw an exception if the platform is not recognized.
            return Collections.emptyList();
        }
    }
    public List<MemberDTO> getMembersByAPINameAndInstanceType(String platform, String instanceType) {
        List<MemberEntity> memberEntities = memberRepository.findAll();

        Stream<MemberDTO> stream;
        if (platform.equalsIgnoreCase("Linux")) {
            stream = memberEntities.stream()
                    .map(entity -> new MemberDTO(
                            entity.getId(),
                            entity.getName(),
                            entity.getAPIName(),
                            entity.getInstanceMemory(),
                            entity.getNetwork(),
                            entity.getLinuxOnDemand(),
                            entity.getLinuxReserved(),
                            entity.getLinuxSpot(),
                            null,  // Assuming no Windows OnDemand for Linux
                            null   // Assuming no Windows Reserved for Linux
                    ));
        } else if (platform.equalsIgnoreCase("Windows")) {
            stream = memberEntities.stream()
                    .map(entity -> new MemberDTO(
                            entity.getId(),
                            entity.getName(),
                            entity.getAPIName(),
                            entity.getInstanceMemory(),
                            entity.getNetwork(),
                            null,  // Assuming no Linux OnDemand for Windows
                            null,  // Assuming no Linux Reserved for Windows
                            null,  // Assuming no Linux Spot for Windows
                            entity.getWindowsOnDemand(),
                            entity.getWindowsReserved()
                    ));
        } else {
            return Collections.emptyList();
        }

        if (instanceType != null) {
            stream = stream.filter(member -> instanceType.equals(member.getAPIName()));
        }

        return stream
                .filter(member -> member.getAPIName() != null)
                .filter(member -> (platform.equalsIgnoreCase("Linux") && !member.getLinuxOnDemand().equalsIgnoreCase("unavailable")) ||
                        (platform.equalsIgnoreCase("Windows") && !member.getWindowsOnDemand().equalsIgnoreCase("unavailable")))
                .filter(member -> member.getName() != null)
                .filter(member -> member.getNetwork() != null)
                .filter(member -> member.getInstanceMemory() != null)
                .collect(Collectors.toList());
        }



    public List<String> getSplitByAPIName(String platform) {
        List<MemberEntity> memberEntities;
        logger.info("myname is memberentites{}",platform);
        memberEntities = memberRepository.findAll();
        Stream<MemberEntity> stream;
        if(platform.equalsIgnoreCase("linux") ){
            stream = memberEntities.stream()
                    .filter(member ->
                    !member.getLinuxOnDemand().equalsIgnoreCase("unavailable") &&
                            !member.getLinuxReserved().equalsIgnoreCase("unavailable") &&
                            !member.getLinuxSpot().equalsIgnoreCase("unavailable"));
        }else if (platform.equalsIgnoreCase("windows")){
            stream = memberEntities.stream()
                    .filter(member ->
                            !member.getWindowsOnDemand().equalsIgnoreCase("unavailable") &&
                            !member.getWindowsReserved().equalsIgnoreCase("unavailable"));
        }else{
            return Collections.emptyList();
        }

        return stream.map(entity -> {
                    String[] parts = entity.getAPIName().split("\\.");
                    return parts.length > 0 ? parts[0] : entity.getAPIName();
                })
                .distinct() // 중복 제거
                .collect(Collectors.toList());
    }

    public List<String> getSplitByAPINameAndInstanceType(String platform, String InstanceType) {
        List<MemberEntity> memberEntities;
        logger.info("myname is memberentites{}",platform);
        memberEntities = memberRepository.findAll();
        Stream<MemberEntity> stream;
        if(platform.equalsIgnoreCase("linux") ){
            stream = memberEntities.stream()
                    .filter(member ->
                            !member.getLinuxOnDemand().equalsIgnoreCase("unavailable") &&
                            !member.getLinuxReserved().equalsIgnoreCase("unavailable") &&
                            !member.getLinuxSpot().equalsIgnoreCase("unavailable"));
        }else if (platform.equalsIgnoreCase("windows")){
            stream = memberEntities.stream()
                    .filter(member ->
                            !member.getWindowsOnDemand().equalsIgnoreCase("unavailable") &&
                            !member.getWindowsReserved().equalsIgnoreCase("unavailable"));
        }else{
            return Collections.emptyList();
        }

        return stream.map(entity -> {
                    String[] parts = entity.getAPIName().split("\\.");
                    return parts.length > 0 ? parts[0] : entity.getAPIName();
                })
                .distinct() // 중복 제거
                .collect(Collectors.toList());
    }



}

