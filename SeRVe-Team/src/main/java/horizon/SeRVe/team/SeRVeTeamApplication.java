package horizon.SeRVe.team;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"horizon.SeRVe.team", "horizon.SeRVe.common"})
public class SeRVeTeamApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeRVeTeamApplication.class, args);
    }
}
