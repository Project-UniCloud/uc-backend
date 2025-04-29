package com.unicloudapp.cloudmanagment.application;

import java.util.Optional;

public interface CloudAccessClientResolverPort {

    Optional<CloudAccessClientPort> getClient(String cloudAccessKey);
}
