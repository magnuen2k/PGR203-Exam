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
We have been working exclusively over GitHub and Discord while sharing screens and observing each other.
We have also been doing some ping pong programming and quite a bit of test driven development for some of
the more challenging parts that was outside of the syllabus.
Making GitHub issues has been a major key to our working method and process, and has kept us focused on the task.
Commit logs does not necessarily indicate the process and all the member has been involved in most of the code.
The reason behind this is that it has been helping out our work flow and utilizing the time most efficient,
when swapping between screens on discord while doing the ping pong programming was somewhat less pleasing,
when there has been stability issues on both the discord servers and our individual ISP's.
The way we have been working has reduced the time spent on solving the different issues and gathering information
to a minimum, and it has given us way more freedom and time to develop the functionality and code.
We have had a lot of conversations during the project and have been able to learn from each other.
In many situations it has been great to have more than two eyes looking at the code, and analyzing the functionality,
to make the best of it and to make the code more readable and understandable for a third party such as the person
grading the exam. 

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
Kai Amundsen, Stian Westerheim & Magnus Enholm
