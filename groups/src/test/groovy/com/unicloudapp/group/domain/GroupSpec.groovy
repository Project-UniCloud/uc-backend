package com.unicloudapp.group.domain

import com.unicloudapp.common.domain.cloud.CloudResourceAccessId
import com.unicloudapp.common.domain.group.GroupId
import com.unicloudapp.common.domain.group.GroupName
import com.unicloudapp.common.domain.group.Semester
import com.unicloudapp.common.domain.user.UserId
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate

class GroupSpec extends Specification {

    def "should create a group with all required fields"() {
        given:
        def groupId = GroupId.of(UUID.randomUUID())
        def name = GroupName.of("Test Group")
        def status = GroupStatus.of(GroupStatus.Type.INACTIVE)
        def semester = Semester.of("2023Z")
        def startDate = StartDate.of(LocalDate.now())
        def endDate = EndDate.of(LocalDate.now().plusMonths(6))
        def lecturers = [UserId.of(UUID.randomUUID())] as Set
        def students = [] as Set
        def cloudResourceAccesses = [] as Set
        def description = Description.of("Test Description")

        when:
        def group = Group.builder()
                .groupId(groupId)
                .name(name)
                .groupStatus(status)
                .semester(semester)
                .startDate(startDate)
                .endDate(endDate)
                .lecturers(lecturers)
                .students(students)
                .cloudResourceAccesses(cloudResourceAccesses)
                .description(description)
                .build()

        then:
        group.groupId == groupId
        group.name == name
        group.groupStatus == status
        group.semester == semester
        group.startDate == startDate
        group.endDate == endDate
        group.lecturers == lecturers
        group.students == students
        group.cloudResourceAccesses == cloudResourceAccesses
        group.description == description
    }

    def "should add a student to the group"() {
        given:
        def group = Group.builder()
                .groupId(GroupId.of(UUID.randomUUID()))
                .name(GroupName.of("Test Group"))
                .groupStatus(GroupStatus.of(GroupStatus.Type.INACTIVE))
                .semester(Semester.of("2023Z"))
                .startDate(StartDate.of(LocalDate.now()))
                .endDate(EndDate.of(LocalDate.now().plusMonths(6)))
                .lecturers([] as Set)
                .students([] as Set)
                .cloudResourceAccesses([] as Set)
                .description(Description.of("Test Description"))
                .build()
        def studentId = UserId.of(UUID.randomUUID())

        when:
        group.addStudent(studentId)

        then:
        group.students.size() == 1
        group.students.contains(studentId)
    }

    def "should give cloud resource access to the group"() {
        given:
        def group = Group.builder()
                .groupId(GroupId.of(UUID.randomUUID()))
                .name(GroupName.of("Test Group"))
                .groupStatus(GroupStatus.of(GroupStatus.Type.INACTIVE))
                .semester(Semester.of("2023Z"))
                .startDate(StartDate.of(LocalDate.now()))
                .endDate(EndDate.of(LocalDate.now().plusMonths(6)))
                .lecturers([] as Set)
                .students([] as Set)
                .cloudResourceAccesses([] as Set)
                .description(Description.of("Test Description"))
                .build()
        def cloudResourceAccessId = CloudResourceAccessId.of(UUID.randomUUID())

        when:
        group.giveCloudResourceAccess(cloudResourceAccessId)

        then:
        group.cloudResourceAccesses.size() == 1
        group.cloudResourceAccesses.contains(cloudResourceAccessId)
    }

    def "should update group details"() {
        given:
        def group = Group.builder()
                .groupId(GroupId.of(UUID.randomUUID()))
                .name(GroupName.of("Old Name"))
                .groupStatus(GroupStatus.of(GroupStatus.Type.INACTIVE))
                .semester(Semester.of("2023Z"))
                .startDate(StartDate.of(LocalDate.now()))
                .endDate(EndDate.of(LocalDate.now().plusMonths(6)))
                .lecturers([] as Set)
                .students([] as Set)
                .cloudResourceAccesses([] as Set)
                .description(Description.of("Old Description"))
                .build()

        def newName = GroupName.of("New Name")
        def newLecturers = [UserId.of(UUID.randomUUID())] as Set
        def newStartDate = StartDate.of(LocalDate.now().plusDays(1))
        def newEndDate = EndDate.of(LocalDate.now().plusMonths(7))
        def newDescription = Description.of("New Description")

        when:
        group.update(newName, newLecturers, newStartDate, newEndDate, newDescription)

        then:
        group.name == newName
        group.lecturers == newLecturers
        group.startDate == newStartDate
        group.endDate == newEndDate
        group.description == newDescription
    }

    def "should identify empty description"() {
        expect:
        Description.of(null).isEmpty()
        Description.of("").isEmpty()
        Description.of("   ").isEmpty()
        Description.of("\t").isEmpty()
        Description.of("\n").isEmpty()
    }

    def "should identify non-empty description"() {
        expect:
        !Description.of("Test Description").isEmpty()
        !Description.of("  Test Description  ").isEmpty()
        !Description.of("Test\nDescription").isEmpty()
    }
} 