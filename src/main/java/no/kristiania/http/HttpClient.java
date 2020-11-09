package no.kristiania.http;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;

public class HttpClient {

    private final int statusCode;
    private final Map<String, String> responseHeaders;
    private final String responseBody;

    // If constructor is called without a requestBody, call itself again with a requestBody of null
    public HttpClient(final String hostname, int port, final String requestTarget) throws IOException {
        this(hostname, port, requestTarget, "GET", null);
    }

    public HttpClient(final String hostname, int port, final String requestTarget, final String httpMethod, String requestBody) throws IOException {
        // Create connection to server
        Socket socket = new Socket(hostname, port);

        // Get content-length
        String contentLengthHeader = requestBody != null ? "Content-Length: " + requestBody.length() + "\r\n" : "";

        // Creating HTTP request
        String request = httpMethod + " " + requestTarget + " HTTP/1.1\r\n" +
                "Host: " + hostname + "\r\n" +
                contentLengthHeader +
                "\r\n";

        // Send request made to server
        socket.getOutputStream().write(request.getBytes());

        if (requestBody != null) {
            socket.getOutputStream().write(requestBody.getBytes());
        }

        HttpMessage response = new HttpMessage(socket);

        String responseLine = response.getStartLine();
        responseHeaders = response.getHeaders();
        responseBody = response.getBody();

        String[] responseLineParts = responseLine.split(" ");

        statusCode = Integer.parseInt(responseLineParts[1]);
    }

    public static void main(String[] args) throws IOException {
        HttpClient client = new HttpClient("urlecho.appspot.com", 80, "/echo?status=404&Content-Type=text%2Fhtml&body=Hello+world");
        System.out.println(client.getResponseBody());
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseHeader(String headerName) {
        return responseHeaders.get(headerName);
    }

    public String getResponseBody() {
        return responseBody;
    }


}
