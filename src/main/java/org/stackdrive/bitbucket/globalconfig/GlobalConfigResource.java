package org.stackdrive.bitbucket.globalconfig;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

import static org.stackdrive.bitbucket.Global.*;

@Path("/global")
public class GlobalConfigResource {

    private static final Logger log = LoggerFactory.getLogger(GlobalConfigResource.class);

    @ComponentImport
    private final UserManager userManager;

    @ComponentImport
    private final PluginSettingsFactory pluginSettingsFactory;

    @ComponentImport
    private final TransactionTemplate transactionTemplate;

    private final GlobalConfigService globalConfigService;

    @Inject
    public GlobalConfigResource(UserManager userManager, PluginSettingsFactory pluginSettingsFactory,
                                TransactionTemplate transactionTemplate, GlobalConfigService globalConfigService) {
        this.userManager = userManager;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.transactionTemplate = transactionTemplate;
        this.globalConfigService = globalConfigService;
    }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static final class Config {
        @XmlElement
        private String front;
        @XmlElement
        private String back;
        @XmlElement
        private String stackdriveUser;
        @XmlElement
        private String log;

        public String getWeb() {
            return front;
        }

        public void setWeb(String front) {
            this.front = front;
        }

        public String getBeta() {
            return back;
        }

        public void setBeta(String back) {
            this.back = back;
        }

        public String getStackdriveUser() {
            return stackdriveUser;
        }

        public void setStackdriveUser(String stackdriveUser) {
            this.stackdriveUser = stackdriveUser;
        }

        public String getLogger() {
            return log;
        }

        public void setLogger(String log) {
            this.log = log;
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@Context HttpServletRequest request) {
        UserKey userKey = userManager.getRemoteUserKey(request);
        if (userKey == null || !userManager.isSystemAdmin(userKey)) {
            return Response.status(Status.UNAUTHORIZED).build();
        }

        return Response.ok(transactionTemplate.execute(() -> {
            Config config = new Config();
            config.setBeta(globalConfigService.getBack());
            config.setWeb(globalConfigService.getSolution());
            config.setStackdriveUser(globalConfigService.getStackdriveUser());
            config.setLogger(globalConfigService.getLog());
            return config;
        })).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response put(final Config config, @Context HttpServletRequest request) {
        UserKey userKey = userManager.getRemoteUserKey(request);
        if (userKey == null || !userManager.isSystemAdmin(userKey)) {
            return Response.status(Status.UNAUTHORIZED).build();
        }

        transactionTemplate.execute(() -> {
            PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
            pluginSettings.put(BACK, config.getBeta());
            pluginSettings.put(FRONT, config.getWeb());
            pluginSettings.put(STACKDRIVE_USER, config.getStackdriveUser());
            pluginSettings.put(LOG, config.getLogger());
            return null;
        });
        return Response.noContent().build();
    }
}