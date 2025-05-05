DELETE FROM loans;
DELETE FROM book_copies;
DELETE FROM books;
DELETE FROM authors;

ALTER SEQUENCE IF EXISTS authors_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS books_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS book_copies_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS loans_seq RESTART WITH 1;

INSERT INTO authors (id, name, country, created_at)
VALUES
    (1, 'Joshua Bloch', 'USA', CURRENT_TIMESTAMP),
    (2, 'Craig Walls', 'USA', CURRENT_TIMESTAMP);

INSERT INTO books (id, title, author_id, isbn, publication_year, created_at)
VALUES
    (1, 'Effective Java', 1, '978-0134685991', 2018, CURRENT_TIMESTAMP),
    (2, 'Spring in Action', 2, '978-1617294945', 2020, CURRENT_TIMESTAMP);

INSERT INTO book_copies (id, book_id, available, created_at)
VALUES
    (1, 1, true, CURRENT_TIMESTAMP),
    (2, 2, true, CURRENT_TIMESTAMP);