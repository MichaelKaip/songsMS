package mk.microservices.userservice.dao;

import mk.microservices.userservice.model.User;

public interface UserDao {

    User getUserById(String userId);

    void createUser(User user);
}
