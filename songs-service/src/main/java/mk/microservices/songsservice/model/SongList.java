package mk.microservices.songsservice.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "songlists")
public class SongList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "\"ownerId\"")
    private String songListOwner;

    @Column(name = "name")
    private String songListName;

    @Column(name = "\"isPrivate\"")
    private Boolean isPrivate;

    @ManyToMany
    @JoinTable(name = "songs_to_list",
            joinColumns = {@JoinColumn(name = "\"listId\"", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "\"songId\"", referencedColumnName = "id")})
    private List<Song> songs;

}

