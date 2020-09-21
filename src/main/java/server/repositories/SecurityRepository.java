package server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import server.data.DAOs.SecurityInfoDAO;

import java.util.Optional;

@Repository
public interface SecurityRepository extends MongoRepository<SecurityInfoDAO, String> {
    Optional<SecurityInfoDAO> findByUserID(String userID);
    Optional<SecurityInfoDAO> findByUsername(String username);
}
