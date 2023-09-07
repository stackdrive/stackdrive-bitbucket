package org.stackdrive.bitbucket.source;

import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.scm.ScmUrlRequest;
import com.atlassian.bitbucket.scm.http.RepositoryUrlFragment;
import com.atlassian.bitbucket.ssh.SshConfigurationService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import java.net.URI;
import java.net.URISyntaxException;

@Named
public class SourceCodeManagementService {

    private static final Logger log = LoggerFactory.getLogger(SourceCodeManagementService.class);

    @ComponentImport
    private final SshConfigurationService sshConfigurationService;

    @Inject
    public SourceCodeManagementService(SshConfigurationService sshConfigurationService) {
        this.sshConfigurationService = sshConfigurationService;
    }

    public String getScmUrl(Repository repository) {
        try {
            ScmUrlRequest scmUrlRequest = getRequest(repository);
            return format(scmUrlRequest).toASCIIString();
        } catch (URISyntaxException e) {
            log.error("git url creation error", e);
        }
        return "";
    }

    @Nonnull
    private ScmUrlRequest getRequest(Repository repository) throws URISyntaxException {
        return new ScmUrlRequest.Builder(repository, new URI(sshConfigurationService.getBaseUrl())).build();
    }

    @Nonnull
    private URI format(@Nonnull ScmUrlRequest request) throws URISyntaxException {
        Repository repository = request.getRepository();
        URI uri = request.getBaseUrl();
        String path = RepositoryUrlFragment.fromRepository(repository).toPath(uri.getPath());

        return new URI(uri.getScheme(), repository.getScmId(), uri.getHost(), uri.getPort(),
                path, uri.getQuery(), uri.getFragment());
    }
}
