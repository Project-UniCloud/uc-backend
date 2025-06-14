package com.unicloudapp.group.infrastructure.persistence;

import com.unicloudapp.common.domain.cloud.CloudAccessClientId;
import com.unicloudapp.common.domain.cloud.CloudResourceType;
import com.unicloudapp.common.domain.group.GroupName;
import com.unicloudapp.group.domain.GroupStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class GroupSpecifications {

    public static Specification<GroupEntity> hasStatus(GroupStatus status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<GroupEntity> nameLike(GroupName groupName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("groupName")),
                        "%" + groupName.getName().toLowerCase() + "%"
                );
    }

    public static Specification<GroupEntity> hasCloudClientId(CloudAccessClientId cloudClientId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("cloudClientId"), cloudClientId);
    }

    public static Specification<GroupEntity> hasResourceType(CloudResourceType resourceType) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("resourceType"), resourceType);
    }
}
