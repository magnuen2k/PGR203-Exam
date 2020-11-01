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
        Integer projectId = Integer.valueOf(responseParameter.getParameter("projectId"));
        Task task = taskDao.retrieve(taskId);
        taskDao.update(task); //Johannes has the update on line 37 in ProductDao

        HttpMessage redirect = new HttpMessage();
        redirect.setStartLine("HTTP/1.1 302 Redirect");
        redirect.getHeaders().put("Location", "http://localhost:8080/idenx.html");
        return redirect;
    }
}
