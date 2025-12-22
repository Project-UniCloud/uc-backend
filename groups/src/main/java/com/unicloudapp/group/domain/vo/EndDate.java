package com.unicloudapp.group.domain.vo;

import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EndDate {

    LocalDate value;

    public static EndDate of(LocalDate value) {
        return new EndDate(value);
    }
}
