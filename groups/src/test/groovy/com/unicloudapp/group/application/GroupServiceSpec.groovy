package com.unicloudapp.group.application

import com.unicloudapp.common.cloud.CloudResourceAccessCommandService
import com.unicloudapp.common.cloud.CloudResourceAccessQueryService
import com.unicloudapp.common.cloud.CloudResourceRowView
import com.unicloudapp.common.vo.Email
import com.unicloudapp.common.vo.cloud.CloudConnectorId
import com.unicloudapp.common.vo.cloud.CloudResourceAccessId
import com.unicloudapp.common.vo.cloud.CloudResourceType
import com.unicloudapp.common.vo.cloud.CostLimit
import com.unicloudapp.common.vo.group.GroupId
import com.unicloudapp.common.vo.group.GroupName
import com.unicloudapp.common.vo.group.Semester
import com.unicloudapp.common.vo.user.FirstName
import com.unicloudapp.common.vo.user.LastName
import com.unicloudapp.common.vo.user.UserId
import com.unicloudapp.common.vo.user.UserLogin
import com.unicloudapp.common.user.*
import com.unicloudapp.group.application.port.GroupRepositoryPort
import com.unicloudapp.group.domain.*
import com.unicloudapp.group.domain.vo.Description
import com.unicloudapp.group.domain.vo.EndDate
import com.unicloudapp.group.domain.vo.GroupStatus
import com.unicloudapp.group.domain.vo.StartDate
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate
import java.time.LocalDateTime

class GroupServiceSpec extends Specification {

    def groupRepository = Mock(GroupRepositoryPort)
    def groupFactory = Mock(GroupFactory)
    def userQueryService = Mock(UserQueryService)
    def cloudResourceAccessQueryService = Mock(CloudResourceAccessQueryService)
    def cloudResourceAccessCommandService = Mock(CloudResourceAccessCommandService)
    def userCommandService = Mock(UserCommandService)

    @Subject
    def groupService = new GroupService(
            groupRepository,
            groupFactory,
            userQueryService,
            cloudResourceAccessQueryService,
            cloudResourceAccessCommandService,
            userCommandService
    )

    def "should create a new group"() {
        given:
        def groupDTO = GroupDTO.builder()
                .groupId(UUID.randomUUID())
                .name("Test Group")
                .semester("2023Z")
                .lecturers([UUID.randomUUID()] as Set)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(6))
                .description("Test Description")
                .build()
        def group = Mock(Group)
        def groupName = GroupName.of(groupDTO.name())
        def semester = Semester.of(groupDTO.semester())

        when:
        groupService.createGroup(groupDTO)

        then:
        1 * groupRepository.existsByNameAndSemester(groupName, semester) >> false
        1 * groupFactory.create(
                groupDTO.name(),
                groupDTO.semester(),
                groupDTO.lecturers(),
                groupDTO.startDate(),
                groupDTO.endDate(),
                groupDTO.description()
        ) >> group
        1 * groupRepository.save(group) >> group
    }

    def "should throw exception when creating group with existing name and semester"() {
        given:
        def groupDTO = GroupDTO.builder()
                .groupId(UUID.randomUUID())
                .name("Test Group")
                .semester("2023Z")
                .lecturers([UUID.randomUUID()] as Set)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(6))
                .description("Test Description")
                .build()
        def groupName = GroupName.of(groupDTO.name())
        def semester = Semester.of(groupDTO.semester())

        when:
        groupService.createGroup(groupDTO)

        then:
        1 * groupRepository.existsByNameAndSemester(groupName, semester) >> true
        def exception = thrown(RuntimeException)
        exception.message == "Group with name Test Group and semester 2023Z already exists."
    }

    def "should throw exception when creating group with end date before start date"() {
        given:
        def groupDTO = GroupDTO.builder()
                .groupId(UUID.randomUUID())
                .name("Test Group")
                .semester("2023Z")
                .lecturers([UUID.randomUUID()] as Set)
                .startDate(LocalDate.now().plusMonths(6))
                .endDate(LocalDate.now())
                .description("Test Description")
                .build()

        when:
        groupService.createGroup(groupDTO)

        then:
        def exception = thrown(RuntimeException)
        exception.message == "Start date cannot be after end date."
    }

    def "should throw exception when adding students to non-existent group"() {
        given:
        def groupId = GroupId.of(UUID.randomUUID())
        def studentBasicDataList = [
            new StudentBasicData("John", "Doe", "john.doe@example.com", "123456")
        ]

        when:
        groupService.addStudents(groupId, studentBasicDataList)

        then:
        1 * groupRepository.findById(groupId.uuid) >> Optional.empty()
        def exception = thrown(RuntimeException)
        exception.message == "Group not found with id: " + groupId
    }

    def "should get cloud resource accesses for group"() {
        given:
        def groupId = GroupId.of(UUID.randomUUID())
        def group = Mock(Group)
        def cloudResourceAccessIds = [
            CloudResourceAccessId.of(UUID.randomUUID()),
            CloudResourceAccessId.of(UUID.randomUUID())
        ] as Set
        def today = LocalDate.now()
        def now = LocalDateTime.now()
        def resourceTypeRowView = CloudResourceRowView.builder()
                .clientId("aws")
                .name("EC2")
                .costLimit(BigDecimal.ONE)
                .cronCleanupSchedule("0 0 0 * * ?")
                .expiresAt(today)
                .lastUsedAt(now)
                .limitUsed(BigDecimal.ONE)
                .build()
        def cloudResourceTypeViews = [resourceTypeRowView]

        when:
        def result = groupService.getCloudResourceAccesses(groupId)

        then:
        1 * groupRepository.findById(groupId.uuid) >> Optional.of(group)
        1 * group.getCloudResourceAccesses() >> cloudResourceAccessIds
        1 * cloudResourceAccessQueryService.getCloudResourceDetails(cloudResourceAccessIds) >> cloudResourceTypeViews

        and:
        result == cloudResourceTypeViews
        result.size() == 1
        result[0].name() == "EC2"
        result[0].clientId() == "aws"
        result[0].limitUsed() == BigDecimal.ONE
        result[0].cronCleanupSchedule() == "0 0 0 * * ?"
        result[0].expiresAt() == today
        result[0].lastUsedAt() == now
        result[0].limitUsed() == BigDecimal.ONE
    }

    def "should throw exception when getting cloud resource accesses for non-existent group"() {
        given:
        def groupId = GroupId.of(UUID.randomUUID())

        when:
        groupService.getCloudResourceAccesses(groupId)

        then:
        1 * groupRepository.findById(groupId.uuid) >> Optional.empty()
        def exception = thrown(RuntimeException)
        exception.message == "Group not found with id: " + groupId
    }

    def "should get students details by group id"() {
        given:
        def groupId = GroupId.of(UUID.randomUUID())
        def group = Mock(Group)
        def studentIds = [
            UserId.of(UUID.randomUUID()),
            UserId.of(UUID.randomUUID())
        ] as Set
        def studentDetail = UserDetails.builder()
                .userId(studentIds[0])
                .firstName(FirstName.of("John"))
                .lastName(LastName.of("Doe"))
                .login(UserLogin.of("123456"))
                .email(Email.empty())
                .build()
        def pageable = PageRequest.of(0, 10)
        def pagedStudentDetails = new PageImpl<>([studentDetail], pageable, 1)
        userQueryService.getUserDetailsByIds(studentIds, 0, 10) >> pagedStudentDetails

        when:
        def result = groupService.getStudentsDetailsByGroupId(groupId, pageable)

        then:
        1 * groupRepository.findById(groupId.uuid) >> Optional.of(group)
        1 * group.getStudents() >> studentIds

        and:
        result == pagedStudentDetails
        result.content.size() == 1
        result.content[0].userId() == studentIds[0]
        result.content[0].firstName().value == "John"
        result.content[0].lastName().value == "Doe"
        result.content[0].login().value == "123456"
        result.content[0].email() == Email.empty()
    }

    def "should throw exception when getting students details for non-existent group"() {
        given:
        def groupId = GroupId.of(UUID.randomUUID())
        def pageable = PageRequest.of(0, 10)

        when:
        groupService.getStudentsDetailsByGroupId(groupId, pageable)

        then:
        1 * groupRepository.findById(groupId.uuid) >> Optional.empty()
        def exception = thrown(RuntimeException)
        exception.message == "Group not found with id: " + groupId
    }

    def "should find group by id"() {
        given:
        def groupId = UUID.randomUUID()
        def details = Mock(GroupDetailsProjection)
        def userId = UUID.randomUUID()
        def userFullName = new UserFullName(UserId.of(userId), FirstName.of("John"), LastName.of("Doe"))

        when:
        def result = groupService.findById(groupId)

        then:
        1 * groupRepository.findGroupDetailsByUuid(groupId) >> details
        1 * details.getLecturers() >> [userId]
        1 * userQueryService.getFullNameForUserIds(_) >> [(UserId.of(userId)): userFullName]
        1 * details.getUuid() >> groupId
        1 * details.getName() >> "Test Group"
        1 * details.getGroupStatus() >> GroupStatus.Type.ACTIVE
        1 * details.getDescription() >> "Test Description"
        1 * details.getEndDate() >> LocalDate.now().plusMonths(6)
        1 * details.getStartDate() >> LocalDate.now()
        1 * details.getSemester() >> "2023Z"

        and:
        result.groupId == groupId
        result.name == "Test Group"
        result.lecturerFullNames == [UserFullNameDTO.from(userFullName)] as Set
        result.status == GroupStatus.Type.ACTIVE.displayName
        result.description == "Test Description"
    }

    def "should give cloud resource access to group"() {
        given:
        def groupId = GroupId.of(UUID.randomUUID())
        def CloudVendorConnectorId = CloudConnectorId.of("aws")
        def cloudResourceType = CloudResourceType.of("ec2")
        def group = Mock(Group)
        def cloudResourceAccessId = CloudResourceAccessId.of(UUID.randomUUID())
        def lecturerLogins = [new UserLogin("john.doe@example.com")]
        1 * cloudResourceAccessQueryService.getCloudResourceDetails(_) >> []
        def costLimit = CostLimit.zero()
        group.getGroupStatus() >> GroupStatus.of(GroupStatus.Type.ACTIVE)

        when:
        def result = groupService.grantCloudResourceAccess(groupId, CloudVendorConnectorId, cloudResourceType, costLimit)

        then:
        1 * groupRepository.findById(groupId.uuid) >> Optional.of(group)
        1 * group.getName() >> GroupName.of("Test Group")
        1 * group.getSemester() >> Semester.of("2023Z")
        1 * group.getLecturers() >> [UserId.of(UUID.randomUUID())]
        1 * userQueryService.getUserLoginsAndEmailsByIds(_) >> lecturerLogins
        1 * cloudResourceAccessQueryService.isCloudGroupExists(_, CloudVendorConnectorId) >> false
        1 * cloudResourceAccessCommandService.createGroup(_, CloudVendorConnectorId, lecturerLogins, cloudResourceType)
        1 * cloudResourceAccessCommandService.giveGroupCloudResourceAccess(CloudVendorConnectorId, cloudResourceType, _, costLimit) >> cloudResourceAccessId
        1 * group.grantCloudResourceAccess(cloudResourceAccessId)
        1 * groupRepository.save(group)

        and:
        result == cloudResourceAccessId
    }

    def "should update group"() {
        given:
        def groupId = GroupId.of(UUID.randomUUID())
        def lecturerId = UUID.randomUUID()
        def groupDTO = GroupDTO.builder()
                .groupId(UUID.randomUUID())
                .name("Updated Group")
                .semester("2023Z")
                .lecturers([lecturerId] as Set)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(6))
                .description("Updated Description")
                .build()
        def group = Mock(Group)
        def cloudResourceAccessId = CloudResourceAccessId.of(UUID.randomUUID())
        def cloudResourceRowView = CloudResourceRowView.builder()
                .clientId("test-client")
                .name("test-resource")
                .costLimit(BigDecimal.ZERO)
                .limitUsed(BigDecimal.ZERO)
                .expiresAt(LocalDate.now())
                .lastUsedAt(LocalDateTime.now())
                .cronCleanupSchedule("0 0 0 * * ?")
                .status("ACTIVE")
                .build()

        when:
        groupService.updateGroup(groupId, groupDTO)

        then:
        1 * groupRepository.findById(groupId.uuid) >> Optional.of(group)
        _ * group.getName() >> GroupName.of("Old Name")
        _ * group.getSemester() >> Semester.of("2023Z")
        _ * group.getLecturers() >> ([] as Set)
        1 * userQueryService.getUserLoginsByIds(_ as Set) >> [] // deleted lecturers
        1 * userQueryService.getUserLoginsByIds(_ as Set) >> ["new.lecturer"] // added lecturers
        1 * group.getCloudResourceAccesses() >> ([cloudResourceAccessId] as Set)
        1 * cloudResourceAccessQueryService.getCloudResourceDetails(cloudResourceAccessId) >> cloudResourceRowView
        1 * cloudResourceAccessCommandService.addLecturersToGroup(CloudConnectorId.of("test-client"), _, ["new.lecturer"] as Set)
        1 * cloudResourceAccessCommandService.removeUsers(CloudConnectorId.of("test-client"), [] as Set, _)
        1 * group.update(
                GroupName.of(groupDTO.name()),
                _ as Set,
                StartDate.of(groupDTO.startDate()),
                EndDate.of(groupDTO.endDate()),
                Description.of(groupDTO.description())
        )
        1 * groupRepository.save(group)
    }

    def "should delete student from group and update cloud if group is active"() {
        given:
        def groupId = GroupId.of(UUID.randomUUID())
        def studentId = UserId.of(UUID.randomUUID())
        def group = Mock(Group)
        def studentLogin = UserLogin.of("s123")
        def status = Mock(GroupStatus)
        def accessId = CloudResourceAccessId.of(UUID.randomUUID())
        def cloudResource = CloudResourceRowView.builder()
                .clientId("connector-1")
                .build()

        when:
        groupService.deleteStudentFromGroup(groupId, studentId)

        then:
        1 * groupRepository.findById(groupId.uuid) >> Optional.of(group)
        1 * userQueryService.getUserLoginsByIds(Set.of(studentId)) >> [studentLogin]
        1 * group.getName() >> GroupName.of("Group A")
        1 * group.getSemester() >> Semester.of("2024L")
        1 * group.getGroupStatus() >> status
        1 * status.isActive() >> true
        1 * group.getCloudResourceAccesses() >> Set.of(accessId)
        1 * cloudResourceAccessQueryService.getCloudResourceDetails(Set.of(accessId)) >> [cloudResource]
        1 * group.deleteStudent(studentId)
        1 * cloudResourceAccessCommandService.removeUsers(
                CloudConnectorId.of("connector-1"),
                Set.of(studentLogin),
                { it.groupName() == GroupName.of("Group A") && it.semester() == Semester.of("2024L") }
        )
        1 * groupRepository.save(group)
    }

    def "should delete student from group and not update cloud if group is inactive"() {
        given:
        def groupId = GroupId.of(UUID.randomUUID())
        def studentId = UserId.of(UUID.randomUUID())
        def group = Mock(Group)
        def studentLogin = UserLogin.of("s123")
        def status = Mock(GroupStatus)

        when:
        groupService.deleteStudentFromGroup(groupId, studentId)

        then:
        1 * groupRepository.findById(groupId.uuid) >> Optional.of(group)
        1 * userQueryService.getUserLoginsByIds(Set.of(studentId)) >> [studentLogin]
        1 * group.getGroupStatus() >> status
        1 * status.isActive() >> false
        1 * group.deleteStudent(studentId)
        0 * cloudResourceAccessCommandService.removeUsers(_, _, _)
        1 * groupRepository.save(group)
    }

    def "should throw exception when deleting student from non-existent group"() {
        given:
        def groupId = GroupId.of(UUID.randomUUID())
        def studentId = UserId.of(UUID.randomUUID())

        when:
        groupService.deleteStudentFromGroup(groupId, studentId)

        then:
        1 * groupRepository.findById(groupId.uuid) >> Optional.empty()
        thrown(RuntimeException)
    }
}