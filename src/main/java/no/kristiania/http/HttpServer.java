package no.kristiania.http;

import no.kristiania.db.Member;
import no.kristiania.db.MemberDao;
import no.kristiania.db.ProjectDao;
import no.kristiania.db.TaskDao;
import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.sql.DataSource;
import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class HttpServer {

    private final MemberDao memberDao;
    private final ServerSocket serverSocket;
    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);
    private final Map<String, HttpController> controllers;

    // Constructor
    public HttpServer(int port, DataSource dataSource) throws IOException {
        memberDao = new MemberDao(dataSource);
        TaskDao taskDao = new TaskDao(dataSource);
        ProjectDao projectDao = new ProjectDao(dataSource);
        controllers = Map.of(
                "/api/tasks", new TaskPostController(taskDao),
                "/api/projectTasks", new TaskGetController(taskDao),
                "/api/members", new MemberPostController(memberDao),
                "/api/projectMembers", new MemberGetController(memberDao),
                "/api/projects", new ProjectPostController(projectDao),
                "/api/projectList", new ProjectGetController(projectDao),
                "/api/updateProject", new ProjectUpdateController(projectDao),
                "/api/taskOptions", new TaskOptionsController(taskDao),
                "/api/projectOptions", new ProjectOptionsController(projectDao),
                "/api/updateTask", new TaskUpdateController(taskDao)
        );

        serverSocket = new ServerSocket(port);

        // Creates Runnable to contain the code to be executed in a separate thread
        Runnable runnable = () -> {
            while (true) {
                try {
                    // Wait for client to connect with blocking call to serverSocket
                    // Get the socket from client connection in return
                    Socket clientSocket = serverSocket.accept();
                    handleRequest(clientSocket); // Calling to handle request
                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                }
            }
        };

        // New Thread for handling the listener socket (serverSocket)
        Thread t = new Thread(runnable);
        t.start(); // Start executing the thread code defined in runnable
    }

    public HttpServer(int port) throws IOException {
        this(port, null);
    }

    // Will be executed per client
    // Does not care about request Method
    private void handleRequest(Socket clientSocket) throws IOException, SQLException {
        // Get back request as string
        // Request can look like this: GET /index.html HTTP/1.1
        HttpMessage request = new HttpMessage(clientSocket);
        String requestLine = request.getStartLine();
        logger.info(requestLine);

        // Split requestLine on space and put element 1 in requestTarget
        // This can look like: /index.html
        String requestTarget = requestLine.split(" ")[1];

        // Split requestLine on space and put element 0 in requestMethod
        // This can look like: GET etc
        String requestMethod = requestLine.split(" ")[0];


        // Position of "?" will make sure we can find the parameters in the request
        // This can be: echo?body=hello where "body=hello" is the parameter
        int questionPos = requestTarget.indexOf('?');

        // If there is parameters set requestPath to requestTarget.substring(0, questionPos)
        // If there is no parameters set requestPath to just requestTarget
        String requestPath = questionPos != -1 ? requestTarget.substring(0, questionPos) : requestTarget;

        if (requestMethod.equals("POST")) {
            getController(requestPath).handle(request, clientSocket);
        }
        else {
            if (requestPath.equals("/echo")) {
                handleEcho(clientSocket, requestTarget, questionPos);
            } else {
                HttpController controller = controllers.get(requestPath);
                if(controller != null) {
                    controller.handle(request, clientSocket);
                } else {
                    handleResource(clientSocket, requestPath);
                }
            }
        }
    }

    private HttpController getController(String requestPath) {
        return controllers.get(requestPath);
    }

    private void handleResource(Socket clientSocket, String requestPath) throws IOException {
        try (InputStream inputStream = getClass().getResourceAsStream(requestPath)) {
            if(inputStream == null) {
                String body = requestPath + " does not exist";
                String response = "HTTP/1.1 404 Not Found\r\n" +
                        "Content-Length: " + body.length() + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n" +
                        body;
                clientSocket.getOutputStream().write(response.getBytes());
            }
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            inputStream.transferTo(buffer);

            // If the file is HTML, make sure contentType is "text/html"
            String contentType = "text/plain";
            if (requestPath.endsWith(".html")) {
                contentType = "text/html";
            }

            // If the file is CSS, make sure contentType is "text/css"
            if (requestPath.endsWith(".css")) {
                contentType = "text/css";
            }

            //Here we added buffer.toByteArray().length to make sure we got the right .length for UTF-8
            String response = "HTTP/1.1 200 OK\r\n" +
                    "Content-Length: " + buffer.toByteArray().length + "\r\n" +
                    "Connection: close\r\n" +
                    "Content-Type: " + contentType + "\r\n" +
                    "\r\n";
            clientSocket.getOutputStream().write(response.getBytes());
            clientSocket.getOutputStream().write(buffer.toByteArray());
        } catch (NullPointerException err) {
           logger.info("NullPointerException caught!");
        }
    }

    private void handleEcho(Socket clientSocket, String requestTarget, int questionPos) throws IOException {
        String returnCode = "200";
        String returnBody = "<strong>Welcome to RP</strong>!";
        if(questionPos != -1) {
            QueryString queryString = new QueryString(requestTarget.substring(questionPos+1));
            if (queryString.getParameter("status") != null) {
                returnCode = queryString.getParameter("status");
            }
            if (queryString.getParameter("body") != null) {
                returnBody = queryString.getParameter("body");
            }
        } else {
            returnBody = "Nothing to echo";
        }
        // Create response
        String response = "HTTP/1.1 " + returnCode + " OK\r\n" +
                "Content-Length: " + returnBody.length() + "\r\n" +
                "Connection: close\r\n" +
                "Content-Type: text/plain\r\n" +
                "\r\n" +
                returnBody;

        // Send back response to client
        clientSocket.getOutputStream().write(response.getBytes());
    }

    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        try(FileReader fileReader = new FileReader("pgr203.properties")) {
            properties.load(fileReader);
        }

        //Required to make it work with an empty database since we .gitignore'd the pgr203.properties-file.
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(properties.getProperty("dataSource.url"));
        dataSource.setUser(properties.getProperty("dataSource.username"));
        dataSource.setPassword(properties.getProperty("dataSource.password"));

        Flyway.configure().dataSource(dataSource).load().migrate();

        HttpServer server = new HttpServer(8080, dataSource);

        logger.info("To interact with the server, go to: localhost:" + server.getPort() + "/index.html");
    }

    public List<Member> getMemberNames() throws SQLException {
        return memberDao.list();
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }
}
