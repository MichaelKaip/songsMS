package mk.microservices.songsinfoservice.repositories;

import mk.microservices.songsinfoservice.domain.SongInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SongInfoRepository extends MongoRepository<SongInfo, Integer> {

}
