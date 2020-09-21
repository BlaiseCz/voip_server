package server.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:email.properties", ignoreResourceNotFound = true)
public class EmailConfig {
    @Getter
    @Value("${email.address:#{environment.EMAIL}}")
    private String email;

    @Getter
    @Value("${email.password:#{environment.EMAIL_PASS}}")
    private String password;
}
