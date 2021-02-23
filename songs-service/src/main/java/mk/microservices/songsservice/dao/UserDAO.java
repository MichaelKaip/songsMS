package mk.microservices.songsservice.dao;

import mk.microservices.songsservice.model.User;

public interface UserDAO {

    User getUserById(String userId);

    void createUser(User user);
}
