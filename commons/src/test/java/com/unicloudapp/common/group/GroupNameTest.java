package com.unicloudapp.common.group;

import com.unicloudapp.common.vo.group.GroupName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GroupNameTest {

    @Test
    @DisplayName("of() stores value and toString returns it")
    void ofStoresValue() {
        GroupName name = GroupName.of("Informatyka");
        assertNotNull(name);
        assertEquals("Informatyka", name.toString());
    }
}
