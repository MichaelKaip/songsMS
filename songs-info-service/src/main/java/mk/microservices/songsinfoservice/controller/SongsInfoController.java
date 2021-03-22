package mk.microservices.songsinfoservice.controller;

import lombok.RequiredArgsConstructor;
import mk.microservices.songsinfoservice.domain.SongInfo;
import mk.microservices.songsinfoservice.repositories.SongInfoRepository;
import mk.microservices.songsinfoservice.services.SongInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@SuppressWarnings({"rawtypes", "unchecked"})
@RequiredArgsConstructor
@RestController
@RequestMapping("/songsinfo")
public class SongsInfoController {

    private final SongInfoService songInfoService;

    @GetMapping(value = "/{songId}", headers = "Accept=application/json", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<SongInfo> getSongInfoBySongId(@PathVariable(value = "songId") Integer songId) {
        SongInfo songInfo = songInfoService.getSongInfoBySongId(songId);
        if (songInfo == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(songInfo, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<SongInfo>> getAllSongInfos() {
        List<SongInfo> songInfoList = songInfoService.getAllSongInfos();
        if (songInfoList.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(songInfoList, HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<String> postNewSongInfo(@RequestBody SongInfo songInfo) {
        // Todo: RestTemplate call to SongsService to check, if provided songId exists!!!
        if (songInfo.getDescription() == null || songInfo.getSongName() == null  ||
                songInfo.getDescription().equals("") || songInfo.getSongName().equals("") ||
                songInfo.getDescription().trim().isEmpty() || songInfo.getSongName().trim().isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        int id = songInfoService.saveNewSongInfo(songInfo);
        if (id == -1) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        HttpHeaders header = new HttpHeaders();
        header.set("Location", "localhost:8083/songsinfo/" + id);
        return ResponseEntity.ok().headers(header).body("");
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity updateSongInfoBySongId(@PathVariable(value = "id") Integer id, @RequestBody SongInfo songInfo) {
        SongInfo info = songInfoService.getSongInfoBySongId(id);
        if (info == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity(songInfoService.updateSongInfoBySongId(id, songInfo), HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity deleteSongBySongId(@PathVariable(value = "id") Integer id) {
        if (songInfoService.deleteSongInfoBySongId(id) == -1)
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

}
