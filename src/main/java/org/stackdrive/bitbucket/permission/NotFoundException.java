package org.stackdrive.bitbucket.permission;

import javax.annotation.Nullable;

public class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = 6374167568769913992L;

    public NotFoundException(String message) {
        this(null, message);
    }

    /**
     * Construct new {@code NotFoundException} with error context and message.
     *
     * @param context error context
     * @param message error message
     */
    public NotFoundException(@Nullable String context, @Nullable String message) {
    }
}