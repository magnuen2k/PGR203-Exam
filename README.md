# The Exam 2020

![Java CI with Maven](https://github.com/kristiania/pgr203innevering3-magnuen2k/workflows/Java%20CI%20with%20Maven/badge.svg?branch=master)

## How to run this program
### Build an executable jar-file
1. Run `mvn clean` first to clean up the target folder.
2. Run `mvn package` to build the project and create the .jar file.
3. Make file named `pgr203.properties` that has to contain the following:
    * dataSource.url = jdbc:postgresql://localhost:5432/ + the name of the database.
    * dataSource.username = provide the username of the database owner.
    * dataSource.password = provide the password of the database owner.
3. 
    * If you are using Mac run: `java -jar target/eksamen-pgr203-1.0-SNAPSHOT.jar`.
    * If you are using Windows run: `java -Dfile.encoding="UTF-8" -jar pgr203eksamen-1.0-SNAPSHOT.jar`.
    * The reasoning behind this is declared [here](https://github.com/kristiania/pgr203eksamen-magnuen2k/blob/master/README.md#declaration-regarding-runtime-and-bugs)
4. Go to `localhost:8080` to interact with the server.

## Program functionality
-The program is developed to be used within the net browser(we have mainly tested it in chrome) by 
visiting the address http://localhost:8080/index.html.
-There is implemented a navigation menu in the program that should make it easy  to use.
-There is functionality for adding members, projects and tasks, and you could assign multiple members to
multiple tasks, and a task could be assigned to a project.
-Status is assigned to every task and project, and could be set to active or inactive.
-There is filtering functionality for filtering task on members and task status.

## Declaration regarding runtime and bugs
The code is mainly written on mac computers and testing has been done in intellij and chrome on mac machines.
We have tried to look for bugs running the application on both mac and windows. There has been some issues regarding
the UTF-8 decoding while running it from the executable .jar-file on windows exclusively and not on mac os.
This bug does not happen while running it in intellij. This is a bug that occurs because the default encoding on
windows when running .jar files is not UTF-8, while mac OS natively runs this in UTF-8.
We did a lot of research regarding this issue, and the solution we ended up with is as follows:
You run "java -Dfile.encoding="UTF-8" -jar pgr203eksamen-1.0-SNAPSHOT.jar" to make sure it encodes as UTF-8.
This will force the encoding to be UTF-8 on things such as String.getBytes() or Charset.defaultCharSet().
The result is that it will make the program accept norwegian characters such as "æ, ø, å/ Æ, Ø, Å", 
and special characters like @.
We also managed to force it using this source: 
https://stackoverflow.com/questions/361975/setting-the-default-java-character-encoding/14987992#14987992.
We did choose not to, because of the warnings it gave, and the fact that we are not familiar with the
possible side effects and bugs that might occur and to stay on the safe path.

## The work process
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
There is quite a few commits on the master branch. This is because we had some issues during one of the merges, and 
github did recommend using github desktop (the issues because we changed the file structure of the project).
This did not work out, and we had to manually fix it, 
or revert changes back to the previous commit. We did resolve the issues, but the extra commits remain on the branch. 

## Things learned during the project
-The usage of GitHub issues has made it way more easy to stay on point and not get too distracted. It has also made
the communication better within the group. Issues via github is really a great feature.
-Ping pong programming works well in some cases, but there is also other efficient ways of working while being
multiple people working on the same issue/ problem.
-Communication is key and the communication and planning has always been great. This sets the bar for the exam for sure.
-We have still managed to learn some new "dos and don'ts" when it comes to github. This is declared in the work process.
-It is important to take on a project step by step, and frequent breaks is key to productivity. Our general productivity
and common sense drops, if there is too long in between each break.
-It is the very first time we have ever worked on a project of this scale, some fixes and implementations might
be less ideal, but there has been countless hours of brainstorming and problem fixing to get to where we are at now.
The final result might reflect the fact that it is "home made", but it has been working fine during our testing, and
we are quite proud of the end result.


## Datamodel
We are using 4 tables. The member_tasks table is just to connect members and tasks.
We realized after consulting with Johannes and the project was more or less finished, that we could
have used a slightly different approach, where we would have projects connected to members instead of tasks
to make the datamodel a bit better, and it would be a better way of doing it i larger and more advanced projects.
We did learn from it, and the application is good enough for this project.
![UML image](https://user-images.githubusercontent.com/56038804/98135355-0583a600-1ec0-11eb-90d6-b8965e1efb21.png)
![UML image](https://user-images.githubusercontent.com/56038804/98588895-b9c06a80-22cc-11eb-95db-cba06285fde1.png)

### Contributors 
Kai Amundsen, Stian Westerheim & Magnus Enholm
