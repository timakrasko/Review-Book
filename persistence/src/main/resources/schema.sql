
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

INSERT INTO books (title, author, pub_year) VALUES ('Kobzar', 'Taras Shevchenko', 1840);
INSERT INTO books (title, author, pub_year) VALUES ('Eneida', 'Ivan Kotliarevsky', 1798);
INSERT INTO books (title, author, pub_year) VALUES ('Tiger Trappers', 'Ivan Bahrianyi', 1944);
INSERT INTO books (title, author, pub_year) VALUES ('Forest Song', 'Lesia Ukrainka', 1911);