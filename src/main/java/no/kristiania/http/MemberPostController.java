package no.kristiania.http;

import no.kristiania.db.Member;
import no.kristiania.db.MemberDao;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
        // Create response
        HttpMessage response = handle(request);
        response.write(clientSocket);
    }

    public HttpMessage handle(HttpMessage request) throws UnsupportedEncodingException, SQLException {
        QueryString requestParameter = new QueryString(request.getBody());

        // Getting data from POST request
        String firstName = requestParameter.getParameter("first_name");
        String lastName = requestParameter.getParameter("last_name");
        String email = requestParameter.getParameter("email_address");

        // Decode data to UTF-8 format
        String decodedFirstName = URLDecoder.decode(firstName, "UTF-8"); //Makes us able to use "æøå"
        String decodedLastName = URLDecoder.decode(lastName, "UTF-8"); //Makes us able to use "æøå"
        String decodedEmail = URLDecoder.decode(email, "UTF-8"); // Decoding email address to make sure '@' is correct and not %40

        // Create member object
        Member member = new Member();
        member.setFirstName(decodedFirstName);
        member.setLastName(decodedLastName);
        member.setEmail(decodedEmail);

        // Insert member object to db
        memberDao.insertMember(member);

        // Add redirect to response
        HttpMessage redirect = new HttpMessage();
        redirect.setStartLine("HTTP/1.1 302 Redirect");
        redirect.getHeaders().put("Location", "http://localhost:8080/redirect.html");
        return redirect;
    }
}
