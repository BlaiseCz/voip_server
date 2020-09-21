package server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import server.data.DAOs.UserDAO;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends MongoRepository<UserDAO, String> {

    Optional<UserDAO> findByUsername(String username);

    Optional<UserDAO> findByEmail(String email);

    List<UserDAO> findAllBy_idIn(List<String> favouritesIDs);

    List<UserDAO> findAllBy_idNotIn(List<String> favouritesIDs);
}
