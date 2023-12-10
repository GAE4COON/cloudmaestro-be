package com.gae4coon.cloudmaestro.domain.alert.service;

import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class LogConnectService {
    public String LogConnectCheck(List<LinkData> linkDataList) {
        String result = "false";
        for (LinkData link : linkDataList) {
            if (link.getFrom().contains("Athena") || link.getFrom().contains("OpenSearch") || link.getFrom().contains("QuickSight")) {
                if (checkForS3(link.getTo(), linkDataList, new HashSet<>())) {
                    result = "true";
                    break;
                }
            }
        }

        return result;
    }

    private boolean checkForS3(String currentTo, List<LinkData> linkDatalist, Set<String> visited) {
        if (visited.contains(currentTo)) {
            return false;
        }

        if (currentTo.contains("S3")) {
            return true;
        }

        for (LinkData nextLink : linkDatalist) {
            if (nextLink.getFrom().contains(currentTo)) {
                visited.add(currentTo);
                if (checkForS3(nextLink.getTo(), linkDatalist, visited)) {
                    return true;
                }
            }
        }

        return false;
    }
}
