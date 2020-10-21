package no.kristiania.http;

import no.kristiania.db.Member;
import no.kristiania.db.MemberDao;

import java.io.IOException;
import java.net.Socket;
import java.net.URLDecoder;
import java.sql.SQLException;

public class MemberPostController implements HttpController{
    private MemberDao memberDao;

    public MemberPostController(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws SQLException, IOException {
        QueryString requestParameter = new QueryString(request.getBody());

        // Getting data from POST request
        String firstName = requestParameter.getParameter("first_name");
        String lastName = requestParameter.getParameter("last_name");
        String email = requestParameter.getParameter("email_address");

        // Decode data
        String decodedFirstName = URLDecoder.decode(firstName, "UTF-8");
        String decodedLastName = URLDecoder.decode(lastName, "UTF-8");
        String decodedEmail = URLDecoder.decode(email, "UTF-8"); // Decoding email address to make sure '@' is correct

        // Create member object
        Member member = new Member();
        member.setFirstName(decodedFirstName);
        member.setLastName(decodedLastName);
        member.setEmail(decodedEmail);

        // Insert member object to db
        memberDao.insertMember(member);

        // Create response
        String body = "Okay";
        String response = "HTTP/1.1 200 OK\r\n" +
                "Connection: close\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "\r\n" +
                body;

        // Write the response back to the client
        clientSocket.getOutputStream().write(response.getBytes());
    }
}
