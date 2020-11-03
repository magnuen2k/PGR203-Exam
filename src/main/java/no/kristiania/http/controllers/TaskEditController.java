package no.kristiania.http.controllers;

import no.kristiania.db.daos.TaskDao;
import no.kristiania.db.objects.Task;
import no.kristiania.http.HttpMessage;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class TaskEditController implements HttpController{
    private TaskDao taskDao;

    public TaskEditController(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws SQLException, IOException {
        HttpMessage response = new HttpMessage(getBody());
        response.write(clientSocket);
    }

    public String getBody() throws SQLException {
        String body = "";
        for (Task task : taskDao.list()) {
            // Check if task is active or not
            String status = task.getTaskStatus() ? "Active" : "Inactive";
            String notStatus = !(task.getTaskStatus()) ? "Active" : "Inactive";
            String statusDropDown = "<select><option value='" + task.getTaskStatus() + " '>" + status + "</option><option value='" + !(task.getTaskStatus()) + "'>" + notStatus + "</option></select>";

            // Build output string
            // TODO add styling in css file
            body += "<div style='border: 2px solid black; margin-bottom: 20px'>" +
                    "<form method='POST' action='/api/updateTask'>" +
                    "<label>Task Name<input type='text' name='task_name' value='" + task.getTaskName() + "' /></label>" +
                    "<label style='display: none;'>Task ID<input type='text' name='id'' value='" + task.getId() + "'/></label>" +
                    " - " + statusDropDown +
                    "<label>Task description <input type='text' name='task_desc' value='" + task.getDesc() + "' /></label>" +
                    "<br><button>Edit task</button>" +
                    "</form>" +
                    "</div>";
        }
        return body;
    }
}
