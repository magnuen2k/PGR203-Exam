package no.kristiania.http;

import no.kristiania.db.ProjectDao;
import no.kristiania.db.Project;

import java.io.IOException;
import java.net.Socket;
import java.net.URLDecoder;
import java.sql.SQLException;

public class ProjectPostController implements HttpController {
    private ProjectDao projectDao;

    public ProjectPostController(ProjectDao projectDao) {
        this.projectDao = projectDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws SQLException, IOException {
        QueryString requestParameter = new QueryString(request.getBody());

        // Get data from POST
        String projectName = requestParameter.getParameter("project_name");
        String projectDesc = requestParameter.getParameter("project_desc");

        // Setting default status TRUE
        Boolean projectStatus = true;

        // Decode data
        String decodedProjectName = URLDecoder.decode(projectName, "UTF-8"); //Makes us able to use "æøå"
        String decodedProjectDesc = URLDecoder.decode(projectDesc, "UTF-8"); //Makes us able to use "æøå"

        // Insert data to project object
        Project project = new Project();
        project.setProjectName(decodedProjectName);
        project.setDesc(decodedProjectDesc);
        project.setProjectStatus(projectStatus);

        // Insert project object to db
        projectDao.insert(project);

        // Create response
        //Here we added buffer.toByteArray().length to make sure we got the right .length for UTF-8
        String body = "Project added!";
        String response = "HTTP/1.1 200 OK\r\n" +
                "Connection: close\r\n" +
                "Content-Length: " + body.getBytes().length + "\r\n" +
                "\r\n" +
                body;

        // Write the response back to the client
        clientSocket.getOutputStream().write(response.getBytes());
    }
}
