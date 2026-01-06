package com.unicloudapp.group.infrastructure.rest

import com.unicloudapp.common.vo.group.GroupId
import com.unicloudapp.common.vo.user.UserId
import com.unicloudapp.group.application.GroupService
import com.unicloudapp.group.application.port.StudentImporterPort
import spock.lang.Specification

class GroupRestControllerSpec extends Specification {

    GroupService groupService = Mock()
    StudentImporterPort studentImporterPort = Mock()
    GroupRestController controller = new GroupRestController(groupService, studentImporterPort)

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
