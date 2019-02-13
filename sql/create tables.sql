drop table if exists session;
drop table if exists shopping_cart_item;
drop table if exists order_status;
drop table if exists order_item;
drop table if exists order_;
drop table if exists product;
drop table if exists user;


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
  name        varchar(200)   not null,
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
  status_                  varchar(500) not null,
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

create table order_status
(
  id                        int auto_increment,
  order_id                  int not null,
  created                   boolean default false,
  shipping_details_provided boolean default false,
  order_paid                boolean default false,
  sent_by_seller            boolean default false,
  delivered                 boolean default false,
  foreign key (order_id) references order_ (id),
  primary key (id)
);

create table shopping_cart_item
(

  id         int auto_increment,
  product_id int not null,
  quantity   int not null,
  user_id    int not null,
  foreign key (product_id) references product (id),
  foreign key (user_id) references user (id),
  primary key (id)

);




insert into user
(username, password, name, surname, role, email)
values ("jack456", "123123123", "jack", "smell", "user", "jack456@mail.ru"),
       ("tim555", "123123123", "tim", "goover", "admin", "tim555@mail.ru"),
       ("stalkkker", "123123123", "oliver", "g.", "user", "stalkkker@mail.ru"),
       ("robot_t", "123123123", "mark", "goose", "user", "robot_t@mail.ru"),
       ("customer", "123123123", "jogn", "smith", "user", "j.smith222@mail.ru");

insert into product
(name, description, seller, price, quantity)
values ("apple macbook 2013", "good though old", 1, 699.99, 1),
       ("toyota camry", "nice car, in a well condition", 3, 5000.99, 5),
       ("a box of matches", "matches", 4, 0.99, 10),
       ("iphone x", "previous year version", 3, 1000.99, 1),
       ("leather jacket", "by levis, durable and almost brand new", 1, 900, 1),
       ("steve jobs biography", "in nice condition", 3, 15.99, 10),
       ("the longest product name that you have ever seen and it is the longest besause we need to test how out design reacts to such long names i think that is enough", "And this is going to be the longest description you have ever seen because we again need to test how out design reacts to such long descriptions and if something will go a wrong way we can fix it to make it perfectly cool from look i guess the description is long enough so we can stop here", 4, 950, 1),
       ("samsung flat tv, 45 inch", "wand to but this one because bought a rolling tv on the other day", 4, 950, 1);

# insert into order_
#   (buyer_id, order_creation_date_time, status_, stage)
# VALUES (2, "01.01.2000 10:23", "seller is processing the order", 1),
#        (5, "01.01.2000 10:23", "seller is processing the order", 1);
#
# insert into order_item
#   (order_id, product_id, products_quantity)
# VALUES (2, 2, 1);


