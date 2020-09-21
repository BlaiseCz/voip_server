package server.data.DAOs;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "SecurityInfo")
@AllArgsConstructor
@Builder
@Getter
@Setter
public class SecurityInfoDAO {
    @Id
    private ObjectId _id;
    private String userID;
    private String token;
    private String username;
    private long expires;
}
