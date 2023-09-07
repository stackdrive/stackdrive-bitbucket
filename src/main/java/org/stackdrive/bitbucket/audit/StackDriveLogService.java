package org.stackdrive.bitbucket.audit;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import org.stackdrive.audit.dto.AuditDTO;
import org.stackdrive.audit.dto.Environment;
import org.stackdrive.audit.logger.StackDriveLogLogger;
import org.stackdrive.bitbucket.EventCode;
import org.stackdrive.bitbucket.globalconfig.GlobalConfigService;
import org.stackdrive.bitbucket.properties.StackDriveProperties;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class StackDriveLogService {

    @ComponentImport
    private final UserManager userManager;

    private final StackDriveLogLogger stackDriveLog;

    private final String buildVersion;

    @Inject
    public StackDriveLogService(GlobalConfigService globalConfigService,
                                UserManager userManager,
                                StackDriveProperties stackDriveProperties) {
        this.userManager = userManager;
        this.stackDriveLog = new StackDriveLogLogger(globalConfigService.getLog());
        this.buildVersion = stackDriveProperties.getBuildVersion();
    }

    public void sendLog(String code, String repo, Object extension) {
        UserProfile userProfile = userManager.getRemoteUser();
        sendLog(code, repo, userProfile.getUsername(), extension);
    }

    public void sendLog(String code, String repo, String user, Object extension) {
        AuditDTO auditDTO = new AuditDTO();
        auditDTO.setCode(code);
        auditDTO.setLogin(user);
        auditDTO.setProject(repo);
        auditDTO.setExtension(extension);

        auditDTO.setEnv(Environment.BITBUCKET);
        auditDTO.setVersion(buildVersion);

        stackDriveLog.asyncSend(auditDTO);

        sendAREventIfNeeded(code, repo, user, extension);
    }

    /**
     * Отправляет синтетически AREvent
     * <p>
     * Данный event необходим для подсчета Adoption Rate продукта,
     * данный тип event-ов используется только командой метрик
     */
    private void sendAREventIfNeeded(String code, String repo, String user, Object extension) {
        if (EventCode.AR_EVENTS.contains(code)) {
            sendLog(EventCode.AR_EVENT, repo, user, extension);
        }
    }
}