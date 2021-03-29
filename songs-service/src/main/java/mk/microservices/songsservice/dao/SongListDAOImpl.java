package mk.microservices.songsservice.dao;

import mk.microservices.songsservice.exception.NotFoundException;
import mk.microservices.songsservice.model.SongList;
import org.hibernate.Hibernate;
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
        this.emf = Persistence.createEntityManagerFactory("songs-service");
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

        return em.createQuery("SELECT sl from SongList sl where songListOwner = :userId")
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public List<SongList> findPublicListsOf(String userId) {
        return findListsOf(userId).stream()
                .filter(not(SongList::getIsPrivate))
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
    public int updateSongList(SongList songList) {
        EntityTransaction eta = em.getTransaction();
        eta.begin();
        Query query = em.createQuery("SELECT s.id from Song s");
        List<Integer> ids = query.getResultList();

        boolean allSongsExist = true;

        if (songList.getSongs() != null)
            allSongsExist = songList.getSongs().stream().allMatch(song -> ids.contains(song.getId()));
        if (!allSongsExist) {
            eta.rollback();
            return 0;
        }
        // Get the songslist from database
        SongList toBeUpdatedSongList = em.find(SongList.class, songList.getId());

        // Update name
        toBeUpdatedSongList.setSongListName(songList.getSongListName());
        // Update privacy
        toBeUpdatedSongList.setIsPrivate(songList.getIsPrivate());
        // Update Owner
        toBeUpdatedSongList.setSongListOwner(songList.getSongListOwner());
        // Update Songs
        toBeUpdatedSongList.setSongs(songList.getSongs());

        // Commit
        try {
            eta.commit();
            Hibernate.isInitialized(toBeUpdatedSongList.getSongs());
            return 1;
        } catch (Exception e) {
            // If something goes wrong make sure not to change anything inside the database
            eta.rollback();
            return 0;
        }
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
