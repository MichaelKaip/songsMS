package mk.microservices.songsinfoservice.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "songinfo")
public class SongInfo {

    @Id
    private String id;

    private int songId;

    private String songName;

    private String description;
}
