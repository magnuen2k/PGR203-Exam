package no.kristiania.http.controllers;

import no.kristiania.db.daos.ProjectDao;
import no.kristiania.http.HttpMessage;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.stream.Collectors;

import static no.kristiania.http.controllers.TaskEditController.statusDropDown;

public class ProjectEditController implements HttpController {
    private final ProjectDao projectDao;

    public ProjectEditController(ProjectDao projectDao) {
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
                        "<form method='POST' action='/api/updateProject'>" +
                        "<label>Project Name<input type='text' name='project_name' value='" + p.getProjectName() + "' /></label>" +
                        "<input type='hidden' name='id'' value='" + p.getId() + "'/>" +
                        "Status - " + statusDropDown(p.getProjectStatus(), "project") +
                        "<br><br><label>Project description <input type='text' name='project_desc' value='" + p.getDesc() + "' /></label>" +
                        "<br><button>Edit project</button>" +
                        "</form>" +
                        "</div>")
                .collect(Collectors.joining());
    }
}
