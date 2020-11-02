package no.kristiania.http.controllers;

import no.kristiania.db.objects.Project;
import no.kristiania.db.daos.ProjectDao;
import no.kristiania.http.HttpMessage;
import no.kristiania.http.QueryString;

import java.io.IOException;
import java.net.Socket;
import java.net.URLDecoder;
import java.sql.SQLException;

public class ProjectUpdateController implements HttpController {
    private ProjectDao projectDao;
    public ProjectUpdateController(ProjectDao projectDao) {
        this.projectDao = projectDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws SQLException, IOException {
        QueryString requestParameter = new QueryString(request.getBody());

        // Get data from POST
        String projectName = requestParameter.getParameter("project_name");
        int projectId = Integer.parseInt(requestParameter.getParameter("id"));
        boolean projectStatus = Boolean.parseBoolean(requestParameter.getParameter("project_status"));

        // Decode data
        String decodedProjectName = URLDecoder.decode(projectName, "UTF-8"); //Makes us able to use "æøå"

        // Insert data to project object
        Project project = new Project();
        project.setProjectName(decodedProjectName);
        project.setProjectStatus(projectStatus);

        // Insert project object to db
        projectDao.update(project, projectId);

        // Create response
        //Here we added buffer.toByteArray().length to make sure we got the right .length for UTF-8
        String body = "Project updated!";
        String response = "HTTP/1.1 200 OK\r\n" +
                "Connection: close\r\n" +
                "Content-Length: " + body.getBytes().length + "\r\n" +
                "\r\n" +
                body;

        // Write the response back to the client
        clientSocket.getOutputStream().write(response.getBytes());
    }
}
