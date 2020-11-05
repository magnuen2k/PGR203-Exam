package no.kristiania.http.controllers;

import no.kristiania.http.HttpMessage;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class RedirectController implements HttpController{
    private final String path;
    public RedirectController(String path) {
        this.path = path;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws SQLException, IOException {
        HttpMessage redirect = new HttpMessage();
        redirect.setStartLine("HTTP/1.1 302 Redirect");
        redirect.getHeaders().put("Location", "http://localhost:8080/" + path);
        redirect.write(clientSocket);
    }
}
