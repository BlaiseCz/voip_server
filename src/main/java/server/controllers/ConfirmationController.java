package server.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.services.EmailUtility;
import server.services.UserService;
import server.utility.exceptions.CannotConfirmEmailException;

@RestController
@RequestMapping("/confirm")
public class ConfirmationController {

    private final UserService userService;

    public ConfirmationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<String> confirmEmail(@PathVariable String id) {
        if (userService.tryToConfirmEmail(id)) {
            try {
                return ResponseEntity.ok(EmailUtility.getTHX_4_CONFIRMATION_TEXT());
            } catch (CannotConfirmEmailException ex) {
                return ResponseEntity.ok("Sorry, something went wrong, can't confirm your email");
            }
        }
        throw new CannotConfirmEmailException("Something failed during the confirmation, sorry");
    }
}
