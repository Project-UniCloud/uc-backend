package com.unicloudapp.auth.application;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

@Configuration
@EnableConfigurationProperties(LdapProperties.class)
class LdapConfig {

    @Bean
    LdapContextSource ldapContextSource(LdapProperties props) {
        LdapContextSource cs = new LdapContextSource();
        cs.setUrl(props.url());
        cs.setBase(props.baseDn());
        cs.setAnonymousReadOnly(true);
        cs.setReferral("ignore");
        cs.afterPropertiesSet();
        return cs;
    }

    @Bean
    LdapTemplate ldapTemplate(LdapContextSource contextSource) {
        LdapTemplate template = new LdapTemplate(contextSource);
        template.setIgnorePartialResultException(true);
        return template;
    }
}
