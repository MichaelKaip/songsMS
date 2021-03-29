package mk.microservices.songsservice.dao;

import mk.microservices.songsservice.model.Song;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;
import java.util.Objects;

@Component
public class SongDAOImpl implements SongDAO {

    private EntityManagerFactory emf;
    private EntityManager em;

    @PostConstruct
    public void init() {
        this.emf = Persistence.createEntityManagerFactory("songs-service");
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
    public List<Song> getAllSongs() {
        final CriteriaQuery<Song> cq = em.getCriteriaBuilder().createQuery(Song.class);
        final CriteriaQuery<Song> all = cq.select(cq.from(Song.class));
        return em.createQuery(all).getResultList();
    }

    @Override
    public Song getSongById(int id) {
        return em.find(Song.class, id);
    }

    @Override
    public int addSong(Song song) {
        if (song.getId() != null) throw new IllegalArgumentException("Song is already persisted");
        try {
            em.getTransaction().begin();
            em.persist(song);
            em.getTransaction().commit();
            return song.getId();
        } catch (Exception e) {
            em.getTransaction().rollback();
            return -1;
        }
    }

    @Override
    public boolean updateSong(Song song) {
        if (song.getId() == null) return false;
        try {
            em.getTransaction().begin();
            Song stored = em.find(Song.class, song.getId());
            if (!Objects.equals(stored.getTitle(), song.getTitle())) stored.setTitle(song.getTitle());
            if (!Objects.equals(stored.getArtist(), song.getArtist())) stored.setArtist(song.getArtist());
            if (!Objects.equals(stored.getLabel(), song.getLabel())) stored.setLabel(song.getLabel());
            if (!Objects.equals(stored.getReleased(), song.getReleased())) stored.setReleased(song.getReleased());
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            em.getTransaction().rollback();
            return false;
        }
    }

    @Override
    public boolean deleteSong(int id) {
        try {
            Song song = em.find(Song.class, id);
            if (song == null) return false;

            em.getTransaction().begin();
            em.remove(song);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            return false;
        }
        return true;
    }
}
