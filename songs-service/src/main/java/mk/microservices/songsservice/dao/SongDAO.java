package mk.microservices.songsservice.dao;

import mk.microservices.songsservice.model.Song;

import java.util.List;

public interface SongDAO {

    /**
     * Gets all currently stored songs.
     *
     * @return A list of songs.
     */
    List<Song> getAllSongs();

    /**
     * Gets a song.
     *
     * @param id The id.
     * @return The song or null if none exists.
     */
    Song getSongById(int id);

    /**
     * Adds a song.
     *
     * @param song The song to add.
     * @return The new song id or -1 on an error.
     */
    int addSong(Song song);

    /**
     * Updates a given song.
     *
     * @param song The song to update.
     * @return Whether the update was successful or not.
     */
    boolean updateSong(Song song);

    /**
     * Deletes a song.
     *
     * @param id The song id to delete.
     * @return Whether there was a song to be deleted.
     */
    boolean deleteSong(int id);
}
