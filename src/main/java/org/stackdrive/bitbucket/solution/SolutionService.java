package org.stackdrive.bitbucket.solution;

import com.atlassian.bitbucket.project.Project;
import com.atlassian.bitbucket.repository.Repository;
import org.stackdrive.bitbucket.repositoryui.RepositorySettingsService;
import org.stackdrive.bitbucket.permission.StackDriveWebPermissionService;
import org.stackdrive.bitbucket.projectui.ProjectSettingsService;
import org.stackdrive.bitbucket.dto.StackDriveForm;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class SolutionService {

    private final RepositorySettingsService repositorySettingsService;

    private final StackDriveWebPermissionService stackDriveWebPermissionService;

    private final ProjectSettingsService projectSettingsService;

    @Inject
    public SolutionService(RepositorySettingsService repositorySettingsService,
                           StackDriveWebPermissionService stackDriveWebPermissionService, ProjectSettingsService projectSettingsService) {
        this.repositorySettingsService = repositorySettingsService;
        this.stackDriveWebPermissionService = stackDriveWebPermissionService;
        this.projectSettingsService = projectSettingsService;
    }

    public boolean hasSolutionPermission(Repository repository) {
        StackDriveForm repoSetting = repositorySettingsService.getRepoSetting(repository);
        return repoSetting.getPluginEnabled()
                && repositorySettingsService.getStackdriveUserFlag(repository)
                && stackDriveWebPermissionService.hasPermission(repository);
    }

    public boolean hasSolutionPermission(Project project) {
        StackDriveForm projectSetting = projectSettingsService.getProjectSettings(project);
        return projectSetting.getPluginEnabled()
                && projectSettingsService.getStackdriveUserFlag(project)
                && stackDriveWebPermissionService.hasPermission(project);
    }
}
