-- liquibase formatted sql

-- changeset michal:0001-init-tables
CREATE TABLE IF NOT EXISTS cloud_resource_access_entity
(
    cloud_resource_access_id uuid           NOT NULL,
    cloud_access_client_id   VARCHAR(255)   NOT NULL,
    cost_limit               numeric(38, 2) NOT NULL,
    cron_expression          VARCHAR(255)   NOT NULL,
    expires_at               date,
    resource_type            VARCHAR(255)   NOT NULL,
    status                   VARCHAR(255)   NOT NULL,
    used_limit               numeric(38, 2) NOT NULL,
    CONSTRAINT pk_cloud_resource_access PRIMARY KEY (cloud_resource_access_id)
);

CREATE TABLE IF NOT EXISTS groups
(
    uuid         uuid         NOT NULL,
    description  VARCHAR(255),
    end_date     date,
    group_status VARCHAR(255) NOT NULL,
    name         VARCHAR(255) NOT NULL,
    semester     VARCHAR(255) NOT NULL,
    start_date   date,
    CONSTRAINT pk_groups PRIMARY KEY (uuid),
    CONSTRAINT uk_groups_name_semester UNIQUE (name, semester)
);

CREATE TABLE IF NOT EXISTS users
(
    uuid        uuid         NOT NULL,
    email       VARCHAR(255),
    first_name  VARCHAR(255) NOT NULL,
    last_login  TIMESTAMP WITHOUT TIME ZONE,
    last_name   VARCHAR(255) NOT NULL,
    login       VARCHAR(255) NOT NULL,
    CONSTRAINT  pk_users PRIMARY KEY (uuid),
    CONSTRAINT  uk_users_login UNIQUE (login)
);

CREATE TABLE IF NOT EXISTS user_roles
(
    user_uuid   uuid         NOT NULL,
    role        VARCHAR(255) NOT NULL,
    PRIMARY KEY (user_uuid, role),
    CONSTRAINT fk_ur_users
        FOREIGN KEY (user_uuid)
            REFERENCES users (uuid)
);

CREATE TABLE IF NOT EXISTS group_cloud_resource_accesses
(
    group_id                 uuid NOT NULL,
    cloud_resource_access_id uuid NOT NULL,
    CONSTRAINT pk_group_cloud_resource_accesses PRIMARY KEY (group_id, cloud_resource_access_id),
    CONSTRAINT fk_gcra_group
        FOREIGN KEY (group_id) REFERENCES groups (uuid),
    CONSTRAINT fk_gcra_cloud_resource_access
        FOREIGN KEY (cloud_resource_access_id)
            REFERENCES cloud_resource_access_entity (cloud_resource_access_id)
);

CREATE TABLE IF NOT EXISTS group_lecturers
(
    group_id uuid NOT NULL,
    user_id  uuid NOT NULL,
    CONSTRAINT pk_group_lecturers PRIMARY KEY (group_id, user_id),
    CONSTRAINT fk_group_lecturers_group
        FOREIGN KEY (group_id) REFERENCES groups (uuid),
    CONSTRAINT fk_group_lecturers_user
        FOREIGN KEY (user_id) REFERENCES users (uuid)
);

CREATE TABLE IF NOT EXISTS group_students
(
    group_id uuid NOT NULL,
    user_id  uuid NOT NULL,
    CONSTRAINT pk_group_students PRIMARY KEY (group_id, user_id),
    CONSTRAINT fk_group_students_group
        FOREIGN KEY (group_id) REFERENCES groups (uuid),
    CONSTRAINT fk_group_students_user
        FOREIGN KEY (user_id) REFERENCES users (uuid)
);
