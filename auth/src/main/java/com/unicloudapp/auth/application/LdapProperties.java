package com.unicloudapp.auth.application;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth.ldap")
public record LdapProperties(String domainSuffix, String baseDn, String facultyPeopleOu, String url) {}
