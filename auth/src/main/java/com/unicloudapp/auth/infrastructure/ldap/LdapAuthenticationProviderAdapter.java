package com.unicloudapp.auth.infrastructure.ldap;

import com.unicloudapp.auth.application.AdminProperties;
import com.unicloudapp.auth.application.port.out.AuthenticationProviderPort;
import com.unicloudapp.common.domain.Email;
import com.unicloudapp.common.domain.user.FirstName;
import com.unicloudapp.common.domain.user.LastName;
import com.unicloudapp.common.domain.user.UserLogin;
import com.unicloudapp.common.domain.user.UserRole;
import com.unicloudapp.common.user.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.naming.directory.DirContext;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
class LdapAuthenticationProviderAdapter implements AuthenticationProviderPort, UserExternalQueryService {

    private static final String DOMAIN_SUFFIX = "labs.wmi.amu.edu.pl";
    private static final String BASE_DN = "DC=labs,DC=wmi,DC=amu,DC=edu,DC=pl";

    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;
    private final AdminProperties adminProperties;
    private final LdapTemplate ldapTemplate;

    @Override
    public UserRole authenticate(String username, String password) {
        DirContext ctx = null;
        try {
            // Try to bind with user credentials to verify authentication
            String principal = username + "@" + DOMAIN_SUFFIX;
            ctx = ldapTemplate.getContextSource().getContext(principal, password);

            String filter = "(&(objectClass=user)(sAMAccountName=" + username + "))";

            List<UserRecord> found = ldapTemplate.search(
                    BASE_DN,
                    filter,
                    buildUserContextMapper()
            );

            if (found.isEmpty()) {
                return null;
            }

            UserRecord user = found.getFirst();
            UserRole role = adminProperties.admins().contains(username)
                    ? UserRole.of(UserRole.Type.ADMIN)
                    : mapOuToRole(user.dn());

            if (!userQueryService.existsByLogin(username)) {
                userCommandService.createUser(
                        UserCreateCommand.builder()
                                .userLogin(UserLogin.of(username))
                                .firstName(FirstName.of(user.firstName()))
                                .lastName(LastName.of(user.lastName()))
                                .userRole(role)
                                .email(Email.of(user.email()))
                                .build()
                );
            }

            return role;

        } catch (Exception e) {
            log.error("LDAP authentication failed for user {}: {}", username, e.getMessage());
            return null;
        } finally {
            LdapUtils.closeContext(ctx);
        }
    }

    private UserRole mapOuToRole(String dn) {
        return dn != null && dn.contains("OU=Faculty")
                ? UserRole.of(UserRole.Type.LECTURER)
                : UserRole.of(UserRole.Type.STUDENT);
    }

    @Override
    public List<UserFullNameAndLoginProjection> searchLecturers(String containsQuery) {
        DirContext ctx = null;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String password = authentication.getCredentials().toString();
            String username = authentication.getName();

            // Ensure we can bind with current user credentials
            String principal = username + "@" + DOMAIN_SUFFIX;
            ctx = ldapTemplate.getContextSource().getContext(principal, password);

            String filter = "(|" +
                    "(sAMAccountName=*" + containsQuery + "*)" +
                    "(givenName=*" + containsQuery + "*)" +
                    "(sn=*" + containsQuery + "*)" +
                    ")";

            String facultyBaseDn = "OU=Faculty,OU=People," + BASE_DN;

            List<UserRecord> found = ldapTemplate.search(
                    facultyBaseDn,
                    filter,
                    buildUserContextMapper()
            );

            List<UserFullNameAndLoginProjection> users = new ArrayList<>();
            for (UserRecord ur : found) {
                String login = ur.login();
                String firstName = ur.firstName();
                String lastName = ur.lastName();
                String email = ur.email();

                users.add(new DefaultUserFullNameAndLoginProjection(
                        userQueryService.existsByLogin(login)
                                ? userQueryService.getUserDetailsByUsername(UserLogin.of(login))
                                .orElseThrow()
                                .userId()
                                .getValue()
                                : null,
                        login, firstName, lastName, email)
                );
            }

            return users;

        } catch (Exception e) {
            log.error("LDAP search failed for query {}: {}", containsQuery, e.getMessage());
            return Collections.emptyList();
        } finally {
            LdapUtils.closeContext(ctx);
        }
    }

    private ContextMapper<UserRecord> buildUserContextMapper() {
        return (Object ctx) -> {
            DirContextAdapter adapter = (DirContextAdapter) ctx;
            String dn = adapter.getNameInNamespace();
            String login = optString(adapter, "sAMAccountName");
            String firstName = optString(adapter, "givenName");
            String lastName = optString(adapter, "sn");
            String email = optString(adapter, "mail");
            return new UserRecord(dn, login, firstName, lastName, email);
        };
    }

    private String optString(DirContextOperations ctx, String attr) {
        String val = ctx.getStringAttribute(attr);
        return val != null ? val : "";
    }

    private record UserRecord(String dn, String login, String firstName, String lastName, String email) {}

    @Getter
    @RequiredArgsConstructor
    private static final class DefaultUserFullNameAndLoginProjection implements UserFullNameAndLoginProjection {

        private final UUID uuid;
        private final String login;
        private final String firstName;
        private final String lastName;
        private final String email;
    }
}
