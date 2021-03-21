package mk.microservices.songsservice.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mk.microservices.songsservice.dao.SongDAO;
import mk.microservices.songsservice.model.Song;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping(value = "/songs")
public class SongController {

    private final SongDAO songDao;

    @GetMapping(value = "/{id}", headers = "Accept=application/json", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Song> getSongJson(@PathVariable(value = "id") Integer id) {
        Song song = songDao.getSongById(id);

        if (song == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(song, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", headers = "Accept=application/xml", produces = {MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Song> getSongXml(@PathVariable(value = "id") Integer id) {
        Song song = songDao.getSongById(id);

        if (song == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(song, HttpStatus.OK);
    }

    @GetMapping(headers = "Accept=application/json", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<Song>> getAllSongsJson() {
        List<Song> songs = songDao.getAllSongs();

        if (songs == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(songs, HttpStatus.OK);
    }

    @GetMapping(headers = "Accept=application/xml", produces = {MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Song>> getAllSongsXml() {
        List<Song> songs = songDao.getAllSongs();

        if (songs == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(songs, HttpStatus.OK);
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<String> postSong(@RequestBody Song newsong,
                                           @RequestHeader("Authorization") String token) {
        if(newsong.getTitle()==null || newsong.getTitle().equals("") || newsong.getTitle().trim().isEmpty()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        int id = songDao.addSong(newsong);

        if (id != -1) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Location", "/localhost:8080/songs/" + id);
            return new ResponseEntity<>(headers, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<?> updateSong(@RequestBody Song updatedSong,
                                        @PathVariable Integer id) {
        if (!id.equals(updatedSong.getId()) || updatedSong.getTitle() == null || updatedSong.getTitle().equals("") || updatedSong.getTitle().trim().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        } else {
            if (songDao.updateSong(updatedSong)) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteSong(@PathVariable Integer id) {
        if (songDao.deleteSong(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
