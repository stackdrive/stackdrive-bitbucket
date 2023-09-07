package org.stackdrive.bitbucket.repositoryui;

import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class RepositoryConfigResourceTest {

    @Mock
    private UserManager mockUserManager;
    @Mock
    private PluginSettingsFactory mockPluginSettingsFactory;
    @Mock
    private TransactionTemplate mockTransactionTemplate;

    private RepositoryConfigResource repositoryConfigResourceUnderTest;

    @Before
    public void setUp() {
        initMocks(this);
        repositoryConfigResourceUnderTest = new RepositoryConfigResource(mockUserManager, mockPluginSettingsFactory, mockTransactionTemplate);
    }

    @Test
    public void testGet() {
        // Setup
        final HttpServletRequest request = null;
        when(mockUserManager.getRemoteUsername(any(HttpServletRequest.class))).thenReturn("result");
        when(mockUserManager.isSystemAdmin("s")).thenReturn(false);

        // Configure TransactionTemplate.execute(...).
        final RepositoryConfigResource.Config config = new RepositoryConfigResource.Config();
        when(mockTransactionTemplate.execute(any(TransactionCallback.class))).thenReturn(config);

        when(mockPluginSettingsFactory.createGlobalSettings()).thenReturn(null);

        // Run the test
        final Response result = repositoryConfigResourceUnderTest.get(request);

        // Verify the results
        assertNotNull(result);
    }

    @Test
    public void testPut() {
        // Setup
        final RepositoryConfigResource.Config config = new RepositoryConfigResource.Config();

        final HttpServletRequest request = null;
        when(mockUserManager.getRemoteUsername(any(HttpServletRequest.class))).thenReturn("result");
        when(mockUserManager.isSystemAdmin("s")).thenReturn(false);
        when(mockTransactionTemplate.execute(any(TransactionCallback.class))).thenReturn("result");
        when(mockPluginSettingsFactory.createGlobalSettings()).thenReturn(null);


        // Run the test
        final Response result = repositoryConfigResourceUnderTest.put(config, request);

        // Verify the results
        assertNotNull(result);
    }
}
