#### Task:
Task 1. (60 points): Trade System 

(15 points) Implement Controller and View layers using Spring MVC for Trade System. 
It should contain next components and pages 
- Login (2 inputs, link on the registration page and submit button) 
- Registration page with 2 inputs and select box (roles) and submit button 
- View for purchase with list of goods (CRUD operations) 
- View for Orders with list of all goods from all purchases with order button 
- View for delivery with all goods was ordered from all order. We can change status of delivery 

1. (5 points) Implement CRUD operation for all entities via services 
2. (5 points) Spring should be configured via XML or Java-based config and web.xml should be 
removed 
3. (3 points) Use JDBC for communication with database 
4. (7 points) JSP pages and other layers should support search, sort, pagination operations. 
5. (3 points) Introduce i18n: support RU and EN languages 
6. (3 points) Use a template solution: Tiles or Thymeleaf 
7. (5 points) Prepare validation solution for incorrect input (based on JSR-303 Form Validation) 
8. (3 points) At least one entity should have image field. Your app can provide possibility 
to upload/download image and display it. 
9. (3 point) try to use ThemeResolver, prepare 2 themes 
10. (3 point) handle exception correctly 
11. (5 points) use cookies to track something (like username or special counter of views) 
during the session


### Result:

main task: partially

1. done
2. done
3. done
4. no
5. done
6. done
7. done
8. done
9. no
10. done
11. done


### To Run this project locally

1. launch MySQL server.

2. create database called 'trade_sys'

3. execute sql queries from the 'sql/create tables.sql' file

4. execute sql queries from the 'sql/populate tables.sql' file

5. launch jetty server by maven plugin. 
If you want to start the server through console use this: 
 
```shell
$ mvn jetty:run
```

6 navigate to this address ```http://localhost:8080/```

7 use creds: username: customer, password: 123213123