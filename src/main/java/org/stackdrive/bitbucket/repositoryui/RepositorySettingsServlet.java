package org.stackdrive.bitbucket.repositoryui;

import com.atlassian.bitbucket.NoSuchEntityException;
import com.atlassian.bitbucket.i18n.I18nService;
import com.atlassian.bitbucket.nav.NavBuilder;
import com.atlassian.bitbucket.permission.Permission;
import com.atlassian.bitbucket.permission.PermissionValidationService;
import com.atlassian.bitbucket.project.NoSuchProjectException;
import com.atlassian.bitbucket.project.Project;
import com.atlassian.bitbucket.project.ProjectService;
import com.atlassian.bitbucket.repository.NoSuchRepositoryException;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.repository.RepositoryService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import org.stackdrive.bitbucket.dto.StackDriveForm;
import org.stackdrive.bitbucket.globalconfig.GlobalConfigService;
import org.stackdrive.bitbucket.EventCode;
import org.stackdrive.bitbucket.audit.StackDriveLogService;
import org.stackdrive.bitbucket.permission.RepositoryPermissionService;
import org.stackdrive.bitbucket.properties.StackDriveProperties;

import javax.inject.Inject;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RepositorySettingsServlet extends HttpServlet {

    private static final Pattern REPO_PATTERN = Pattern.compile("/([^/]+)/repos/([^/]+)/settings");

    private static final long serialVersionUID = -8400576920477105409L;

    @ComponentImport
    private final RepositoryService repositoryService;

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

    private final RepositorySettingsService repositorySettingsService;

    private final RepositoryPermissionService repositoryPermissionService;

    private final StackDriveLogService stackDriveLogService;

    private final GlobalConfigService globalConfigService;

    private final String buildVersion;


    @Inject
    public RepositorySettingsServlet(RepositoryService repositoryService,
                                     I18nService i18nService,
                                     NavBuilder navBuilder,
                                     TemplateRenderer renderer,
                                     PermissionValidationService permissionValidationService,
                                     ProjectService projectService,
                                     UserManager userManager, RepositorySettingsService repositorySettingsService,
                                     RepositoryPermissionService repositoryPermissionService,
                                     StackDriveLogService stackDriveLogService, GlobalConfigService globalConfigService,
                                     StackDriveProperties stackDriveProperties
    ) {
        this.repositoryService = repositoryService;
        this.i18nService = i18nService;
        this.navBuilder = navBuilder;
        this.renderer = renderer;
        this.permissionValidationService = permissionValidationService;
        this.projectService = projectService;
        this.userManager = userManager;
        this.repositorySettingsService = repositorySettingsService;
        this.repositoryPermissionService = repositoryPermissionService;
        this.stackDriveLogService = stackDriveLogService;
        this.globalConfigService = globalConfigService;
        this.buildVersion = stackDriveProperties.getBuildVersion();
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Matcher matcher = REPO_PATTERN.matcher(req.getPathInfo());
        if (matcher.matches()) {
            resp.setContentType("text/html;charset=utf-8");

            StackDriveRequest stackDriveRequest = parseRequest(req);
            Repository repository = stackDriveRequest.getRepository();
            StackDriveForm stackDriveForm = repositorySettingsService.getRepoSetting(repository);

            ImmutableMap<String, Object> immutableMap = ImmutableMap.<String, Object>of(
                    "repository", repository,
                    "stackDriveForm", stackDriveForm,
                    "stackDriveWebState", globalConfigService.getHealthCheckState(),
                    "stackDriveBitbucketVersion", buildVersion);
            renderer.render("velocity/repositorySettings.vm", immutableMap, resp.getWriter());
        } else {
            resp.sendRedirect(navBuilder.allRepos().buildRelative());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserProfile userProfile = userManager.getRemoteUser();

        StackDriveRequest stackDriveRequest = parseRequest(req);

        StackDriveForm stackDriveForm = stackDriveRequest.getForm();
        Repository repository = stackDriveRequest.getRepository();

        repositorySettingsService.saveRepoSetting(repository, stackDriveForm);
        if (stackDriveForm.getPluginEnabled()) {
            repositoryPermissionService.grantStackdriveUserPermission(repository);
            stackDriveLogService.sendLog(EventCode.SOLUTION_REPOSITORY_ENABLE_REQUEST, repository.getSlug(), userProfile.getUsername(), repository);
        } else {
            repositoryPermissionService.revokeStackdriveUserPermission(repository);
            stackDriveLogService.sendLog(EventCode.SOLUTION_REPOSITORY_DISABLE_REQUEST, repository.getSlug(), userProfile.getUsername(), repository);
        }

        resp.sendRedirect(req.getRequestURI());
    }

    private StackDriveRequest parseRequest(HttpServletRequest request) {
        StackDriveForm model = extractModel(request);
        String nextUrl = request.getParameter("next");

        Matcher matcher = REPO_PATTERN.matcher(request.getPathInfo());
        if (matcher.matches()) {
            Repository repository = getRepositoryIfAdminOrThrow(matcher.group(1), matcher.group(2));
            return new StackDriveRequest(false, null, repository, null, model, nextUrl);
        } else {
            throw new NoSuchEntityException(i18nService.createKeyedMessage("stackdrive.settings.pathnotfound",
                    request.getPathInfo()));
        }
    }

    private Repository getRepositoryIfAdminOrThrow(String projectKey, String repoSlug) {
        Repository repository = repositoryService.getBySlug(projectKey, repoSlug);
        if (repository == null) {
            permissionValidationService.validateAuthenticated();
            Project project = projectService.getByKey(projectKey);
            if (project == null) {
                throw new NoSuchProjectException(i18nService.createKeyedMessage("stackdrive.settings.nosuchproject", projectKey));
            } else {
                throw new NoSuchRepositoryException(i18nService.createKeyedMessage("stackdrive.settings.nosuchrepo", projectKey, repoSlug), project);
            }
        }
        permissionValidationService.validateForRepository(repository, Permission.REPO_ADMIN);

        return repository;
    }

    private StackDriveForm extractModel(HttpServletRequest request) {
        String nexusLogin = request.getParameter("nexusLogin");
        String nexusPassword = request.getParameter("nexusPassword");
        String pluginEnabled = request.getParameter("pluginEnabled");
        String projectScope = request.getParameter("projectScope");

        boolean pluginEnabledBoolean = pluginEnabled != null && (pluginEnabled.equals("on"));
        boolean projectScopeBoolean = projectScope != null && (projectScope.equals("on"));
        return new StackDriveForm(pluginEnabledBoolean, projectScopeBoolean, Strings.emptyToNull(nexusLogin), Strings.emptyToNull(nexusPassword));
    }
}