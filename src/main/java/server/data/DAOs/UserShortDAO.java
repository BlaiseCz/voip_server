package server.data.DAOs;

import lombok.*;
import server.data.DTOs.UserShortTO;

@Data
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserShortDAO {
    private String userID;
    private String username;
    private String IPAddress;

    public static UserShortDAO map(UserShortTO userShortTO) {
        return UserShortDAO.builder()
                .username(userShortTO.getUsername())
                .userID(userShortTO.getUserID())
                .IPAddress(userShortTO.getIPAddress())
                .build();
    }

    public static UserShortDAO map(UserDAO userDAO) {
        return UserShortDAO.builder()
                .username(userDAO.getUsername())
                .userID(userDAO.get_id().toString())
                .IPAddress(userDAO.getIPAddress())
                .build();
    }
}
