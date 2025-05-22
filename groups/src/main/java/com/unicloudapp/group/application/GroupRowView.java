package com.unicloudapp.group.application;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.UUID;

@Validated
@Builder
public record GroupRowView(
        @NotNull UUID groupId,
        @NotBlank String name,
        @NotBlank String semester,
        @JsonFormat(pattern = "dd-MM-yyyy") @NotNull LocalDate endDate,
        @NotNull String lecturers,
        @NotNull String cloudAccesses
) { }
