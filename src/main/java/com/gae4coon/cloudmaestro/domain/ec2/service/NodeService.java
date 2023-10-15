package com.gae4coon.cloudmaestro.domain.ec2.service;

import com.gae4coon.cloudmaestro.domain.ec2.dto.MemberDTO;

import com.gae4coon.cloudmaestro.domain.ec2.entity.MemberEntity;
import com.gae4coon.cloudmaestro.domain.ec2.repository.NodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Qualifier("NodeMemberService")
@Service
public class NodeService {
    private final NodeRepository nodeRepository;
    private static final Logger logger = LoggerFactory.getLogger(NodeService.class);

    @Autowired
    public NodeService(NodeRepository nodeRepository) {
        this.nodeRepository = nodeRepository;
    }

    public List<MemberDTO> getAllMembers() {
        List<MemberEntity> memberEntities = nodeRepository.findAll();
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
        List<MemberEntity> memberEntities = nodeRepository.findAll();

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
        List<MemberEntity> memberEntities = nodeRepository.findAll();

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



    public List<String> getSplitByAPINameAndInstanceType(String platform, String InstanceType) {
        List<MemberEntity> memberEntities;
        logger.info("myname is getApiNameandInstanceType{}",platform);
        memberEntities = nodeRepository.findAll();
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

        return stream.filter(entity -> entity.getAPIName().contains(InstanceType))
                .map(entity -> {
                    String[] parts = entity.getAPIName().split("\\.");
                    if (parts.length > 1 && parts[0].equalsIgnoreCase(InstanceType)) {
                        return parts[1];
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .distinct() // 중복 제거
                .collect(Collectors.toList());
    }

    public List<String> getSplitByAPIName(String platform) {
        List<MemberEntity> memberEntities;
        logger.info("myname is memberentites{}",platform);

        memberEntities = nodeRepository.findAll();
        logger.info(" iam is memberentites{}",memberEntities);
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
                    return parts.length > 0 ? parts[0]: entity.getAPIName();
                })
                .distinct() // 중복 제거
                .collect(Collectors.toList());
    }



}

