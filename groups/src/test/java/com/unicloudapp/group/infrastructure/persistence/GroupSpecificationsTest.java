package com.unicloudapp.group.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

import com.unicloudapp.common.vo.cloud.CloudResourceAccessId;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.jpa.domain.Specification;

class GroupSpecificationsTest {

    @Test
    void hasCloudResourceAccess_usesOrPredicate_whenMultipleIdsProvided() {
        // given
        CloudResourceAccessId id1 = CloudResourceAccessId.of(UUID.randomUUID());
        CloudResourceAccessId id2 = CloudResourceAccessId.of(UUID.randomUUID());
        Set<CloudResourceAccessId> ids = Set.of(id1, id2);

        Specification<GroupEntity> spec = GroupSpecifications.hasCloudResourceAccess(ids);

        Root<GroupEntity> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Path<Object> path = mock(Path.class);

        when(root.get("cloudResourceAccesses")).thenReturn(path);

        Predicate p1 = mock(Predicate.class);
        Predicate p2 = mock(Predicate.class);
        when(cb.isMember(eq(id1.getValue()), any(Expression.class))).thenReturn(p1);
        when(cb.isMember(eq(id2.getValue()), any(Expression.class))).thenReturn(p2);

        // when
        spec.toPredicate(root, query, cb);

        // then
        // We want to verify that cb.or is called with predicates
        ArgumentCaptor<Predicate[]> captor = ArgumentCaptor.forClass(Predicate[].class);
        verify(cb).or(captor.capture());

        Predicate[] predicates = captor.getValue();
        assertThat(predicates).containsExactlyInAnyOrder(p1, p2);
        verify(cb, never()).and(any(Predicate[].class));
    }
}
