package com.unicloudapp.group.application;

import com.unicloudapp.common.cloud.CloudResourceAccessCommandService;
import com.unicloudapp.common.cloud.CloudResourceAccessQueryService;
import com.unicloudapp.common.cloud.CloudResourceTypeRowView;
import com.unicloudapp.common.domain.cloud.CloudAccessClientId;
import com.unicloudapp.common.domain.cloud.CloudResourceAccessId;
import com.unicloudapp.common.domain.cloud.CloudResourceType;
import com.unicloudapp.common.domain.group.GroupId;
import com.unicloudapp.common.domain.group.GroupName;
import com.unicloudapp.common.domain.group.Semester;
import com.unicloudapp.common.domain.user.UserId;
import com.unicloudapp.common.domain.user.UserLogin;
import com.unicloudapp.common.group.GroupUniqueName;
import com.unicloudapp.common.user.*;
import com.unicloudapp.group.domain.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class GroupService {

    private final GroupRepositoryPort groupRepository;
    private final GroupFactory groupFactory;
    private final UserQueryService userQueryService;
    private final CloudResourceAccessQueryService cloudResourceAccessQueryService;
    private final CloudResourceAccessCommandService cloudResourceAccessCommandService;
    private final UserCommandService userCommandService;

    @Transactional
    public Group createGroup(GroupDTO groupDTO) {
        if (!groupDTO.endDate().isAfter(groupDTO.startDate())) {
            throw new RuntimeException("Start date cannot be after end date.");
        }
        boolean isGroupWithSameNameAndSemesterExists = groupRepository.existsByNameAndSemester(
                GroupName.of(groupDTO.name()),
                Semester.of(groupDTO.semester())
        );
        if (isGroupWithSameNameAndSemesterExists) {
            throw new RuntimeException("Group with name " + groupDTO.name() + " and semester " + groupDTO.semester() + " already exists.");
        }
        Group group = groupFactory.create(
                groupDTO.name(),
                groupDTO.semester(),
                groupDTO.lecturers(),
                groupDTO.startDate(),
                groupDTO.endDate(),
                groupDTO.description()
        );
        return groupRepository.save(group);
    }

    @Transactional
    public void addStudent(GroupId groupId, StudentBasicData studentBasicData) {
        UserId userId = userCommandService.createStudent(studentBasicData);
        Group group = groupRepository.findById(groupId.getUuid())
                .orElseThrow();
        group.addStudent(userId);
        List<CloudResourceTypeRowView> cloudResourceTypesDetails =
                cloudResourceAccessQueryService.getCloudResourceTypesDetails(group.getCloudResourceAccesses());
        if (group.getGroupStatus().getStatus() == GroupStatus.Type.ACTIVE) {
            Set<String> collect = cloudResourceTypesDetails.stream()
                    .map(CloudResourceTypeRowView::clientId)
                    .collect(Collectors.toSet());
            collect.forEach(s -> cloudResourceAccessCommandService.createUsers(
                        CloudAccessClientId.of(s),
                        List.of(UserLogin.of(studentBasicData.getLogin())),
                        GroupUniqueName.builder()
                                .semester(group.getSemester())
                                .groupName(group.getName())
                                .build()
                    )
            );
        }
        groupRepository.save(group);
    }

    public GroupDetailsView findById(UUID groupId) {
        GroupDetailsProjection details = groupRepository.findGroupDetailsByUuid(groupId);
        Set<UserFullNameDTO> lecturers = userQueryService.getFullNameForUserIds(
                        details.getLecturers()
                                .stream()
                                .map(UserId::of)
                                .toList()
                ).values()
                .stream()
                .map(UserFullNameDTO::from)
                .collect(Collectors.toSet());
        return GroupDetailsView.builder()
                .groupId(details.getUuid())
                .name(details.getName())
                .lecturerFullNames(lecturers)
                .status(details.getGroupStatus().getDisplayName())
                .description(details.getDescription())
                .endDate(details.getEndDate())
                .startDate(details.getStartDate())
                .semester(details.getSemester())
                .build();
    }

    public Page<UserDetails> getStudentsDetailsByGroupId(GroupId groupId, Pageable pageable) {
        Group group = groupRepository.findById(groupId.getUuid())
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));
        int size = pageable.getPageSize();
        int page = pageable.getPageNumber();
        int offset = page * size;
        return userQueryService.getUserDetailsByIds(
                group.getStudents(), offset, size
        );
    }

    @Transactional
    public void addStudents(GroupId groupId, List<StudentBasicData> studentBasicData) {
        Group group = groupRepository.findById(groupId.getUuid())
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));
        List<UserId> importedStudents = userCommandService.importStudents(studentBasicData);
        importedStudents.forEach(group::addStudent);
        List<CloudResourceTypeRowView> cloudResourceTypesDetails =
                cloudResourceAccessQueryService.getCloudResourceTypesDetails(group.getCloudResourceAccesses());
        if (group.getGroupStatus().getStatus() == GroupStatus.Type.ACTIVE) {
            Set<String> collect = cloudResourceTypesDetails.stream()
                    .map(CloudResourceTypeRowView::clientId)
                    .collect(Collectors.toSet());
            collect.forEach(s -> cloudResourceAccessCommandService.createUsers(
                            CloudAccessClientId.of(s),
                            studentBasicData.stream()
                                    .map(studentBasic -> UserLogin.of(studentBasic.getLogin()))
                                    .toList(),
                            GroupUniqueName.builder()
                                    .semester(group.getSemester())
                                    .groupName(group.getName())
                                    .build()
                    )
            );
        }
        groupRepository.save(group);
    }

    public CloudResourceAccessId giveCloudResourceAccess(
            GroupId groupId,
            CloudAccessClientId cloudAccessClientId,
            CloudResourceType cloudResourceType
    ) {
        Group group = groupRepository.findById(groupId.getUuid())
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));
        GroupUniqueName groupUniqueName = GroupUniqueName.builder()
                .groupName(group.getName())
                .semester(group.getSemester())
                .build();
        boolean hasGroupCloudResourceType = cloudResourceAccessQueryService.getCloudResourceTypesDetails(group.getCloudResourceAccesses())
                .stream()
                .anyMatch(cloudResourceTypeRowView ->
                        cloudResourceTypeRowView.name().equals(cloudResourceType.getName())
                                && cloudResourceTypeRowView.clientId().equals(cloudAccessClientId.getValue())
                );
        if (hasGroupCloudResourceType) {
            throw new RuntimeException("Group already has access to cloud resource type: " + cloudResourceType.getName());
        }
        List<UserLogin> lecturerLogins = userQueryService.getUserLoginsByIds(
                group.getLecturers()
        );
        if (!cloudResourceAccessQueryService.isCloudGroupExists(groupUniqueName, cloudAccessClientId)) {
            cloudResourceAccessCommandService.createGroup(groupUniqueName, cloudAccessClientId, lecturerLogins, cloudResourceType);
        }
        CloudResourceAccessId cloudResourceAccessId = cloudResourceAccessCommandService.giveGroupCloudResourceAccess(
                cloudAccessClientId,
                cloudResourceType,
                groupUniqueName
        );
        group.giveCloudResourceAccess(cloudResourceAccessId);
        groupRepository.save(group);
        return cloudResourceAccessId;
    }

    public List<CloudResourceTypeRowView> getCloudResourceAccesses(GroupId groupId) {
        Group group = groupRepository.findById(groupId.getUuid())
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));
        Set<CloudResourceAccessId> cloudResourceAccesses = group.getCloudResourceAccesses();
        return cloudResourceAccessQueryService.getCloudResourceTypesDetails(cloudResourceAccesses);
    }

    public void updateGroup(GroupId groupId, GroupDTO groupDTO) {
        if (!groupDTO.endDate().isAfter(groupDTO.startDate())) {
            throw new RuntimeException("End date is not after start date");
        }
        Group group = groupRepository.findById(groupId.getUuid())
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));
        group.update(
                GroupName.of(groupDTO.name()),
                groupDTO.lecturers().stream().map(UserId::of).collect(Collectors.toSet()),
                StartDate.of(groupDTO.startDate()),
                EndDate.of(groupDTO.endDate()),
                Description.of(groupDTO.description())
        );
        groupRepository.save(group);
    }

    public Page<GroupRowView> getGroupsByFilter(
            GroupFilterCriteria criteria,
            Pageable pageable
    ) {
        Set<CloudResourceAccessId> accessIds = null;

        if (criteria.getCloudClientId() != null) {
            if (criteria.getResourceType() == null) {
                accessIds = cloudResourceAccessQueryService.getCloudResourceAccessesByCloudClientId(
                        criteria.getCloudClientId()
                );
            } else {
                accessIds = cloudResourceAccessQueryService.getCloudResourceAccessesByCloudClientIdAndResourceType(
                        criteria.getCloudClientId(),
                        criteria.getResourceType()
                );
            }
        }

        Page<GroupRowProjection> groups = (accessIds != null)
                ? groupRepository.findAllByCriteriaAndContainsCloudResourceAccess(criteria, pageable, accessIds)
                : groupRepository.findAllByCriteria(criteria, pageable);

        Map<UserId, UserFullName> userFullNames = userQueryService.getFullNameForUserIds(
                groups.stream()
                        .flatMap(g -> g.getLecturers().stream().map(UserId::of))
                        .toList()
        );

        Map<UUID, Set<CloudResourceType>> cloudResourceTypes = groups.stream()
                .collect(Collectors.toMap(
                        GroupRowProjection::getUuid,
                        group -> cloudResourceAccessQueryService.getCloudResourceTypes(
                                group.getCloudResourceAccesses()
                                        .stream()
                                        .map(CloudResourceAccessId::of)
                                        .collect(Collectors.toSet())
                        )
                ));

        List<GroupRowView> groupViews = groups.stream()
                .map(group -> {
                    String joinedLecturers = group.getLecturers()
                            .stream()
                            .map(uuid -> userFullNames.get(UserId.of(uuid)))
                            .filter(Objects::nonNull)
                            .map(UserFullName::getFullName)
                            .collect(Collectors.joining(", "));

                    String joinedAccessList = cloudResourceTypes.entrySet()
                            .stream()
                            .filter(entry -> entry.getKey().equals(group.getUuid()))
                            .flatMap(entry -> entry.getValue().stream().map(CloudResourceType::getName))
                            .collect(Collectors.joining(", "));

                    return new GroupRowView(
                            group.getUuid(),
                            group.getName(),
                            group.getSemester(),
                            group.getEndDate(),
                            joinedLecturers,
                            joinedAccessList
                    );
                })
                .toList();
        return new PageImpl<>(groupViews, groups.getPageable(), groups.getTotalPages());
    }

    public void activate(GroupId groupId) {
        Group group = groupRepository.findById(groupId.getUuid())
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));
        group.activate();
        GroupUniqueName groupUniqueName = GroupUniqueName.builder()
                .semester(group.getSemester())
                .groupName(group.getName())
                .build();
        List<CloudResourceTypeRowView> resourceTypesDetails =
                cloudResourceAccessQueryService.getCloudResourceTypesDetails(group.getCloudResourceAccesses());
        List<UserLogin> studentLogins = userQueryService.getUserLoginsByIds(group.getStudents());
        resourceTypesDetails.stream()
                .map(CloudResourceTypeRowView::clientId)
                .forEach(s -> cloudResourceAccessCommandService.createUsers(
                        CloudAccessClientId.of(s),
                        studentLogins,
                        groupUniqueName
                ));
        groupRepository.save(group);
    }

    //TODO implement taking away cloud resource access to students
    public void archive(GroupId groupId) {
        Group group = groupRepository.findById(groupId.getUuid())
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));
        group.archive();
        groupRepository.save(group);
    }
}
