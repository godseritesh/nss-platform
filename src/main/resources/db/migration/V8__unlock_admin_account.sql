-- V8: Unlock admin account and reset failed attempts
UPDATE users
SET failed_attempts = 0,
    lockout_time    = NULL
WHERE email = 'admin@nssviit.ac.in';
