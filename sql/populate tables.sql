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
       ("toyota camry", "nice car, in a well condition", 3, 5000.99, 1),
       ("a box of matches", "matches", 4, 0.99, 10);


# insert into order_
#   (buyer_id, order_creation_date_time, status_, stage)
# VALUES (2, "01.01.2000 10:23", "seller is processing the order", 1),
#        (5, "01.01.2000 10:23", "seller is processing the order", 1);
#
# insert into order_item
#   (order_id, product_id, products_quantity)
# VALUES (2, 2, 1);
