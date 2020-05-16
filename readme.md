This is a simple backend application trying to replicate some 
functionalities of a very popular app primarily used for searching
and identifying unknown phone numbers, **_TrueCaller_**.

This application has been developed using **Java 8**, framework used is
**Spring Boot(v2.2.6.RELEASE)**, and the database used is **MySql**.
This project uses JWT based authentication system and so 
requires the bearer token (Bearer jwt_token) in the 
Authorization header for each request made. Only authentication
and sign up end points don't require the token and are open 
to all. For any other request, the user must be logged in.
There is no role based access in the system as the system has been developed
only for the common/end users.

The database has mainly three tables :
1) user - This is where the record of each registered user is.
2) contacts - In this table, a user's contacts gets stored.
3) spam - Numbers reported as spam gets stored in this table.

This application doesn't have any implementation for uploading the registered user's
contacts to the database, so, if you wish to implement this backend application for
your frontend application, you need to write that implementation by yourself.

The main features of the application are :
1) **Spam** :

A user can mark a number as spam so that other
users can identify the spam numbers. The number may or may not
belong to any registered user or their contacts.

2) **Search** :

**_a)_** User can search for a person by name in the global database. Search results
display the name, phone number and spam likelihood for each result
matching that name completely or partially. Results first show people
whose names start with the search query, and then people whose names
contain but don’t start with the search query.

**_b)_** User can search for a person by phone number in the global database. If
there is a registered user with that phone number, only that result shows.
Otherwise, shows all results matching that phone number completely - note
that there can be multiple names for a particular phone number in the global
database, since contact books of multiple registered users may have different
names for the same phone number.

**_c)_** Clicking a search result displays all the details for that person along with the
spam likelihood. But the person’s email is only displayed if the person is a
registered user and the user who is searching is in the person’s contact list.


For running the application on your local machine, clone this project. 
Open the project, preferably in IntelliJ IDEA (as this was used to develop this 
application), as a gradle project. For setting up the database, create a database 
named _truecaller_ on your MySql server and import the .sql file from the db folder 
inside the resources package for creating the tables and getting them populated 
with some data. Now run the application from IbApplication.java file. 
For every account in the user table, the password is 123456. The project is also 
integrated with Swagger for api documentation which can be accessed 
by hitting http://localhost:8080/swagger-ui.html from your browser once the 
project start to run successfully. You can also get the postman collection of the 
api's by importing this link in postman 
https://www.getpostman.com/collections/a8e9ba565dd148bd38e9

**NOTE : ** Make sure to set the database credentials in the application.properties 
file according to your local credentials.

Please reach out to me on asperanza23@gmail.com if you face any issue in setting 
up the project or you have any questions.
