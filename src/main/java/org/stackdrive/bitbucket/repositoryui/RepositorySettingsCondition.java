package org.stackdrive.bitbucket.repositoryui;

import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import org.stackdrive.bitbucket.permission.StackDriveWebPermissionService;

import javax.inject.Inject;
import java.util.Map;

public class RepositorySettingsCondition implements Condition {

    private final StackDriveWebPermissionService stackDriveWebPermissionService;

    @Inject
    public RepositorySettingsCondition(StackDriveWebPermissionService stackDriveWebPermissionService) {
        this.stackDriveWebPermissionService = stackDriveWebPermissionService;
    }

    @Override
    public void init(Map<String, String> map) throws PluginParseException {
        // Не требует предварительной инициализации
    }

    @Override
    public boolean shouldDisplay(Map<String, Object> context) {
        Repository repository = (Repository) context.get("repository");
        if (repository == null) {
            return false;
        } else {
            return stackDriveWebPermissionService.hasPermission(repository);
        }
    }
}