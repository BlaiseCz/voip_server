package server.services;

import lombok.Getter;
import org.springframework.stereotype.Service;
import server.configuration.EmailConfig;
import server.data.DTOs.RegistrationForm;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailUtility {
    private final EmailConfig emailConfig;

    private static final String EMAIL_HEADER = "Email confirmation from Voice Over IP app";
    private static final String EMAIL_TEXT_PT_1 = "Hi!\n" +
            "This is VoIP app.\n" +
            "We're just trying to make sure that your email is valid.\n" +
            "Click in the link below (or paste it into your browser address bar) to validate your email.\n";
    private static final String EMAIL_TEXT_PT_2 = "\nThanks for registration and have a good day!\n" +
            "Sincerely,\n" +
            "VoIP App Dev Team.\n\n" +
            "PS If you haven't registered in VoIP App, please ignore this message.";
    @Getter
    private static final String THX_4_CONFIRMATION_TEXT = "Thanks for email confirmation!\n" +
            "You can now close this tab and start using VoIP app :)";

    public EmailUtility(EmailConfig emailConfig) {
        this.emailConfig = emailConfig;
    }


    public void sendConfirmationEmail(RegistrationForm registrationForm, String userID) {
        String to = registrationForm.getEmail();
        String from = emailConfig.getEmail();
        String host = "smtp.gmail.com";
        Properties properties = setProperties(host);
        Session session = getSessionObj(properties);

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(EMAIL_HEADER);
            message.setText(composeMessage(userID));

            Transport.send(message);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    private Properties setProperties(String host) {
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");
        return properties;
    }

    private Session getSessionObj(Properties properties) {
        return Session.getInstance(properties, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        emailConfig.getEmail(),
                        emailConfig.getPassword());
            }
        });
    }

    private String composeConfirmationURL(String email) {
        return "127.0.0.1:8080/confirm/" + email;
    }

    private String composeMessage(String email) {
        return EMAIL_TEXT_PT_1 + composeConfirmationURL(email) + EMAIL_TEXT_PT_2;
    }
}
