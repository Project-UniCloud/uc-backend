-- liquibase formatted sql

-- changeset michal:0002-add-audit-logs
create table if not exists audit_logs
(
    id          bigserial    not null primary key,
    action      varchar(255) not null,
    actor       varchar(255) not null,
    occurred_at timestamp    not null
);

create table if not exists audit_log_details
(
    audit_log_id bigint       not null
        constraint fk_audit_log_details_to_audit_logs_constraint
            references audit_logs,
    detail_key   varchar(255) not null,
    detail_value varchar(255),
    primary key (audit_log_id, detail_key)
);
