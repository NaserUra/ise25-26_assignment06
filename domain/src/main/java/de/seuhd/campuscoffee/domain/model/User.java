package de.seuhd.campuscoffee.domain.model;

import lombok.Builder;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record User(
        @Nullable Long id, // null when the User has not been created yet
        @Nullable LocalDateTime createdAt, // set on User creation
        @Nullable LocalDateTime updatedAt, // set on User creation and update
        @NonNull String loginName,
        @NonNull String emailAddress,
        @NonNull String firstName,
        @NonNull String lastName
) implements Serializable { // serializable to allow cloning (see TestFixtures class).
    @Serial
    private static final long serialVersionUID = 1L;
}