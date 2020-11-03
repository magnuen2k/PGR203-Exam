package no.kristiania.http.controllers;

import no.kristiania.db.objects.Project;
import no.kristiania.db.daos.ProjectDao;
import no.kristiania.http.HttpMessage;
import no.kristiania.http.QueryString;

import java.io.IOException;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

public class ProjectUpdateController implements HttpController {
    private ProjectDao projectDao;
    public ProjectUpdateController(ProjectDao projectDao) {
        this.projectDao = projectDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws SQLException, IOException {
        HttpMessage response = handle(request);
        response.write(clientSocket);
    }

    public HttpMessage handle(HttpMessage request) throws SQLException {
        QueryString requestParameter = new QueryString(request.getBody());

        // Get data from POST
        String projectName = requestParameter.getParameter("project_name");
        String projectDesc = requestParameter.getParameter("project_desc");
        long projectId = Integer.parseInt(requestParameter.getParameter("id"));
        boolean projectStatus = Boolean.parseBoolean(requestParameter.getParameter("project_status"));

        // Decode data
        String decodedProjectName = URLDecoder.decode(projectName, StandardCharsets.UTF_8); //Makes us able to use "æøå"

        // Insert data to project object
        Project project = projectDao.retrieve(projectId);
        project.setProjectName(decodedProjectName);
        project.setDesc(projectDesc);
        project.setProjectStatus(projectStatus);

        // Insert project object to db
        projectDao.update(project, projectId);

        // Create response
        HttpMessage redirect = new HttpMessage();
        redirect.setStartLine("HTTP/1.1 302 Redirect");
        redirect.getHeaders().put("Location", "http://localhost:8080/redirect.html");
        return redirect;
    }
}
