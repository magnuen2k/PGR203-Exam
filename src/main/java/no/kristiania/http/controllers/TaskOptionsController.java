package no.kristiania.http.controllers;

import no.kristiania.db.daos.TaskDao;
import no.kristiania.http.HttpMessage;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class TaskOptionsController implements HttpController {
    private final TaskDao taskDao;

    public TaskOptionsController(TaskDao taskdao) {
        this.taskDao = taskdao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws SQLException, IOException {
        HttpMessage response = new HttpMessage(getBody());
        response.write(clientSocket);
    }

    private String getBody() throws SQLException {
        return taskDao.list()
                .stream().map(t -> "<option value=" + t.getId() + ">" + t.getTaskName() + "</option>")
                .collect(Collectors.joining());
    }
}
