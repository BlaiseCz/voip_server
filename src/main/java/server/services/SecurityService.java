package server.services;

import com.google.common.hash.Hashing;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.data.DAOs.SecurityInfoDAO;
import server.repositories.SecurityRepository;
import server.repositories.UsersRepository;
import server.utility.TokenServiceUtils;

import java.nio.charset.StandardCharsets;

@Service
@SuppressWarnings("UnstableApiUsage")
public class SecurityService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private SecurityRepository securityRepository;

    public SecurityInfoDAO getNewToken(String userID) {
        String token = getTokenString();

        return SecurityInfoDAO.builder()
                .userID(userID)
                .token(token)
                .expires(System.currentTimeMillis() + (90 * 60 * 1000))
                .build();
    }

    @NotNull
    private String getTokenString() {
        String originalString = String.valueOf(System.currentTimeMillis());

        return Hashing.sha256()
                .hashString(originalString, StandardCharsets.UTF_8)
                .toString();
    }

    public boolean isTokenValid(String userID, String token) {
        return TokenServiceUtils.isTokenValid(userID, token);
    }

    public boolean isTokenValid(SecurityInfoDAO securityInfoDAO, String token) {
        if (!securityInfoDAO.getToken().equals(token)) {
            return false;
        }

        return securityInfoDAO.getExpires() > System.currentTimeMillis();
    }
}
