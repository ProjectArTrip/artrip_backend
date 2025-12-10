package org.atdev.artrip.domain.devicetoken.data;

import jakarta.persistence.*;

@Entity
public class TopicToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "topic_id")
    private Long topicId;

    @ManyToOne
    @JoinColumn(name = "device_token_id", nullable = false)
    private DeviceToken deviceToken;

    private String topic; // 키워드 기반
}
