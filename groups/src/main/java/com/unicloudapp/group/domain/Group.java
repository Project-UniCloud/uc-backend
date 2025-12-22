package com.unicloudapp.group.domain;

import static com.unicloudapp.group.domain.vo.GroupStatus.Type.ACTIVE;
import static com.unicloudapp.group.domain.vo.GroupStatus.Type.ARCHIVED;

import com.unicloudapp.common.vo.cloud.CloudResourceAccessId;
import com.unicloudapp.common.vo.group.GroupId;
import com.unicloudapp.common.vo.group.GroupName;
import com.unicloudapp.common.vo.group.Semester;
import com.unicloudapp.common.vo.user.UserId;
import com.unicloudapp.group.domain.vo.Description;
import com.unicloudapp.group.domain.vo.EndDate;
import com.unicloudapp.group.domain.vo.GroupStatus;
import com.unicloudapp.group.domain.vo.StartDate;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

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

    public void grantCloudResourceAccess(CloudResourceAccessId cloudResourceAccessId) {
        cloudResourceAccesses.add(cloudResourceAccessId);
    }

    public void update(
            GroupName name, Set<UserId> lecturers, StartDate startDate, EndDate endDate, Description description) {
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

    @SuppressWarnings("unused")
    static class GroupBuilder {

        GroupBuilder students(Set<UserId> students) {
            GroupBuilder.this.students = new HashSet<>(students);
            return this;
        }
    }
}
