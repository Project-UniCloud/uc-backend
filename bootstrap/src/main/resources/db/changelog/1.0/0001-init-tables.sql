-- liquibase formatted sql

-- changeset michal:0001-init-tables
create table if not exists cloud_connectors
(
    id                    varchar(255)   not null primary key,
    default_clean_up_cron varchar(255)   not null,
    default_cost_limit    numeric(38, 2) not null,
    host                  varchar(255)   not null,
    name                  varchar(255)   not null,
    port                  integer        not null
);

create table if not exists cloud_connectors_resource_types
(
    cloud_connector_id varchar(255) not null
        constraint fk_ccrt_to_cc_constraint
            references cloud_connectors,
    resource_type      varchar(255) not null
);

create table if not exists cloud_resource_access_entity
(
    cloud_resource_access_id uuid           not null primary key,
    cloud_connector_id       varchar(255)   not null,
    cost_limit               numeric(38, 2) not null,
    cron_expression          varchar(255)   not null,
    expires_at               date,
    notification_level1      integer,
    notification_level2      integer,
    notification_level3      integer,
    resource_type            varchar(255)   not null,
    status                   varchar(255)   not null
        constraint cloud_resource_access_entity_status_check_constraint
            check ((status)::text = ANY ((ARRAY ['ACTIVE'::character varying, 'INACTIVE'::character varying])::text[])),
    used_limit               numeric(38, 2) not null
);

create table if not exists groups
(
    uuid         uuid         not null primary key,
    description  varchar(255),
    end_date     date,
    group_status varchar(255) not null
        constraint groups_group_status_check_constraint
            check ((group_status)::text = ANY
                   ((ARRAY ['ACTIVE'::character varying, 'INACTIVE'::character varying, 'ARCHIVED'::character varying])::text[])),
    name         varchar(255) not null,
    semester     varchar(255) not null,
    start_date   date,
    constraint groups_unique_name_semester_constraint
        unique (name, semester)
);

create table if not exists group_cloud_resource_accesses
(
    group_id                  uuid not null
        constraint fk_group_cloud_resource_accesses_to_groups_constraint
            references groups,
    cloud_resource_access_id uuid
);

create table if not exists group_lecturers
(
    group_id uuid not null
        constraint fk_group_lecturers_to_groups_constraint
            references groups,
    user_id  uuid
);

create table if not exists group_students
(
    group_id uuid not null
        constraint fk_group_students_to_groups_constraint
            references groups,
    user_id  uuid
);

create table if not exists users
(
    uuid       uuid         not null
        primary key,
    email      varchar(255),
    first_name varchar(255) not null,
    last_login timestamp(6),
    last_name  varchar(255) not null,
    login      varchar(255) not null
        constraint users_login_unique_constraint
            unique
);

create table if not exists user_roles
(
    user_uuid uuid         not null
        constraint fk_user_roles_to_users_constraint
            references users,
    role      varchar(255) not null
        constraint user_roles_role_check
            check ((role)::text = ANY
                   ((ARRAY ['ADMIN'::character varying, 'STUDENT'::character varying, 'LECTURER'::character varying])::text[])),
    primary key (user_uuid, role)
);
