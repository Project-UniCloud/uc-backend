package com.unicloudapp.group.infrastructure.rest;

import java.util.UUID;
import lombok.Builder;

@Builder
record UserRowViewResponse(UUID uuid, String firstName, String lastName, String login, String email) {}
