package com.unicloudapp.auth.infrastructure.ldap;

import com.unicloudapp.auth.application.port.out.AuthenticationProviderPort;
import org.springframework.stereotype.Component;

import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

//TODO generate token and auth in spring security
@Component
class LdapAuthenticationProviderAdapter implements AuthenticationProviderPort {

    //TODO .env albo do docker-compose
    private static final String LDAP_URL = "ldaps://dc1-2016.labs.wmi.amu.edu.pl:636";
    private static final String DOMAIN_SUFFIX = "labs.wmi.amu.edu.pl";

    @Override
    public boolean authenticate(String username,
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
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
