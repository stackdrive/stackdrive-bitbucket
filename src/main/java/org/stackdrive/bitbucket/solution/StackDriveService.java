package org.stackdrive.bitbucket.solution;

import com.atlassian.bitbucket.content.AbstractContentTreeCallback;
import com.atlassian.bitbucket.content.ContentService;
import com.atlassian.bitbucket.content.ContentTreeNode;
import com.atlassian.bitbucket.content.ContentTreeSummary;
import com.atlassian.bitbucket.repository.RefService;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.util.PageRequest;
import com.atlassian.bitbucket.util.PageRequestImpl;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class StackDriveService {

    private static final Logger log = LoggerFactory.getLogger(StackDriveService.class);

    @ComponentImport
    private final ContentService contentService;

    @ComponentImport
    private final RefService refService;

    @Inject
    public StackDriveService(ContentService contentService, RefService refService) {
        this.contentService = contentService;
        this.refService = refService;
    }

    public String searchForDotStackdriveDirectory(Repository repository) {
        final ContentTreeSummary[] summaryRef = new ContentTreeSummary[1];
        final PageRequest pageRequest = new PageRequestImpl(0, 500);
        final String[] result = {""};

        String at = refService.getDefaultBranch(repository).getId();
        contentService.streamDirectory(repository, at, null, false, new AbstractContentTreeCallback() {
            @Override
            public void onEnd(@Nonnull ContentTreeSummary summary) {
                summaryRef[0] = summary;
            }

            @Override
            public boolean onTreeNode(@Nonnull ContentTreeNode node) {
                if (node.getType() == ContentTreeNode.Type.DIRECTORY) {
                    log.warn("onTreeNode 1 {} {} {}", node.getPath(), node.getContentId(), node.getType());
                    if (node.getPath().getName().equals(".stackdrive")) {
                        log.warn("onTreeNode isStackDrive {} {} {}", node.getPath(), node.getPath().getName().equals(".stackdrive"), node.getType());
                        result[0] = node.getPath().getName();
                    }
                } else if (node.getType() == ContentTreeNode.Type.SUBMODULE) {
                    log.warn("onTreeNode 2 {} {} {}", node.getPath(), node.getContentId(), node.getType());
                } else {
                    log.warn("onTreeNode 3 {} {} {}", node.getPath(), node.getContentId(), node.getType());
                }
                return true;
            }
        }, pageRequest);

        return result[0];
    }

    public boolean hasDotStackdriveDirectory(Repository repository) {
        return StringUtils.isNotBlank(searchForDotStackdriveDirectory(repository));
    }

    public String searchForStackdrivePropertiesFile(Repository repository) {
        final String[] result = {""};
        if (hasDotStackdriveDirectory(repository)) {
            final ContentTreeSummary[] summaryRef = new ContentTreeSummary[1];
            final PageRequest pageRequest = new PageRequestImpl(0, 500);
            final String at = refService.getDefaultBranch(repository).getId();

            contentService.streamDirectory(repository, at, ".stackdrive", false, new AbstractContentTreeCallback() {
                @Override
                public void onEnd(@Nonnull ContentTreeSummary summary) {
                    summaryRef[0] = summary;
                }

                @Override
                public boolean onTreeNode(@Nonnull ContentTreeNode node) {
                    if (node.getType() == ContentTreeNode.Type.DIRECTORY) {
                        log.warn("searchForStackdrivePropertiesFile 1 {} {} {}", node.getPath(), node.getContentId(), node.getType());
                    } else if (node.getType() == ContentTreeNode.Type.SUBMODULE) {
                        log.warn("searchForStackdrivePropertiesFile 2 {} {} {}", node.getPath(), node.getContentId(), node.getType());
                    } else {
                        log.warn("searchForStackdrivePropertiesFile 3 {} {} {}", node.getPath(), node.getContentId(), node.getType());
                        if (node.getPath().getName().equals("stackdrive.properties")) {
                            log.warn("searchForStackdrivePropertiesFile isStackDrive {} {} {}", node.getPath(), node.getPath().getName().equals(".stackdrive"), node.getType());
                            result[0] = node.getPath().getName();
                        }
                    }
                    return true;
                }
            }, pageRequest);


            return result[0];
        } else {
            return result[0];
        }
    }

    public boolean hasStackdrivePropertiesFile(Repository repository) {
        return StringUtils.isNotBlank(searchForStackdrivePropertiesFile(repository));
    }
}
