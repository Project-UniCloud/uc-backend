package com.unicloudapp.group.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.unicloudapp.common.vo.group.GroupId;
import com.unicloudapp.group.application.port.GroupRepositoryPort;
import com.unicloudapp.group.domain.vo.GroupStatus;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;

class ExpiredGroupsCleanerTest {

    @Test
    @DisplayName("archiveExpiredGroups is scheduled via property and cron computes next run with a fixed clock time")
    void scheduledAnnotation_and_cronNextExecution() throws Exception {
        // Reflect annotation
        Method m = ExpiredGroupsCleaner.class.getDeclaredMethod("archiveExpiredGroups");
        Scheduled scheduled = m.getAnnotation(Scheduled.class);
        assertNotNull(scheduled, "@Scheduled should be present on archiveExpiredGroups");
        assertEquals("${groups.archiveExpiredGroupsCron}", scheduled.cron());

        // From bootstrap application.yml
        String cronFromConfig = "0 0 0 * * *"; // every midnight
        CronExpression expr = CronExpression.parse(cronFromConfig);

        // Fixed reference time from the issue description
        LocalDateTime ref = LocalDateTime.of(2025, 11, 10, 15, 26, 0);
        LocalDateTime next = expr.next(ref);
        assertNotNull(next);
        assertEquals(LocalDateTime.of(2025, 11, 11, 0, 0, 0), next);
    }

    @Test
    @DisplayName("archiveExpiredGroups fetches ACTIVE groups and archives each of them")
    void archiveExpiredGroups_callsArchiveForEachActiveGroup() {
        GroupRepositoryPort repository = mock(GroupRepositoryPort.class);
        GroupService groupService = mock(GroupService.class);

        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        GroupRowProjection p1 = new GroupRowProjection() {
            @Override
            public UUID getUuid() {
                return id1;
            }

            @Override
            public String getName() {
                return "G1";
            }

            @Override
            public String getSemester() {
                return "2024L";
            }

            @Override
            public java.time.LocalDate getEndDate() {
                return java.time.LocalDate.of(2024, 12, 31);
            }

            @Override
            public java.util.Set<UUID> getLecturers() {
                return java.util.Set.of();
            }

            @Override
            public java.util.Set<UUID> getCloudResourceAccesses() {
                return java.util.Set.of();
            }
        };
        GroupRowProjection p2 = new GroupRowProjection() {
            @Override
            public UUID getUuid() {
                return id2;
            }

            @Override
            public String getName() {
                return "G2";
            }

            @Override
            public String getSemester() {
                return "2024L";
            }

            @Override
            public java.time.LocalDate getEndDate() {
                return java.time.LocalDate.of(2024, 12, 31);
            }

            @Override
            public java.util.Set<UUID> getLecturers() {
                return java.util.Set.of();
            }

            @Override
            public java.util.Set<UUID> getCloudResourceAccesses() {
                return java.util.Set.of(UUID.randomUUID());
            }
        };

        when(repository.findAllByCriteria(any(GroupFilterCriteria.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(p1, p2)));

        ExpiredGroupsCleaner cleaner = new ExpiredGroupsCleaner(repository, groupService);
        cleaner.archiveExpiredGroups();

        ArgumentCaptor<GroupFilterCriteria> criteriaCaptor = ArgumentCaptor.forClass(GroupFilterCriteria.class);
        verify(repository).findAllByCriteria(criteriaCaptor.capture(), eq(Pageable.unpaged()));
        GroupFilterCriteria criteria = criteriaCaptor.getValue();
        assertEquals(GroupStatus.of(GroupStatus.Type.ACTIVE), criteria.getStatus());

        verify(groupService, times(1)).archive(GroupId.of(id1));
        verify(groupService, times(1)).archive(GroupId.of(id2));
        verifyNoMoreInteractions(groupService);
    }
}
