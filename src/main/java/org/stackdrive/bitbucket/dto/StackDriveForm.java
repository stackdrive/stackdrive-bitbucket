package org.stackdrive.bitbucket.dto;

import java.util.LinkedHashMap;

public class StackDriveForm extends LinkedHashMap<String, Object> {

    public static final String KEY_PLUGIN_ENABLED = "pluginEnabled";
    public static final String KEY_PROJECT_SCOPE = "projectScope";
    public static final String KEY_NEXUS_LOGIN = "nexusLogin";
    public static final String KEY_NEXUS_PASSWORD = "nexusPassword";

    public StackDriveForm(boolean pluginEnabled) {
        put(KEY_PLUGIN_ENABLED, pluginEnabled);
    }

    public StackDriveForm(boolean pluginEnabled, String nexusLogin, String nexusPassword) {
        if (nexusLogin != null) {
            put(KEY_NEXUS_LOGIN, nexusLogin);
        }
        if (nexusPassword != null) {
            put(KEY_NEXUS_PASSWORD, nexusPassword);
        }

        put(KEY_PLUGIN_ENABLED, pluginEnabled);
    }

    public StackDriveForm(boolean pluginEnabled, boolean projectScope, String nexusLogin, String nexusPassword) {
        if (nexusLogin != null) {
            put(KEY_NEXUS_LOGIN, nexusLogin);
        }
        if (nexusPassword != null) {
            put(KEY_NEXUS_PASSWORD, nexusPassword);
        }

        put(KEY_PLUGIN_ENABLED, pluginEnabled);
        put(KEY_PROJECT_SCOPE, projectScope);
    }

    public String getNexusLogin() {
        return (String) get(KEY_NEXUS_LOGIN);
    }

    public String getNexusPassword() {
        return (String) get(KEY_NEXUS_PASSWORD);
    }

    public boolean getPluginEnabled() {
        return (Boolean) get(KEY_PLUGIN_ENABLED);
    }

    public boolean getProjectScope() {
        return (Boolean) get(KEY_PROJECT_SCOPE);
    }
}
