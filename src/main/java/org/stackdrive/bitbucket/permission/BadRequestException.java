package org.stackdrive.bitbucket.permission;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BadRequestException extends RuntimeException {

    private static final long serialVersionUID = 9187161493815859599L;

    public BadRequestException(String message) {
        this(null, message);
    }

    /**
     * Construct new {@code BadRequestException} with error context and message.
     *
     * @param context error context
     * @param message error message
     */
    public BadRequestException(@Nullable String context, @Nullable String message) {
    }

    public BadRequestException(@Nonnull Iterable<String> messages) {
    }
}