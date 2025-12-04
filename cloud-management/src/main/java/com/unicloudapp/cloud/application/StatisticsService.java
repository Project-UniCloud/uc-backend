package com.unicloudapp.cloud.application;

import com.unicloudapp.common.cloud.CloudResourceRowView;
import com.unicloudapp.common.group.GroupCloudDto;
import com.unicloudapp.common.group.GroupQueryService;
import com.unicloudapp.common.group.GroupUniqueName;
import com.unicloudapp.common.vo.cloud.CloudResourceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {

    private final CloudResourceAccessService cloudResourceAccessService;
    private final GroupQueryService groupQueryService;

    public OverallCostValuesDto getTotalCosts() {
        List<GroupCloudDto> activeGroups = groupQueryService.getActiveGroups();
        BigDecimal overallCostFromActiveGroups = BigDecimal.ZERO;
        int allActiveResourcesCount = 0;
        BigDecimal averageActiveGroupCost = BigDecimal.ZERO;
        for (GroupCloudDto activeGroup : activeGroups) {
            List<CloudResourceRowView> cloudResourceDetails
                    = cloudResourceAccessService.getCloudResourceDetails(new HashSet<>(activeGroup.cloudResourceAccesses()));
            for (CloudResourceRowView cloudResourceDetail : cloudResourceDetails) {
                overallCostFromActiveGroups = overallCostFromActiveGroups.add(cloudResourceDetail.limitUsed());
            }
            GroupCloudDto groupCloudDto = new GroupCloudDto(activeGroup.groupUniqueName(), activeGroup.cloudResourceAccesses());
            allActiveResourcesCount += cloudResourceAccessService.countResources(groupCloudDto);
        }
        if (!activeGroups.isEmpty()) {
            averageActiveGroupCost = overallCostFromActiveGroups.divide(BigDecimal.valueOf(activeGroups.size()), 2, RoundingMode.HALF_UP);
        }
        return new OverallCostValuesDto(overallCostFromActiveGroups, allActiveResourcesCount, averageActiveGroupCost);
    }

    public Map<CloudResourceType, BigDecimal> getOverallCostsPerResourceType() {
        List<GroupCloudDto> activeGroups = groupQueryService.getActiveGroups();
        Map<CloudResourceType, BigDecimal> result = new HashMap<>();
        for (GroupCloudDto activeGroup : activeGroups) {
            Map<CloudResourceType, BigDecimal> costsPerResourceType = cloudResourceAccessService.getCostsByResourceTypes(activeGroup);
            result.putAll(costsPerResourceType);
        }
        return result;
    }

    public Map<GroupUniqueName, BigDecimal> getTotalCostPerGroup() {
        List<GroupCloudDto> activeGroups = groupQueryService.getActiveGroups();
        Map<GroupUniqueName, BigDecimal> result = new HashMap<>();
        for (GroupCloudDto activeGroup : activeGroups) {
            List<CloudResourceRowView> cloudResourceDetails
                    = cloudResourceAccessService.getCloudResourceDetails(new HashSet<>(activeGroup.cloudResourceAccesses()));
            BigDecimal overallCost = BigDecimal.ZERO;
            for (CloudResourceRowView cloudResourceDetail : cloudResourceDetails) {
                overallCost = overallCost.add(cloudResourceDetail.limitUsed());
            }
            result.put(activeGroup.groupUniqueName(), overallCost);
        }
        return result;
    }

    public Map<LocalDate, BigDecimal> getTotalCostInTime() {
        List<GroupCloudDto> activeGroups = groupQueryService.getActiveGroups();
        Map<LocalDate, BigDecimal> result = new TreeMap<>();
        for (GroupCloudDto activeGroup : activeGroups) {
            Map<LocalDate, BigDecimal> totalCostInTime = cloudResourceAccessService.getTotalCostInTime(activeGroup);
            totalCostInTime.forEach((key, value) ->
                    result.put(key, result.getOrDefault(key, BigDecimal.ZERO).add(value)));
        }
        return result;
    }

    public record OverallCostValuesDto(
            BigDecimal overallCostFromActiveGroups,
            int allActiveResourcesCount,
            BigDecimal averageActiveGroupCost
    ) { }
}
