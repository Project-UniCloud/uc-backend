package com.unicloudapp.common.cloud;

import java.util.Map;
import java.util.Set;

public interface CloudAccessQueryService {

    Map<String, String> getCloudAccessNames(Set<String> cloudAccessIds);
}
