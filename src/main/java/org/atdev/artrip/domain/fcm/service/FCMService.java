//package org.atdev.artrip.domain.fcm.service;
//
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.atdev.artrip.domain.auth.data.User;
//import org.atdev.artrip.domain.auth.repository.UserRepository;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class FCMService {
//
//    private final TokenRepository tokenRepository;
//    private final UserRepository userRepository;
//    private final TopicTokenRepository topicTokenRepository;
//
//    @Transactional
//    public void sendByAlarm(AlarmDto alarmDto) {
//        // 사용자 조회
//        User user = userRepository.findByEmail(alarmDto.getEmail())
//                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));
//
//        List<Token> tokens = user.getPushToken();
//
//        // 실패한 토큰 리스트 초기화
//        List<Token> failedTokens = new ArrayList<>();
//
//        for (Token token : tokens) {
//            Message message = Message.builder()
//                    .setToken(token.getTokenValue())
//                    .setNotification(Notification.builder()
//                            .setTitle(alarmDto.getTitle())
//                            .setBody(alarmDto.getContent())
//                            .setImage(alarmDto.getImg())
//                            .build())
//                    .putData("click_action", alarmDto.getUrl())
//                    .build();
//            try {
//                FirebaseMessaging.getInstance().send(message);
//            } catch (FirebaseMessagingException e) {
//                failedTokens.add(token);
//                log.error("Failed to send notification to token: " + token.getTokenValue(), e);
//            }
//        }
//
//        if (!failedTokens.isEmpty()) {
//            log.warn("유효하지 않은 토큰 목록 : " + failedTokens);
//            tokenRepository.deleteByTokenIn(failedTokens);
//            topicTokenRepository.deleteByTokenIn(failedTokens);
//        }
//
//        ResponseEntity.ok().body(ApiResponseDto.builder()
//                .successStatus(HttpStatus.OK)
//                .successContent("푸쉬 알림 성공")
//                .Data(tokens)
//                .build()
//        );
//    }
//
//    @Transactional
//    public ResponseEntity<ApiResponseDto> sendByToken(TokenNotificationRequestDto tokenNotificationRequestDto, Principal principal) {
//        String email = principal.getName();
//        // 사용자 조회
//        Member member = memberRepository.findByEmail(email)
//                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));
//
//        List<Token> tokens = member.getTokens();
//
//        // 실패한 토큰 리스트 초기화
//        List<Token> failedTokens = new ArrayList<>();
//
//        for (Token token : tokens) {
//            log.info("메시지 보내는 토큰 " + token.toString());
//            Message message = Message.builder()
//                    .setToken(token.getTokenValue())
//                    .setNotification(Notification.builder()
//                            .setTitle(tokenNotificationRequestDto.getTitle())
//                            .setBody(tokenNotificationRequestDto.getContent())
//                            .setImage(tokenNotificationRequestDto.getImg())
//                            .build())
//                    .putData("click_action", tokenNotificationRequestDto.getUrl())
//                    .build();
//            try {
//                FirebaseMessaging.getInstance().send(message);
//            } catch (FirebaseMessagingException e) {
//                failedTokens.add(token);
//                log.error("알림 전송에 실패한 token: " + token.getTokenValue(), e);
//            }
//        }
//
//        if (!failedTokens.isEmpty()) {
//            log.warn("유효하지 않은 토큰 목록 : " + failedTokens);
//            tokenRepository.deleteByTokenIn(failedTokens);
//            topicTokenRepository.deleteByTokenIn(failedTokens);
//        }
//
//        return ResponseEntity.ok().body(ApiResponseDto.builder()
//                .successStatus(HttpStatus.OK)
//                .successContent("푸시 알림 성공")
//                .build()
//        );
//    }
//
//
//    public ResponseEntity<ApiResponseDto> sendByTopic(TopicNotificationRequestDto topicNotificationRequestDto) {
//
//        Message message = Message.builder()
//                .setTopic(topicNotificationRequestDto.getTopic())
//                .setNotification(Notification.builder()
//                        .setTitle(topicNotificationRequestDto.getTitle())
//                        .setBody(topicNotificationRequestDto.getContent())
//                        .setImage(topicNotificationRequestDto.getImg())
//                        .build())
//                .putData("click_action", topicNotificationRequestDto.getUrl())
//                .build();
//
//        try {
//            FirebaseMessaging.getInstance().send(message);
//        } catch (FirebaseMessagingException e) {
//            throw new RuntimeException(e);
//        }
//
//        return ResponseEntity.ok().body(ApiResponseDto.builder()
//                .successStatus(HttpStatus.OK)
//                .successContent("푸쉬 알림 성공")
//                .Data(topicNotificationRequestDto.getTopic())
//                .build()
//        );
//
//    }
//
//
//    public void subscribeToTopic(String topicName, List<String> tokens) {
//
//        List<String> failedTokens = new ArrayList<>();
//
//        try {
//            TopicManagementResponse response = FirebaseMessaging.getInstance().subscribeToTopicAsync(tokens, topicName).get();
//            log.info("구독하는 topic: " + topicName);
//            log.info(response.getSuccessCount() + "개의 토큰이 구독에 성공했습니다.");
//            if (response.getFailureCount() > 0) {
//                log.info(response.getFailureCount() + "개의 토큰이 구독을 실패했습니다.");
//                response.getErrors().forEach(error -> {
//                    failedTokens.add(tokens.get(error.getIndex()));
//                    log.info("Error for token at index " + error.getIndex() + ": " + error.getReason() +
//                            " (Token: " + failedTokens.get(error.getIndex()-1) + ")");
//                });
//            }
//        } catch (InterruptedException | ExecutionException e) {
//            throw new CustomException(CustomErrorCode.SUBSCRIBE_FAILED);
//        }
//
//        if (!failedTokens.isEmpty()) {
//            log.warn("구독에 실패한 토큰입니다 " + failedTokens);
//            topicTokenRepository.deleteByTokenValueIn(failedTokens);
//            tokenRepository.deleteByTokenValueIn(failedTokens);
//        }
//    }
//
//    public void unsubscribeFromTopic(String topic, List<String> tokens) {
//
//        List<String> failedTokens = new ArrayList<>();
//
//        try {
//            TopicManagementResponse response = FirebaseMessaging.getInstance().unsubscribeFromTopicAsync(tokens, topic).get();
//            log.info("구독 취소하는 topic: " + topic);
//            log.info(response.getSuccessCount() + "개의 토큰이 구독 취소에 성공했습니다.");
//            if (response.getFailureCount() > 0) {
//                log.info(response.getFailureCount() + "개의 토큰이 구독 취소를 실패했습니다.");
//                response.getErrors().forEach(error -> {
//                    failedTokens.add(tokens.get(error.getIndex()));
//                    log.info("Error for token at index " + error.getIndex() + ": " + error.getReason() +
//                            " (Token: " + failedTokens.get(error.getIndex()-1) + ")");
//                });
//
//            }
//        } catch (InterruptedException | ExecutionException e) {
//            throw new CustomException(CustomErrorCode.SUBSCRIBE_CANCEL_FAILED);
//        }
//
//        if (!failedTokens.isEmpty()) {
//            log.warn("구독 취소에 실패한 토큰입니다 " + failedTokens);
//            tokenRepository.deleteByTokenValueIn(failedTokens);
//        }
//    }
//}
//
