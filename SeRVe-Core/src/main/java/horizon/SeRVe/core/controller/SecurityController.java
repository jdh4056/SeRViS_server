package horizon.SeRVe.core.controller;

import com.google.crypto.tink.KeysetHandle;
import horizon.SeRVe.common.security.crypto.CryptoManager;
import horizon.SeRVe.common.security.crypto.KeyExchangeService;
import horizon.SeRVe.core.dto.security.ClientPublicKeyRequest;
import horizon.SeRVe.core.dto.security.ServerKeyResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/security")
@RequiredArgsConstructor
@Slf4j
public class SecurityController {

    private final KeyExchangeService keyExchangeService;
    private final CryptoManager cryptoManager;

    @PostMapping("/handshake")
    public ResponseEntity<ServerKeyResponse> handshake(@RequestBody ClientPublicKeyRequest request) {
        try {
            log.info(">>> [Handshake 요청] 클라이언트 공개키 수신됨");

            // 1. 서버: 저장소용 AES 키 생성
            KeysetHandle serverAesKey = cryptoManager.generateAesKey();

            // 2. 서버: 클라이언트의 공개키로 AES 키를 포장(Wrap)
            byte[] wrappedKey = keyExchangeService.wrapAesKey(serverAesKey, request.getPublicKeyJson());

            // 3. 응답: 포장된 키 전송
            log.info(">>> [Handshake 응답] 암호화된 AES 키 전송 완료");
            return ResponseEntity.ok(new ServerKeyResponse(wrappedKey));
        } catch (Exception e) {
            log.error("핸드셰이크 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
