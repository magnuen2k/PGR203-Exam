package no.kristiania.http.controllers;

import no.kristiania.db.daos.TaskDao;
import no.kristiania.db.objects.Task;
import no.kristiania.http.HttpMessage;
import no.kristiania.http.HttpServer;
import no.kristiania.http.QueryString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

public class TaskUpdateController implements HttpController{
    private final TaskDao taskDao;
    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    public TaskUpdateController(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws SQLException, IOException {
        HttpMessage response = handle(request);
        response.write(clientSocket);
    }

    public HttpMessage handle(HttpMessage request) throws SQLException {
        QueryString requestParameter = new QueryString(request.getBody());

        // Get data from POST
        String taskName = requestParameter.getParameter("task_name");
        String taskDesc = requestParameter.getParameter("task_desc");
        long taskId = Integer.parseInt(requestParameter.getParameter("id"));
        boolean taskStatus = Boolean.parseBoolean(requestParameter.getParameter("task_status"));

        // Decode data
        String decodedTaskName = URLDecoder.decode(taskName, StandardCharsets.UTF_8); //Makes us able to use "æøå"
        String decodedTaskDesc = URLDecoder.decode(taskDesc, StandardCharsets.UTF_8); //Makes us able to use "æøå"

        // Insert data to project object
        Task task = taskDao.retrieve(taskId);
        task.setTaskName(decodedTaskName);
        task.setDesc(decodedTaskDesc);
        task.setTaskStatus(taskStatus);

        // Insert project object to db
        taskDao.updateTask(task, taskId);
        logger.info("Updated " + task.getTaskName() + " in the database");

        // Create response
        HttpMessage redirect = new HttpMessage();
        redirect.setStartLine("HTTP/1.1 302 Redirect");
        redirect.getHeaders().put("Location", "http://localhost:8080/redirect.html");
        return redirect;
    }
}
