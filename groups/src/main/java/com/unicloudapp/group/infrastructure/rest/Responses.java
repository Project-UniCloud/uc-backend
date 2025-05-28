package com.unicloudapp.group.infrastructure.rest;

import lombok.Builder;

import java.util.UUID;

@Builder
record UserRowViewResponse(
        UUID uuid,
        String firstName,
        String lastName,
        String login,
        String email
) {}
