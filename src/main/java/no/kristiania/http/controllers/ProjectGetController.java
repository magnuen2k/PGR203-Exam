package no.kristiania.http.controllers;

import no.kristiania.db.daos.ProjectDao;
import no.kristiania.db.objects.Project;
import no.kristiania.http.HttpMessage;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ProjectGetController implements HttpController {
    private ProjectDao projectDao;
    public ProjectGetController(ProjectDao projectDao) {
        this.projectDao = projectDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws SQLException, IOException {
        HttpMessage response = new HttpMessage(getBody());
        response.write(clientSocket);
    }

    public String getBody() throws SQLException {
        String body = "<div class='container'>";
        for (Project project : projectDao.list()) {
            // Check if project is active or not
            String status = project.getProjectStatus() ? "Active" : "Inactive";
            String notStatus = !(project.getProjectStatus()) ? "Active" : "Inactive";
            String statusDropDown = "<select name='project_status'><option value='" + project.getProjectStatus() + " '>" + status + "</option><option value='" + !(project.getProjectStatus()) + "'>" + notStatus + "</option></select>";

            // Build output string
            // TODO add styling in css file
            // TODO add project to output
            // TODO display members assigned to project (dropdownlist?)
            // TODO option to add member to project
            body += "<div style='border: 2px solid black; margin-bottom: 20px'>" +
                    "<form method='POST' action='/api/updateProject'>" +
                    "<label>Project Name<input type='text' name='project_name' value='" + project.getProjectName() + "' /></label>" +
                    "<label style='display: none;'>Project ID<input type='text' name='id'' value='" + project.getId() + "'/></label>" +
                    " - " + statusDropDown +
                    "<p>" + project.getDesc() + "</p>" +
                    "<br><button>SUBMIT</button>" +
                    "</form>" +
                    "</div>";
        }

        body += "</div>";

        return body;
    }
}
