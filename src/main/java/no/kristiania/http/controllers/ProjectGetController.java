package no.kristiania.http.controllers;

import no.kristiania.db.daos.ProjectDao;
import no.kristiania.http.HttpMessage;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class ProjectGetController implements HttpController {
    private final ProjectDao projectDao;
    public ProjectGetController(ProjectDao projectDao) {
        this.projectDao = projectDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws SQLException, IOException {
        HttpMessage response = new HttpMessage(getBody());
        response.write(clientSocket);
    }

    public String getBody() throws SQLException {
        return projectDao.list()
                .stream().map(p -> "<div class='outputDiv'>" +
                        "<p>Project Name: " + p.getProjectName() + " - " + (p.getProjectStatus() ? "Active" : "Inactive") + "</p>" +
                        "<p>Project Description: " + p.getDesc() + "</p>" +
                        "</div>")
                .collect(Collectors.joining());
    }
}
