-- Bootstrap admin account. Password: AdminPass123! (change in any non-local environment)
INSERT INTO users (email, password_hash, role) VALUES
    ('admin@eventhub.com', '$2a$10$CcuFhXITKWQ1W5vn7eQwJeF9Qq/OLnOMLlBqBw4Ztz5zj/eDlrgke', 'ADMIN');
