package com.unicloudapp.group.infrastructure.persistence;

import com.unicloudapp.common.vo.cloud.CloudResourceAccessId;
import com.unicloudapp.common.vo.group.GroupName;
import com.unicloudapp.common.vo.user.UserId;
import com.unicloudapp.group.domain.vo.GroupStatus;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class GroupSpecifications {

    public static Specification<@NotNull GroupEntity> hasStatus(GroupStatus status) {
        return (root, _, criteriaBuilder) -> criteriaBuilder.equal(root.get("groupStatus"), status.getStatus());
    }

    public static Specification<@NotNull GroupEntity> nameLike(GroupName groupName) {
        return (root, _, criteriaBuilder) -> criteriaBuilder.like(
                criteriaBuilder.lower(root.get("name")),
                "%" + groupName.getName().toLowerCase() + "%");
    }

    public static Specification<@NotNull GroupEntity> hasCloudResourceAccess(
            Set<CloudResourceAccessId> cloudResourceAccesses) {
        return (root, _, cb) -> {
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

    public static Specification<@NotNull GroupEntity> hasPastExpiresDate() {
        return (root, _, cb) ->
                cb.and(cb.isNotNull(root.get("endDate")), cb.lessThan(root.get("endDate"), LocalDate.now()));
    }

    public static Specification<@NotNull GroupEntity> isLecturer(UserId lecturerId) {
        return (root, _, cb) -> cb.isMember(lecturerId.getValue(), root.get("lecturers"));
    }
}
