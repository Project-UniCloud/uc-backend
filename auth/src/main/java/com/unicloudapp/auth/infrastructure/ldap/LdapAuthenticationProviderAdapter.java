package com.unicloudapp.auth.infrastructure.ldap;

import com.unicloudapp.auth.application.port.out.AuthenticationProviderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Component;

import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

@Component
@RequiredArgsConstructor
class LdapAuthenticationProviderAdapter implements AuthenticationProviderPort {

    //TODO .env albo do docker-compose
    private static final String LDAP_URL = "ldaps://dc1-2016.labs.wmi.amu.edu.pl:636";
    private static final String DOMAIN_SUFFIX = "labs.wmi.amu.edu.pl";

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean authenticate(
            String username,
            String password
    ) {
        try {

            //TODO - fix me. Hashtable is not recommended
            Hashtable<String, String> env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, LDAP_URL);
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, username + "@" + DOMAIN_SUFFIX);
            env.put(Context.SECURITY_CREDENTIALS, password);

            DirContext ctx = new InitialDirContext(env);
            ctx.close();
            if (!((InMemoryUserDetailsManager) userDetailsService).userExists(username)) {
                ((InMemoryUserDetailsManager) userDetailsService).createUser(
                        User.builder()
                                .username(username)
                                .password(passwordEncoder.encode(password))
                                .roles("ADMIN")
                                .build()
                );
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
