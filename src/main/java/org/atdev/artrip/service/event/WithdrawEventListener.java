package org.atdev.artrip.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.service.AuthService;
import org.atdev.artrip.validator.social.SocialVerifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class WithdrawEventListener {

    private final AuthService authService;
    private final List<SocialVerifier> socialVerifiers;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(WithdrawEvent event) {
        for (WithdrawEvent.SocialInfo social : event.socials()) {
            socialVerifiers.stream()
                    .filter(v -> v.getProvider() == social.provider())
                    .findFirst()
                    .ifPresent(verifier -> {
                        try {
                            verifier.unlink(social.providerId(), social.refreshToken());
                        } catch (Exception e) {
                            log.error("unlink 실패 provider={}, providerId={}",
                                    social.provider(), social.providerId(), e);
                        }
                    });
        }
    }
}
