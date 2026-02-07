package horizon.SeRVe.team.controller;

import horizon.SeRVe.team.dto.edge.RegisterEdgeNodeRequest;
import horizon.SeRVe.team.service.EdgeNodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/edge-nodes")
@RequiredArgsConstructor
public class EdgeNodeController {

    private final EdgeNodeService edgeNodeService;

    @PostMapping("/register")
    public ResponseEntity<String> registerEdgeNode(@Valid @RequestBody RegisterEdgeNodeRequest request) {
        String nodeId = edgeNodeService.registerEdgeNode(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nodeId);
    }

    @GetMapping("/{nodeId}/team-key")
    public ResponseEntity<String> getTeamKey(@PathVariable String nodeId) {
        String encryptedTeamKey = edgeNodeService.getTeamKey(nodeId);
        return ResponseEntity.ok(encryptedTeamKey);
    }
}
