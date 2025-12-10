package com.unicloudapp.cloud.infrastructure.rest;

import com.unicloudapp.cloud.application.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/statistics/costs")
@RequiredArgsConstructor
class CostStatisticsRestController {

    private final StatisticsService statisticsService;

    @GetMapping("/overall")
    ResponseEntity<StatisticsService.OverallCostValuesDto> getTotalCosts() {
        return ResponseEntity.ok(statisticsService.getTotalCosts());
    }

    @GetMapping("/per-resources-type")
    ResponseEntity<List<StatisticsService.CostPerResourceTypeDto>> getOverallCostsPerResourceType() {
        return ResponseEntity.ok(statisticsService.getOverallCostsPerResourceType());
    }

    @GetMapping("/per-group")
    ResponseEntity<List<StatisticsService.CostPerGroupDto>> getTotalCostPerGroup() {
        return ResponseEntity.ok(statisticsService.getTotalCostPerGroup());
    }

    @GetMapping("/in-time")
    ResponseEntity<List<StatisticsService.CostPerMonthDto>> getTotalCostInTime() {
        return ResponseEntity.ok(statisticsService.getTotalCostInTime());
    }
}
