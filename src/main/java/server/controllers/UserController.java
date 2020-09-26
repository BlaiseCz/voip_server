package server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.data.DTOs.UserFavouritesTO;
import server.data.DTOs.UserShortTO;
import server.data.DTOs.UserTO;
import server.services.SecurityService;
import server.services.UserService;
import server.utility.TokenServiceUtils;
import server.utility.Validator;
import server.utility.exceptions.WrongFormatException;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    //debug only
    @GetMapping( "/test")
    public boolean test() {
        return true;
    }

    @GetMapping( "/all")
    public ResponseEntity<List<UserShortTO>> getAllUsers(
            @RequestParam("userID") String userID,
            @RequestHeader("token") String token) {
        if (TokenServiceUtils.isTokenValid(userID, token)) {
            return ResponseEntity.ok(userService.getAllUsers(userID));
        } else {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping( "/me")
    public ResponseEntity<UserTO> getMeXD(
            @RequestParam("userID") String userID,
            @RequestHeader("token") String token) {
        if (TokenServiceUtils.isTokenValid(userID, token)) {
            return ResponseEntity.ok(userService.getUser(userID));
        } else {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }

    //debug only
    @GetMapping( "/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable("username") String username) {
        if (Validator.isUsernameValid(username)) {
            return ResponseEntity.ok(userService.getUserByUsername(username));
        }
        throw new WrongFormatException("Invalid username!");
    }

    @GetMapping( "/favourites")
    public ResponseEntity<?> getFavouritesOfUser(@RequestParam("userID") String userID,
                                                 @RequestHeader("token") String token) {
        try {
            if (TokenServiceUtils.isTokenValid(userID, token)) {
                UserFavouritesTO favouritesOfUser = userService.getFavouritesOfUser(userID);
                return ResponseEntity.ok(favouritesOfUser.getFavourites());
            } else {
                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<String>(Arrays.toString(e.getStackTrace()), HttpStatus.OK);
        }
    }

    @PostMapping( "/favourites")
    public ResponseEntity<List<UserShortTO>> addUserToFavourites(
            @RequestParam("userID") String userID,
            @RequestParam("favUsername") String favUsername,
            @RequestHeader("token") String token) {
        if (TokenServiceUtils.isTokenValid(userID, token)) {
            UserFavouritesTO userFavouritesTO = userService.addToFavourites(userID, favUsername);
            return ResponseEntity.ok(userFavouritesTO.getFavourites());
        } else {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping( "/favourites")
    public ResponseEntity<List<UserShortTO>> removeUserFromFavourites(
            @RequestParam("userID") String userID,
            @RequestParam("favUsername") String favUsername,
            @RequestHeader("token") String token) {
        if (TokenServiceUtils.isTokenValid(userID, token)) {
            UserFavouritesTO userFavouritesTO = userService.deleteFavourite(userID, favUsername);
            return ResponseEntity.ok(userFavouritesTO.getFavourites());
        } else {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }

    //debug only
    @DeleteMapping( "/all")
    public ResponseEntity<String> removeAllUsers() {
        userService.deleteAllUsers();

        return ResponseEntity.ok("All users deleted");
    }

    @DeleteMapping( "/{userID}")
    public ResponseEntity<String> removeUser(@PathVariable String userID,
                                        @RequestHeader("token") String token) {
        if (TokenServiceUtils.isTokenValid(userID, token)) {
            userService.deleteUser(userID);
            return ResponseEntity.ok("Deleted user");
        } else {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping( "/favourites/all")
    public ResponseEntity<String> deleteAllFavouritesOfUser(@RequestParam("userID") String userID,
                                                       @RequestHeader("token") String token) {
        userService.deleteAllFavouritesOfUser(userID);

        return ResponseEntity.ok("Deleted all favourites");
    }
}
