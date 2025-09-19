package com.github.cortiz.onvopay.exceptions;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the OnvoPayException class.
 * This class validates the behavior of the getStatusCode method
 * and ensures it correctly returns the status code assigned during initialization.
 */
class OnvoPayExceptionTest {

    @Test
    void testGetStatusCode_WithStatusCodeProvided() {
        // Arrange
        int expectedStatusCode = 400;
        OnvoPayException exception = new OnvoPayException(
                expectedStatusCode,
                "API_123",
                List.of("Invalid input", "Missing field"),
                "Bad Request"
        );

        // Act
        int actualStatusCode = exception.getStatusCode();

        // Assert
        assertEquals(expectedStatusCode, actualStatusCode, "The status code should match the provided value.");
    }

    @Test
    void testGetStatusCode_WithDefaultConstructor() {
        // Arrange
        String message = "A simple error occurred.";
        OnvoPayException exception = new OnvoPayException(message);

        // Act
        int actualStatusCode = exception.getStatusCode();

        // Assert
        assertEquals(0, actualStatusCode, "The status code should be 0 for the default constructor.");
    }
}