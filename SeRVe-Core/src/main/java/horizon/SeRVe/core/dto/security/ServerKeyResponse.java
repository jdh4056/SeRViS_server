package horizon.SeRVe.core.dto.security;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class ServerKeyResponse {
    private byte[] encryptedAesKey;
}
