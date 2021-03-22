package mk.microservices.songsinfoservice.services;

import mk.microservices.songsinfoservice.domain.SongInfo;

import java.util.List;

public interface SongInfoService {


    /**
     * Gets the song info with the given songId
     * @param songId The id of the song
     * @return The song infos related to a given song
     */
    SongInfo getSongInfoBySongId(Integer songId);


    /**
     * Gets all available song infos stored in the database
     * @return All song infos.
     */
    List<SongInfo> getAllSongInfos();


    /**
     * Stores new song information into the database
     * @param songInfo Information about a song that has to be persisted.
     * @return 1, if the song information could be stored successfully and the id of the resource otherwise.
     */
    int saveNewSongInfo(SongInfo songInfo);


    /**
     * Updates the song with the given id in the database
     * @param id The id of the song
     * @param songInfo The values to be updated
     * @return 1, if the update could be performed successfully and -1 otherwise.
     */
    int updateSongInfoBySongId(Integer id, SongInfo songInfo);

    /**
     * Deletes a song with the given id from the database
     * @param id The id of the song
     * @return 1, if the song has been deleted successfully and -1 otherwise.
     */
    int deleteSongInfoBySongId(Integer id);
}
