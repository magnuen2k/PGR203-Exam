package no.kristiania.http;

import no.kristiania.db.Task;
import no.kristiania.db.TaskDao;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class TaskUpdateController implements HttpController{
    private TaskDao taskDao;

    public TaskUpdateController(TaskDao taskdao) {
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

        taskDao.update(task);

        HttpMessage redirect = new HttpMessage();
        redirect.setStartLine("HTTP/1.1 302 Redirect");
        redirect.getHeaders().put("Location", "http://localhost:8080/redirect.html");
        return redirect;
    }
}
