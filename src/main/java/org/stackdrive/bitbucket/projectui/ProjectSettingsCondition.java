package org.stackdrive.bitbucket.projectui;

import com.atlassian.bitbucket.project.Project;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import org.stackdrive.bitbucket.permission.StackDriveWebPermissionService;

import javax.inject.Inject;
import java.util.Map;

public class ProjectSettingsCondition implements Condition {

    private final StackDriveWebPermissionService stackDriveWebPermissionService;

    @Inject
    public ProjectSettingsCondition(StackDriveWebPermissionService stackDriveWebPermissionService) {
        this.stackDriveWebPermissionService = stackDriveWebPermissionService;
    }

    @Override
    public void init(Map<String, String> map) throws PluginParseException {
        // Не требует предварительной инициализации
    }

    @Override
    public boolean shouldDisplay(Map<String, Object> context) {
        Project project = (Project) context.get("project");
        if (project == null) {
            return false;
        } else {
            return stackDriveWebPermissionService.hasPermission(project);
        }
    }
}