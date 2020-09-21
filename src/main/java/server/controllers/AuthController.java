package server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.data.DAOs.SecurityInfoDAO;
import server.data.DAOs.UserDAO;
import server.data.DTOs.LoginForm;
import server.data.DTOs.RegistrationForm;
import server.data.DTOs.UserTO;
import server.services.SecurityService;
import server.services.UserService;
import server.utility.TokenServiceUtils;
import server.utility.Validator;
import server.utility.exceptions.WrongFormatException;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    @PostMapping(value = "/renewToken")
    public ResponseEntity<SecurityInfoDAO> renewToken(@RequestParam("userID") String userID,
                                                      @RequestHeader("token") String token) {
        SecurityInfoDAO renewedToken = TokenServiceUtils.renewToken(userID, token);
        return ResponseEntity.ok(renewedToken);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<UserTO> tryToLogin(@RequestBody LoginForm loginForm) {
        if (Validator.isLoginFormValid(loginForm)) {
            UserDAO userDAO = userService.tryToLogIn(loginForm);
            SecurityInfoDAO newToken = TokenServiceUtils.getNewToken(userDAO.get_id().toString());
            return ResponseEntity.ok().header("token", newToken.getToken()).body(UserTO.map(userDAO));
        }
        throw new WrongFormatException("Invalid username and/or password!");
    }

    @PostMapping(value = "/register")
    public ResponseEntity<UserTO> tryToRegister(@RequestBody RegistrationForm registrationForm) {
        if (Validator.isRegistrationFormValid(registrationForm)) {
            return ResponseEntity.ok(userService.tryToRegister(registrationForm));
        }
        throw new WrongFormatException("Invalid data format!");
    }
}
