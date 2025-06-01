package com.unicloudapp.common.group;

import com.unicloudapp.common.domain.group.GroupName;
import com.unicloudapp.common.domain.group.Semester;
import lombok.Builder;

@Builder
public record GroupUniqueName(
        GroupName groupName,
        Semester semester
) {

    public static GroupUniqueName fromString(String groupName) {
        if (groupName == null || !groupName.matches(".* \\d{4}[ZL]")) {
            throw new IllegalArgumentException("Niepoprawny format nazwy grupy: " + groupName);
        }
        int lastSpaceIndex = groupName.lastIndexOf(' ');
        if (lastSpaceIndex == -1 || lastSpaceIndex >= groupName.length() - 5) {
            throw new IllegalArgumentException("Nie znaleziono poprawnej spacji oddzielającej nazwę od sufiksu: " + groupName);
        }
        String name = groupName.substring(0, lastSpaceIndex);
        String suffix = groupName.substring(groupName.length() - 5);
        return new GroupUniqueName(GroupName.of(name), Semester.of(suffix));
    }


    public String toString() {
        return groupName + " " + semester;
    }
}
