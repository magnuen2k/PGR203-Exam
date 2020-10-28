package no.kristiania.http;

import no.kristiania.db.Task;
import no.kristiania.db.TaskDao;

import java.io.IOException;
import java.net.Socket;
import java.net.URLDecoder;
import java.sql.SQLException;

public class TaskPostController implements HttpController {
    private TaskDao taskDao;

    public TaskPostController(TaskDao taskdao) {
        this.taskDao = taskdao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws SQLException, IOException {
        QueryString requestParameter = new QueryString(request.getBody());

        // Get data from POST
        String taskName = requestParameter.getParameter("task_name");
        String taskDesc = requestParameter.getParameter("task_desc");
        Boolean taskStatus = true;

        // Decode data
        String decodedTaskName = URLDecoder.decode(taskName, "UTF-8"); //Makes us able to use "æøå"
        String decodedTaskDesc = URLDecoder.decode(taskDesc, "UTF-8"); //Makes us able to use "æøå"

        // Insert data to task object
        Task task = new Task();
        task.setTaskName(decodedTaskName);
        task.setDesc(decodedTaskDesc);
        task.setTaskStatus(taskStatus);

        // Insert task object to db
        taskDao.insertTask(task);

        // Create response
        //Here we added buffer.toByteArray().length to make sure we got the right .length for UTF-8
        String body = "Task added!";
        String response = "HTTP/1.1 200 OK\r\n" +
                "Connection: close\r\n" +
                "Content-Length: " + body.getBytes().length + "\r\n" +
                "\r\n" +
                body;

        // Write the response back to the client
        clientSocket.getOutputStream().write(response.getBytes());
    }
}
