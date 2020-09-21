package server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import server.data.DAOs.ConversationDAO;

import java.util.List;

@Repository
public interface ConversationRepository extends MongoRepository<ConversationDAO, String> {

    List<ConversationDAO> findAllByIsOngoing(boolean isOngoing);
}
