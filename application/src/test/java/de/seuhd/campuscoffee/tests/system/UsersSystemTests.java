package de.seuhd.campuscoffee.tests.system;

import de.seuhd.campuscoffee.domain.model.Pos;
import de.seuhd.campuscoffee.domain.model.User;
import de.seuhd.campuscoffee.domain.tests.TestFixtures;
import org.junit.jupiter.api.Test;

import java.util.List;

import static de.seuhd.campuscoffee.tests.SystemTestUtils.Requests.posRequests;
import static de.seuhd.campuscoffee.tests.SystemTestUtils.Requests.userRequests;

public class UsersSystemTests extends AbstractSysTest {

    @Test
    void createUser() {
        User userToCreate = TestFixtures.getUserListForInsertion().getFirst();
        User createdUser = userDtoMapper.toDomain(userRequests.create(List.of(userDtoMapper.fromDomain(userToCreate))).getFirst());

        assertEqualsIgnoringIdAndTimestamps(createdUser, userToCreate);
    }

    @Test
    void getAllCreatedUser(){
        List<User> createdUserList = TestFixtures.createUserFixtures(userService);

        List<User> retrievedUser = userRequests.retrieveAll()
                .stream()
                .map(userDtoMapper::toDomain)
                .toList();

        assertEqualsIgnoringTimestamps(retrievedUser, createdUserList);
    }

    @Test
    void getUserById() {
        List<User> createdUserList = TestFixtures.createUserFixtures(userService);
        User createdUser = createdUserList.getFirst();

        User retrievedUser = userDtoMapper.toDomain(
                userRequests.retrieveById(createdUser.id())
        );

        assertEqualsIgnoringTimestamps(retrievedUser, createdUser);
    }

}