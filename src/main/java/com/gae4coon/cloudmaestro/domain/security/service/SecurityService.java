package com.gae4coon.cloudmaestro.domain.security.service;

import com.gae4coon.cloudmaestro.domain.requirements.dto.RequireDTO;
import com.gae4coon.cloudmaestro.domain.requirements.dto.ZoneDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface SecurityService {
    HashMap<String, Object> security(RequireDTO requireDTO, Map<String, Object> responseArray);

}
