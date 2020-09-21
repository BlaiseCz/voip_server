package server.utility;

import com.google.common.hash.Hashing;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import server.data.DAOs.SecurityInfoDAO;
import server.services.SecurityService;
import server.utility.exceptions.CannotRenewTokenException;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TokenServiceUtils {

    public static HashMap<String, Boolean> usersActive = new HashMap<>();
    public static HashMap<String, Pair<String, Long>> tokensWithUserIDsAndExpires = new HashMap<>();

    public static SecurityInfoDAO getNewToken(String userID) {
        long expires = System.currentTimeMillis() + 2 * 60 * 1000;
        String tokenString = getTokenString();
        tokensWithUserIDsAndExpires.put(userID, Pair.of(tokenString, expires));
        usersActive.put(userID, true);

        return SecurityInfoDAO.builder()
                .token(tokenString)
                .expires(expires)
                .userID(userID)
                ._id(new ObjectId())
                .username("username")
                .build();
    }


    private static String getTokenString() {
        String originalString = String.valueOf(System.currentTimeMillis());

        return Hashing.sha256()
                .hashString(originalString, StandardCharsets.UTF_8)
                .toString();
    }

    public static void updateUsers() {
        HashMap<String, Pair<String, Long>> tokensWithUserIDsAndExpires = TokenServiceUtils.tokensWithUserIDsAndExpires;
        for (Map.Entry<String, Pair<String, Long>> userIDTokenExpires : tokensWithUserIDsAndExpires.entrySet()) {
            if (userIDTokenExpires.getValue().getSecond() > System.currentTimeMillis()) {
                TokenServiceUtils.tokensWithUserIDsAndExpires.put(
                        userIDTokenExpires.getKey(),
                        Pair.of(
                                userIDTokenExpires.getValue().getFirst(),
                                System.currentTimeMillis() + 2 * 60 * 1000
                        )
                );
                TokenServiceUtils.usersActive.put(userIDTokenExpires.getKey(), true);
            } else {
                TokenServiceUtils.usersActive.put(userIDTokenExpires.getKey(), false);
            }
        }
    }

    public static boolean isTokenValid(String userID, String token) {
        if(tokensWithUserIDsAndExpires.containsKey(userID)) {
            usersActive.put(userID, true);
            updateUsers();
            return tokensWithUserIDsAndExpires.get(userID).getFirst().equals(token);
        } else {
            usersActive.put(userID, false);
            return false;
        }
    }

    public static SecurityInfoDAO renewToken(String userID, String token) {
        Long expires = 0L;
        if(tokensWithUserIDsAndExpires.containsKey(userID)) {
            if(tokensWithUserIDsAndExpires.get(userID).getSecond() > System.currentTimeMillis()
                    && tokensWithUserIDsAndExpires.get(userID).getFirst().equals(token)) {
                expires = System.currentTimeMillis() + 2 * 60 * 1000;
                tokensWithUserIDsAndExpires.put(userID, Pair.of(token, expires));
                usersActive.put(userID, true);
            }
            else {
                usersActive.put(userID, false);
                throw new CannotRenewTokenException("Current token invalid, login again");
            }
        } else {
            usersActive.put(userID, false);
            throw new CannotRenewTokenException("Current token invalid, login again");
        }
        updateUsers();

        return SecurityInfoDAO.builder()
                .token(token)
                .userID(userID)
                .expires(expires)
                .build();
    }
}
