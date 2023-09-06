package org.stackdrive.bitbucket.properties;

import com.atlassian.plugin.util.ClassLoaderUtils;

import javax.inject.Named;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Named
public class StackDriveProperties {

    private static final String STACKDRIVE_WEB_BACK_HOST = "stackdrive.web.back.host";

    private static final String STACKDRIVE_WEB_FRONT_HOST = "stackdrive.web.front.host";

    private static final String STACKDRIVE_LOG_HOST = "stackdrive.loghost";

    private static final String STACKDRIVE_USER = "stackdrive.bitbucket.user";

    private static final String STACKDRIVE_BUILD_VERSION = "stackdrive.build.version";

    private final Properties properties = new Properties();

    private final InputStream inputStream = ClassLoaderUtils.getResourceAsStream("stack-drive.properties", this.getClass());

    public String getBackWebHost() {
        return getProperty(STACKDRIVE_WEB_BACK_HOST);
    }

    public String getFrontWebHost() {
        return getProperty(STACKDRIVE_WEB_FRONT_HOST);
    }

    public String getLogHost() {
        return getProperty(STACKDRIVE_LOG_HOST);
    }

    public String getStackdriveUser() {
        return getProperty(STACKDRIVE_USER);
    }

    public String getBuildVersion() {
        return getProperty(STACKDRIVE_BUILD_VERSION);
    }

    private String getProperty(String key) {
        try {
            if (inputStream != null) {
                properties.load(inputStream);
                return properties.getProperty(key);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return key;
    }
}
