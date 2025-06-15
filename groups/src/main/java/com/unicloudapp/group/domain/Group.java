package com.unicloudapp.group.domain;

import com.unicloudapp.common.domain.cloud.CloudResourceAccessId;
import com.unicloudapp.common.domain.group.GroupId;
import com.unicloudapp.common.domain.group.GroupName;
import com.unicloudapp.common.domain.group.Semester;
import com.unicloudapp.common.domain.user.UserId;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

import static com.unicloudapp.group.domain.GroupStatus.Type.ACTIVE;
import static com.unicloudapp.group.domain.GroupStatus.Type.ARCHIVED;

@Builder(access = AccessLevel.PACKAGE)
@Getter
public class Group {

    private final GroupId groupId;
    private GroupName name;
    private GroupStatus groupStatus;
    private Semester semester;
    private StartDate startDate;
    private EndDate endDate;
    private Set<UserId> lecturers;
    private final Set<UserId> students;
    private final Set<CloudResourceAccessId> cloudResourceAccesses;
    private Description description;

    public void addStudent(UserId studentId) {
        students.add(studentId);
    }

    public void giveCloudResourceAccess(CloudResourceAccessId cloudResourceAccessId) {
        cloudResourceAccesses.add(cloudResourceAccessId);
    }

    public void update(GroupName name,
                       Set<UserId> lecturers,
                       StartDate startDate,
                       EndDate endDate,
                       Description description
    ) {
        this.name = name;
        this.lecturers = new HashSet<>(lecturers);
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
    }

    public void activate() {
        this.groupStatus = GroupStatus.of(ACTIVE);
    }

    public void archive() {
        this.groupStatus = GroupStatus.of(ARCHIVED);
    }

    static class GroupBuilder {

        GroupBuilder students(Set<UserId> students) {
            GroupBuilder.this.students = new HashSet<>(students);
            return this;
        }
    }
}
