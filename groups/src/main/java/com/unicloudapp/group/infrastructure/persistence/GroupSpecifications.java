package com.unicloudapp.group.infrastructure.persistence;

import com.unicloudapp.common.domain.cloud.CloudResourceAccessId;
import com.unicloudapp.common.domain.group.GroupName;
import com.unicloudapp.group.domain.GroupStatus;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class GroupSpecifications {

    public static Specification<GroupEntity> hasStatus(GroupStatus status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status.getStatus());
    }

    public static Specification<GroupEntity> nameLike(GroupName groupName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + groupName.getName().toLowerCase() + "%"
                );
    }

    public static Specification<GroupEntity> hasCloudResourceAccess(Set<CloudResourceAccessId> cloudResourceAccesses) {
        return (root, query, cb) -> {
            if (cloudResourceAccesses == null) {
                return cb.conjunction();
            }

            if (cloudResourceAccesses.isEmpty()) {
                return cb.disjunction();
            }

            var values = cloudResourceAccesses.stream()
                    .map(CloudResourceAccessId::getValue)
                    .toList();

            var predicates = values.stream()
                    .map(id -> cb.isMember(id, root.get("cloudResourceAccesses")))
                    .toArray(Predicate[]::new);

            return cb.or(predicates);
        };
    }
}
