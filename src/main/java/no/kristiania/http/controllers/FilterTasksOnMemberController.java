package no.kristiania.http.controllers;

import no.kristiania.db.daos.MemberDao;
import no.kristiania.db.daos.MemberTasksDao;
import no.kristiania.db.daos.ProjectDao;
import no.kristiania.db.daos.TaskDao;
import no.kristiania.db.objects.MemberTasks;
import no.kristiania.db.objects.Task;
import no.kristiania.http.HttpMessage;
import no.kristiania.http.QueryString;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class FilterTasksOnMemberController implements HttpController {
    private MemberDao memberDao;
    private MemberTasksDao memberTasksDao;
    private ProjectDao projectDao;
    private TaskDao taskDao;

    public FilterTasksOnMemberController(MemberDao memberDao, MemberTasksDao memberTasksDao, ProjectDao projectDao, TaskDao taskDao) {
        this.memberDao = memberDao;
        this.memberTasksDao = memberTasksDao;
        this.projectDao = projectDao;
        this.taskDao = taskDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws SQLException, IOException {
        HttpMessage response = handle(request);
        response.write(clientSocket);
    }

    private HttpMessage handle(HttpMessage request) throws SQLException {
        QueryString requestParameter = new QueryString(request.getBody());

        Long memberId = Long.parseLong(requestParameter.getParameter("memberId"));
        boolean status = Boolean.parseBoolean(requestParameter.getParameter("status"));

        String body = memberTasksDao.filter(memberId, status)
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
                        return "<br>" + projectName + " - " + t.getTaskName() + " - " + (t.getTaskStatus() ? "Active" : "Inactive") + " - " + memberDao.retrieve(mt.getMemberId()).getName();
                    } catch (SQLException throwable) {
                        throwable.printStackTrace();
                        return null;
                    }
                })
                .collect(Collectors.joining());

        HttpMessage response = new HttpMessage(body);
        return response;
    }
}
