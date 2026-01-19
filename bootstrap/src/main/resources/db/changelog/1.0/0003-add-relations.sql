-- liquibase formatted sql

-- changeset michal:0003-add-relations
ALTER TABLE cloud_resource_access_entity
RENAME cloud_resource_access_id TO uuid;

ALTER TABLE group_students
ADD CONSTRAINT fk_gs_users
FOREIGN KEY (user_id)
REFERENCES users(uuid);

ALTER TABLE group_lecturers
ADD CONSTRAINT fk_gl_users
FOREIGN KEY (user_id)
REFERENCES users(uuid);

ALTER TABLE group_cloud_resource_accesses
ADD CONSTRAINT fk_gcra_cloud_resource_access_id
FOREIGN KEY (cloud_resource_access_id)
REFERENCES cloud_resource_access_entity(uuid);
