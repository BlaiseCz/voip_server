package server.utility;

import server.data.DTOs.LoginForm;
import server.data.DTOs.RegistrationForm;

public class Validator {
    private Validator() {
    }

    public static boolean isUsernameValid(String username) {
        return username.matches("[a-zA-Z0-9]+") && !username.isEmpty();
    }

    public static boolean isEmailValid(String email) {
        return email.matches("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
    }

    public static boolean isLoginFormValid(LoginForm loginForm) {
        return Validator.isUsernameValid(loginForm.getUsername());
    }

    public static boolean isRegistrationFormValid(RegistrationForm registrationForm) {
        return Validator.isEmailValid(registrationForm.getEmail())
                && Validator.isUsernameValid(registrationForm.getUsername());
    }
}
