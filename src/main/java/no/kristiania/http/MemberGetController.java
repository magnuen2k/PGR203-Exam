package no.kristiania.http;

import no.kristiania.db.Member;
import no.kristiania.db.MemberDao;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class MemberGetController implements HttpController{
    private MemberDao memberDao;

    public MemberGetController(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws SQLException, IOException {
        String statusCode = "200";

        // Create string to build response body
        String body = "<ul>";
        for (Member member : memberDao.list()) {
            body += "<li>Name: " + member.getName() + " - Email: " + member.getEmail() + "</li>";
        }
        body += "</ul>";

        // Create response
        String response = "HTTP/1.1 " + statusCode + " OK\r\n" +
                "Connection: close\r\n" +
                "Content-Length: " + body.getBytes().length + "\r\n" +
                "Content-Type: text/plain\r\n" +
                "\r\n" +
                body;

        // Send back response to client
        clientSocket.getOutputStream().write(response.getBytes());
    }
}
