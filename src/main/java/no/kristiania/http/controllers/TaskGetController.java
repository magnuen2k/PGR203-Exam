package no.kristiania.http.controllers;

import no.kristiania.db.daos.MemberDao;
import no.kristiania.db.daos.MemberTasksDao;
import no.kristiania.db.daos.ProjectDao;
import no.kristiania.db.objects.Member;
import no.kristiania.db.objects.Task;
import no.kristiania.db.daos.TaskDao;
import no.kristiania.http.HttpMessage;
import no.kristiania.http.QueryString;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class TaskGetController implements HttpController {
    private final TaskDao taskDao;
    private final MemberTasksDao memberTasksDao;
    private final MemberDao memberDao;
    private final ProjectDao projectDao;

    public TaskGetController(TaskDao taskdao, MemberTasksDao memberTasksDao, MemberDao memberDao, ProjectDao projectDao) {
        this.taskDao = taskdao;
        this.memberTasksDao = memberTasksDao;
        this.memberDao = memberDao;
        this.projectDao = projectDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws SQLException, IOException {
        HttpMessage response = new HttpMessage(getBody(request));
        response.write(clientSocket);
    }

    public String getBody(HttpMessage request) throws SQLException {

        String requestLine = request.getStartLine();
        String requestTarget = requestLine.split(" ")[1];
        int questionPos = requestTarget.indexOf('?');

        if(questionPos != -1 ) {
            QueryString queryString = new QueryString(requestTarget.substring(questionPos+1));
            Long memberId = Long.parseLong(queryString.getParameter("memberId"));
            boolean status = Boolean.parseBoolean(queryString.getParameter("status"));
            return memberTasksDao.filter(memberId, status)
                    .stream().map(mt -> {
                        try {
                            Task t = taskDao.retrieve(mt.getTaskId());
                            String projectName = "Not assigned";
                            if(t == null) {
                                return null;
                            }
                            if(t.getProjectId() != null) {
                                projectName = projectDao.retrieve(t.getProjectId()).getProjectName();
                            }
                            return "<div class='outputDiv'>" + projectName + " - " + t.getTaskName() + " - " + (t.getTaskStatus() ? "Active" : "Inactive") + " - " + memberDao.retrieve(mt.getMemberId()).getName() + "</div>";
                        } catch (SQLException throwable) {
                            throwable.printStackTrace();
                            return null;
                        }
                    })
                    .collect(Collectors.joining());
        }

        // TODO Try using streams
        StringBuilder body = new StringBuilder();
        for(Task t : taskDao.list()) {
            body.append("<div class='outputDiv'>" + "<p>Task: ").append(t.getTaskName()).append(" - ").append(t.getTaskStatus() ? "Active" : "Inactive").append("</p>").append("<p>Description: ").append(t.getDesc()).append("</p>");

            body.append("<select>");
            List<Member> members = memberDao.getMembersOnTask(t.getId());
            for (Member member : members) {
                body.append("<option value='").append(member.getId()).append("'>").append(member.getName()).append("</option>");
            }
            body.append("</select></div>");
        }

        return body.toString();
    }
}
