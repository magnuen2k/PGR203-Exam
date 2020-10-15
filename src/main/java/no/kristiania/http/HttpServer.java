package no.kristiania.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class HttpServer {

    private File contentRoot;

    // Arraylist to store members (Not in use right now, made for POST requests)
    private List<String> memberNames = new ArrayList<>();

    // Constructor
    public HttpServer(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);

        // Creates Runnable to contain the code to be executed in a separate thread
        Runnable runnable = () -> {
            while (true) {
                try {
                    // Wait for client to connect with blocking call to serverSocket
                    // Get the socket from client connection in return
                    Socket clientSocket = serverSocket.accept();
                    handleRequest(clientSocket); // Calling to handle request
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        // New Thread for handling the listener socket (serverSocket)
        Thread t = new Thread(runnable);
        t.start(); // Start executing the thread code defined in runnable

        // Adding sample users
        memberNames.add("Magnus - magnus@magnus.no");
        memberNames.add("Lauri - Lauri@lauri.no");
    }

    // Will be executed per client
    // Does not care about request Method
    private void handleRequest(Socket clientSocket) throws IOException {
        // Get back request as string
        // Request can look like this: GET /index.html HTTP/1.1
        HttpMessage request = new HttpMessage(clientSocket);
        String requestLine = request.getStartLine();
        System.out.println(requestLine);

        // Split requestLine on space and put element 1 in requestTarget
        // This can look like: /index.html
        String requestTarget = requestLine.split(" ")[1];


        // Position of "?" will make sure we can find the parameters in the request
        // This can be: echo?body=hello where "body=hello" is the parameter
        int questionPos = requestTarget.indexOf('?');

        // If there is parameters set requestPath to requestTarget.substring(0, questionPos)
        // If there is no parameters set requestPath to just requestTarget
        String requestPath = questionPos != -1 ? requestTarget.substring(0, questionPos) : requestTarget;

        if (requestPath.equals("/echo")) {
            handleEcho(clientSocket, requestTarget, questionPos);
        } else if (requestPath.equals("/api/projectMembers")){
            String statusCode = "200";
            String body = "";

            for (int i = 0; i < memberNames.size(); i++) {
                body += memberNames.get(i) + "<br>\n";
            }

            // Create response
            String response = "HTTP/1.1 " + statusCode + " OK\r\n" +
                    "Content-Length: " + body.length() + "\r\n" +
                    "Content-Type: text/plain\r\n" +
                    "\r\n" +
                    body;

            // Send back response to client
            clientSocket.getOutputStream().write(response.getBytes());
        } else {
            handleResource(clientSocket, requestPath);
        }
    }

    private void handleResource(Socket clientSocket, String requestPath) throws IOException {
        String statusCode = "200";
        String body = "<strong>Welcome to RP</strong>!";
        // No resource requested
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
                "Content-Type: " + contentType + "\r\n" +
                "\r\n";

        // Send back response to client
        clientSocket.getOutputStream().write(response.getBytes());

        new FileInputStream(file).transferTo(clientSocket.getOutputStream());

        // Create response
        response = "HTTP/1.1 " + statusCode + " OK\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "Content-Type: text/plain\r\n" +
                "\r\n" +
                body;

        // Send back response to client
        clientSocket.getOutputStream().write(response.getBytes());
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
                "Content-Type: text/plain\r\n" +
                "\r\n" +
                returnBody;

        // Send back response to client
        clientSocket.getOutputStream().write(response.getBytes());
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = new HttpServer(8080);

        // Make sure only files form resources are available
        server.setContentRoot(new File("src/main/resources"));

        System.out.println("To interact with the server, go to this URL");
        System.out.println("localhost:8080");
    }

    public void setContentRoot(File contentRoot) {
        this.contentRoot = contentRoot;
    }

    public List<String> getMemberNames() {
        return memberNames;
    }
}
