create table USER
(
  id       int auto_increment,
  email    varchar(70) not null unique,
  username varchar(30) not null unique,
  password varchar(30) not null,
  name     varchar(30),
  surname  varchar(30),
  role     varchar(10) not null default "user",
  image    MEDIUMBLOB,
  primary key (id)
);

create table session
(
  id               int auto_increment,
  user_id          int          not null,
  session_token    varchar(100) not null unique,
  login_date_time  varchar(19)  not null,
  logout_date_time varchar(19),
  is_expired       boolean      not null,
  foreign key (user_id) references user (id),
  primary key (id)

);

create table product
(
  id          int auto_increment,
  name        varchar(70)   not null,
  description varchar(1000) not null,
  seller      int           not null,
  price       double        not null,
  quantity    int           not null,
  image       MEDIUMBLOB,
  foreign key (seller) references user (id),
  primary key (id)
);

create table order_
(
  id                       int auto_increment,
  buyer_id                 int          not null,
  order_creation_date_time varchar(19)  not null,
  order_closed_date_time   varchar(19),
  status_                   varchar(500) not null,
  paid                     boolean default false,
  address                  varchar(300),
  stage                    int     default 1,
  foreign key (buyer_id) references user (id),
  primary key (id)
);


create table order_item
(
  id                int auto_increment,
  order_id          int not null,
  product_id        int not null,
  products_quantity int not null,
  foreign key (order_id) references order_ (id),
  foreign key (product_id) references product (id),
  primary key (id)
);

create table shopping_cart_item
(

  id         int auto_increment,
  product_id int not null,
  quantity int not null,
  user_id    int not null,
  foreign key (product_id) references product (id),
  foreign key (user_id) references user (id),
  primary key (id)

);



