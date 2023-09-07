package org.stackdrive.bitbucket.projectui;

import com.atlassian.bitbucket.NoSuchEntityException;
import com.atlassian.bitbucket.i18n.I18nService;
import com.atlassian.bitbucket.nav.NavBuilder;
import com.atlassian.bitbucket.permission.Permission;
import com.atlassian.bitbucket.permission.PermissionValidationService;
import com.atlassian.bitbucket.project.NoSuchProjectException;
import com.atlassian.bitbucket.project.Project;
import com.atlassian.bitbucket.project.ProjectService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.collect.ImmutableMap;
import org.stackdrive.bitbucket.dto.StackDriveForm;
import org.stackdrive.bitbucket.globalconfig.GlobalConfigService;
import org.stackdrive.bitbucket.repositoryui.StackDriveRequest;
import org.stackdrive.bitbucket.EventCode;
import org.stackdrive.bitbucket.audit.StackDriveLogService;
import org.stackdrive.bitbucket.permission.ProjectPermissionService;

import javax.inject.Inject;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProjectSettingsServlet extends HttpServlet {

    private static final Pattern PROJECT_PATTERN = Pattern.compile("/([^/]+)/settings");

    private static final long serialVersionUID = -8400576920477105409L;

    @ComponentImport
    private final I18nService i18nService;

    @ComponentImport
    private final NavBuilder navBuilder;

    @ComponentImport
    private final TemplateRenderer renderer;

    @ComponentImport
    private final PermissionValidationService permissionValidationService;

    @ComponentImport
    private final ProjectService projectService;

    @ComponentImport
    private final UserManager userManager;

    private final StackDriveLogService stackDriveLogService;

    private final GlobalConfigService globalConfigService;

    private final ProjectSettingsService projectSettingsService;

    private final ProjectPermissionService projectPermissionService;

    @Inject
    public ProjectSettingsServlet(I18nService i18nService,
                                  NavBuilder navBuilder,
                                  TemplateRenderer renderer,
                                  PermissionValidationService permissionValidationService,
                                  ProjectService projectService,
                                  UserManager userManager,
                                  StackDriveLogService stackDriveLogService,
                                  GlobalConfigService globalConfigService,
                                  ProjectSettingsService projectSettingsService,
                                  ProjectPermissionService projectPermissionService) {
        this.i18nService = i18nService;
        this.navBuilder = navBuilder;
        this.renderer = renderer;
        this.permissionValidationService = permissionValidationService;
        this.projectService = projectService;
        this.userManager = userManager;
        this.stackDriveLogService = stackDriveLogService;
        this.globalConfigService = globalConfigService;
        this.projectSettingsService = projectSettingsService;
        this.projectPermissionService = projectPermissionService;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Matcher matcher = PROJECT_PATTERN.matcher(req.getPathInfo());
        if (matcher.matches()) {
            resp.setContentType("text/html;charset=utf-8");

            StackDriveRequest stackDriveRequest = parseRequest(req);
            Project project = stackDriveRequest.getProject();
            StackDriveForm stackDriveForm = projectSettingsService.getProjectSettings(project);

            ImmutableMap<String, Object> immutableMap = ImmutableMap.<String, Object>of(
                    "project", project,
                    "stackDriveForm", stackDriveForm,
                    "stackDriveWebState", globalConfigService.getHealthCheckState());
            renderer.render("velocity/projectSettings.vm", immutableMap, resp.getWriter());
        } else {
            resp.sendRedirect(navBuilder.allProjects().buildRelative());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserProfile userProfile = userManager.getRemoteUser();

        StackDriveRequest stackDriveRequest = parseRequest(req);

        StackDriveForm stackDriveForm = stackDriveRequest.getForm();
        Project project = stackDriveRequest.getProject();

        projectSettingsService.saveProjectSettings(project, stackDriveForm);
        if (stackDriveForm.getPluginEnabled()) {
            projectPermissionService.grantStackdriveUserPermission(project);
            stackDriveLogService.sendLog(EventCode.SOLUTION_PROJECT_ENABLE_REQUEST, project.getKey(), userProfile.getUsername(), project);
        } else {
            projectPermissionService.revokeStackdriveUserPermission(project);
            stackDriveLogService.sendLog(EventCode.SOLUTION_PROJECT_DISABLE_REQUEST, project.getKey(), userProfile.getUsername(), project);
        }

        resp.sendRedirect(req.getRequestURI());
    }

    private StackDriveRequest parseRequest(HttpServletRequest request) {
        StackDriveForm model = extractModel(request);
        String nextUrl = request.getParameter("next");

        Matcher matcher = PROJECT_PATTERN.matcher(request.getPathInfo());
        if (matcher.matches()) {
            Project project = getProjectIfAdminOrThrow(matcher.group(1));
            return new StackDriveRequest(false, project, null, null, model, nextUrl);
        } else {
            throw new NoSuchEntityException(i18nService.createKeyedMessage("stackdrive.settings.pathnotfound",
                    request.getPathInfo()));
        }
    }

    private Project getProjectIfAdminOrThrow(String projectKey) {
        Project project = projectService.getByKey(projectKey);
        if (project == null) {
            permissionValidationService.validateAuthenticated();
            throw new NoSuchProjectException(i18nService.createKeyedMessage("stackdrive.settings.nosuchproject", projectKey));
        }
        permissionValidationService.validateForProject(project, Permission.PROJECT_ADMIN);
        return project;
    }

    private StackDriveForm extractModel(HttpServletRequest request) {
        String pluginEnabled = request.getParameter("pluginEnabled");

        boolean pluginEnabledBoolean = pluginEnabled != null && (pluginEnabled.equals("on"));
        return new StackDriveForm(pluginEnabledBoolean);
    }
}