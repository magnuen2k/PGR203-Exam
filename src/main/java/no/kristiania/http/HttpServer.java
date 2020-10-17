package no.kristiania.http;

import no.kristiania.db.Member;
import no.kristiania.db.MemberDao;
import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGSimpleDataSource;
import software.amazon.awssdk.core.Response;

import javax.sql.DataSource;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Properties;


public class HttpServer {

    private File contentRoot;
    private MemberDao memberDao;

    // Constructor
    public HttpServer(int port, DataSource dataSource) throws IOException {
        memberDao = new MemberDao(dataSource);

        ServerSocket serverSocket = new ServerSocket(port);

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
        System.out.println(requestLine);

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

        if(requestMethod.equals("POST")) {
            QueryString requestParameter = new QueryString(request.getBody());

            // Getting data from POST request
            String firstName = requestParameter.getParameter("first_name");
            String lastName = requestParameter.getParameter("last_name");
            String email = requestParameter.getParameter("email_address");
            String decodedEmail = URLDecoder.decode(email, "UTF-8"); // Decoding email address to make sure '@' is correct

            Member member = new Member(firstName, lastName, decodedEmail);

            memberDao.insertMember(member);
        }

        if (requestPath.equals("/echo")) {
            handleEcho(clientSocket, requestTarget, questionPos);
        } else if (requestPath.equals("/api/projectMembers")){
            handleProjectMembers(clientSocket);
        } else {
            handleResource(clientSocket, requestPath);
        }
    }

    private void handleProjectMembers(Socket clientSocket) throws SQLException, IOException {
        String statusCode = "200";

        String body = "<ul>" + memberDao.list() + "</ul>";

        // Create response
        String response = "HTTP/1.1 " + statusCode + " OK\r\n" +
                "Connection: close\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "Content-Type: text/plain\r\n" +
                "\r\n" +
                body;

        // Send back response to client
        clientSocket.getOutputStream().write(response.getBytes());
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

            String response = "HTTP/1.1 200 OK\r\n" +
                    "Content-Length: " + buffer.toByteArray().length + "\r\n" +
                    "Connection: close\r\n" +
                    "Content-Type: " + contentType + "\r\n" +
                    "\r\n";
            clientSocket.getOutputStream().write(response.getBytes());
            clientSocket.getOutputStream().write(buffer.toByteArray());
        } catch (NullPointerException err) {
            System.out.println("NullPointerException caught!");
        }

       /* // No resource requested
        boolean resourceRequested = true;
        boolean resourceExists = false;
        if (requestPath.equals("/")) {
            resourceRequested = false;
        } else {
            Path p = Paths.get(String.valueOf(contentRoot) + "/" + requestPath);
            if (Files.exists(p)) {
                resourceExists = true;
            }
            System.out.println("Resource found: " + resourceExists);
        }

        // If the requested file does not exist the page will return an error with code 404 Not Found
        if (!resourceRequested || !resourceExists) {
            body = requestPath + " does not exist";
            String response = "HTTP/1.1 404 Not Found\r\n" +
                    "Content-Length: " + body.length() + "\r\n" +
                    "Connection: close\r\n" +
                    "\r\n" +
                    body;
            clientSocket.getOutputStream().write(response.getBytes());
            return;
        }
        File file = new File(contentRoot, requestPath);

        // If the file is HTML, make sure contentType is "text/html"
        String contentType = "text/plain";
        if (file.getName().endsWith(".html")) {
            contentType = "text/html";
        }

        // If the file is CSS, make sure contentType is "text/css"
        if (file.getName().endsWith(".css")) {
            contentType = "text/css";
        }

        // Create response
        String response = "HTTP/1.1 " + statusCode + " OK\r\n" +
                "Content-Length: " + file.length() + "\r\n" +
                "Connection: close\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "\r\n";

        // Send back response to client
        clientSocket.getOutputStream().write(response.getBytes());

        new FileInputStream(file).transferTo(clientSocket.getOutputStream());

        // Create response
        response = "HTTP/1.1 " + statusCode + " OK\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "Connection: close\r\n" +
                "Content-Type: text/plain\r\n" +
                "\r\n" +
                body;

        // Send back response to client
        clientSocket.getOutputStream().write(response.getBytes());*/
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

        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(properties.getProperty("dataSource.url"));
        dataSource.setUser(properties.getProperty("dataSource.username"));
        dataSource.setPassword(properties.getProperty("dataSource.password"));

        Flyway.configure().dataSource(dataSource).load().migrate();

        HttpServer server = new HttpServer(8080, dataSource);

        // Make sure only files form resources are available
       /* server.setContentRoot(new File("src/main/resources"));*/

        System.out.println("To interact with the server, go to this URL");
        System.out.println("localhost:8080");
    }

    public void setContentRoot(File contentRoot) {
        this.contentRoot = contentRoot;
    }
}
