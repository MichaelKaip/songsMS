package mk.microservices.songsservice.dao;

import mk.microservices.songsservice.model.SongList;

import java.util.List;

public interface SongListDAO {

    /**
     * Gets a song list from the database by given id.
     * @param id The id of the song list to find
     * @return The song list with the given id and NULL, if it doesn't exist.
     */
    SongList findById(int id);

    /**
     * Gets all public song list from a given usr from the database.
     *
     * @param userId The id of user whose song lists are asked for
     * @return A list of all song lists for the given user.
     */
    List<SongList> findListsOf(String userId);


    /**
     * Gets all public song list from a given usr from the database.
     *
     * @param userId The id of the user whose the song lists are asked for
     * @return A list of all public song lists for the given user.
     */
    List<SongList> findPublicListsOf(String userId);

    /**
     * Saves a new songlist, but only if all songs in the given list are already in the database.
     *
     * @param songList The song list to be persisted.
     * @return 0, if the song list couldn't be stored and the id of the song list to be persisted in case of success.
     */
    int saveSongList(SongList songList);

    /**
     * Deletes a songlist from the database
     * @param id The id of the song list to be deleted.
     * @return 0, if the song list couldn't be deleted and 1 in case of success.
     */
    int deleteSongList(int id);
}
