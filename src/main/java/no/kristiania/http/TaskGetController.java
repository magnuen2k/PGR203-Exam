package no.kristiania.http;

import no.kristiania.db.Task;
import no.kristiania.db.TaskDao;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class TaskGetController implements HttpController{
    private TaskDao taskDao;

    public TaskGetController(TaskDao taskdao) {
        this.taskDao = taskdao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws SQLException, IOException {
        String body = "<div>";
        for (Task task : taskDao.list()) {
            // Check if task is active or not
            String status = task.getTaskStatus() ? "Active" : "Inactive";
            String notStatus = !(task.getTaskStatus()) ? "Active" : "Inactive";
            String statusDropDown = "<select><option value='" + task.getTaskStatus() + " '>" + status + "</option><option value='" + !(task.getTaskStatus()) + "'>" + notStatus + "</option></select>";

            // Build output string
            // TODO add styling in css file
            // TODO add project to output
            // TODO display members assigned to task (dropdownlist?)
            // TODO option to add member to task
            body += "<div style='border: 2px solid black; margin-bottom: 20px'><p>" + task.getTaskName() + " - " + statusDropDown + "</p><p>" + task.getDesc() + "</p></div>";
        }

        body += "</div>";
        //Here we added buffer.toByteArray().length to make sure we got the right .length for UTF-8
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: " + body.getBytes().length + "\r\n" +
                "Content-Type: text/html\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                body;

        // Write the response back to the client
        clientSocket.getOutputStream().write(response.getBytes());
    }
}
