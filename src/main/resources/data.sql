insert into author_types (id, type) values (1, 'Newbie');
insert into author_types (id, type) values (2, 'Master');

-- we will add a new book to Gaetano Piazzolla
insert into authors (id, author_type_id, first_name, last_name) values (1, 1, 'Gaetano', 'Piazzolla');
-- we will delete a book from Flowers
insert into authors (id, author_type_id, first_name, last_name) values (2, 2, 'Martin', 'Flowers');
-- we will update the name of this Author
insert into authors (id, author_type_id, first_name, last_name) values (3, 1, 'Josh', 'Long');

-- all books by Martin Flowers
insert into books (id, title, description, author_id) values (1, 'Spring Boot Basics', 'Introduction to Spring Boot', 2);
insert into books (id, title, description, author_id) values (2, 'Advanced Spring Boot', 'Deep dive into Spring Boot features', 2);
insert into books (id, title, description, author_id) values (3, 'Spring Boot Security', 'Implementing security in Spring Boot', 2);
insert into books (id, title, description, author_id) values (4, 'Spring Boot Testing', 'Testing Spring Boot applications', 2);
insert into books (id, title, description, author_id) values (5, 'Spring Boot with Kotlin', 'Using Kotlin with Spring Boot', 2);
insert into books (id, title, description, author_id) values (6, 'Spring Boot Microservices', 'Building microservices with Spring Boot', 2);

-- fix the sequence
ALTER TABLE books ALTER COLUMN id RESTART WITH 7;
ALTER TABLE authors ALTER COLUMN id RESTART WITH 4;