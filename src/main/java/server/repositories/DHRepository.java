package server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import server.data.DAOs.DHDAO;

@Repository
public interface DHRepository extends MongoRepository<DHDAO, String> {
}
