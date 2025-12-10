package com.unicloudapp.cloud.application;

import com.unicloudapp.common.cloud.CloudResourceRowView;
import com.unicloudapp.common.group.GroupCloudDto;
import com.unicloudapp.common.group.GroupQueryService;
import com.unicloudapp.common.group.GroupUniqueName;
import com.unicloudapp.common.vo.cloud.CloudResourceAccessId;
import com.unicloudapp.common.vo.cloud.CloudResourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StatisticsServiceTest {

    CloudResourceAccessService cloudResourceAccessService;
    GroupQueryService groupQueryService;

    StatisticsService statisticsService;

    @BeforeEach
    void setUp() {
        cloudResourceAccessService = mock(CloudResourceAccessService.class);
        groupQueryService = mock(GroupQueryService.class);
        statisticsService = new StatisticsService(cloudResourceAccessService, groupQueryService);
    }

    @Test
    @DisplayName("getTotalCosts returns zeros when there are no active groups")
    void getTotalCosts_emptyActiveGroups_returnsZeros() {
        when(groupQueryService.getActiveGroups()).thenReturn(List.of());

        StatisticsService.OverallCostValuesDto dto = statisticsService.getTotalCosts();

        assertEquals(BigDecimal.ZERO, dto.overallCostFromActiveGroups());
        assertEquals(0, dto.allActiveResourcesCount());
        assertEquals(BigDecimal.ZERO, dto.averageActiveGroupCost());
    }

    @Test
    @DisplayName("getTotalCosts aggregates overall cost, counts resources using countResources, and computes average per group")
    void getTotalCosts_nonEmpty_calculatesSumsAndAverage() {
        GroupUniqueName g1 = GroupUniqueName.fromString("AI 2024L");
        GroupUniqueName g2 = GroupUniqueName.fromString("ML 2024L");
        CloudResourceAccessId a1 = CloudResourceAccessId.of(UUID.randomUUID());
        CloudResourceAccessId a2 = CloudResourceAccessId.of(UUID.randomUUID());
        GroupCloudDto group1 = new GroupCloudDto(g1, List.of(a1));
        GroupCloudDto group2 = new GroupCloudDto(g2, List.of(a2));
        when(groupQueryService.getActiveGroups()).thenReturn(List.of(group1, group2));

        // Details per group
        CloudResourceRowView r1 = CloudResourceRowView.builder().limitUsed(new BigDecimal("10.50")).build();
        CloudResourceRowView r2 = CloudResourceRowView.builder().limitUsed(new BigDecimal("4.25")).build();
        when(cloudResourceAccessService.getCloudResourceDetails(new HashSet<>(group1.cloudResourceAccesses())))
                .thenReturn(List.of(r1));
        when(cloudResourceAccessService.getCloudResourceDetails(new HashSet<>(group2.cloudResourceAccesses())))
                .thenReturn(List.of(r2));

        when(cloudResourceAccessService.countResources(group1)).thenReturn(3);
        when(cloudResourceAccessService.countResources(group2)).thenReturn(7);

        StatisticsService.OverallCostValuesDto dto = statisticsService.getTotalCosts();

        assertEquals(new BigDecimal("14.75"), dto.overallCostFromActiveGroups());
        assertEquals(10, dto.allActiveResourcesCount());
        assertEquals(new BigDecimal("7.38"), dto.averageActiveGroupCost()); // 14.75 / 2 = 7.375 -> 7.38
    }

    @Test
    @DisplayName("getOverallCostsPerResourceType merges maps from all active groups")
    void getOverallCostsPerResourceType_mergesMaps() {
        GroupCloudDto group1 = new GroupCloudDto(GroupUniqueName.fromString("AI 2024L"), List.of(CloudResourceAccessId.of(UUID.randomUUID())));
        GroupCloudDto group2 = new GroupCloudDto(GroupUniqueName.fromString("ML 2024L"), List.of(CloudResourceAccessId.of(UUID.randomUUID())));
        when(groupQueryService.getActiveGroups()).thenReturn(List.of(group1, group2));

        CloudResourceType s3 = CloudResourceType.of("S3");
        CloudResourceType ec2 = CloudResourceType.of("EC2");

        Map<CloudResourceType, BigDecimal> g1Map = new HashMap<>();
        g1Map.put(s3, new BigDecimal("5.00"));
        Map<CloudResourceType, BigDecimal> g2Map = new HashMap<>();
        g2Map.put(ec2, new BigDecimal("3.50"));

        when(cloudResourceAccessService.getCostsByResourceTypes(group1)).thenReturn(g1Map);
        when(cloudResourceAccessService.getCostsByResourceTypes(group2)).thenReturn(g2Map);

        List<StatisticsService.CostPerResourceTypeDto> result = statisticsService.getOverallCostsPerResourceType();

        Map<String, BigDecimal> asMap = result.stream()
                .collect(Collectors.toMap(StatisticsService.CostPerResourceTypeDto::resourceType, StatisticsService.CostPerResourceTypeDto::cost));

        assertEquals(2, asMap.size());
        assertEquals(new BigDecimal("5.00"), asMap.get(s3.toString()));
        assertEquals(new BigDecimal("3.50"), asMap.get(ec2.toString()));
    }

    @Test
    @DisplayName("getTotalCostPerGroup returns sum of limitUsed per group")
    void getTotalCostPerGroup_sumsPerGroup() {
        GroupUniqueName g1 = GroupUniqueName.fromString("AI 2024L");
        GroupUniqueName g2 = GroupUniqueName.fromString("ML 2024L");
        GroupCloudDto group1 = new GroupCloudDto(g1, List.of(CloudResourceAccessId.of(UUID.randomUUID())));
        GroupCloudDto group2 = new GroupCloudDto(g2, List.of(CloudResourceAccessId.of(UUID.randomUUID())));
        when(groupQueryService.getActiveGroups()).thenReturn(List.of(group1, group2));

        CloudResourceRowView r1a = CloudResourceRowView.builder().limitUsed(new BigDecimal("2.00")).build();
        CloudResourceRowView r1b = CloudResourceRowView.builder().limitUsed(new BigDecimal("3.10")).build();
        CloudResourceRowView r2a = CloudResourceRowView.builder().limitUsed(new BigDecimal("1.25")).build();

        when(cloudResourceAccessService.getCloudResourceDetails(new HashSet<>(group1.cloudResourceAccesses())))
                .thenReturn(List.of(r1a, r1b));
        when(cloudResourceAccessService.getCloudResourceDetails(new HashSet<>(group2.cloudResourceAccesses())))
                .thenReturn(List.of(r2a));

        List<StatisticsService.CostPerGroupDto> result = statisticsService.getTotalCostPerGroup();

        Map<String, BigDecimal> asMap = result.stream()
                .collect(Collectors.toMap(StatisticsService.CostPerGroupDto::groupUniqueName, StatisticsService.CostPerGroupDto::cost));

        assertEquals(new BigDecimal("5.10"), asMap.get(g1.toString()));
        assertEquals(new BigDecimal("1.25"), asMap.get(g2.toString()));
    }

    @Test
    @DisplayName("getTotalCostInTime sums values for the same date across groups")
    void getTotalCostInTime_sumsByDateAcrossGroups() {
        GroupCloudDto group1 = new GroupCloudDto(GroupUniqueName.fromString("AI 2024L"), List.of(CloudResourceAccessId.of(UUID.randomUUID())));
        GroupCloudDto group2 = new GroupCloudDto(GroupUniqueName.fromString("ML 2024L"), List.of(CloudResourceAccessId.of(UUID.randomUUID())));
        when(groupQueryService.getActiveGroups()).thenReturn(List.of(group1, group2));

        LocalDate d1 = LocalDate.of(2024, 1, 1);
        LocalDate d2 = LocalDate.of(2024, 1, 2);

        Map<LocalDate, BigDecimal> m1 = new HashMap<>();
        m1.put(d1, new BigDecimal("1.00"));
        m1.put(d2, new BigDecimal("2.00"));
        Map<LocalDate, BigDecimal> m2 = new HashMap<>();
        m2.put(d1, new BigDecimal("3.00"));

        when(cloudResourceAccessService.getTotalCostInTime(group1)).thenReturn(m1);
        when(cloudResourceAccessService.getTotalCostInTime(group2)).thenReturn(m2);

        List<StatisticsService.CostPerMonthDto> result = statisticsService.getTotalCostInTime();

        assertEquals(2, result.size());

        Map<LocalDate, BigDecimal> asMap = result.stream()
                .collect(Collectors.toMap(StatisticsService.CostPerMonthDto::date, StatisticsService.CostPerMonthDto::cost));

        assertEquals(new BigDecimal("4.00"), asMap.get(d1));
        assertEquals(new BigDecimal("2.00"), asMap.get(d2));
        // TreeMap ordering by date
        assertEquals(List.of(d1, d2), result.stream().map(StatisticsService.CostPerMonthDto::date).toList());
    }
}
