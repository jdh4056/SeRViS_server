package horizon.SeRVe.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"horizon.SeRVe.core", "horizon.SeRVe.common"})
public class SeRVeCoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeRVeCoreApplication.class, args);
    }
}
