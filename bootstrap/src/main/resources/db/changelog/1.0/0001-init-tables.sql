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
    CONSTRAINT cloud_resource_access_entity_pkey PRIMARY KEY (cloud_resource_access_id)
);

CREATE TABLE IF NOT EXISTS group_cloud_resource_accesses
(
    group_id                  uuid NOT NULL,
    cloud_resource_access_id  uuid NOT NULL
);

CREATE TABLE IF NOT EXISTS group_lecturers
(
    group_id uuid NOT NULL,
    user_id  uuid
);

CREATE TABLE IF NOT EXISTS group_students
(
    group_id uuid NOT NULL,
    user_id  uuid
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
    CONSTRAINT groups_pkey PRIMARY KEY (uuid)
);

CREATE TABLE IF NOT EXISTS users
(
    uuid       uuid         NOT NULL,
    email      VARCHAR(255),
    first_name VARCHAR(255) NOT NULL,
    last_login TIMESTAMP    WITHOUT TIME ZONE,
    last_name  VARCHAR(255) NOT NULL,
    login      VARCHAR(255) NOT NULL,
    role       VARCHAR(255) NOT NULL,
    CONSTRAINT users_pkey PRIMARY KEY (uuid)
);

ALTER TABLE groups
    ADD CONSTRAINT uk75amegq7vb6lfkh7ofpgvbtyf UNIQUE (name, semester);

ALTER TABLE users
    ADD CONSTRAINT ukow0gan20590jrb00upg3va2fn UNIQUE (login);

ALTER TABLE group_students
    ADD CONSTRAINT fk5xwqsk30pbk3opg7gyuqydfua FOREIGN KEY (group_id) REFERENCES groups (uuid) ON DELETE NO ACTION;

ALTER TABLE group_lecturers
    ADD CONSTRAINT fk6cogcp6nqeicoe4778gtr7stb FOREIGN KEY (group_id) REFERENCES groups (uuid) ON DELETE NO ACTION;

ALTER TABLE group_cloud_resource_accesses
    ADD CONSTRAINT fkh49j08oinmat190qeeo5uddcn FOREIGN KEY (group_id) REFERENCES groups (uuid) ON DELETE NO ACTION;

