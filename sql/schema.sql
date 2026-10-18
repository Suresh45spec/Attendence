-- Student Attendance Management System (web) - schema v2
-- Adds: email + hashed passwords, student login, attendance records.
-- Run:  mysql -u root -p < sql\schema.sql
-- NOTE: This recreates the database from scratch.

CREATE DATABASE IF NOT EXISTS attendance_db;
USE attendance_db;

DROP TABLE IF EXISTS attendance_records;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS teachers;

CREATE TABLE teachers (
    teacher_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,          -- sha256$salt$hash
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL DEFAULT ''
);

CREATE TABLE students (
    roll_no VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    division VARCHAR(10) NOT NULL,
    email VARCHAR(100) NOT NULL DEFAULT '',
    password VARCHAR(100) NOT NULL DEFAULT '', -- sha256$salt$hash
    total_classes INT NOT NULL DEFAULT 0,
    classes_attended INT NOT NULL DEFAULT 0,
    CONSTRAINT chk_total_non_negative CHECK (total_classes >= 0),
    CONSTRAINT chk_attended_non_negative CHECK (classes_attended >= 0),
    CONSTRAINT chk_attended_not_over_total CHECK (classes_attended <= total_classes)
);

-- One row per student + subject + date. The UNIQUE key prevents duplicates.
CREATE TABLE attendance_records (
    record_id INT AUTO_INCREMENT PRIMARY KEY,
    roll_no VARCHAR(20) NOT NULL,
    subject VARCHAR(60) NOT NULL,
    class_date DATE NOT NULL,
    status ENUM('PRESENT','ABSENT') NOT NULL,
    marked_by VARCHAR(50) NOT NULL DEFAULT '',
    UNIQUE KEY uq_student_subject_date (roll_no, subject, class_date),
    CONSTRAINT fk_record_student FOREIGN KEY (roll_no)
        REFERENCES students(roll_no) ON DELETE CASCADE
);

-- Demo teacher: teacher / teacher123  (SHA-256 salted hash)
INSERT INTO teachers (username, password, full_name, email) VALUES
('teacher', 'sha256$a1b2c3d4e5f60718$129c625c1de14a3a4c8daa3f585dfc85f06e3cc77f931665d9a3f987e7166134', 'Demo Teacher', 'teacher@college.edu');

-- Sample students (also login accounts): roll / student123
INSERT INTO students (roll_no, name, division, email, password, total_classes, classes_attended) VALUES
('101', 'Aarav Sharma',   'A', 'aarav@college.edu',   'sha256$f9e8d7c6b5a49382$7472b277891b8b67ea6c98a05b9846861bef634b80277adc59ef1c28bcb02a1d', 40, 36),
('102', 'Diya Patel',     'A', 'diya@college.edu',    'sha256$f9e8d7c6b5a49382$7472b277891b8b67ea6c98a05b9846861bef634b80277adc59ef1c28bcb02a1d', 40, 28),
('103', 'Rohan Mehta',    'B', 'rohan@college.edu',   'sha256$f9e8d7c6b5a49382$7472b277891b8b67ea6c98a05b9846861bef634b80277adc59ef1c28bcb02a1d', 40, 24),
('104', 'Isha Kulkarni',  'B', 'isha@college.edu',    'sha256$f9e8d7c6b5a49382$7472b277891b8b67ea6c98a05b9846861bef634b80277adc59ef1c28bcb02a1d', 40, 32),
('105', 'Kabir Khan',     'A', 'kabir@college.edu',   'sha256$f9e8d7c6b5a49382$7472b277891b8b67ea6c98a05b9846861bef634b80277adc59ef1c28bcb02a1d', 40, 20);
