package mk.microservices.songsservice.controller;

import lombok.AllArgsConstructor;
import mk.microservices.songsservice.dao.SongListDAO;
import mk.microservices.songsservice.dao.UserDAO;
import mk.microservices.songsservice.model.SongList;
import mk.microservices.songsservice.model.User;
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
@RequestMapping(value = "/songLists")
public class SongListController {

    private final SongListDAO songListDAO;
    private final UserDAO userDAO;

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<SongList> getSongListXml(
            @PathVariable(value = "id") Integer id) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        SongList songList = songListDAO.findById(id);

        if (songList == null)
            return ResponseEntity.notFound().build();

        if (songList.isPrivate() && !Objects.equals(userId, songList.getSongListOwner().getUserId()))
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
        User user = userDAO.getUserById(userId);

        if (newSongList.getSongListName() == null || newSongList.getSongListName().equals("") ||
                newSongList.getSongListName().trim().isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        newSongList.setSongListOwner(user);
        int id = songListDAO.saveSongList(newSongList);
        if (id != 0) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Location", "/songsWS-originalverbuggt/rest/songLists/" + id);
            return new ResponseEntity<>(headers, HttpStatus.CREATED);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<Void> deleteSongList(@PathVariable Integer id) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        SongList list = songListDAO.findById(id);

        if (list == null) return ResponseEntity.notFound().build();
        if (!Objects.equals(list.getSongListOwner().getUserId(), userId))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        int ret = songListDAO.deleteSongList(id);
        if (ret == 1) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
