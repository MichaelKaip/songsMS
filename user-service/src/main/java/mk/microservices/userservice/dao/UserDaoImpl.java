package mk.microservices.userservice.dao;

import mk.microservices.userservice.model.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@Service
public class UserDaoImpl implements UserDao {

    protected EntityManagerFactory emf;
    protected EntityManager em;

    @PostConstruct
    public void init() {
        this.emf = Persistence.createEntityManagerFactory("user-service");
        this.em = emf.createEntityManager();
    }

    @PreDestroy
    public void cleanup() {
        if (this.em != null) em.close();
        if (this.emf != null) emf.close();
        this.em = null;
        this.emf = null;
    }

    @Override
    public User getUserById(String userId) {
        return em.find(User.class, userId);
    }

    @Override
    public void createUser(User user) {
        em.persist(user);
    }
}
