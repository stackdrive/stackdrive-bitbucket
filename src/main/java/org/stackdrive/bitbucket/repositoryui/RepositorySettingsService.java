package org.stackdrive.bitbucket.repositoryui;

import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import org.stackdrive.bitbucket.dto.StackDriveForm;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Objects;

import static org.stackdrive.bitbucket.Global.REPO_NAMESPACE;
import static org.stackdrive.bitbucket.Global.STACKDRIVE_USER_FLAG;

@Named
public class RepositorySettingsService {

    @ComponentImport
    private final PluginSettingsFactory pluginSettingsFactory;

    @ComponentImport
    private final TransactionTemplate transactionTemplate;

    @Inject
    public RepositorySettingsService(PluginSettingsFactory pluginSettingsFactory, TransactionTemplate transactionTemplate) {
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.transactionTemplate = transactionTemplate;
    }

    public StackDriveForm getRepoSetting(Repository repository) {
        return transactionTemplate.execute(() -> {
            PluginSettings repoSettings = pluginSettingsFactory.createSettingsForKey(REPO_NAMESPACE + repository.getHierarchyId());

            boolean pluginEnabled = Boolean.parseBoolean(String.valueOf(repoSettings.get(StackDriveForm.KEY_PLUGIN_ENABLED)));
            boolean projectScope = Boolean.parseBoolean(String.valueOf(repoSettings.get(StackDriveForm.KEY_PROJECT_SCOPE)));

            String nexusLogin = repoSettings.get(StackDriveForm.KEY_NEXUS_LOGIN) != null ? String.valueOf(repoSettings.get(StackDriveForm.KEY_NEXUS_LOGIN)) : null;
            String nexusPassword = repoSettings.get(StackDriveForm.KEY_NEXUS_PASSWORD) != null ? String.valueOf(repoSettings.get(StackDriveForm.KEY_NEXUS_PASSWORD)) : null;
            return new StackDriveForm(pluginEnabled, projectScope, nexusLogin, nexusPassword);
        });
    }

    public void saveRepoSetting(Repository repository, StackDriveForm stackDriveForm) {
        transactionTemplate.execute(() -> {
            PluginSettings repoSettings = pluginSettingsFactory.createSettingsForKey(REPO_NAMESPACE + repository.getHierarchyId());

            repoSettings.put(StackDriveForm.KEY_PLUGIN_ENABLED, String.valueOf(stackDriveForm.getPluginEnabled()));
            repoSettings.put(StackDriveForm.KEY_PROJECT_SCOPE, String.valueOf(stackDriveForm.getProjectScope()));

            if (Objects.nonNull(stackDriveForm.getNexusLogin())) {
                repoSettings.put(StackDriveForm.KEY_NEXUS_LOGIN, stackDriveForm.getNexusLogin());
            } else if (Objects.nonNull(repoSettings.get(StackDriveForm.KEY_NEXUS_LOGIN))) {
                repoSettings.remove(StackDriveForm.KEY_NEXUS_LOGIN);
            }
            if (Objects.nonNull(stackDriveForm.getNexusPassword())) {
                repoSettings.put(StackDriveForm.KEY_NEXUS_PASSWORD, stackDriveForm.getNexusPassword());
            } else if (Objects.nonNull(repoSettings.get(StackDriveForm.KEY_NEXUS_PASSWORD))) {
                repoSettings.remove(StackDriveForm.KEY_NEXUS_PASSWORD);
            }
            return stackDriveForm;
        });
    }

    public void setStackdriveUserFlag(Repository repository, Boolean stackdriveUserFlag) {
        transactionTemplate.execute(() -> {
            PluginSettings repoSettings = pluginSettingsFactory.createSettingsForKey(REPO_NAMESPACE + repository.getHierarchyId());
            repoSettings.put(STACKDRIVE_USER_FLAG, String.valueOf(stackdriveUserFlag));
            return repoSettings;
        });
    }

    public boolean getStackdriveUserFlag(Repository repository) {
        return transactionTemplate.execute(() -> {
            PluginSettings repoSettings = pluginSettingsFactory.createSettingsForKey(REPO_NAMESPACE + repository.getHierarchyId());
            boolean stackdriveUserFlag = Boolean.parseBoolean(String.valueOf(repoSettings.get(STACKDRIVE_USER_FLAG)));
            return stackdriveUserFlag;
        });
    }
}
