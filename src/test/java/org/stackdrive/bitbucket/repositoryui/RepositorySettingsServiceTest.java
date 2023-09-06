package org.stackdrive.bitbucket.repositoryui;

import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.stackdrive.bitbucket.dto.StackDriveForm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class RepositorySettingsServiceTest {

    @Mock
    private PluginSettingsFactory mockPluginSettingsFactory;
    @Mock
    private TransactionTemplate mockTransactionTemplate;

    private RepositorySettingsService repositorySettingsServiceUnderTest;

    @Before
    public void setUp() {
        initMocks(this);
        repositorySettingsServiceUnderTest = new RepositorySettingsService(mockPluginSettingsFactory, mockTransactionTemplate);
    }

    @Test
    public void testGetRepoSetting() {
        // Setup
        final Repository repository = null;
        final StackDriveForm expectedResult = new StackDriveForm(false, "nexusLogin", "nexusPassword");

        // Configure TransactionTemplate.execute(...).
        final StackDriveForm stackDriveForm = new StackDriveForm(false, "nexusLogin", "nexusPassword");
        when(mockTransactionTemplate.execute(any(TransactionCallback.class))).thenReturn(stackDriveForm);

        when(mockPluginSettingsFactory.createSettingsForKey("s")).thenReturn(null);

        // Run the test
        final StackDriveForm result = repositorySettingsServiceUnderTest.getRepoSetting(repository);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testSaveRepoSetting() {
        // Setup
        final Repository repository = null;
        final StackDriveForm stackDriveForm = new StackDriveForm(false, "nexusLogin", "nexusPassword");

        // Configure TransactionTemplate.execute(...).
        final StackDriveForm stackDriveForm1 = new StackDriveForm(false, "nexusLogin", "nexusPassword");
        when(mockTransactionTemplate.execute(any(TransactionCallback.class))).thenReturn(stackDriveForm1);

        when(mockPluginSettingsFactory.createSettingsForKey("s")).thenReturn(null);

        // Run the test
        repositorySettingsServiceUnderTest.saveRepoSetting(repository, stackDriveForm);

        // Verify the results
        assertNotNull(repositorySettingsServiceUnderTest);
    }
}
