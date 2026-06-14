-- V7: Fix admin password hash (previous hash was incorrect)
-- Password: Admin@12345 (BCrypt, strength 12)
UPDATE users
SET password_hash = '$2b$12$JSrDNwzx502DRcXAp6NVXeiMoxwjVvqJlhvBFOyRu57Tvo2.JzMH2'
WHERE email = 'admin@nssviit.ac.in'
  AND password_hash = '$2a$12$LkfvJrJCW/LDpvSmEbLI.OsriXuHkRCgOYiuSHaKz71F.EqH7jS86';
