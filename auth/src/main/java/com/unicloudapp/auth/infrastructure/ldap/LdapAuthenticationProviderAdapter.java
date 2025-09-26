package com.unicloudapp.auth.infrastructure.ldap;

import com.unicloudapp.auth.application.AdminProperties;
import com.unicloudapp.auth.application.port.out.AuthenticationProviderPort;
import com.unicloudapp.common.domain.Email;
import com.unicloudapp.common.domain.user.FirstName;
import com.unicloudapp.common.domain.user.LastName;
import com.unicloudapp.common.domain.user.UserLogin;
import com.unicloudapp.common.domain.user.UserRole;
import com.unicloudapp.common.user.UserCommandService;
import com.unicloudapp.common.user.UserCreateCommand;
import com.unicloudapp.common.user.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import java.util.Hashtable;

@Component
@RequiredArgsConstructor
@Slf4j
class LdapAuthenticationProviderAdapter implements AuthenticationProviderPort {

    private static final String LDAP_URL = "ldaps://dc1-2016.labs.wmi.amu.edu.pl:636";
    private static final String DOMAIN_SUFFIX = "labs.wmi.amu.edu.pl";
    private static final String BASE_DN = "DC=labs,DC=wmi,DC=amu,DC=edu,DC=pl";

    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;
    private final AdminProperties adminProperties;

    @Override
    public UserRole authenticate(String username, String password) {
        try {
            Hashtable<String, String> env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, LDAP_URL);
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, username + "@" + DOMAIN_SUFFIX);
            env.put(Context.SECURITY_CREDENTIALS, password);

            DirContext ctx = new InitialDirContext(env);

            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String filter = "(&(objectClass=user)(sAMAccountName=" + username + "))";

            var results = ctx.search(BASE_DN, filter, controls);
            if (!results.hasMore()) {
                ctx.close();
                return null;
            }

            SearchResult searchResult = results.next();
            String dn = searchResult.getNameInNamespace();
            UserRole role = adminProperties.admins().contains(username) ? UserRole.of(UserRole.Type.ADMIN) : mapOuToRole(dn);
            String firstName = searchResult.getAttributes().get("givenName") != null ? (String) searchResult.getAttributes().get("givenName").get() : "";
            String lastName = searchResult.getAttributes().get("sn") != null ? (String) searchResult.getAttributes().get("sn").get() : "";
            String email = searchResult.getAttributes().get("mail") != null ? (String) searchResult.getAttributes().get("mail").get() : "";

            if (!userQueryService.existsByLogin(username)) {
                userCommandService.createUser(
                        UserCreateCommand.builder()
                                .userLogin(UserLogin.of(username))
                                .firstName(FirstName.of(firstName))
                                .lastName(LastName.of(lastName))
                                .userRole(role)
                                .email(Email.of(email))
                                .build()
                );
            }

            ctx.close();
            return role;

        } catch (Exception e) {
            log.error("LDAP authentication failed for user {}: {}", username, e.getMessage());
            return null;
        }
    }

    private UserRole mapOuToRole(String dn) {
        return dn.contains("OU=Faculty")
                ? UserRole.of(UserRole.Type.LECTURER)
                : UserRole.of(UserRole.Type.STUDENT);
    }
}
