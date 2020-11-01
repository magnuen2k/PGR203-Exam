package no.kristiania.http;

import no.kristiania.db.ProjectDao;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class ProjectOptionsController implements HttpController {
    private ProjectDao projectDao;

    public ProjectOptionsController(ProjectDao projectDao) {
        this.projectDao = projectDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws SQLException, IOException {
        HttpMessage response = new HttpMessage(getBody());
        response.write(clientSocket);
    }

    private String getBody() throws SQLException {
        return projectDao.list()
                .stream().map(p -> "<option value=" + p.getId() + ">" + p.getProjectName() + "</option>")
                .collect(Collectors.joining());
    }
}
