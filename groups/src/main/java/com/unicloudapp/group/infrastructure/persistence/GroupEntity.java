package com.unicloudapp.group.infrastructure.persistence;

import com.unicloudapp.group.domain.GroupStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(
        name = "groups",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "semester"})
)
@RequiredArgsConstructor
@Getter
@Builder
@AllArgsConstructor
class GroupEntity {

    @Id
    private UUID uuid;

    @Column(nullable = false)
    private String name;

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
    private Set<UUID> lecturers;

    @ElementCollection
    @CollectionTable(name = "group_students", joinColumns = @JoinColumn(name = "group_id"))
    @Column(name = "user_id")
    private Set<UUID> students;

    @ElementCollection
    @CollectionTable(name = "group_cloud_resource_accesses", joinColumns = @JoinColumn(name = "group_id"))
    @Column(name = "cloud_resource_accesse_id")
    private Set<UUID> cloudResourceAccesses;

    private String description;
}
