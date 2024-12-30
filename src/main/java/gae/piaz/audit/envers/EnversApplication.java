package gae.piaz.audit.envers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class EnversApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnversApplication.class, args);
    }
}
