package com.unicloudapp.group.infrastructure.persistence;

import com.unicloudapp.group.domain.GroupName;
import com.unicloudapp.group.domain.GroupStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "groups")
@RequiredArgsConstructor
@Getter
@Builder
@AllArgsConstructor
class GroupEntity {

    @Id
    private UUID uuid;

    @Embedded
    private GroupName name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GroupStatus.Type groupStatus;

    @Column(nullable = false)
    private String semester;

    private LocalDate startDate;

    private LocalDate endDate;

    @ElementCollection
    @CollectionTable(name = "group_lecturers", joinColumns = @JoinColumn(name = "group_id"))
    @Column(name = "user_id")
    private List<UUID> lecturers;

    @ElementCollection
    @CollectionTable(name = "group_attenders", joinColumns = @JoinColumn(name = "group_id"))
    @Column(name = "user_id")
    private List<UUID> attenders;

    @ElementCollection
    @CollectionTable(name = "group_attenders", joinColumns = @JoinColumn(name = "group_id"))
    //@Column(name = "cloudAccess_id") TODO
    private List<String> cloudAccesses;
}
