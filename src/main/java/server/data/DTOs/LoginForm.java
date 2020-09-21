package server.data.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class LoginForm {
    @NonNull
    private String username;
    @NonNull
    private String password;
    @NonNull
    private String IPAddress;
}
