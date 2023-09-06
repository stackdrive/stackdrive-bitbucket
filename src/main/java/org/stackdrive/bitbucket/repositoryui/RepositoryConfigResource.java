package org.stackdrive.bitbucket.repositoryui;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Path("/repository")
public class RepositoryConfigResource {
    @ComponentImport
    private final UserManager userManager;
    @ComponentImport
    private final PluginSettingsFactory pluginSettingsFactory;
    @ComponentImport
    private final TransactionTemplate transactionTemplate;

    @Inject
    public RepositoryConfigResource(UserManager userManager, PluginSettingsFactory pluginSettingsFactory,
                                    TransactionTemplate transactionTemplate) {
        this.userManager = userManager;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.transactionTemplate = transactionTemplate;
    }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static final class Config {
        @XmlElement
        private String defaultBranchId;
        @XmlElement
        private boolean pluginEnabled;

        public String getDefaultBranchId() {
            return defaultBranchId;
        }

        public void setDefaultBranchId(String defaultBranchId) {
            this.defaultBranchId = defaultBranchId;
        }

        public boolean isPluginEnabled() {
            return pluginEnabled;
        }

        public void setPluginEnabled(boolean pluginEnabled) {
            this.pluginEnabled = pluginEnabled;
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@Context HttpServletRequest request) {
        String username = userManager.getRemoteUsername(request);
        if (username == null || !userManager.isSystemAdmin(username)) {
            return Response.status(Status.UNAUTHORIZED).build();
        }

        return Response.ok(transactionTemplate.execute(() -> {
            PluginSettings settings = pluginSettingsFactory.createGlobalSettings();
            Config config = new Config();
            config.setDefaultBranchId((String) settings.get(Config.class.getName() + ".defaultBranchId"));

            String enabled = (String) settings.get(Config.class.getName() + ".enabled");
            if (enabled != null) {
                config.setPluginEnabled(Boolean.parseBoolean(enabled));
            }
            return config;
        })).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response put(final Config config, @Context HttpServletRequest request) {
        String username = userManager.getRemoteUsername(request);
        if (username == null || !userManager.isSystemAdmin(username)) {
            return Response.status(Status.UNAUTHORIZED).build();
        }

        transactionTemplate.execute(() -> {
            PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
            pluginSettings.put(Config.class.getName() + ".defaultBranchId", config.getDefaultBranchId());
            pluginSettings.put(Config.class.getName() + ".enabled", Boolean.toString(config.isPluginEnabled()));
            return null;
        });
        return Response.noContent().build();
    }
}