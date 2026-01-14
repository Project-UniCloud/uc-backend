package com.unicloudapp.group.infrastructure.rest

import com.unicloudapp.common.user.UserQueryService
import com.unicloudapp.common.vo.group.GroupId
import com.unicloudapp.common.vo.user.UserId
import com.unicloudapp.group.application.GroupService
import com.unicloudapp.group.application.port.StudentImporterPort
import org.springframework.security.core.Authentication
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
}
