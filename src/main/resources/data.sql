INSERT INTO app_user (username, password, first_name, last_name, role_user)
VALUES
    ('admin', '$2a$10$xszlj1pbFnz6kKreb3X9pugjNUg0MAcC1ff/NR43oKNthJYYcCRj6', 'Admin', 'Administrator' , 'ADMIN'),
    ('professor', '$2a$10$xszlj1pbFnz6kKreb3X9pugjNUg0MAcC1ff/NR43oKNthJYYcCRj6', 'Milica', 'Krmpotic', 'PROFESSOR'),
    ('student', '$2a$10$xszlj1pbFnz6kKreb3X9pugjNUg0MAcC1ff/NR43oKNthJYYcCRj6', 'Matej', 'Galic', 'STUDENT');

INSERT INTO course (course_code, course_name, description, credits, max_students, semester, academic_year, professor_id)
VALUES
    ('CS101', 'Introduction to Computer Science', 'Basic concepts of computer science and programming.', 6, 100, 'SUMMER', '2024/2025', 2),
    ('MATH201', 'Calculus I', 'Differential and integral calculus of one variable.', 5, 80, 'SUMMER', '2024/2025', 2),
    ('PHY101', 'General Physics', 'Fundamental principles of physics with laboratory work.', 6, 60, 'SUMMER', '2024/2025', 2),
    ('ENG101', 'English Literature', 'Study of classic and modern English literature.', 4, 50, 'WINTER', '2024/2025', 2),
    ('HIST101', 'World History', 'Overview of major events in world history.', 4, 70, 'WINTER', '2024/2025', 2);