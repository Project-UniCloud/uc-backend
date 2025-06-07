-- Users
INSERT INTO users (uuid, login, first_name, last_name, email, last_login, role)
VALUES ('03a021bd-6b89-4aea-8fee-14e5e32aaf1e', 's12345', 'Jan', 'Kowalski', 'jan.kowalski@example.com', null, 'STUDENT'),
       ('dd978dc3-661d-4a72-a210-51bfcecb33e3', 'bikol', 'Patryk', 'Żywica', 'bikol@amu.edu.pl', null, 'LECTURER');

-- Groups
INSERT INTO groups (uuid, name, group_status, semester, start_date, end_date)
VALUES ('387a914e-2f8b-4d0b-9248-f72f05e1c73b', 'Grupa zajęciowa AWS', 'ACTIVE', '2024Z', '2024-10-01', '2025-02-15');

-- Lecturers
INSERT INTO group_lecturers (group_id, user_id)
VALUES ('387a914e-2f8b-4d0b-9248-f72f05e1c73b', 'dd978dc3-661d-4a72-a210-51bfcecb33e3');

-- Students
INSERT INTO group_students (group_id, user_id)
VALUES ('387a914e-2f8b-4d0b-9248-f72f05e1c73b', '03a021bd-6b89-4aea-8fee-14e5e32aaf1e');

-- Cloud accesses
--INSERT INTO cloud_access_entity (cloud_access_id, cloud_access_client_id, external_user_id, user_id)
--VALUES ('387a914e-2f8b-4d0b-9248-f72f05e1c73b', 'aws', 'boto3-generated-user', '03a021bd-6b89-4aea-8fee-14e5e32aaf1e');
