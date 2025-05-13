package com.unicloudapp.group.domain;

import lombok.*;

import java.time.Year;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Semester {

    Year year;
    Type type;

    public static Semester of(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Semester cannot be null or empty");
        } else if (name.length() != 5) {
            throw new IllegalArgumentException("Semester must be exactly 5 characters");
        }
        return new Semester(Year.parse(name.substring(0, 4)), Type.of(name.charAt(4)));
    }

    @RequiredArgsConstructor
    @Getter(value = AccessLevel.PRIVATE)
    public enum Type {
        SUMMER('L'), WINTER('Z');

        private final char symbol;

        private static Type of(char symbol) {
            return symbol == 'L' ? SUMMER : WINTER;
        }
    }

    @Override
    public String toString() {
        return year.toString() + type.getSymbol();
    }
}
