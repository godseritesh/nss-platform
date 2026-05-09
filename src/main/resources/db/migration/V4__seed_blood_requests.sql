-- V4: Real Maharashtra blood request seed data + donor interests
-- Locations: Pune, Mumbai, Nagpur, Nashik, Aurangabad, Kolhapur, Satara, Solapur

INSERT INTO blood_requests (patient_name, blood_group, units_needed, hospital, city, district, state, latitude, longitude, contact_name, contact_phone, contact_email, description, urgency, deadline, status, created_at) VALUES

-- CRITICAL cases
('Suresh Mane',    'O_NEG',  2, 'Sassoon General Hospital',           'Pune',       'Pune',        'Maharashtra', 18.5204,  73.8567, 'Kavita Mane',     '9876543210', 'kavita.mane@gmail.com',    'Patient undergoing emergency surgery. O- blood urgently needed. Please contact immediately.', 'CRITICAL', CURRENT_DATE + 1,  'OPEN', NOW() - INTERVAL '2 hours'),
('Priya Shinde',   'AB_NEG', 1, 'KEM Hospital',                        'Mumbai',     'Mumbai',      'Maharashtra', 19.0762,  72.8777, 'Ramesh Shinde',   '9823456789', 'ramesh.shinde@yahoo.com',  'Young mother post-delivery in ICU, rare AB- required immediately. Lives are at stake.',       'CRITICAL', CURRENT_DATE + 1,  'OPEN', NOW() - INTERVAL '4 hours'),
('Balaji Patil',   'B_NEG',  3, 'Nair Hospital',                       'Mumbai',     'Mumbai',      'Maharashtra', 19.0330,  72.8397, 'Sunita Patil',    '9765432109', NULL,                       'Road accident victim. Requires B- blood urgently for surgery scheduled tonight.',             'CRITICAL', CURRENT_DATE + 1,  'OPEN', NOW() - INTERVAL '1 hour'),
('Ramkrishna More','O_POS',  4, 'GMCH Nagpur',                         'Nagpur',     'Nagpur',      'Maharashtra', 21.1458,  79.0882, 'Anita More',      '9634521087', 'anita.more@rediffmail.com', 'Thalassemia patient requires O+ blood for monthly transfusion. Regular donor needed.',        'CRITICAL', CURRENT_DATE + 2,  'OPEN', NOW() - INTERVAL '6 hours'),
('Deepak Jadhav',  'A_NEG',  2, 'Ruby Hall Clinic',                    'Pune',       'Pune',        'Maharashtra', 18.5314,  73.8446, 'Meena Jadhav',    '9543210876', 'meena.j@gmail.com',        'Cancer patient undergoing chemo. A- blood required urgently. Please help save a life.',        'CRITICAL', CURRENT_DATE + 1,  'OPEN', NOW() - INTERVAL '30 minutes'),

-- URGENT cases
('Lata Deshmukh',  'A_POS',  2, 'Civil Hospital Nashik',               'Nashik',     'Nashik',      'Maharashtra', 19.9975,  73.7898, 'Dinesh Deshmukh', '9456789023', 'dinesh.d@gmail.com',       'Elderly patient with hip fracture surgery. A+ blood required within 2 days.',                 'URGENT',   CURRENT_DATE + 3,  'OPEN', NOW() - INTERVAL '1 day'),
('Sandeep Kulkarni','B_POS', 2, 'MGM Hospital',                         'Aurangabad', 'Aurangabad',  'Maharashtra', 19.8762,  75.3433, 'Priti Kulkarni',  '9321098765', NULL,                       'Patient with liver condition needs B+ blood before scheduled transplant evaluation.',           'URGENT',   CURRENT_DATE + 4,  'OPEN', NOW() - INTERVAL '2 days'),
('Rekha Bhosale',  'O_POS',  3, 'Government Medical College',           'Kolhapur',   'Kolhapur',    'Maharashtra', 16.7050,  74.2433, 'Sunil Bhosale',   '9210987654', 'sunil.b@hotmail.com',      'Post-partum hemorrhage patient in stable but critical condition. Multiple units needed.',      'URGENT',   CURRENT_DATE + 3,  'OPEN', NOW() - INTERVAL '12 hours'),
('Ajay Sawant',    'AB_POS', 1, 'Satara Civil Hospital',                'Satara',     'Satara',      'Maharashtra', 17.6805,  73.9979, 'Ashwini Sawant',  '9098765432', 'ashwini.s@gmail.com',      'Heart surgery patient requiring AB+ blood. Doctor has scheduled operation for day after.',    'URGENT',   CURRENT_DATE + 5,  'OPEN', NOW() - INTERVAL '3 days'),
('Vijay Pawar',    'A_POS',  2, 'Solapur Civil Hospital',               'Solapur',    'Solapur',     'Maharashtra', 17.6599,  75.9064, 'Sushma Pawar',    '8987654321', NULL,                       'Dialysis patient requires regular A+ blood donations every 21 days.',                        'URGENT',   CURRENT_DATE + 6,  'OPEN', NOW() - INTERVAL '1 day'),
('Nandini Ghuge',  'B_POS',  1, 'District Hospital Jalgaon',            'Jalgaon',    'Jalgaon',     'Maharashtra', 21.0077,  75.5626, 'Rajendra Ghuge',  '8765432109', 'rajendra.ghuge@gmail.com', 'Young girl with dengue-related complications. B+ blood urgently needed.',                    'URGENT',   CURRENT_DATE + 4,  'OPEN', NOW() - INTERVAL '5 hours'),
('Prakash Nimkar', 'O_NEG',  2, 'HCG Manavata Cancer Centre',           'Nashik',     'Nashik',      'Maharashtra', 20.0059,  73.7797, 'Varsha Nimkar',   '8654321098', 'varsha.n@yahoo.com',       'Cancer patient O- blood required before chemotherapy session next week.',                    'URGENT',   CURRENT_DATE + 7,  'OPEN', NOW() - INTERVAL '2 days'),

-- STANDARD cases
('Mangal Kamble',  'A_POS',  1, 'Deenanath Mangeshkar Hospital',       'Pune',       'Pune',        'Maharashtra', 18.5362,  73.8081, 'Yogesh Kamble',   '8543210987', 'yogesh.k@gmail.com',       'Planned surgery next week. A+ blood required as backup.',                                    'STANDARD', CURRENT_DATE + 10, 'OPEN', NOW() - INTERVAL '4 days'),
('Shanta Kokate',  'B_POS',  2, 'Jupiter Hospital',                     'Pune',       'Pune',        'Maharashtra', 18.5529,  73.9182, 'Mahesh Kokate',   '8432109876', NULL,                       'Elderly patient with chronic anemia. Monthly blood transfusion requirement.',                 'STANDARD', CURRENT_DATE + 12, 'OPEN', NOW() - INTERVAL '3 days'),
('Rohit Thorat',   'O_POS',  1, 'Wockhardt Hospital',                   'Mumbai',     'Mumbai',      'Maharashtra', 19.1136,  72.8697, 'Gauri Thorat',    '8321098765', 'gauri.t@rediffmail.com',   'Elective surgery scheduled. Looking for volunteer donors to be on standby.',                 'STANDARD', CURRENT_DATE + 14, 'OPEN', NOW() - INTERVAL '5 days'),

-- FULFILLED cases (historical)
('Anand Kale',     'A_POS',  2, 'Jehangir Hospital',                    'Pune',       'Pune',        'Maharashtra', 18.5204,  73.8567, 'Sonal Kale',      '8210987654', NULL,                       'Required blood for bypass surgery. Fulfilled with help from NSS volunteers.',                'CRITICAL', CURRENT_DATE - 5,  'FULFILLED', NOW() - INTERVAL '7 days'),
('Meena Gaikwad',  'B_NEG',  1, 'Bombay Hospital',                      'Mumbai',     'Mumbai',      'Maharashtra', 18.9750,  72.8258, 'Arun Gaikwad',    '7987654321', 'arun.g@gmail.com',         'Post-accident patient. Blood arranged successfully by NSS volunteers within 3 hours.',       'CRITICAL', CURRENT_DATE - 8,  'FULFILLED', NOW() - INTERVAL '10 days'),
('Ramesh Borate',  'O_POS',  3, 'Sassoon General Hospital',             'Pune',       'Pune',        'Maharashtra', 18.5204,  73.8567, 'Seema Borate',    '7876543210', NULL,                       'Thalassemia patient. Regular donors arranged through NSS VIIT Pune network.',               'URGENT',   CURRENT_DATE - 12, 'FULFILLED', NOW() - INTERVAL '14 days'),
('Sunita Jagtap',  'AB_POS', 1, 'Lilavati Hospital',                    'Mumbai',     'Mumbai',      'Maharashtra', 19.0504,  72.8303, 'Pramod Jagtap',   '7765432109', 'pramod.j@gmail.com',       'Bone marrow procedure required AB+. Donor found within 6 hours via platform.',              'URGENT',   CURRENT_DATE - 3,  'FULFILLED', NOW() - INTERVAL '5 days'),
('Kiran Dhole',    'O_NEG',  2, 'AIIMS Nagpur',                         'Nagpur',     'Nagpur',      'Maharashtra', 21.1458,  79.0882, 'Priya Dhole',     '7654321098', 'priya.dhole@gmail.com',    'Emergency trauma case. O- blood sourced via platform in under 2 hours. Patient stable.',    'CRITICAL', CURRENT_DATE - 1,  'FULFILLED', NOW() - INTERVAL '2 days');

-- Seed donor interests for open critical requests
INSERT INTO donor_interests (request_id, name, phone, email, message, created_at)
SELECT r.id, 'Amit Kulkarni', '9123456789', 'amit.k@gmail.com', 'I am O- donor. Available immediately at Sassoon.', NOW() - INTERVAL '1 hour'
FROM blood_requests r WHERE r.patient_name = 'Suresh Mane' LIMIT 1;

INSERT INTO donor_interests (request_id, name, phone, email, message, created_at)
SELECT r.id, 'Sneha Joshi', '9234567890', 'sneha.j@gmail.com', 'O- blood type confirmed. Can come today.', NOW() - INTERVAL '45 minutes'
FROM blood_requests r WHERE r.patient_name = 'Suresh Mane' LIMIT 1;

INSERT INTO donor_interests (request_id, name, phone, email, message, created_at)
SELECT r.id, 'Vikram Rane', '9345678901', NULL, 'B- donor available in Andheri. Can reach Nair in 30 mins.', NOW() - INTERVAL '20 minutes'
FROM blood_requests r WHERE r.patient_name = 'Balaji Patil' LIMIT 1;

INSERT INTO donor_interests (request_id, name, phone, email, message, created_at)
SELECT r.id, 'Pooja Deshpande', '9456789012', 'pooja.d@gmail.com', 'I am a regular donor. Ready to help the thalassemia patient.', NOW() - INTERVAL '2 hours'
FROM blood_requests r WHERE r.patient_name = 'Ramkrishna More' LIMIT 1;

INSERT INTO donor_interests (request_id, name, phone, email, message, created_at)
SELECT r.id, 'Akash Patil', '9567890123', NULL, 'A- blood type. Will be at Ruby Hall by evening.', NOW() - INTERVAL '15 minutes'
FROM blood_requests r WHERE r.patient_name = 'Deepak Jadhav' LIMIT 1;
