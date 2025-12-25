INSERT INTO app_user (username, password, first_name, last_name, role_user)
VALUES
    ('admin', '$2a$10$xszlj1pbFnz6kKreb3X9pugjNUg0MAcC1ff/NR43oKNthJYYcCRj6', 'Admin', 'Administrator' , 'ADMIN'),
    ('mkrmpotic', '$2a$10$xszlj1pbFnz6kKreb3X9pugjNUg0MAcC1ff/NR43oKNthJYYcCRj6', 'Milica', 'Krmpotic', 'PROFESSOR'),
    ('aradovan', '$2a$10$xszlj1pbFnz6kKreb3X9pugjNUg0MAcC1ff/NR43oKNthJYYcCRj6', 'Ana', 'Radovan', 'PROFESSOR'),
    ('iobad', '$2a$10$xszlj1pbFnz6kKreb3X9pugjNUg0MAcC1ff/NR43oKNthJYYcCRj6', 'Ivan', 'Obad', 'PROFESSOR'),
    ('lkrmpotic', '$2a$10$xszlj1pbFnz6kKreb3X9pugjNUg0MAcC1ff/NR43oKNthJYYcCRj6', 'Lara', 'Krmpotic', 'PROFESSOR'),
    ('jpetrovic', '$2a$10$xszlj1pbFnz6kKreb3X9pugjNUg0MAcC1ff/NR43oKNthJYYcCRj6', 'Jovan', 'Petrovic', 'PROFESSOR'),
    ('sivanovic', '$2a$10$xszlj1pbFnz6kKreb3X9pugjNUg0MAcC1ff/NR43oKNthJYYcCRj6', 'Sara', 'Ivanovic', 'STUDENT'),
    ('dmarinkovic', '$2a$10$xszlj1pbFnz6kKreb3X9pugjNUg0MAcC1ff/NR43oKNthJYYcCRj6', 'David', 'Marinkovic', 'STUDENT'),
    ('lpetrovic', '$2a$10$xszlj1pbFnz6kKreb3X9pugjNUg0MAcC1ff/NR43oKNthJYYcCRj6', 'Lena', 'Petrovic', 'STUDENT'),
    ('mstojanovic', '$2a$10$xszlj1pbFnz6kKreb3X9pugjNUg0MAcC1ff/NR43oKNthJYYcCRj6', 'Marko', 'Stojanovic', 'STUDENT'),
    ('njakovljevic', '$2a$10$xszlj1pbFnz6kKreb3X9pugjNUg0MAcC1ff/NR43oKNthJYYcCRj6', 'Nina', 'Jakovljevic', 'STUDENT'),
    ('tmitrovic', '$2a$10$xszlj1pbFnz6kKreb3X9pugjNUg0MAcC1ff/NR43oKNthJYYcCRj6', 'Tina', 'Mitrovic', 'STUDENT'),
    ('mgalic', '$2a$10$xszlj1pbFnz6kKreb3X9pugjNUg0MAcC1ff/NR43oKNthJYYcCRj6', 'Matej', 'Galic', 'STUDENT');

INSERT INTO course (course_code, course_name, description, credits, max_students, semester, academic_year, professor_id)
VALUES
    ('CS101', 'Introduction to Computer Science', 'Basic concepts of computer science and programming.', 6, 100, 'SUMMER', '2024/2025', 2),
    ('MATH201', 'Calculus I', 'Differential and integral calculus of one variable.', 5, 80, 'SUMMER', '2024/2025', 2),
    ('PHY101', 'General Physics', 'Fundamental principles of physics with laboratory work.', 6, 60, 'SUMMER', '2024/2025', 2),
    ('ENG101', 'English Literature', 'Study of classic and modern English literature.', 4, 50, 'WINTER', '2024/2025', 2),
    ('HIST101', 'World History', 'Overview of major events in world history.', 4, 70, 'WINTER', '2024/2025', 2),
    ('BIO101', 'Introduction to Biology', 'Basic principles of biology and life sciences.', 5, 90, 'SUMMER', '2024/2025', 3),
    ('CHEM101', 'General Chemistry', 'Fundamental concepts of chemistry with laboratory work.', 6, 60, 'SUMMER', '2024/2025', 3),
    ('PSY101', 'Introduction to Psychology', 'Basic concepts and theories in psychology.', 4, 80, 'WINTER', '2024/2025', 3),
    ('SOC101', 'Sociology Basics', 'Introduction to sociological theories and concepts.', 4, 70, 'WINTER', '2024/2025', 3),
    ('ART101', 'Art History', 'Study of major art movements and their historical contexts.', 4, 50, 'WINTER', '2024/2025', 3),
    ('CS201', 'Data Structures', 'In-depth study of data structures and algorithms.', 6, 80, 'SUMMER', '2024/2025', 4),
    ('MATH301', 'Linear Algebra', 'Matrix theory and linear algebra applications.', 5, 70, 'SUMMER', '2024/2025', 4),
    ('PHY201', 'Electromagnetism', 'Study of electric and magnetic fields and their interactions.', 6, 60, 'SUMMER', '2024/2025', 4),
    ('ENG201', 'Creative Writing', 'Techniques and practice of creative writing.', 4, 50, 'WINTER', '2024/2025', 4),
    ('HIST201', 'Modern History', 'Examination of modern historical events and trends.', 4, 70, 'WINTER', '2024/2025', 4);

INSERT INTO enrollment (student_id, course_id, status)
VALUES
    (7, 1, 'ENROLLED'),
    (8, 1, 'ENROLLED'),
    (9, 2, 'ENROLLED'),
    (10, 2, 'ENROLLED'),
    (11, 3, 'ENROLLED'),
    (12, 3, 'ENROLLED'),
    (13, 4, 'ENROLLED'),
    (7, 5, 'ENROLLED'),
    (8, 6, 'ENROLLED'),
    (9, 7, 'ENROLLED'),
    (10, 8, 'ENROLLED'),
    (11, 9, 'ENROLLED'),
    (12, 10, 'ENROLLED'),
    (13, 11, 'ENROLLED');