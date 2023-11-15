package com.gae4coon.cloudmaestro.domain.security.service;

import java.util.List;
import java.util.Map;

public interface SecurityService {
    void globalService(List<String> globalRequirements, Map<String, Object> responseArray);

}
