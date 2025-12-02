package de.seuhd.campuscoffee.domain.impl;

import de.seuhd.campuscoffee.domain.exceptions.DuplicationException;
import de.seuhd.campuscoffee.domain.model.Pos;
import de.seuhd.campuscoffee.domain.model.User;
import de.seuhd.campuscoffee.domain.ports.OsmDataService;
import de.seuhd.campuscoffee.domain.ports.PosDataService;
import de.seuhd.campuscoffee.domain.ports.UserDataService;
import de.seuhd.campuscoffee.domain.ports.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDataService userDataService;
    private final OsmDataService osmDataService;

    public void clear(){
        log.warn("Clearing all User data");
        userDataService.clear();
    }

    @Override
    public @NonNull List<User> getAll(){
        log.debug("Retrieving all User");
        return userDataService.getAll();
    }

    @Override
    public @NonNull User getById(@NonNull Long id){
        log.debug("Retrieving User with ID: {}", id);
        return userDataService.getById(id);
    }

    @Override
    public @NonNull User getByLoginName(@NonNull String name){
        log.debug("Retrieving User with name: {}", name);
        return userDataService.getByLoginName(name);
    }

    @Override
    public @NonNull User upsert(@NonNull User user){
        if (user.id() == null) {
            // create a new POS
            log.info("Creating new User: {}", user.loginName());
        } else {
            // update an existing POS
            log.info("Updating User with ID: {}", user.id());
            // POS ID must be set
            Objects.requireNonNull(user.id());
            // POS must exist in the database before the update
            userDataService.getById(user.id());
        }
        return performUpsert(user);
    }

    @Override
    public void delete(@NonNull Long id){}

    private @org.jspecify.annotations.NonNull User performUpsert(@org.jspecify.annotations.NonNull User user) {
        try {
            User upsertedUser = userDataService.upsert(user);
            log.info("Successfully upserted User with ID: {}", upsertedUser.id());
            return upsertedUser;
        } catch (DuplicationException e) {
            log.error("Error upserting POS '{}': {}", user.loginName(), e.getMessage());
            throw e;
        }
    }
}
