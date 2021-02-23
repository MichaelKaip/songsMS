package mk.microservices.songsservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user")
public class User {

    @Id
    @Column(name = "userId", nullable = false, updatable = false)
    private String userId;
    @JsonIgnore // Do not leak the password when we return a user somewhere
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "firstName", nullable = false)
    private String firstName;
    @Column(name = "lastName", nullable = false)
    private String lastName;
    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "songListOwner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SongList> songLists;
}
