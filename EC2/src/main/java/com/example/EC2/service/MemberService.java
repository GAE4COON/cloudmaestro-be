package com.example.EC2.service;

import com.example.EC2.dto.MemberDTO;
import com.example.EC2.entity.MemberEntity;
import com.example.EC2.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemberService {
    private final MemberRepository memberRepository;

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

    public List<MemberDTO> getMembersByAPIName(String APIName) {
        return memberRepository.findByAPIName(APIName)
                .stream()
                .map(MemberDTO::new)
                .collect(Collectors.toList());
    }


}
