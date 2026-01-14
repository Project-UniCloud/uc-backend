package com.unicloudapp.group.infrastructure.rest

import com.unicloudapp.common.user.UserDetails
import com.unicloudapp.common.user.UserQueryService
import com.unicloudapp.common.vo.group.GroupId
import com.unicloudapp.common.vo.user.UserId
import com.unicloudapp.common.vo.user.UserLogin
import com.unicloudapp.group.application.GroupDetailsView
import com.unicloudapp.group.application.GroupService
import com.unicloudapp.group.application.port.StudentImporterPort
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import spock.lang.Specification

class GroupRestControllerSpec extends Specification {

    GroupService groupService = Mock()
    StudentImporterPort studentImporterPort = Mock()
    UserQueryService userQueryService = Mock()
    GroupRestController controller = new GroupRestController(groupService, studentImporterPort, userQueryService)

    def setup() {
        def authentication = Mock(Authentication)
        def securityContext = Mock(SecurityContext)
        SecurityContextHolder.setContext(securityContext)
        securityContext.getAuthentication() >> authentication
        authentication.getAuthorities() >> []
    }

    def cleanup() {
        SecurityContextHolder.clearContext()
    }

    def "should delete student from group"() {
        given:
        def groupId = UUID.randomUUID()
        def studentId = UUID.randomUUID()

        when:
        controller.deleteStudentFromGroup(groupId, studentId)

        then:
        1 * groupService.deleteStudentFromGroup(GroupId.of(groupId), UserId.of(studentId))
    }

    def "should allow access for admin"() {
        given:
        def groupId = UUID.randomUUID()
        def authentication = SecurityContextHolder.getContext().getAuthentication()
        def authority = Mock(GrantedAuthority)
        authority.getAuthority() >> "ROLE_ADMIN"
        authentication.getAuthorities() >> [authority]

        when:
        controller.getGroupById(groupId)

        then:
        1 * groupService.findById(groupId) >> Mock(GroupDetailsView)
        0 * userQueryService.getUserDetailsByUsername(_)
    }

    def "should allow access for lecturer assigned to group"() {
        given:
        def groupId = UUID.randomUUID()
        def lecturerId = UserId.of(UUID.randomUUID())
        def lecturerLogin = "lecturer1"
        
        def authentication = Mock(Authentication)
        def securityContext = Mock(SecurityContext)
        SecurityContextHolder.setContext(securityContext)
        securityContext.getAuthentication() >> authentication
        
        def authority = Mock(GrantedAuthority)
        authority.getAuthority() >> "ROLE_LECTURER"
        authentication.getAuthorities() >> [authority]
        authentication.getName() >> lecturerLogin

        def userDetails = UserDetails.builder()
                .userId(lecturerId)
                .build()
        def groupDetails = GroupDetailsView.builder()
                .lecturerIds(Set.of(lecturerId.getValue()))
                .build()

        when:
        controller.getGroupById(groupId)

        then:
        2 * groupService.findById(groupId) >> groupDetails
        1 * userQueryService.getUserDetailsByUsername(UserLogin.of(lecturerLogin)) >> Optional.of(userDetails)
        notThrown(AccessDeniedException)
    }

    def "should deny access for lecturer NOT assigned to group"() {
        given:
        def groupId = UUID.randomUUID()
        def lecturerId = UserId.of(UUID.randomUUID())
        def lecturerLogin = "lecturer1"

        def authentication = Mock(Authentication)
        def securityContext = Mock(SecurityContext)
        SecurityContextHolder.setContext(securityContext)
        securityContext.getAuthentication() >> authentication

        def authority = Mock(GrantedAuthority)
        authority.getAuthority() >> "ROLE_LECTURER"
        authentication.getAuthorities() >> [authority]
        authentication.getName() >> lecturerLogin

        def userDetails = UserDetails.builder()
                .userId(lecturerId)
                .build()
        def groupDetails = GroupDetailsView.builder()
                .lecturerIds(Set.of(UUID.randomUUID())) // Different lecturer
                .build()

        when:
        controller.getGroupById(groupId)

        then:
        1 * groupService.findById(groupId) >> groupDetails
        1 * userQueryService.getUserDetailsByUsername(UserLogin.of(lecturerLogin)) >> Optional.of(userDetails)
        thrown(AccessDeniedException)
    }
}
