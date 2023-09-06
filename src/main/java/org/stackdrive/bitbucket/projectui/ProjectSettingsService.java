package org.stackdrive.bitbucket.projectui;

import com.atlassian.bitbucket.project.Project;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import org.stackdrive.bitbucket.dto.StackDriveForm;

import javax.inject.Inject;
import javax.inject.Named;

import static org.stackdrive.bitbucket.Global.*;

@Named
public class ProjectSettingsService {

    @ComponentImport
    private final PluginSettingsFactory pluginSettingsFactory;

    @ComponentImport
    private final TransactionTemplate transactionTemplate;

    @Inject
    public ProjectSettingsService(PluginSettingsFactory pluginSettingsFactory, TransactionTemplate transactionTemplate) {
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.transactionTemplate = transactionTemplate;
    }

    public StackDriveForm getProjectSettings(Project project) {
        return transactionTemplate.execute(() -> {
            PluginSettings projectSettings = pluginSettingsFactory.createSettingsForKey(PROJECT_NAMESPACE + project.getKey());
            return new StackDriveForm(Boolean.parseBoolean(String.valueOf(projectSettings.get(StackDriveForm.KEY_PLUGIN_ENABLED))));
        });
    }

    public void saveProjectSettings(Project project, StackDriveForm stackDriveForm) {
        transactionTemplate.execute(() -> {
            PluginSettings projectSettings = pluginSettingsFactory.createSettingsForKey(PROJECT_NAMESPACE + project.getKey());

            projectSettings.put(StackDriveForm.KEY_PLUGIN_ENABLED, String.valueOf(stackDriveForm.getPluginEnabled()));
            return stackDriveForm;
        });
    }

    public void setStackdriveUserFlag(Project project, Boolean stackdriveUserFlag) {
        transactionTemplate.execute(() -> {
            PluginSettings repoSettings = pluginSettingsFactory.createSettingsForKey(PROJECT_NAMESPACE + project.getKey());
            repoSettings.put(STACKDRIVE_USER_FLAG, String.valueOf(stackdriveUserFlag));
            return repoSettings;
        });
    }

    public boolean getStackdriveUserFlag(Project project) {
        return transactionTemplate.execute(() -> {
            PluginSettings repoSettings = pluginSettingsFactory.createSettingsForKey(PROJECT_NAMESPACE + project.getKey());
            return Boolean.parseBoolean(String.valueOf(repoSettings.get(STACKDRIVE_USER_FLAG)));
        });
    }
}
