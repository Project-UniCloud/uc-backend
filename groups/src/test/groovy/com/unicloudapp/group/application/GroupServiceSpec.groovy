package com.unicloudapp.group.application

import com.unicloudapp.common.cloud.CloudResourceAccessCommandService
import com.unicloudapp.common.cloud.CloudResourceAccessQueryService
import com.unicloudapp.common.cloud.CloudResourceTypeRowView
import com.unicloudapp.common.domain.Email
import com.unicloudapp.common.domain.cloud.CloudAccessClientId
import com.unicloudapp.common.domain.cloud.CloudResourceAccessId
import com.unicloudapp.common.domain.cloud.CloudResourceType
import com.unicloudapp.common.domain.group.GroupId
import com.unicloudapp.common.domain.group.GroupName
import com.unicloudapp.common.domain.group.Semester
import com.unicloudapp.common.domain.user.FirstName
import com.unicloudapp.common.domain.user.LastName
import com.unicloudapp.common.domain.user.UserId
import com.unicloudapp.common.domain.user.UserLogin
import com.unicloudapp.common.user.*
import com.unicloudapp.group.domain.*
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

    def "should add a student to the group"() {
        given:
        def groupId = GroupId.of(UUID.randomUUID())
        def studentBasicData = new StudentBasicData(
                "John",
                "Doe",
                "john.doe@example.com",
                "123456"
        )
        def userId = UserId.of(UUID.randomUUID())
        def group = Mock(Group)

        when:
        groupService.addStudent(groupId, studentBasicData)

        then:
        1 * userCommandService.createStudent(studentBasicData) >> userId
        1 * groupRepository.findById(groupId.uuid) >> Optional.of(group)
        1 * group.addStudent(userId)
        1 * groupRepository.save(group) >> group
    }

    def "should add multiple students to the group"() {
        given:
        def groupId = GroupId.of(UUID.randomUUID())
        def studentBasicDataList = [
            new StudentBasicData("John", "Doe", "john.doe@example.com", "123456"),
            new StudentBasicData("Jane", "Smith", "jane.smith@example.com", "789012")
        ]
        def userIds = [
            UserId.of(UUID.randomUUID()),
            UserId.of(UUID.randomUUID())
        ]
        def group = Mock(Group)

        when:
        groupService.addStudents(groupId, studentBasicDataList)

        then:
        1 * userCommandService.importStudents(studentBasicDataList) >> userIds
        1 * groupRepository.findById(groupId.uuid) >> Optional.of(group)
        1 * group.addStudent(userIds[0])
        1 * group.addStudent(userIds[1])
        1 * groupRepository.save(group) >> group
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
        def resourceTypeRowView = CloudResourceTypeRowView.builder()
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
        1 * cloudResourceAccessQueryService.getCloudResourceTypesDetails(cloudResourceAccessIds) >> cloudResourceTypeViews

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
        def studentDetails = [studentDetail]
        def pageable = PageRequest.of(0, 10)
        def pagedStudentDetails = new PageImpl<>(studentDetails, pageable, studentDetails.size())
        userQueryService.getUserDetailsByIds(studentIds, 0, 10) >> studentDetails

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

    def "should get all groups by status"() {
        given:
        def pageable = PageRequest.of(0, 10)
        def status = GroupStatus.Type.ACTIVE
        def groupProjection = Mock(GroupRowProjection)
        def userId = UUID.randomUUID()
        def userFullName = new UserFullName(UserId.of(userId), FirstName.of("John"), LastName.of("Doe"))
        def cloudResourceType = CloudResourceType.of("ec2")

        when:
        def result = groupService.getAllGroupsByStatus(pageable, status, null)

        then:
        1 * groupRepository.findAllByStatus(0, 10, status) >> [groupProjection]
        1 * groupRepository.countByStatus(status) >> 1L
        2 * groupProjection.getLecturers() >> [userId]
        3 * groupProjection.getUuid() >> UUID.randomUUID()
        1 * groupProjection.getCloudResourceAccesses() >> [UUID.randomUUID()]
        1 * userQueryService.getFullNameForUserIds(_) >> [(UserId.of(userId)): userFullName]
        1 * cloudResourceAccessQueryService.getCloudResourceTypes(_) >> [cloudResourceType]
        result.totalElements == 1
        result.content.size() == 1
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
        result.lecturerFullNames == ["John Doe"] as Set
        result.status == GroupStatus.Type.ACTIVE.displayName
        result.description == "Test Description"
    }

    def "should give cloud resource access to group"() {
        given:
        def groupId = GroupId.of(UUID.randomUUID())
        def cloudAccessClientId = CloudAccessClientId.of("aws")
        def cloudResourceType = CloudResourceType.of("ec2")
        def group = Mock(Group)
        def cloudResourceAccessId = CloudResourceAccessId.of(UUID.randomUUID())
        def lecturerLogins = [new UserLogin("john.doe@example.com")]

        when:
        def result = groupService.giveCloudResourceAccess(groupId, cloudAccessClientId, cloudResourceType)

        then:
        1 * groupRepository.findById(groupId.uuid) >> Optional.of(group)
        1 * group.getName() >> GroupName.of("Test Group")
        1 * group.getSemester() >> Semester.of("2023Z")
        1 * group.getLecturers() >> [UserId.of(UUID.randomUUID())]
        1 * userQueryService.getUserLoginsByIds(_) >> lecturerLogins
        1 * cloudResourceAccessQueryService.isCloudGroupExists(_, cloudAccessClientId) >> false
        1 * cloudResourceAccessCommandService.createGroup(_, cloudAccessClientId, lecturerLogins, cloudResourceType)
        1 * cloudResourceAccessCommandService.giveGroupCloudResourceAccess(cloudAccessClientId, cloudResourceType, _) >> cloudResourceAccessId
        1 * group.giveCloudResourceAccess(cloudResourceAccessId)
        1 * groupRepository.save(group)

        and:
        result == cloudResourceAccessId
    }

    def "should update group"() {
        given:
        def groupId = GroupId.of(UUID.randomUUID())
        def groupDTO = GroupDTO.builder()
                .groupId(UUID.randomUUID())
                .name("Updated Group")
                .semester("2023Z")
                .lecturers([UUID.randomUUID()] as Set)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(6))
                .description("Updated Description")
                .build()
        def group = Mock(Group)

        when:
        groupService.updateGroup(groupId, groupDTO)

        then:
        1 * groupRepository.findById(groupId.uuid) >> Optional.of(group)
        1 * group.update(
                GroupName.of(groupDTO.name()),
                _ as Set,
                StartDate.of(groupDTO.startDate()),
                EndDate.of(groupDTO.endDate()),
                Description.of(groupDTO.description())
        )
        1 * groupRepository.save(group)
    }
} 