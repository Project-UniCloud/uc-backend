-- liquibase formatted sql

-- changeset michal:0003-add-relations
ALTER TABLE cloud_resource_access_entity
RENAME TO cloud_resource_accesses;

ALTER TABLE audit_logs
ADD CONSTRAINT fk_ald_actor
FOREIGN KEY (actor)
REFERENCES users(uuid);
