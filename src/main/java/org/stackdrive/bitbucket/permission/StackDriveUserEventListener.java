package org.stackdrive.bitbucket.permission;

import com.atlassian.bitbucket.event.permission.ProjectPermissionGrantedEvent;
import com.atlassian.bitbucket.event.permission.ProjectPermissionRevokedEvent;
import com.atlassian.bitbucket.event.permission.RepositoryPermissionGrantedEvent;
import com.atlassian.bitbucket.event.permission.RepositoryPermissionRevokedEvent;
import com.atlassian.event.api.EventListener;
import org.stackdrive.bitbucket.globalconfig.GlobalConfigService;
import org.stackdrive.bitbucket.repositoryui.RepositorySettingsService;
import org.stackdrive.bitbucket.projectui.ProjectSettingsService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Objects;

@Named
public class StackDriveUserEventListener {

    private final GlobalConfigService globalConfigService;

    private final RepositorySettingsService repositorySettingsService;

    private final ProjectSettingsService projectSettingsService;

    @Inject
    public StackDriveUserEventListener(GlobalConfigService globalConfigService,
                                       RepositorySettingsService repositorySettingsService, ProjectSettingsService projectSettingsService) {
        this.globalConfigService = globalConfigService;
        this.repositorySettingsService = repositorySettingsService;
        this.projectSettingsService = projectSettingsService;
    }

    @EventListener
    public void onRepositoryPermissionGrantedEvent(RepositoryPermissionGrantedEvent event) throws Exception {
        String stackdriveUser = globalConfigService.getStackdriveUser();
        if (Objects.nonNull(stackdriveUser) && Objects.nonNull(event.getAffectedUser()) && stackdriveUser.equalsIgnoreCase(event.getAffectedUser().getSlug())) {
            repositorySettingsService.setStackdriveUserFlag(event.getRepository(), Boolean.TRUE);
        }
    }

    @EventListener
    public void onRepositoryPermissionRevokedEvent(RepositoryPermissionRevokedEvent event) throws Exception {
        String stackdriveUser = globalConfigService.getStackdriveUser();
        if (Objects.nonNull(stackdriveUser) && Objects.nonNull(event.getAffectedUser()) && stackdriveUser.equalsIgnoreCase(event.getAffectedUser().getSlug())) {
            repositorySettingsService.setStackdriveUserFlag(event.getRepository(), Boolean.FALSE);
        }
    }

    @EventListener
    public void onProjectPermissionGrantedEvent(ProjectPermissionGrantedEvent event) throws Exception {
        String stackdriveUser = globalConfigService.getStackdriveUser();
        if (Objects.nonNull(stackdriveUser) && Objects.nonNull(event.getAffectedUser()) && stackdriveUser.equalsIgnoreCase(event.getAffectedUser().getSlug())) {
            projectSettingsService.setStackdriveUserFlag(event.getProject(), Boolean.TRUE);
        }
    }

    @EventListener
    public void onProjectPermissionRevokedEvent(ProjectPermissionRevokedEvent event) throws Exception {
        String stackdriveUser = globalConfigService.getStackdriveUser();
        if (Objects.nonNull(stackdriveUser) && Objects.nonNull(event.getAffectedUser()) && stackdriveUser.equalsIgnoreCase(event.getAffectedUser().getSlug())) {
            projectSettingsService.setStackdriveUserFlag(event.getProject(), Boolean.FALSE);
        }
    }
}