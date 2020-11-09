package no.kristiania.http.controllers;

import no.kristiania.db.daos.ProjectDao;
import no.kristiania.db.objects.Project;
import no.kristiania.http.HttpMessage;
import no.kristiania.http.HttpServer;
import no.kristiania.http.QueryString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

public class ProjectPostController implements HttpController {
    private final ProjectDao projectDao;
    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    public ProjectPostController(ProjectDao projectDao) {
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
        boolean projectStatus = Boolean.parseBoolean(requestParameter.getParameter("project_status"));

        // Decode data
        String decodedProjectName = URLDecoder.decode(projectName, StandardCharsets.UTF_8); //Makes us able to use "æøå"
        String decodedProjectDesc = URLDecoder.decode(projectDesc, StandardCharsets.UTF_8); //Makes us able to use "æøå"

        // Insert data to project object
        Project project = new Project();
        project.setProjectName(decodedProjectName);
        project.setDesc(decodedProjectDesc);
        project.setProjectStatus(projectStatus);

        // Insert project object to db
        projectDao.insert(project);
        logger.info("Added " + project.getProjectName() + " to the database");

        // Create response
        HttpMessage redirect = new HttpMessage();
        redirect.setStartLine("HTTP/1.1 302 Redirect");
        redirect.getHeaders().put("Location", "http://localhost:8080/redirect.html");
        return redirect;
    }
}
