package no.kristiania.http.controllers;

import no.kristiania.db.daos.TaskDao;
import no.kristiania.http.HttpMessage;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class TaskEditController implements HttpController{
    private final TaskDao taskDao;

    public TaskEditController(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws SQLException, IOException {
        HttpMessage response = new HttpMessage(getBody());
        response.write(clientSocket);
    }

    public String getBody() throws SQLException {
        return taskDao.list()
                .stream().map(t -> "<div class='outputDiv'>" +
                        "<form method='POST' action='/api/updateTasks'>" +
                        "<label>Task Name<input type='text' name='task_name' value='" + t.getTaskName() + "' /></label>" +
                        "<input type='hidden' name='id' value='" + t.getId() + "'/>" +
                        "Status - " + statusDropDown(t.getTaskStatus(), "task") +
                        "<br><br><label>Task description <input type='text' name='task_desc' value='" + t.getDesc() + "' /></label>" +
                        "<br><button>Edit task</button>" +
                        "</form>" +
                        "</div>")
                .collect(Collectors.joining());
    }

    public static String statusDropDown(boolean statusState, String type) {
        String status = statusState ? "Active" : "Inactive";
        String notStatus = !statusState ? "Active" : "Inactive";
        return "<select name='" + type + "_status'><option value='" + statusState + "'>" + status + "</option><option value='" + !statusState + "'>" + notStatus + "</option></select>";
    }
}
