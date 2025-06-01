package com.unicloudapp.cloudmanagment.infrastructure.rest;

import com.unicloudapp.cloudmanagment.application.CloudAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cloud")
@RequiredArgsConstructor
class CloudAccessRestController {

    private final CloudAccessService cloudAccessService;
}
