package com.unicloudapp.auth.infrastructure.ldap;

import com.unicloudapp.auth.application.LdapProperties;
import com.unicloudapp.auth.application.port.out.AuthenticationProviderPort;
import com.unicloudapp.common.auth.AdminProperties;
import com.unicloudapp.common.vo.Email;
import com.unicloudapp.common.vo.user.FirstName;
import com.unicloudapp.common.vo.user.LastName;
import com.unicloudapp.common.vo.user.UserLogin;
import com.unicloudapp.common.vo.user.UserRole;
import com.unicloudapp.common.user.UserCommandService;
import com.unicloudapp.common.user.UserCreateCommand;
import com.unicloudapp.common.user.UserExternalQueryService;
import com.unicloudapp.common.user.UserFullNameAndLoginProjection;
import com.unicloudapp.common.user.UserQueryService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.OrFilter;
import org.springframework.ldap.filter.WhitespaceWildcardsFilter;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.naming.directory.DirContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class LdapAuthenticationProviderAdapter implements AuthenticationProviderPort, UserExternalQueryService {

    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;
    private final AdminProperties adminProperties;
    private final LdapTemplate ldapTemplate;
    private final LdapProperties ldapProperties;

    @Override
    public UserRole authenticate(String username, String password) {
        DirContext ctx = null;
        try {
            // Try to bind with user credentials to verify authentication
            String principal = username + "@" + ldapProperties.domainSuffix();
            ctx = ldapTemplate.getContextSource().getContext(principal, password);

            AndFilter filter = new AndFilter();
            filter.and(new EqualsFilter("objectClass", "user"));
            filter.and(new EqualsFilter("sAMAccountName", username));

            List<UserRecord> found = ldapTemplate.search(
                    "",
                    filter.encode(),
                    buildUserContextMapper()
            );

            if (found.isEmpty()) {
                return null;
            }

            UserRecord user = found.getFirst();
            Set<UserRole.Type> roleTypes = new HashSet<>();
            roleTypes.add(mapOuToRoleType(user.dn()));
            if (adminProperties.getAdmins().contains(UserLogin.of(username))) {
                roleTypes.add(UserRole.Type.ADMIN);
            }
            UserRole role = UserRole.of(roleTypes);

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

    private UserRole.Type mapOuToRoleType(String dn) {
        return dn != null && dn.contains("OU=Faculty")
                ? UserRole.Type.LECTURER
                : UserRole.Type.STUDENT;
    }

    @Override
    public List<UserFullNameAndLoginProjection> searchLecturers(String containsQuery) {
        DirContext ctx = null;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || authentication.getCredentials() == null) {
                return Collections.emptyList();
            }
            String password = authentication.getCredentials().toString();
            String username = authentication.getName();

            // Ensure we can bind with current user credentials
            String principal = username + "@" + ldapProperties.domainSuffix();
            ctx = ldapTemplate.getContextSource().getContext(principal, password);

            OrFilter filter = new OrFilter();
            filter.or(new WhitespaceWildcardsFilter("sAMAccountName", containsQuery));
            filter.or(new WhitespaceWildcardsFilter("givenName", containsQuery));
            filter.or(new WhitespaceWildcardsFilter("sn", containsQuery));

            String facultyBaseDn = ldapProperties.facultyPeopleOu();

            List<UserRecord> found = ldapTemplate.search(
                    facultyBaseDn,
                    filter.encode(),
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
