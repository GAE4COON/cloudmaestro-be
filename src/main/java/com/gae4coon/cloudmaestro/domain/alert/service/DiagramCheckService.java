package com.gae4coon.cloudmaestro.domain.alert.service;

import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class DiagramCheckService {
    public HashMap<String, String> linkCheck(LinkData linkData) {
        HashMap<String, String> check = new HashMap<>();
        if (linkData.getTo().contains("IDS") || linkData.getTo().contains("IPS") && linkData.getFrom().contains("Firewall")) {
            check.put("status", "success");
            check.put("message", "Firewall 다음에는 IPS, IDS가 올 수 없습니다.");
        } else {
            check.put("status", "fail");
        }

        return check;
    }
}
