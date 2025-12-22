package com.unicloudapp.auth.infrastructure.rest;

import java.util.List;

record AuthenticateResponse(
        List<String> roles
) {
}
