package server.data.DTOs;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserFavouritesTO {
    private String userID;
    private String username;

    @Builder.Default
    private List<UserShortTO> favourites = new ArrayList<>();
}
