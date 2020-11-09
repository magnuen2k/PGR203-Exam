package no.kristiania.http.controllers;

import no.kristiania.db.objects.Task;
import no.kristiania.db.daos.TaskDao;
import no.kristiania.http.HttpMessage;
import no.kristiania.http.QueryString;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class TaskAddToProjectController implements HttpController {
    private final TaskDao taskDao;

    public TaskAddToProjectController(TaskDao taskdao) {
        this.taskDao = taskdao;
    }


    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws SQLException, IOException {
        HttpMessage response = handle(request);
        response.write(clientSocket);
    }

    private HttpMessage handle(HttpMessage request) throws SQLException {
        QueryString responseParameter = new QueryString(request.getBody());

        Long taskId = Long.valueOf(responseParameter.getParameter("taskId"));
        Long projectId = Long.valueOf(responseParameter.getParameter("projectId"));
        Task task = taskDao.retrieve(taskId);
        task.setProjectId(projectId);

        taskDao.addTaskToProject(task);

        HttpMessage redirect = new HttpMessage();
        redirect.setStartLine("HTTP/1.1 302 Redirect");
        redirect.getHeaders().put("Location", "http://localhost:8080/redirect.html");
        return redirect;
    }
}
