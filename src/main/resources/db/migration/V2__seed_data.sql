-- V2: Seed an admin user and sample data for demo
-- Password: Admin@12345 (BCrypt hash, strength 12)
INSERT INTO users (name, email, password_hash, role)
VALUES (
    'NSS Admin VIIT',
    'admin@nssviit.ac.in',
    '$2a$12$LkfvJrJCW/LDpvSmEbLI.OsriXuHkRCgOYiuSHaKz71F.EqH7jS86',
    'ROLE_ADMIN'
) ON CONFLICT (email) DO NOTHING;

-- Sample blood donation event
INSERT INTO events (title, description, event_date, location, category, created_by)
SELECT
    'NSS Blood Donation Drive 2024',
    'Annual blood donation camp organized by NSS VIIT Pune in partnership with Sahyadri Hospital. Every unit of blood can save up to 3 lives. Join us in making a difference!',
    CURRENT_DATE + INTERVAL '7 days',
    'VIIT Campus, Pune – Sports Ground',
    'BLOOD_DONATION',
    u.id
FROM users u WHERE u.email = 'admin@nssviit.ac.in'
LIMIT 1;

-- Sample awareness event
INSERT INTO events (title, description, event_date, location, category, created_by)
SELECT
    'World Blood Donor Day Awareness Drive',
    'Interactive awareness session covering blood donation myths, eligibility criteria, and the urgent need for regular donors across blood groups.',
    CURRENT_DATE + INTERVAL '14 days',
    'VIIT Auditorium, Block A',
    'AWARENESS_DRIVE',
    u.id
FROM users u WHERE u.email = 'admin@nssviit.ac.in'
LIMIT 1;

-- Sample volunteer recruitment event
INSERT INTO events (title, description, event_date, location, category, created_by)
SELECT
    'NSS Volunteer Registration 2024-25',
    'Register as an NSS volunteer and be part of a 3500+ strong community dedicated to social impact. Open to all VIIT students.',
    CURRENT_DATE + INTERVAL '3 days',
    'VIIT Campus – Registration Counter',
    'VOLUNTEER_RECRUITMENT',
    u.id
FROM users u WHERE u.email = 'admin@nssviit.ac.in'
LIMIT 1;

-- Sample poll on the blood donation event
WITH event_row AS (
    SELECT id FROM events WHERE title = 'NSS Blood Donation Drive 2024' LIMIT 1
),
admin_row AS (
    SELECT id FROM users WHERE email = 'admin@nssviit.ac.in' LIMIT 1
),
poll_insert AS (
    INSERT INTO polls (event_id, question, expires_at, status, created_by)
    SELECT
        e.id,
        'Will you participate in the NSS Blood Donation Drive this year?',
        NOW() + INTERVAL '30 days',
        'ACTIVE',
        a.id
    FROM event_row e, admin_row a
    RETURNING id
)
INSERT INTO poll_options (poll_id, option_text, display_order)
SELECT p.id, opt.text, opt.ord
FROM poll_insert p,
     (VALUES
        ('Yes, I will donate blood', 0),
        ('No, I am not eligible right now', 1),
        ('Maybe, I need more information', 2),
        ('I will help volunteer instead', 3)
     ) AS opt(text, ord);

-- Sample poll on awareness event
WITH event_row AS (
    SELECT id FROM events WHERE title = 'World Blood Donor Day Awareness Drive' LIMIT 1
),
admin_row AS (
    SELECT id FROM users WHERE email = 'admin@nssviit.ac.in' LIMIT 1
),
poll_insert AS (
    INSERT INTO polls (event_id, question, expires_at, status, created_by)
    SELECT
        e.id,
        'How did you hear about our blood donation awareness drive?',
        NOW() + INTERVAL '21 days',
        'ACTIVE',
        a.id
    FROM event_row e, admin_row a
    RETURNING id
)
INSERT INTO poll_options (poll_id, option_text, display_order)
SELECT p.id, opt.text, opt.ord
FROM poll_insert p,
     (VALUES
        ('Social Media (Instagram/WhatsApp)', 0),
        ('College Notice Board', 1),
        ('Friend / Classmate', 2),
        ('NSS Email / Newsletter', 3),
        ('Campus Announcement', 4)
     ) AS opt(text, ord);
