package no.kristiania.http.controllers;

import no.kristiania.db.daos.ProjectDao;
import no.kristiania.db.objects.Project;
import no.kristiania.http.HttpMessage;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.stream.Collectors;

import static no.kristiania.http.controllers.TaskEditController.statusDropDown;

public class ProjectEditController implements HttpController {
    private ProjectDao projectDao;

    public ProjectEditController(ProjectDao projectDao) {
        this.projectDao = projectDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws SQLException, IOException {
        HttpMessage response = new HttpMessage(getBody());
        response.write(clientSocket);
    }

    public String getBody() throws SQLException {
       // TODO add styling in css file
        return projectDao.list()
                .stream().map(p -> "<div style='border: 2px solid black; margin-bottom: 20px'>" +
                        "<form method='POST' action='/api/updateProject'>" +
                        "<label>Project Name<input type='text' name='project_name' value='" + p.getProjectName() + "' /></label>" +
                        "<label style='display: none;'>Project ID<input type='text' name='id'' value='" + p.getId() + "'/></label>" +
                        " - " + statusDropDown(p.getProjectStatus()) +
                        "<label>Project description <input type='text' name='project_desc' value='" + p.getDesc() + "' /></label>" +
                        "<br><button>Edit project</button>" +
                        "</form>" +
                        "</div>")
                .collect(Collectors.joining());
    }
    /*private String statusDropDown(boolean projectStatus) {
        String status = projectStatus ? "Active" : "Inactive";
        String notStatus = !projectStatus ? "Active" : "Inactive";
        return "<select name='project_status'><option value='" + projectStatus + " '>" + status + "</option><option value='" + !projectStatus + "'>" + notStatus + "</option></select>";
    }*/
}
