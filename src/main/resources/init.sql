drop table if exists purchases_products;
drop table if exists purchases;
drop table if exists products;
drop table if exists customers;

create table customers
(
    id             integer PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    first_name     varchar(50) NOT NULL,
    last_name      varchar(50) NOT NULL
);

create table purchases
(
    id             integer PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    date           date NOT NULL,
    customer_id    integer,
    foreign key (customer_id) references customers(id)
);

create table products
(
    id             integer PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    name           varchar(255) NOT NULL,
    price          numeric
);

create table purchases_products
(
    purchase_id integer references purchases(id) ON DELETE CASCADE,
    product_id integer references products(id) ON DELETE RESTRICT,
    PRIMARY KEY (purchase_id, product_id)
);

insert  into customers (first_name, last_name)
values
    ('Антон', 'Иванов'),
    ('Николай', 'Иванов'),
    ('Валентина', 'Петрова'),
    ('Александра', 'Мельникова'),
    ('Екатерина', 'Сизова'),
    ('Олег', 'Шматко'),
    ('Алексей', 'Маклаков'),
    ('Алексей', 'Ягудин'),
    ('Юрий', 'Петренко'),
    ('Владимир', 'Казанцев'),
    ('Андрей', 'Ларин'),
    ('Ольга', 'Ларина'),
    ('Анатолий', 'Дукалис'),
    ('Анастасия', 'Абдулова'),
    ('Вячеслав', 'Волков'),
    ('Кирилл', 'Порохня'),
    ('Олег', 'Соловец');

insert  into products (name, price)
values
    ('Сыр', 900),
    ('Хлеб', 40),
    ('Мука', 75),
    ('Сметана', 90),
    ('Колбаса', 600),
    ('Минеральная вода', 60);

insert into purchases (date, customer_id)
values
    (current_date, 1),
    (current_date+2, 3),
    (current_date+5, 2),
    (current_date+5, 1),
    (current_date, 1),
    (current_date+2, 1),
    (current_date+6, 4),
    (current_date+6, 5),
    (current_date+6, 12),
    (current_date+7, 13),
    (current_date+7, 14),
    (current_date+7, 15),
    (current_date+7, 16),
    (current_date+10, 17),
    (current_date+10, 3);

insert into purchases_products (purchase_id, product_id)
values
    (1, 4),
    (1, 5),
    (2, 1),
    (2, 2),
    (2, 4),
    (2, 6),
    (3, 1),
    (4, 2),
    (5, 2),
    (6, 2),
    (6, 4),
    (7, 1),
    (7, 3),
    (7, 4),
    (7, 2),
    (7, 5),
    (7, 6),
    (8, 3),
    (8, 4),
    (9, 1),
    (9, 2),
    (9, 4),
    (9, 5),
    (10, 6),
    (11, 1),
    (11, 2),
    (12, 1),
    (12, 2),
    (12, 6),
    (13, 1),
    (13, 2),
    (13, 4),
    (13, 5),
    (14, 1),
    (15, 1),
    (15, 6);