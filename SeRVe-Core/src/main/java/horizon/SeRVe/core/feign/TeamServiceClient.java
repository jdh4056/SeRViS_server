package horizon.SeRVe.core.feign;

import horizon.SeRVe.common.dto.feign.MemberRoleResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "serve-team", url = "${service.team.url}")
public interface TeamServiceClient {

    @GetMapping("/internal/teams/{teamId}/exists")
    Boolean teamExists(@PathVariable String teamId);

    @GetMapping("/internal/teams/{teamId}/members/{userId}/role")
    MemberRoleResponse getMemberRole(@PathVariable String teamId, @PathVariable String userId);

    @GetMapping("/internal/teams/{teamId}/members/{userId}/exists")
    Boolean memberExists(@PathVariable String teamId, @PathVariable String userId);

    @GetMapping("/internal/edge-nodes/{nodeId}/team-id")
    String getEdgeNodeTeamId(@PathVariable String nodeId);
}
