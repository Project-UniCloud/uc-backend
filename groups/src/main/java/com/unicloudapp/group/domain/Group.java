package com.unicloudapp.group.domain;

import com.unicloudapp.common.domain.group.GroupId;
import com.unicloudapp.common.domain.cloud.CloudResourceAccessId;
import com.unicloudapp.common.domain.group.GroupName;
import com.unicloudapp.common.domain.group.Semester;
import com.unicloudapp.common.domain.user.UserId;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Builder(access = AccessLevel.PACKAGE)
@Getter
public class Group {

    private final GroupId groupId;
    private GroupName name;
    private GroupStatus groupStatus;
    private Semester semester;
    private StartDate startDate;
    private EndDate endDate;
    private final Set<UserId> lecturers;
    private final Set<UserId> attenders;
    private final Set<CloudResourceAccessId> cloudResourceAccesses;
    private Description description;

    public void addAttender(UserId attenderId) {
        attenders.add(attenderId);
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
        this.lecturers.addAll(lecturers);
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
    }

    static class GroupBuilder {

        GroupBuilder attenders(Set<UserId> attenders) {
            GroupBuilder.this.attenders = new HashSet<>(attenders);
            return this;
        }
    }
}
