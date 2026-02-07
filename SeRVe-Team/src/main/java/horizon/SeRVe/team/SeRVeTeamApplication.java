package horizon.SeRVe.team;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"horizon.SeRVe.team", "horizon.SeRVe.common"})
@EnableFeignClients
public class SeRVeTeamApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeRVeTeamApplication.class, args);
    }
}
