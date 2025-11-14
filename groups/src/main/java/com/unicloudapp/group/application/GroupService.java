package com.unicloudapp.group.application;

import com.unicloudapp.common.cloud.CloudResourceAccessCommandService;
import com.unicloudapp.common.cloud.CloudResourceAccessDetailsDto;
import com.unicloudapp.common.cloud.CloudResourceAccessQueryService;
import com.unicloudapp.common.cloud.CloudResourceRowView;
import com.unicloudapp.common.group.GroupUniqueName;
import com.unicloudapp.common.user.*;
import com.unicloudapp.common.vo.Email;
import com.unicloudapp.common.vo.cloud.CloudAccessClientId;
import com.unicloudapp.common.vo.cloud.CloudResourceAccessId;
import com.unicloudapp.common.vo.cloud.CloudResourceType;
import com.unicloudapp.common.vo.cloud.CostLimit;
import com.unicloudapp.common.vo.group.GroupId;
import com.unicloudapp.common.vo.group.GroupName;
import com.unicloudapp.common.vo.group.Semester;
import com.unicloudapp.common.vo.user.UserId;
import com.unicloudapp.common.vo.user.UserLogin;
import com.unicloudapp.group.application.port.GroupRepositoryPort;
import com.unicloudapp.group.domain.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
public class GroupService {

    private final GroupRepositoryPort groupRepository;
    private final GroupFactory groupFactory;
    private final UserQueryService userQueryService;
    private final CloudResourceAccessQueryService cloudResourceAccessQueryService;
    private final CloudResourceAccessCommandService cloudResourceAccessCommandService;
    private final UserCommandService userCommandService;

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

    public void addStudent(GroupId groupId, StudentBasicData studentBasicData) {
        boolean isUserExists = userQueryService.existsByLogin(studentBasicData.getLogin());
        UserId userId;
        if (isUserExists) {
            userId = userQueryService.getUserDetailsByUsername(UserLogin.of(studentBasicData.getLogin()))
                    .orElseThrow()
                    .userId();
        } else {
            userId = userCommandService.createStudent(studentBasicData);
        }
        Group group = groupRepository.findById(groupId.getUuid())
                .orElseThrow();
        group.addStudent(userId);
        List<CloudResourceRowView> cloudResourceTypesDetails =
                cloudResourceAccessQueryService.getCloudResourceDetails(group.getCloudResourceAccesses());
        if (group.getGroupStatus().getStatus() == GroupStatus.Type.ACTIVE) {
            Set<String> collect = cloudResourceTypesDetails.stream()
                    .map(CloudResourceRowView::clientId)
                    .collect(Collectors.toSet());
            collect.forEach(s -> {
                        Email email;
                        if (studentBasicData.getEmail() == null || studentBasicData.getEmail().isBlank()) {
                            email = Email.empty();
                        } else {
                            try {
                                email = Email.of(studentBasicData.getEmail());
                            } catch (IllegalArgumentException ex) {
                                email = Email.empty();
                            }
                        }
                        cloudResourceAccessCommandService.createUsers(
                                CloudAccessClientId.of(s),
                                List.of(Map.entry(UserLogin.of(studentBasicData.getLogin()), email)),
                                GroupUniqueName.builder()
                                        .semester(group.getSemester())
                                        .groupName(group.getName())
                                        .build()
                        );
                    }
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

    public void addStudents(GroupId groupId, List<StudentBasicData> studentBasicData) {
        Group group = groupRepository.findById(groupId.getUuid())
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));
        List<UserId> importedStudents = userCommandService.importStudents(studentBasicData);
        importedStudents.forEach(group::addStudent);
        List<CloudResourceRowView> cloudResourceTypesDetails =
                cloudResourceAccessQueryService.getCloudResourceDetails(group.getCloudResourceAccesses());
        if (group.getGroupStatus().getStatus() == GroupStatus.Type.ACTIVE) {
            Set<String> collect = cloudResourceTypesDetails.stream()
                    .map(CloudResourceRowView::clientId)
                    .collect(Collectors.toSet());
            collect.forEach(s -> cloudResourceAccessCommandService.createUsers(
                            CloudAccessClientId.of(s),
                            studentBasicData.stream()
                                    .map(studentBasic ->
                                            Map.entry(UserLogin.of(studentBasic.getLogin()), Email.of(studentBasicData.getFirst().getEmail())))
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
            CloudResourceType cloudResourceType,
            CostLimit costLimit
    ) {
        Group group = groupRepository.findById(groupId.getUuid())
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));
        GroupUniqueName groupUniqueName = GroupUniqueName.builder()
                .groupName(group.getName())
                .semester(group.getSemester())
                .build();
        boolean hasGroupCloudResourceType = cloudResourceAccessQueryService.getCloudResourceDetails(group.getCloudResourceAccesses())
                .stream()
                .anyMatch(cloudResourceTypeRowView ->
                        cloudResourceTypeRowView.name().equals(cloudResourceType.getName())
                                && cloudResourceTypeRowView.clientId().equals(cloudAccessClientId.getValue())
                );
        if (hasGroupCloudResourceType) {
            throw new RuntimeException("Group already has access to cloud resource type: " + cloudResourceType.getName());
        }
        List<Map.Entry<UserLogin, Email>> lecturers = userQueryService.getUserLoginsAndEmailsByIds(
                group.getLecturers()
        );
        if (!cloudResourceAccessQueryService.isCloudGroupExists(groupUniqueName, cloudAccessClientId)) {
            cloudResourceAccessCommandService.createGroup(groupUniqueName, cloudAccessClientId, lecturers, cloudResourceType);
        }
        CloudResourceAccessId cloudResourceAccessId = cloudResourceAccessCommandService.giveGroupCloudResourceAccess(
                cloudAccessClientId,
                cloudResourceType,
                groupUniqueName,
                costLimit
        );
        group.giveCloudResourceAccess(cloudResourceAccessId);
        groupRepository.save(group);
        return cloudResourceAccessId;
    }

    public List<CloudResourceRowView> getCloudResourceAccesses(GroupId groupId) {
        Group group = groupRepository.findById(groupId.getUuid())
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));
        Set<CloudResourceAccessId> cloudResourceAccesses = group.getCloudResourceAccesses();
        return cloudResourceAccessQueryService.getCloudResourceDetails(cloudResourceAccesses);
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
        List<CloudResourceRowView> resourceTypesDetails =
                cloudResourceAccessQueryService.getCloudResourceDetails(group.getCloudResourceAccesses());
        List<Map.Entry<UserLogin, Email>> studentLogins = userQueryService.getUserLoginsAndEmailsByIds(group.getStudents());
        if (!studentLogins.isEmpty()) {
            resourceTypesDetails.stream()
                    .map(CloudResourceRowView::clientId)
                    .forEach(s -> cloudResourceAccessCommandService.createUsers(
                            CloudAccessClientId.of(s),
                            studentLogins,
                            groupUniqueName
                    ));
        }
        group.getCloudResourceAccesses().forEach(cloudResourceAccessCommandService::activateCloudResource);
        groupRepository.save(group);
    }

    //TODO implement taking away cloud resource access to students
    public void archive(GroupId groupId) {
        Group group = groupRepository.findById(groupId.getUuid())
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));
        group.archive();
        GroupUniqueName groupUniqueName = GroupUniqueName.builder()
                .semester(group.getSemester())
                .groupName(group.getName())
                .build();
        cloudResourceAccessCommandService.cleanUpResources(group.getCloudResourceAccesses(), groupUniqueName, false);
        group.getCloudResourceAccesses().forEach(cloudResourceAccessId ->
                deactivateCloudResourcesAccess(groupId, cloudResourceAccessId)
        );
        groupRepository.save(group);
    }

    public CloudResourceAccessDetailsDto getCloudResourceAccess(
            GroupId groupId,
            CloudResourceAccessId cloudResourceAccessId
    ) {
        Group group = groupRepository.findById(groupId.getUuid())
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));
        Set<CloudResourceAccessId> cloudResourceAccesses = group.getCloudResourceAccesses();
        List<CloudResourceRowView> cloudResourceDetails =
                cloudResourceAccessQueryService.getCloudResourceDetails(cloudResourceAccesses);
        CloudResourceRowView cloudResourceDetailsFirst = cloudResourceDetails.getFirst();
        if (!cloudResourceAccessId.getValue().equals(cloudResourceDetailsFirst.id())) {
            throw new RuntimeException("Cloud resource access not found with id: " + cloudResourceAccessId);
        }
        return CloudResourceAccessDetailsDto.builder()
                .id(cloudResourceDetailsFirst.id())
                .cron(cloudResourceDetailsFirst.cronCleanupSchedule())
                .limit(cloudResourceDetailsFirst.costLimit())
                .expiresAt(cloudResourceDetailsFirst.expiresAt())
                .status(cloudResourceDetailsFirst.status())
                .build();
    }

    public void saveCloudResourceAccess(GroupId groupId, CloudResourceAccessDetailsDto request) {
        Group group = groupRepository.findById(groupId.getUuid())
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));
        GroupUniqueName groupUniqueName = GroupUniqueName.builder()
                .semester(group.getSemester())
                .groupName(group.getName())
                .build();
        cloudResourceAccessCommandService.updateGroupCloudResourceAccess(request, groupUniqueName);
    }

    public void deactivateCloudResourcesAccess(GroupId groupId, CloudResourceAccessId cloudResourceAccessId) {
        Group group = groupRepository.findById(groupId.getUuid())
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));
        GroupUniqueName groupUniqueName = GroupUniqueName.builder()
                .groupName(group.getName())
                .semester(group.getSemester())
                .build();
        List<CloudResourceRowView> cloudResourceDetails =
                cloudResourceAccessQueryService.getCloudResourceDetails(Set.of(cloudResourceAccessId));
        cloudResourceAccessCommandService.removeGroup(groupUniqueName, CloudAccessClientId.of(cloudResourceDetails.getFirst().clientId()));
        cloudResourceAccessCommandService.deactivateCloudResourceAccess(cloudResourceAccessId);
    }
}
