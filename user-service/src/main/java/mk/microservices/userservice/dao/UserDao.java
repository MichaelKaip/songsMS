package mk.microservices.userservice.dao;

import mk.microservices.userservice.model.User;
import org.springframework.stereotype.Service;

@Service
public interface UserDao {

    User getUserById(String userId);

    void createUser(User user);
}
