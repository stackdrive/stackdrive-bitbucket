package org.stackdrive.bitbucket.repositoryui;

import org.junit.Before;
import org.junit.Test;
import org.stackdrive.bitbucket.dto.StackDriveForm;

import static org.junit.Assert.*;

public class StackDriveFormTest {

    private StackDriveForm stackDriveFormUnderTest;

    @Before
    public void setUp() {
        stackDriveFormUnderTest = new StackDriveForm(false, "nexusLogin", "nexusPassword");
    }

    @Test
    public void testGetNexusLogin() {
        // Setup

        // Run the test
        final String result = stackDriveFormUnderTest.getNexusLogin();

        // Verify the results
        assertEquals("nexusLogin", result);
    }

    @Test
    public void testGetNexusPassword() {
        // Setup

        // Run the test
        final String result = stackDriveFormUnderTest.getNexusPassword();

        // Verify the results
        assertEquals("nexusPassword", result);
    }

    @Test
    public void testGetPluginEnabled() {
        // Setup

        // Run the test
        final boolean result = stackDriveFormUnderTest.getPluginEnabled();

        // Verify the results
        assertFalse(result);
    }
}
