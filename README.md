# Final-Project
This is an application for salon.
It has a database that allows to save, read, update and delete all information about salon's employees, customers and services performed for customers by employees.
There are 3 entities: employee, customer, service. All 4 CRUD operations can be performed for each entity: create, read, update and delete.
Relationships between entities:
@OneToMany: between employee and customer (employee can have multiple customers); 
            between customer and service (customer can have multiple services done);
@ManyToMany: between employee and service(employee can perform multiple services and, also, one service such as hair color can be done by multiple employees:
for example, one employee applies color, and another finishes service for customer by drying hair)
