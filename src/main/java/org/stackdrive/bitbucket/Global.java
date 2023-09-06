package org.stackdrive.bitbucket;

public interface Global {
    String STACKDRIVE_NAMESPACE = "stackdrive.";
    String INTEGRATION = "global.";

    String BACK = STACKDRIVE_NAMESPACE + INTEGRATION + "back";
    String FRONT = STACKDRIVE_NAMESPACE + INTEGRATION + "front";
    String STACKDRIVE_USER = STACKDRIVE_NAMESPACE + INTEGRATION + "bitbucket";
    String LOG = STACKDRIVE_NAMESPACE + INTEGRATION + "log";

    String REPO = "repo.";
    String REPO_NAMESPACE = STACKDRIVE_NAMESPACE + REPO;

    String PROJECT = "project.";
    String PROJECT_NAMESPACE = STACKDRIVE_NAMESPACE + PROJECT;

    String STACKDRIVE_USER_FLAG = "stackdriveUser";

    String HEALTH_CHECK = "health_check.";
    String HEALTH_CHECK_URL = STACKDRIVE_NAMESPACE + INTEGRATION + HEALTH_CHECK + "url";
    String HEALTH_CHECK_STATE = STACKDRIVE_NAMESPACE + INTEGRATION + HEALTH_CHECK + "state";
}
