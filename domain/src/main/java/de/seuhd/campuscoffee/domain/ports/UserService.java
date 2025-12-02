package de.seuhd.campuscoffee.domain.ports;

import de.seuhd.campuscoffee.domain.exceptions.DuplicationException;
import de.seuhd.campuscoffee.domain.exceptions.MissingFieldException;
import de.seuhd.campuscoffee.domain.exceptions.NotFoundException;
import de.seuhd.campuscoffee.domain.model.CampusType;
import de.seuhd.campuscoffee.domain.model.Pos;
import de.seuhd.campuscoffee.domain.model.User;
import org.jspecify.annotations.NonNull;

import java.util.List;

public interface UserService {
    /**
     * Clears all User data.
     * This operation removes all Users from the system.
     * Warning: This is a destructive operation typically used only for testing
     * or administrative purposes. Use with caution in production environments.
     */
    void clear();

    /**
     * Retrieves all Users in the system.
     *
     * @return a list of all User entities; never null, but may be empty if no Users exist
     */
    @NonNull List<User> getAll();

    /**
     * Retrieves a specific User by its unique identifier.
     *
     * @param id the unique identifier of the User to retrieve; must not be null
     * @return the User entity with the specified ID; never null
     * @throws NotFoundException if no User exists with the given ID
     */
    @NonNull User getById(@NonNull Long id);

    /**
     * Retrieves a specific User by its unique name.
     *
     * @param name the unique name of the User to retrieve; must not be null
     * @return the User with the specified name; never null
     * @throws NotFoundException if no User exists with the given name
     */
    @NonNull User getByLoginName(@NonNull String name);

    /**
     * Creates a new User or updates an existing one.
     * This method performs an "upsert" operation:
     * <ul>
     *   <li>If the User has no ID (null), a new User is created</li>
     *   <li>If the User has an ID, and it exists, the existing User is updated</li>
     * </ul>
     * <p>
     * Business rules enforced:
     * <ul>
     *   <li>Usernames must be unique (enforced by database constraint)</li>
     *   <li>All required fields must be present and valid</li>
     *   <li>Timestamps (createdAt, updatedAt) are managed by the {@link UserDataService}.</li>
     * </ul>
     *
     * @param user the User entity to create or update; must not be null
     * @return the persisted User entity with populated ID and timestamps; never null
     * @throws NotFoundException if attempting to update a User that does not exist
     * @throws DuplicationException if a User with the same name already exists
     */
    @NonNull User upsert(@NonNull User user);

    void delete(@NonNull Long id);

}
