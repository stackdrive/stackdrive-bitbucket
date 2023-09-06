package org.stackdrive.bitbucket.repositoryui;

import com.atlassian.bitbucket.project.Project;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.user.ApplicationUser;
import org.stackdrive.bitbucket.dto.StackDriveForm;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

public class StackDriveRequestTest {

    @Mock
    private Project mockProject;
    @Mock
    private Repository mockRepository;
    @Mock
    private ApplicationUser mockUser;
    @Mock
    private StackDriveForm mockForm;

    private StackDriveRequest stackDriveRequestUnderTest;

    @Before
    public void setUp() {
        initMocks(this);
        stackDriveRequestUnderTest = new StackDriveRequest(false, mockProject, mockRepository, mockUser, mockForm, "nextUrl");
    }

    @Test
    public void testIsForAccessKeys() {
        // Setup

        // Run the test
        final boolean result = stackDriveRequestUnderTest.isForAccessKeys();

        // Verify the results
        assertTrue(result);
    }
}
