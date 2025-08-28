package org.example.imgq.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "auth_log")
public class AuthLog {
    @Id
    @GeneratedValue
    @org.hibernate.annotations.UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private String actorType;

    @Column
    private String actorId;

    @Column(nullable = false)
    private String event;

    @Column
    private String ip;

    @Column
    private String userAgent;

    @Column(nullable = false)
    private OffsetDateTime at;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getActorType() { return actorType; }
    public void setActorType(String actorType) { this.actorType = actorType; }
    public String getActorId() { return actorId; }
    public void setActorId(String actorId) { this.actorId = actorId; }
    public String getEvent() { return event; }
    public void setEvent(String event) { this.event = event; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    public OffsetDateTime getAt() { return at; }
    public void setAt(OffsetDateTime at) { this.at = at; }
}
