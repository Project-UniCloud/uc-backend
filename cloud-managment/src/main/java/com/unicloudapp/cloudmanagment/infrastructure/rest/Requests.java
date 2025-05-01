package com.unicloudapp.cloudmanagment.infrastructure.rest;

import java.util.UUID;

record GiveCloudAccessRequest(String cloudAccessId, UUID userId)  {
}
