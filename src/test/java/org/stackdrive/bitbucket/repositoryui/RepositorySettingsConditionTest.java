package org.stackdrive.bitbucket.repositoryui;

import com.atlassian.bitbucket.repository.Repository;
import org.stackdrive.bitbucket.permission.StackDriveWebPermissionService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class RepositorySettingsConditionTest {

    @Mock
    private StackDriveWebPermissionService mockDisplayService;

    private RepositorySettingsCondition repositorySettingsConditionUnderTest;

    @Before
    public void setUp() {
        initMocks(this);
        repositorySettingsConditionUnderTest = new RepositorySettingsCondition(mockDisplayService);
    }

    @Test
    public void testShouldDisplay() {
        // Setup
        final Map<String, Object> context = new HashMap<>();
        when(mockDisplayService.hasPermission(any(Repository.class))).thenReturn(false);

        // Run the test
        final boolean result = repositorySettingsConditionUnderTest.shouldDisplay(context);

        // Verify the results
        assertFalse(result);
    }
}
