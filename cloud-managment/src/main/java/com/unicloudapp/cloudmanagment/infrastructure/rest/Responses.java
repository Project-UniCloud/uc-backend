package com.unicloudapp.cloudmanagment.infrastructure.rest;

import java.util.UUID;

record CloudResourceNameResponse(
        UUID cloudResourceAccessId,
        String cloudResourceName
) {

}
