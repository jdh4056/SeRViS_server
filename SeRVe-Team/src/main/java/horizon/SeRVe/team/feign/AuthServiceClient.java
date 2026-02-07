package horizon.SeRVe.team.feign;

import horizon.SeRVe.common.dto.feign.UserInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "serve-auth", url = "${service.auth.url}")
public interface AuthServiceClient {

    @GetMapping("/internal/users/{userId}")
    UserInfoResponse getUserInfo(@PathVariable String userId);

    @GetMapping("/internal/users/by-email/{email}")
    UserInfoResponse getUserByEmail(@PathVariable String email);

    @GetMapping("/internal/users/{userId}/exists")
    Boolean userExists(@PathVariable String userId);
}
