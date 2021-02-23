package mk.microservices.songsservice.dao;

import mk.microservices.songsservice.model.User;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class UserDAOImpl implements UserDAO {

    protected EntityManagerFactory emf;
    protected EntityManager em;

    @PostConstruct
    public void init() {
        this.emf = Persistence.createEntityManagerFactory("songsWSb");
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
