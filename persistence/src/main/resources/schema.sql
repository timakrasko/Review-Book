-- Таблиці створюються лише якщо їх ще немає



create table if not exists books (
                                     id identity primary key,
                                     title varchar(255) not null,
    author varchar(255) not null,
    pub_year int not null
    );

create table if not exists comments (
                                        id identity primary key,
                                        book_id bigint not null,
                                        author varchar(64) not null,
    text varchar(1000) not null,
    created_at timestamp not null default current_timestamp,
    constraint fk_book foreign key (book_id) references books(id) on delete cascade
    );

-- Додавання книг лише якщо такої ще немає
INSERT INTO books (title, author, pub_year)
SELECT 'Kobzar', 'Taras Shevchenko', 1840
    WHERE NOT EXISTS (
    SELECT 1 FROM books WHERE title = 'Kobzar' AND author = 'Taras Shevchenko' AND pub_year = 1840
);

INSERT INTO books (title, author, pub_year)
SELECT 'Eneida', 'Ivan Kotliarevsky', 1798
    WHERE NOT EXISTS (
    SELECT 1 FROM books WHERE title = 'Eneida' AND author = 'Ivan Kotliarevsky' AND pub_year = 1798
);

INSERT INTO books (title, author, pub_year)
SELECT 'Tiger Trappers', 'Ivan Bahrianyi', 1944
    WHERE NOT EXISTS (
    SELECT 1 FROM books WHERE title = 'Tiger Trappers' AND author = 'Ivan Bahrianyi' AND pub_year = 1944
);

INSERT INTO books (title, author, pub_year)
SELECT 'Forest Song', 'Lesia Ukrainka', 1911
    WHERE NOT EXISTS (
    SELECT 1 FROM books WHERE title = 'Forest Song' AND author = 'Lesia Ukrainka' AND pub_year = 1911
);