package no.kristiania.http;

import no.kristiania.db.Task;
import no.kristiania.db.TaskDao;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class TaskPostController implements HttpController {
    private TaskDao taskDao;

    public TaskPostController(TaskDao taskdao) {
        this.taskDao = taskdao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws SQLException, IOException {
        QueryString requestParameter = new QueryString(request.getBody());

        Task task = new Task();
        task.setTaskName(requestParameter.getParameter("task_name"));
        task.setDesc(requestParameter.getParameter("task_desc"));

        taskDao.insertTask(task);

        String body = "Okay";
        String response = "HTTP/1.1 200 OK\r\n" +
                "Connection: close\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "\r\n" +
                body;
        // Write the response back to the client
        clientSocket.getOutputStream().write(response.getBytes());
    }
}
