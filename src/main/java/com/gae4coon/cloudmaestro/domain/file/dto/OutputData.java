package com.gae4coon.cloudmaestro.domain.file.dto;
import lombok.Data;

import java.util.*;

@Data
public class OutputData {
    private Map<String, Object> compute;
    private Map<String, Object> database;
    private Map<String, Object> storage;
    // getter, setter, constructor ...
}