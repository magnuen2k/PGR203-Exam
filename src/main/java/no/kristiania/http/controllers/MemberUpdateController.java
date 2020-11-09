package no.kristiania.http.controllers;

import no.kristiania.db.daos.MemberDao;
import no.kristiania.db.objects.Member;
import no.kristiania.http.HttpMessage;
import no.kristiania.http.QueryString;

import java.io.IOException;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

public class MemberUpdateController implements HttpController{
    private final MemberDao memberDao;

    public MemberUpdateController(MemberDao memberDao) {
        this.memberDao = memberDao;
    }


    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws SQLException, IOException {
        HttpMessage response = handle(request);
        response.write(clientSocket);
    }

    private HttpMessage handle(HttpMessage request) throws SQLException {
        QueryString requestParameter = new QueryString(request.getBody());

        // Get data from POST
        String firstName = requestParameter.getParameter("first_name");
        String lastName = requestParameter.getParameter("last_name");
        String email = requestParameter.getParameter("email");
        long memberId = Integer.parseInt(requestParameter.getParameter("id"));

        // Decode data
        String decodedFirstName = URLDecoder.decode(firstName, StandardCharsets.UTF_8); //Makes us able to use "æøå"
        String decodedLastName = URLDecoder.decode(lastName, StandardCharsets.UTF_8); //Makes us able to use "æøå"
        String decodedEmail = URLDecoder.decode(email, StandardCharsets.UTF_8); //Makes us able to use "æøå"

        // Insert data to member object
        Member member = memberDao.retrieve(memberId);
        member.setFirstName(decodedFirstName);
        member.setLastName(decodedLastName);
        member.setEmail(decodedEmail);

        // Insert project object to db
        memberDao.update(member, memberId);

        HttpMessage redirect = new HttpMessage();
        redirect.setStartLine("HTTP/1.1 302 Redirect");
        redirect.getHeaders().put("Location", "http://localhost:8080/redirect.html");
        return redirect;
    }
}
