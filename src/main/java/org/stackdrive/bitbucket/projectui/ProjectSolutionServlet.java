package org.stackdrive.bitbucket.projectui;

import com.atlassian.bitbucket.NoSuchEntityException;
import com.atlassian.bitbucket.i18n.I18nService;
import com.atlassian.bitbucket.nav.NavBuilder;
import com.atlassian.bitbucket.permission.Permission;
import com.atlassian.bitbucket.permission.PermissionValidationService;
import com.atlassian.bitbucket.project.NoSuchProjectException;
import com.atlassian.bitbucket.project.Project;
import com.atlassian.bitbucket.project.ProjectService;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.repository.RepositoryService;
import com.atlassian.bitbucket.util.Page;
import com.atlassian.bitbucket.util.PageRequestImpl;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.collect.ImmutableMap;
import org.stackdrive.bitbucket.dto.StackDriveForm;
import org.stackdrive.bitbucket.globalconfig.GlobalConfigService;
import org.stackdrive.bitbucket.repositoryui.RepositorySettingsService;
import org.stackdrive.bitbucket.repositoryui.StackDriveRequest;
import org.stackdrive.bitbucket.solution.SolutionService;
import org.stackdrive.bitbucket.EventCode;
import org.stackdrive.bitbucket.audit.StackDriveLogService;
import org.stackdrive.bitbucket.source.SourceCodeManagementService;

import javax.inject.Inject;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProjectSolutionServlet extends HttpServlet {

    private static final Pattern PROJECT_PATTERN = Pattern.compile("/([^/]+)");

    private static final long serialVersionUID = -8400576920477105409L;

    @ComponentImport
    private final RepositoryService repositoryService;

    @ComponentImport
    private final I18nService i18nService;

    @ComponentImport
    private final TemplateRenderer renderer;

    @ComponentImport
    private final NavBuilder navBuilder;

    @ComponentImport
    private final PermissionValidationService permissionValidationService;

    @ComponentImport
    private final ProjectService projectService;

    @ComponentImport
    private final UserManager userManager;

    private final GlobalConfigService globalConfigService;

    private final SolutionService solutionService;

    private final SourceCodeManagementService sourceCodeManagementService;

    private final RepositorySettingsService repositorySettingsService;

    private final StackDriveLogService stackDriveLogService;

    @Inject
    public ProjectSolutionServlet(RepositoryService repositoryService,
                                  I18nService i18nService, TemplateRenderer renderer,
                                  NavBuilder navBuilder, PermissionValidationService permissionValidationService, ProjectService projectService, UserManager userManager, GlobalConfigService globalConfigService,
                                  SolutionService solutionService, SourceCodeManagementService sourceCodeManagementService, RepositorySettingsService repositorySettingsService, StackDriveLogService stackDriveLogService) {
        this.repositoryService = repositoryService;
        this.i18nService = i18nService;
        this.renderer = renderer;
        this.navBuilder = navBuilder;
        this.permissionValidationService = permissionValidationService;
        this.projectService = projectService;
        this.userManager = userManager;
        this.globalConfigService = globalConfigService;
        this.solutionService = solutionService;
        this.sourceCodeManagementService = sourceCodeManagementService;
        this.repositorySettingsService = repositorySettingsService;
        this.stackDriveLogService = stackDriveLogService;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Matcher matcher = PROJECT_PATTERN.matcher(req.getPathInfo());
        if (matcher.matches()) {
            resp.setContentType("text/html;charset=utf-8");

            StackDriveRequest stackDriveRequest = parseRequest(req);
            Project project = stackDriveRequest.getProject();
            if (project == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            if (!solutionService.hasSolutionPermission(project)) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            stackDriveLogService.sendLog(EventCode.SOLUTION_PROJECT_VIEW_REPORT, project.getKey(), project);

            List<String> base64GitList = new ArrayList<>(20);
            Page<Repository> byProjectId = repositoryService.findByProjectId(project.getId(), new PageRequestImpl(0, 100));

            for (Repository repository : byProjectId.getValues()) {
                StackDriveForm repoSetting = repositorySettingsService.getRepoSetting(repository);
                if (repoSetting.getProjectScope()) {
                    String git = sourceCodeManagementService.getScmUrl(repository);
                    base64GitList.add(Base64.getUrlEncoder().withoutPadding().encodeToString(git.getBytes(StandardCharsets.UTF_8)));
                }
            }

            String base64Git = String.join(",", base64GitList);

            String base = globalConfigService.getSolution();
            UserProfile userProfile = userManager.getRemoteUser();
            String solution = base + "/" + base64Git + "/" + userProfile.getUsername();

            ImmutableMap<String, Object> immutableMap = ImmutableMap.<String, Object>of(
                    "project", project,
                    "solution", solution,
                    "noRepos", base64GitList.isEmpty());
            renderer.render("velocity/project.vm", immutableMap, resp.getWriter());
        } else {
            resp.sendRedirect(navBuilder.allProjects().buildRelative());
        }
    }

    private StackDriveRequest parseRequest(HttpServletRequest request) {
        StackDriveForm model = extractModel(request);
        String nextUrl = request.getParameter("next");

        Matcher matcher = PROJECT_PATTERN.matcher(request.getPathInfo());
        if (matcher.matches()) {
            Project project = getProjectIfViewerOrThrow(matcher.group(1));
            return new StackDriveRequest(false, project, null, null, model, nextUrl);
        } else {
            throw new NoSuchEntityException(i18nService.createKeyedMessage("stackdrive.settings.pathnotfound",
                    request.getPathInfo()));
        }
    }

    private Project getProjectIfViewerOrThrow(String projectKey) {
        Project project = projectService.getByKey(projectKey);
        if (project == null) {
            permissionValidationService.validateAuthenticated();
            throw new NoSuchProjectException(i18nService.createKeyedMessage("stackdrive.settings.nosuchproject", projectKey));
        }
        permissionValidationService.validateForProject(project, Permission.PROJECT_VIEW);
        return project;
    }

    private StackDriveForm extractModel(HttpServletRequest request) {
        String pluginEnabled = request.getParameter("pluginEnabled");
        boolean pluginEnabledBoolean = pluginEnabled != null && (pluginEnabled.equals("on"));
        return new StackDriveForm(pluginEnabledBoolean);
    }
}
