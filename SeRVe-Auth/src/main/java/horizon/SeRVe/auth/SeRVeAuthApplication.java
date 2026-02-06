package horizon.SeRVe.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"horizon.SeRVe.auth", "horizon.SeRVe.common"})
public class SeRVeAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeRVeAuthApplication.class, args);
    }
}
