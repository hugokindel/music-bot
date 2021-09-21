package com.hugokindel.common;

import com.hugokindel.common.json.annotation.JsonSerializable;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonSerializable
public class BaseConfig {
    @JsonSerializable(necessary = false)
    public Map<String, Object> global = new LinkedHashMap<>();
}
