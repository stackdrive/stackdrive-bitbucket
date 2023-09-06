package org.stackdrive.bitbucket.repositoryui;

import com.atlassian.bitbucket.project.Project;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.user.ApplicationUser;
import org.stackdrive.bitbucket.dto.StackDriveForm;

public class StackDriveRequest {

    private final boolean admin;
    private final Project project;
    private final Repository repository;
    private final ApplicationUser user;
    private final StackDriveForm form;
    private final String nextUrl;

    public StackDriveRequest(boolean admin, Project project, Repository repository, ApplicationUser user, StackDriveForm form, String nextUrl) {
        this.admin = admin;
        this.project = project;
        this.repository = repository;
        this.user = user;
        this.form = form;
        this.nextUrl = nextUrl;
    }

    public Project getProject() {
        return project;
    }

    public Repository getRepository() {
        return repository;
    }

    public ApplicationUser getUser() {
        return user;
    }

    public StackDriveForm getForm() {
        return form;
    }

    public String getNextUrl() {
        return nextUrl;
    }

    public boolean isAdmin() {
        return admin;
    }

    public boolean isForAccessKeys() {
        return project != null || repository != null;
    }
}
