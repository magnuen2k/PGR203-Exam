package no.kristiania.http;

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

    }
}
