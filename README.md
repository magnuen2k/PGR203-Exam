# Assignment 3

![Java CI with Maven](https://github.com/kristiania/pgr203innevering3-magnuen2k/workflows/Java%20CI%20with%20Maven/badge.svg?branch=master)

## How to run this program
### Build an executable jar-file
1. Run `mvn clean` first to clean up the target folder.
2. Run `mvn package` to build the project and create the .jar file.
3. Make file named `pgr203.properties` that has to contain the following:
    * dataSource.url = jdbc:postgresql://localhost:5432/ + the name of the database.
    * dataSource.username = provide the username of the database owner.
    * dataSource.password = provide the password of the database owner.
3. Run `java -jar target/eksamen-pgr203-1.0-SNAPSHOT.jar`.
4. Go to `localhost:8080` to interact with the server.

## Work process
We have been working exclusively over GitHub and Discord while sharing screens.
Making GitHub issues has been key to our working method and has kept us focused on the task.
Committing has been done sporadically and not 100% ping pong style. The reason behind this is that
our work flow has been a lot petter this way, when swapping between screens on discord while doing
the ping pong programming was a bit of a hassle, and it reduced the time spent on solving problems/ being stuck
by a lot. The time spent on research also dropped, when the combined knowledge was greater and therefore reduced
the need for research.

## Things learned while working like this
-The usage of GitHub issues has made it way more easy to stay on point and not get too distracted.
-We already started generating issue's that we will look at and implement on the exam, using GitHub issues.
-Ping pong programming works well in some cases, but there is also other efficient ways of working while being
multiple people working on the same issue/ problem.
-Communication is key and the communication and planning has been great. This sets the bar for the exam for sure.
 
## Datamodel
This is a temporary datamodel. The final result will look different. 
We plan on using 4+ tables to make the database highly functional
![UML image](http://www.plantuml.com/plantuml/png/XSvD2i9030NWVK-HfI9UeAjk58Jg7QAJra3ImYHrKtftBOCW_R7RV2-yLvXZz4ubUmYQmPNqJX7w3A2lc2GCSAmucjzGAGVpo4tzYuBhHeeiIy0X1sdJJScVxUTugCdFtwRTFhtD8oLk93RqD6vQvqiORRRD9jjUBOTfeCJ_dfgNCVKc6RA2cj2fl040)

### Contributors 
© Stian Westerheim & Magnus Enholm
