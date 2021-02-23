package mk.microservices.songsservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "beleg4_songlists")
public class SongList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ownerId")
    private User songListOwner;

    @Column(name = "name")
    private String songListName;

    @Column(name = "isPrivate")
    private boolean isPrivate;

    @ManyToMany
    @JoinTable(name = "beleg4_songs_to_list",
            joinColumns = {@JoinColumn(name = "listId", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "songId", referencedColumnName = "id")})
    private List<Song> songs;
}

