# pk-pz

Politechnika Krakowska / Cracow University of Technology  
Wydział Informatyki i Telekomunikacji / Faculty of Computer Science and Telecommunications

Semestr 6 / Semester 6  
Projekt zespołowy / Team Project

### gymate-restapi

##### Prerequisites
- Java 8 or higher,
- sbt,
- PostgreSQL.

##### Compile & run
If you have PostgreSQL installed, go to the ```psql``` console or any database editor and run the following commands:
```sql
CREATE USER admin WITH PASSWORD 'admin';
CREATE DATABASE gymate WITH OWNER=admin;
```
The database tables will be created automatically from Play Evolutions scripts when the project is ran.
Type ```sbt run``` from the main directory (the one with ```build.sbt```) and go to [localhost:9000/docs](http://localhost:9000/docs) in your browser to see available commands.
