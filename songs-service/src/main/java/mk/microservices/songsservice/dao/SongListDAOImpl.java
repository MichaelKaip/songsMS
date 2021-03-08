package mk.microservices.songsservice.dao;

import mk.microservices.songsservice.exception.NotFoundException;
import mk.microservices.songsservice.model.SongList;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

@Component
public class SongListDAOImpl implements SongListDAO {

    private EntityManagerFactory emf;
    private EntityManager em;

    @PostConstruct
    public void init() {
        this.emf = Persistence.createEntityManagerFactory("songsWSb");
        this.em = emf.createEntityManager();
    }

    @PreDestroy
    public void cleanup() {
        if (this.em != null) {
            this.em.close();
            this.em = null;
        }
        if (this.emf != null) {
            this.emf.close();
            this.emf = null;
        }
    }

    @Override
    public SongList findById(int id) {
        try {
            return em.find(SongList.class, id);
        } catch (PersistenceException | IllegalArgumentException p) {
            p.printStackTrace();
            return null;
        }
    }

    @Override
    public List<SongList> findListsOf(String userId) {
        Query query = em.createQuery("SELECT * from SongList where songListOwner = unserid");
        List<SongList> resultsList = query.getResultList();
        if (resultsList.isEmpty()) throw new NotFoundException();
        return resultsList;
    }

    @Override
    public List<SongList> findPublicListsOf(String userId) {
        return findListsOf(userId).stream()
                .filter(not(SongList::isPrivate))
                .collect(Collectors.toList());
    }

    @Override
    public int saveSongList(SongList songList) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        Query query = em.createQuery("SELECT s.id from Song s");
        List<Integer> ids = query.getResultList();

        boolean allSongsExist = true;

        if (songList.getSongs() != null)
            allSongsExist = songList.getSongs().stream().allMatch(song -> ids.contains(song.getId()));
        if (!allSongsExist) {
            transaction.rollback();
            return 0;
        }

        try {
            em.persist(songList);
            transaction.commit();
            return songList.getId();
        } catch (Exception ignored) {
        }
        transaction.rollback();
        return 0;
    }

    @Override
    @Transactional(rollbackOn = Throwable.class)
    public int deleteSongList(int id) {
        SongList songList = em.find(SongList.class, id);
        if (songList != null) {
            em.remove(songList);
            return 1;
        }
        return 0;
    }
}
