package org.stackdrive.bitbucket.solution;

import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.repository.RepositoryService;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.templaterenderer.RenderingException;
import com.atlassian.templaterenderer.TemplateRenderer;
import org.stackdrive.bitbucket.globalconfig.GlobalConfigService;
import org.stackdrive.bitbucket.permission.StackDriveWebPermissionService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.stackdrive.bitbucket.audit.StackDriveLogService;
import org.stackdrive.bitbucket.source.SourceCodeManagementService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.MockitoAnnotations.initMocks;

public class SolutionServletTest {

    @Mock
    private RepositoryService mockRepositoryService;
    @Mock
    private TemplateRenderer mockRenderer;
    @Mock
    private SourceCodeManagementService mockSourceCodeManagementService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private Repository repository;
    @Mock
    private GlobalConfigService globalConfigService;
    @Mock
    private StackDriveWebPermissionService stackDriveWebPermissionService;
    @Mock
    private SolutionService mockSolutionService;
    @Mock
    private StackDriveService stackDriveService;
    @Mock
    private StackDriveLogService stackDriveLogService;
    @Mock
    private UserManager userManager;
    @Mock
    private UserProfile userProfile;


    private SolutionServlet solutionServletUnderTest;

    @Before
    public void setUp() {
        initMocks(this);
        solutionServletUnderTest = new SolutionServlet(mockRepositoryService, mockRenderer, userManager, globalConfigService, mockSolutionService, mockSourceCodeManagementService, stackDriveService, stackDriveLogService);
    }

    @Test
    public void testDoGet() throws Exception {


        // Run the test
        solutionServletUnderTest.doGet(request, response);

        // Verify the results
        assertNotNull(response);
    }

    @Test
    public void testDoGet_TemplateRendererThrowsRenderingException() throws Exception {
        // Setup


        doThrow(RenderingException.class).when(mockRenderer).render(eq("s"), eq(new HashMap<>()), any(Writer.class));

        // Run the test
        solutionServletUnderTest.doGet(request, response);

        // Verify the results
        assertNotNull(response);
    }

    @Test(expected = IOException.class)
    public void testDoGet_TemplateRendererThrowsIOException() throws Exception {


        // Run the test
        solutionServletUnderTest.doGet(request, response);
    }
}
