package mk.microservices.songsinfoservice.services;

import lombok.RequiredArgsConstructor;
import mk.microservices.songsinfoservice.domain.SongInfo;
import mk.microservices.songsinfoservice.exceptions.NotFoundException;
import mk.microservices.songsinfoservice.repositories.SongInfoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SongInfoServiceImpl implements SongInfoService {

    private final SongInfoRepository songInfoRepository;

    @Override
    public SongInfo getSongInfoBySongId(Integer songId) {
        SongInfo songInfo = null;
        for (SongInfo info : songInfoRepository.findAll()) {
            if (info.getSongId() == songId) {
                return info;
            }
        }
        return null;
    }

    @Override
    public List<SongInfo> getAllSongInfos() {
        return songInfoRepository.findAll();
    }

    @Override
    public int saveNewSongInfo(SongInfo songInfo) {
        if (songInfo == null || songInfo.getId() != null)
            return -1;
        // Only save songinfos for songs which not yet exist in the database.
        for (SongInfo info : songInfoRepository.findAll()) {
            if (info.getSongId() == songInfo.getSongId()) {
                return -1;
            }
        }
        SongInfo newSongInfo = songInfoRepository.save(songInfo);
        return newSongInfo.getSongId();
    }

    @Override
    public int updateSongInfoBySongId(Integer id, SongInfo songInfo) {
        if (songInfo.getId() == null) {
            return -1;
        }
        SongInfo updatedSongInfo = new SongInfo();
        for (SongInfo info : songInfoRepository.findAll()) {
            if (info.getSongId() == id) {
                updatedSongInfo.setId(songInfo.getId());
                updatedSongInfo.setSongId(songInfo.getSongId());
                updatedSongInfo.setSongName(songInfo.getSongName());
                updatedSongInfo.setDescription(songInfo.getDescription());
                songInfoRepository.save(updatedSongInfo);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int deleteSongInfoBySongId(Integer id) {
        for (SongInfo info : songInfoRepository.findAll()) {
            if (info.getSongId() == id) {
                songInfoRepository.delete(info);
                return 1;
            }
        }
        return -1;
    }
}
