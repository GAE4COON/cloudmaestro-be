package com.gae4coon.cloudmaestro.domain.security.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface SecurityService {
    HashMap<String, Object> globalService(List<String> globalRequirements, Map<String, Object> responseArray);

}
