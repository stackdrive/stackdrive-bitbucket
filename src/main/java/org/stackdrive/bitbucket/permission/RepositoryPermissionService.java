package org.stackdrive.bitbucket.permission;

import com.atlassian.bitbucket.i18n.I18nService;
import com.atlassian.bitbucket.permission.Permission;
import com.atlassian.bitbucket.permission.PermissionAdminService;
import com.atlassian.bitbucket.permission.SetPermissionRequest;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.user.ApplicationUser;
import com.atlassian.bitbucket.user.UserService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.google.common.collect.ImmutableSet;
import org.stackdrive.bitbucket.globalconfig.GlobalConfigService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

@Named
public class RepositoryPermissionService {

    private static final Logger log = LoggerFactory.getLogger(RepositoryPermissionService.class);

    @ComponentImport
    private final PermissionAdminService permissionAdminService;
    @ComponentImport
    private final UserService userService;
    @ComponentImport
    private final I18nService i18nService;

    private final GlobalConfigService globalConfigService;

    @Inject
    public RepositoryPermissionService(PermissionAdminService permissionAdminService, PluginSettingsFactory pluginSettingsFactory, UserService userService, I18nService i18nService, GlobalConfigService globalConfigService) {
        this.permissionAdminService = permissionAdminService;
        this.globalConfigService = globalConfigService;
        this.userService = userService;
        this.i18nService = i18nService;
    }

    public void grantStackdriveUserPermission(Repository repository) {
        try {
            String username = globalConfigService.getStackdriveUser();
            grantPermissionForUsers(repository, ImmutableSet.of(username), "REPO_WRITE");
        } catch (Exception e) {
            log.error("error while setting stackdrive user permission " + globalConfigService.getStackdriveUser(), e);
        }
    }

    public void revokeStackdriveUserPermission(Repository repository) {
        try {
            String username = globalConfigService.getStackdriveUser();
            revokePermissionForUser(repository, username);
        } catch (Exception e) {
            log.error("error while setting stackdrive user permission", e);
        }
    }

    private void grantPermissionForUsers(Repository repository,
                                       Set<String> usernames,
                                       String permissionName) {
        Permission permission = validatePermission(permissionName, Repository.class);
        Set<ApplicationUser> users = validateUsers(usernames, false);

        permissionAdminService.setPermission(new SetPermissionRequest.Builder()
                .repositoryPermission(permission, repository)
                .users(users)
                .build());
    }

    private void revokePermissionForUser(Repository repository,
                                       String usernames) {
        ApplicationUser user = validateUser(usernames, false);

        permissionAdminService.revokeAllRepositoryPermissions(repository, user);
    }

    private Permission validatePermission(String permissionName, Class<?> resourceClass) {
        if (permissionName == null) {
            String message = i18nService.getMessage("stackdrive.permissionadmin.missingpermission");
            throw new BadRequestException(message);
        }
        Permission permission;
        try {
            permission = Permission.valueOf(permissionName.toUpperCase().replace('-', '_'));

        } catch (IllegalArgumentException e) {
            String message = i18nService.getMessage("stackdrive.permissionadmin.invalidpermission", permissionName);
            throw new BadRequestException(message);
        }

        if (resourceClass != null && !permission.isResource(resourceClass)) {
            String message = i18nService.getMessage("stackdrive.permission.admin.notapplicablewithresource", permission.name(), resourceClass);
            throw new BadRequestException(message);
        } else if (resourceClass == null && !permission.isGlobal()) {
            String message = i18nService.getMessage("stackdrive.permission.admin.notapplicablewithoutresource", permission.name());
            throw new BadRequestException(message);
        }

        if (!permission.isGrantable()) {
            String message = i18nService.getMessage("stackdrive.permissionadmin.ungrantablepermission", permission.name());
            throw new BadRequestException(message);
        }

        return permission;
    }

    private Set<ApplicationUser> validateUsers(Collection<String> usernames, boolean returnDeleted) {
        if (usernames == null || usernames.isEmpty()) {
            String message = i18nService.getMessage("stackdrive.permission.no.users");
            throw new BadRequestException(message);
        }


        Set<ApplicationUser> users = new HashSet<>(usernames.size());
        List<String> missing = new ArrayList<>();
        for (String username : usernames) {
            try {
                users.add(validateUser(username, returnDeleted));
            } catch (NotFoundException e) {
                missing.add(username);
            }
        }

        if (!missing.isEmpty()) {
            String message = i18nService.getMessage("stackdrive.permission.nosuchusers", StringUtils.join(missing, ", "));
            throw new NotFoundException(message);
        }

        return ImmutableSet.copyOf(users);
    }

    private ApplicationUser validateUser(String username, boolean returnDeleted) {
        if (StringUtils.isEmpty(username)) {
            String message = i18nService.getMessage("stackdrive.permission.admin.invaliduser");
            throw new BadRequestException(message);
        }
        ApplicationUser user = userService.getUserByName(username, returnDeleted);
        if (user == null) {
            String message = i18nService.getMessage("stackdrive.permission.nosuchuser", username);
            throw new NotFoundException(message);
        }
        return user;
    }
}