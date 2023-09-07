package org.stackdrive.bitbucket.permission;

import com.atlassian.bitbucket.repository.Repository;
import org.stackdrive.bitbucket.globalconfig.GlobalConfigService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StackDriveWebPermissionServiceTest {

    @Mock
    private GlobalConfigService globalConfigService;

    private StackDriveWebPermissionService stackDriveWebPermissionServiceUnderTest;

    @Before
    public void setUp() {
        stackDriveWebPermissionServiceUnderTest = new StackDriveWebPermissionService();
    }

    @Test
    public void testShouldDisplay() {
        // Setup
        final Repository repository = null;

        // Run the test
        final boolean result = stackDriveWebPermissionServiceUnderTest.hasPermission(repository);

        // Verify the results
        assertTrue(result);
    }
}