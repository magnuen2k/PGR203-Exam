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
        String body = "<ul>";
        for (Task task : taskDao.list()) {
            body += "<li>" + task.getTaskName() + "</li>";
        }

        body += "</ul>";
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "Content-Type: text/html\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                body;

        // Write the response back to the client
        clientSocket.getOutputStream().write(response.getBytes());
    }
}
