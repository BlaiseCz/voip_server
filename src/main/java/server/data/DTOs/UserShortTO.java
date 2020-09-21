package server.data.DTOs;

import lombok.*;
import server.data.DAOs.UserDAO;
import server.data.DAOs.UserShortDAO;

@Data
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserShortTO {
    private String UserID;
    private String username;
    private String IPAddress;
    private boolean isFavourite;
    private boolean isActive;

    public static UserShortTO map(UserDAO userDAO) {
        return UserShortTO.builder()
                .username(userDAO.getUsername())
                .UserID(userDAO.get_id().toString())
                .IPAddress(userDAO.getIPAddress())
                .build();
    }

    public static UserShortTO map(UserShortDAO userShortDAO) {
        return UserShortTO.builder()
                .username(userShortDAO.getUsername())
                .UserID(userShortDAO.getUserID())
                .IPAddress(userShortDAO.getIPAddress())
                .build();
    }
}
