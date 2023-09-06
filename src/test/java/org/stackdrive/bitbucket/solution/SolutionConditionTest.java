package org.stackdrive.bitbucket.solution;

import com.atlassian.bitbucket.repository.Repository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.stackdrive.bitbucket.dto.StackDriveForm;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SolutionConditionTest {

    @Mock
    private SolutionService mockSolutionService;

    private RepositorySolutionCondition repositorySolutionConditionUnderTest;

    @Before
    public void setUp() {
        initMocks(this);
        repositorySolutionConditionUnderTest = new RepositorySolutionCondition(mockSolutionService);
    }

    @Test
    public void testShouldDisplay() {
        // Setup
        final Map<String, Object> context = new HashMap<>();

        // Configure RepositorySettingsService.getRepoSetting(...).
        final StackDriveForm stackDriveForm = new StackDriveForm(false, "nexusLogin", "nexusPassword");
        when(mockSolutionService.hasSolutionPermission(any(Repository.class))).thenReturn(true);

        // Run the test
        final boolean result = repositorySolutionConditionUnderTest.shouldDisplay(context);

        // Verify the results
        assertFalse(result);
    }
}
