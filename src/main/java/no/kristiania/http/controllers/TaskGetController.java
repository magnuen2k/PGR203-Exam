package no.kristiania.http.controllers;

import no.kristiania.db.objects.Task;
import no.kristiania.db.daos.TaskDao;
import no.kristiania.http.HttpMessage;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class TaskGetController implements HttpController {
    private TaskDao taskDao;

    public TaskGetController(TaskDao taskdao) {
        this.taskDao = taskdao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws SQLException, IOException {
        HttpMessage response = new HttpMessage(getBody());
        response.write(clientSocket);
    }

    public String getBody() throws SQLException {
        return taskDao.list()
                .stream().map(t -> "<div style='border: 2px solid black; margin-bottom: 20px'>" +
                        "<p>Task: " + t.getTaskName() + " - " + (t.getTaskStatus() ? "Active" : "Inactive") + "</p>" +
                        "<p>Description: " + t.getDesc() + "</p>" +
                        "</div>")
                .collect(Collectors.joining());
    }
}
