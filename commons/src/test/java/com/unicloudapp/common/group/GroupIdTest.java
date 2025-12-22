package com.unicloudapp.common.group;

import static org.junit.jupiter.api.Assertions.*;

import com.unicloudapp.common.vo.group.GroupId;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GroupIdTest {

    @Test
    @DisplayName("of() stores UUID and supports equality")
    void storesUuid() {
        UUID id = UUID.randomUUID();
        GroupId gid1 = GroupId.of(id);
        GroupId gid2 = GroupId.of(id);

        assertNotNull(gid1);
        assertEquals(id, gid1.getUuid());
        assertEquals(gid1, gid2);
        assertEquals(gid1.hashCode(), gid2.hashCode());
    }
}
