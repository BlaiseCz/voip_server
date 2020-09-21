package server.data.DAOs;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import server.data.DTOs.UserShortTO;

import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "Users")
@AllArgsConstructor
@Builder
@Getter
@Setter
public class UserDAO {
    @Id
    private ObjectId _id;
    @NonNull
    private String username;
    @NonNull
    private String email;
    @NonNull
    private String password;

    @Builder.Default
    private boolean isEmailValidated = false;
    @Builder.Default
    private List<String> favourites = new ArrayList<>();
    @Builder.Default
    private String IPAddress = "127.0.0.1";

    public UserShortTO mapToFav() {
        return UserShortTO.builder()
                .UserID(_id.toString())
                .username(username)
                .IPAddress(IPAddress)
                .isFavourite(true)
                .build();
    }

    public UserShortTO mapToNotFav() {
        return UserShortTO.builder()
                .UserID(_id.toString())
                .username(username)
                .IPAddress(IPAddress)
                .isFavourite(false)
                .build();
    }
}
