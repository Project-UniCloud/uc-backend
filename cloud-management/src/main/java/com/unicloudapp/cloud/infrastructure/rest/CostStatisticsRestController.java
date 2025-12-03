package com.unicloudapp.cloud.infrastructure.rest;

import com.unicloudapp.cloud.application.StatisticsService;
import com.unicloudapp.common.group.GroupUniqueName;
import com.unicloudapp.common.vo.cloud.CloudResourceType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/statistics/costs")
@RequiredArgsConstructor
class CostStatisticsRestController {

    private final StatisticsService statisticsService;

    @GetMapping("/overall")
    ResponseEntity<StatisticsService.OverallCostValuesDto> getTotalCosts() {
        return ResponseEntity.ok(statisticsService.getTotalCosts());
    }

    @GetMapping("per-resources-type")
    ResponseEntity<Map<CloudResourceType, BigDecimal>> getOverallCostsPerResourceType() {
        return ResponseEntity.ok(statisticsService.getOverallCostsPerResourceType());
    }

    @GetMapping("/per-group")
    ResponseEntity<Map<GroupUniqueName, BigDecimal>> getTotalCostPerGroup() {
        return ResponseEntity.ok(statisticsService.getTotalCostPerGroup());
    }

    @GetMapping("/in-time")
    ResponseEntity<Map<LocalDate, BigDecimal>> getTotalCostInTime() {
        return ResponseEntity.ok(statisticsService.getTotalCostInTime());
    }
}
