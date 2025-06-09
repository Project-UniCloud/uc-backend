package com.unicloudapp.group.domain

import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate

class GroupFactorySpec extends Specification {

    @Subject
    GroupFactory groupFactory = new GroupFactory()

    def "should create a new group with all required fields"() {
        given:
        def groupName = "Test Group"
        def semester = "2023Z"
        def lecturers = [UUID.randomUUID()] as Set
        def startDate = LocalDate.now()
        def endDate = LocalDate.now().plusMonths(6)
        def description = "Test Description"

        when:
        def group = groupFactory.create(groupName, semester, lecturers, startDate, endDate, description)

        then:
        group.name.getName() == groupName
        group.semester.toString() == semester
        group.lecturers.size() == 1
        group.lecturers.first().value == lecturers.first()
        group.startDate.value == startDate
        group.endDate.value == endDate
        group.description.value == description
        group.groupStatus.status == GroupStatus.Type.INACTIVE
        group.students.empty
        group.cloudResourceAccesses.empty
        group.groupId != null
    }

    def "should restore a group with all fields"() {
        given:
        def groupId = UUID.randomUUID()
        def name = "Test Group"
        def status = GroupStatus.Type.ACTIVE
        def semester = "2023Z"
        def startDate = LocalDate.now()
        def endDate = LocalDate.now().plusMonths(6)
        def lecturers = [UUID.randomUUID()] as Set
        def students = [UUID.randomUUID()] as Set
        def cloudResourceAccesses = [UUID.randomUUID()] as Set
        def description = "Test Description"

        when:
        def group = groupFactory.restore(
                groupId,
                name,
                status,
                semester,
                startDate,
                endDate,
                lecturers,
                students,
                cloudResourceAccesses,
                description
        )

        then:
        group.groupId.uuid == groupId
        group.name.name == name
        group.groupStatus.status == status
        group.semester.toString() == semester
        group.startDate.value == startDate
        group.endDate.value == endDate
        group.lecturers.size() == 1
        group.lecturers.first().value == lecturers.first()
        group.students.size() == 1
        group.students.first().value == students.first()
        group.cloudResourceAccesses.size() == 1
        group.cloudResourceAccesses.first().value == cloudResourceAccesses.first()
        group.description.value == description
    }
} 