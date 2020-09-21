package server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import server.data.DAOs.SecurityInfoDAO;
import server.data.DAOs.UserDAO;
import server.data.DTOs.*;
import server.repositories.SecurityRepository;
import server.repositories.UsersRepository;
import server.utility.TokenServiceUtils;
import server.utility.exceptions.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private SecurityRepository securityRepository;

    @Autowired
    private EmailUtility emailUtility;

    public UserFavouritesTO getUserByUsername(String username) {
        UserDAO userDAO = usersRepository
                .findByUsername(username)
                .orElseThrow(() -> new NoSuchUserException(username));

        return getFavouritesOfUser(userDAO.get_id().toString());
    }

    public UserDAO tryToLogIn(LoginForm loginForm) {
        UserDAO userDAO = usersRepository.findByUsername(loginForm.getUsername())
                .orElseThrow(() -> new CannotLogInException("Invalid username!"));

        if (!loginForm.getPassword().equals(userDAO.getPassword())) {
            throw new CannotLogInException("Invalid password!");
        } else if (!userDAO.isEmailValidated()) {
            throw new CannotLogInException("Email not validated!");
        } else {
            updateUserIP(userDAO, loginForm);
            return userDAO;
        }
    }

    public UserTO getUser(String userID) {
        UserDAO userDAO = usersRepository
                .findById(userID)
                .orElseThrow(() -> new NoSuchUserException(userID));
        return UserTO.map(userDAO);
    }

    private void updateUserIP(UserDAO userDAO, LoginForm loginForm) {
        userDAO.setIPAddress(loginForm.getIPAddress());
        usersRepository.save(userDAO);
    }

    public UserTO tryToRegister(RegistrationForm registrationForm) {
        usersRepository.findByUsername(registrationForm.getUsername())
                .ifPresent(e -> {
                    throw new CannotRegisterException("Username " + e.getUsername() + " taken, try different username!");
                });
        usersRepository.findByEmail(registrationForm.getEmail())
                .ifPresent(e -> {
                    throw new CannotRegisterException("Email " + e.getEmail() + " already taken, try different email!");
                });

        UserDAO savedUser = registerNewUser(registrationForm);
        emailUtility.sendConfirmationEmail(registrationForm,
                savedUser.get_id()
                        .toString());
        return UserTO.map(savedUser);
    }

    private UserDAO registerNewUser(RegistrationForm registrationForm) {
        return usersRepository.save(UserDAO.builder()
                .email(registrationForm.getEmail())
                .password(registrationForm.getPassword())
                .username(registrationForm.getUsername())
                .IPAddress(registrationForm.getIPAddress())
                .build()
        );
    }

    public boolean tryToConfirmEmail(String id) {
        try {
            UserDAO userDAO = usersRepository.findById(id)
                    .orElseThrow(CannotConfirmEmailException::new);
            userDAO.setEmailValidated(true);
            usersRepository.save(userDAO);
        } catch (CannotConfirmEmailException ex) {
            return false;
        }
        return true;
    }

    public UserFavouritesTO getFavouritesOfUser(String userID) {
        UserDAO userDAO = usersRepository.findById(userID)
                .orElseThrow(NoSuchUserException::new);

        List<String> favouritesIDs = userDAO.getFavourites();

        List<UserShortTO> favourites = usersRepository.findAllBy_idIn(favouritesIDs)
                .stream()
                .map(UserDAO::mapToFav)
                .collect(Collectors.toList());
        List<UserShortTO> notFavourites = usersRepository.findAllBy_idNotIn(favouritesIDs)
                .stream()
                .map(UserDAO::mapToNotFav)
                .collect(Collectors.toList());

        favourites.addAll(notFavourites);

        TokenServiceUtils.updateUsers();
        for (UserShortTO favourite : favourites) {
            favourite.setActive(TokenServiceUtils.usersActive.getOrDefault(favourite.getUserID(), false));
        }

        return new UserFavouritesTO(userID, userDAO.getUsername(), favourites);
    }



    public UserFavouritesTO addToFavourites(String userID, String favouriteUsername) {
        UserDAO userDAO = usersRepository.findById(userID)
                .orElseThrow(() -> new NoSuchUserException(userID));
        String favouriteID = usersRepository.findByUsername(favouriteUsername)
                .orElseThrow(() -> new NoSuchUserException(favouriteUsername))
                .get_id()
                .toString();

        if (userDAO.getFavourites().contains(favouriteID)) {
            throw new FavouritesException("User " + favouriteUsername + " already is on 'favourites' list");
        }

        userDAO.getFavourites().add(favouriteID);
        usersRepository.save(userDAO);

        return getFavouritesOfUser(userID);
    }

    public UserFavouritesTO deleteFavourite(String userID, String favUsername) {
        UserDAO userDAO = usersRepository
                .findById(userID)
                .orElseThrow(() -> new NoSuchUserException(userID));
        UserDAO favUserDAO = usersRepository
                .findByUsername(favUsername)
                .orElseThrow(() -> new NoSuchUserException((favUsername)));

        List<String> favourites = userDAO.getFavourites();
        String favUserID = favUserDAO.get_id().toString();

        if (!favourites.contains(favUserID)) {
            throw new FavouritesException("User " + favUsername + " is not on favourites list");
        }

        favourites.remove(favUserID);
        userDAO.setFavourites(favourites);
        usersRepository.save(userDAO);

        return getFavouritesOfUser(userID);
    }

    public void deleteAllUsers() {
        usersRepository.deleteAll();
    }

    public void deleteAllFavouritesOfUser(String userID) {
        UserDAO userDAO = usersRepository.findById(userID)
                .orElseThrow(() -> new NoSuchUserException(userID));

        userDAO.setFavourites(new ArrayList<>());

        usersRepository.save(userDAO);
    }

    public void deleteUser(String userID) {
        UserDAO userDAO = usersRepository.findById(userID)
                .orElseThrow(() -> new NoSuchUserException(userID));

        usersRepository.delete(userDAO);
    }

    public List<UserShortTO> getAllUsers(String userID) {
        return getFavouritesOfUser(userID).getFavourites();
    }
}
