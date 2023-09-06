package org.stackdrive.bitbucket.globalconfig;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import org.stackdrive.bitbucket.properties.StackDriveProperties;

import javax.inject.Inject;
import javax.inject.Named;

import static org.stackdrive.bitbucket.Global.*;

@Named
public class GlobalConfigService {

    @ComponentImport
    private final PluginSettingsFactory pluginSettingsFactory;

    @ComponentImport
    private final TransactionTemplate transactionTemplate;

    private final StackDriveProperties stackDriveProperties;

    @Inject
    public GlobalConfigService(PluginSettingsFactory pluginSettingsFactory, TransactionTemplate transactionTemplate, StackDriveProperties stackDriveProperties) {
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.transactionTemplate = transactionTemplate;
        this.stackDriveProperties = stackDriveProperties;
    }

    public String getBack() {
        String back = getGlobalConfigValue(BACK);
        return back != null ? back : stackDriveProperties.getBackWebHost() + "/web";
    }

    public String getStackdriveUser() {
        String stackdriveUser = getGlobalConfigValue(STACKDRIVE_USER);
        return stackdriveUser != null ? stackdriveUser : stackDriveProperties.getStackdriveUser();
    }

    public String getSolution() {
        String front = getGlobalConfigValue(FRONT);
        return front != null ? front : stackDriveProperties.getFrontWebHost() + "/solution";
    }

    public String getHealth() {
        String front = getGlobalConfigValue(FRONT);
        return front != null ? front : stackDriveProperties.getFrontWebHost() + "/web/actuator/health";
    }

    public String getLog() {
        String log = getGlobalConfigValue(LOG);
        return log != null ? log : stackDriveProperties.getLogHost() + "/audit";
    }

    public String getHealthCheckState() {
        String health = getGlobalConfigValue(HEALTH_CHECK_STATE);
        return health != null ? health : "ACTIVE";
    }

    public void setHealthCheckState(String state) {
        transactionTemplate.execute(() -> {
            PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
            pluginSettings.put(HEALTH_CHECK_STATE, state);
            return null;
        });
    }

    private String getGlobalConfigValue(String key) {
        PluginSettings settings = pluginSettingsFactory.createGlobalSettings();
        return (String) settings.get(key);
    }
}