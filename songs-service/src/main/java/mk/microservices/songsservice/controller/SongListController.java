package mk.microservices.songsservice.controller;

import lombok.AllArgsConstructor;
import mk.microservices.songsservice.dao.SongListDAO;
import mk.microservices.songsservice.model.SongList;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping(value = "/songlists")
public class SongListController {

    private final SongListDAO songListDAO;

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<SongList> getSongListXml(@PathVariable(value = "id") Integer id) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        SongList songList = songListDAO.findById(id);

        if (songList == null)
            return ResponseEntity.notFound().build();

        if (songList.getIsPrivate() && !Objects.equals(userId, songList.getSongListOwner()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        return ResponseEntity.ok(songList);
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<SongList>> getSongListsForUser(@RequestParam("userId") String ownerId) {

        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        return ResponseEntity.ok(userId.equals(ownerId)
                ? songListDAO.findListsOf(userId)
                : songListDAO.findPublicListsOf(ownerId));
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<String> postNewSongList(@RequestBody SongList newSongList) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        if (newSongList.getSongListName() == null || newSongList.getSongListName().equals("") ||
                newSongList.getSongListName().trim().isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        newSongList.setSongListOwner(userId);
        int id = songListDAO.saveSongList(newSongList);
        if (id != 0) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Location", "/localhost:8082/songLists/" + id);
            return new ResponseEntity<>(headers, HttpStatus.CREATED);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping(value = "/{songListId}", consumes = "application/json")
    public ResponseEntity<Void>updateSongList(@PathVariable Integer songListId, @RequestBody SongList songList) {

        String userId  = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        // Make sure the name is set
        if (Strings.isBlank(songList.getSongListName())){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        // Get the list from database
        SongList toBeUpdatedSongList = songListDAO.findById(songListId);
        // Make sure the songlist exists
        if (toBeUpdatedSongList == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Verify the songlist owner
        if (!Objects.equals(toBeUpdatedSongList.getSongListOwner(), userId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        if (songListDAO.updateSongList(songList) == 1) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<Void> deleteSongList(@PathVariable Integer id) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        SongList list = songListDAO.findById(id);

        if (list == null) return ResponseEntity.notFound().build();
        if (!Objects.equals(list.getSongListOwner(), userId))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        int ret = songListDAO.deleteSongList(id);
        if (ret == 1) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
