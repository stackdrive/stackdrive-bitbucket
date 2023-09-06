package org.stackdrive.bitbucket.solution;

import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.repository.RepositoryService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.collect.ImmutableMap;
import org.stackdrive.bitbucket.globalconfig.GlobalConfigService;
import org.stackdrive.bitbucket.source.SourceCodeManagementService;
import org.stackdrive.bitbucket.EventCode;
import org.stackdrive.bitbucket.audit.StackDriveLogService;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class SolutionServlet extends HttpServlet {

    private static final long serialVersionUID = -8400576920477105409L;

    @ComponentImport
    private final RepositoryService repositoryService;

    @ComponentImport
    private final TemplateRenderer renderer;

    @ComponentImport
    private final UserManager userManager;

    private final GlobalConfigService globalConfigService;

    private final SolutionService solutionService;

    private final SourceCodeManagementService sourceCodeManagementService;

    private final StackDriveService stackDriveService;

    private final StackDriveLogService stackDriveLogService;

    @Inject
    public SolutionServlet(RepositoryService repositoryService,
                           TemplateRenderer renderer,
                           UserManager userManager, GlobalConfigService globalConfigService,
                           SolutionService solutionService,
                           SourceCodeManagementService sourceCodeManagementService,
                           StackDriveService stackDriveService,
                           StackDriveLogService stackDriveLogService) {
        this.repositoryService = repositoryService;
        this.renderer = renderer;
        this.userManager = userManager;
        this.globalConfigService = globalConfigService;
        this.solutionService = solutionService;
        this.sourceCodeManagementService = sourceCodeManagementService;
        this.stackDriveService = stackDriveService;
        this.stackDriveLogService = stackDriveLogService;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");

        // Get repoSlug from path
        String pathInfo = request.getPathInfo();

        String[] components = pathInfo.split("/");
        if (components.length < 3) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Repository repository = repositoryService.getBySlug(components[1], components[2]);
        if (repository == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (!solutionService.hasSolutionPermission(repository)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        stackDriveLogService.sendLog(EventCode.SOLUTION_REPOSITORY_VIEW_REPORT, repository.getSlug(), repository);

        String git = sourceCodeManagementService.getScmUrl(repository);
        String base64Git = Base64.getUrlEncoder().withoutPadding().encodeToString(git.getBytes(StandardCharsets.UTF_8));

        String base = globalConfigService.getSolution();
        UserProfile userProfile = userManager.getRemoteUser();
        String solution = base + "/" + base64Git + "/" + userProfile.getUsername();

        ImmutableMap<String, Object> stackDriveDto = ImmutableMap.<String, Object>of(
                "base64Git", base64Git,
                "git", git,
                "solution", solution,
                "health", globalConfigService.getHealth()
        );

        if (stackDriveService.hasStackdrivePropertiesFile(repository)) {
            renderer.render("velocity/repository.vm", ImmutableMap.<String, Object>of("repository", repository, "stackDriveDto", stackDriveDto), response.getWriter());
        } else {
            renderer.render("velocity/nostackdrive.vm", ImmutableMap.<String, Object>of("repository", repository, "stackDriveDto", stackDriveDto), response.getWriter());
        }
    }
}
