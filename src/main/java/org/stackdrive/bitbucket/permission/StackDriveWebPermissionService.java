package org.stackdrive.bitbucket.permission;

import com.atlassian.bitbucket.project.Project;
import com.atlassian.bitbucket.repository.Repository;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class StackDriveWebPermissionService {

    @Inject
    public StackDriveWebPermissionService() {
    }

    public boolean hasPermission(Repository repository) {
        return true;
    }

    public boolean hasPermission(Project project) {
        return true;
    }
}