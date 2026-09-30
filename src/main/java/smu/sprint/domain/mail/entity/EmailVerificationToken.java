package smu.sprint.domain.mail.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "email_verification_token")
public class EmailVerificationToken {

    @Id
    @Column(name = "token")
    private String token;

    @Column(name = "email", nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "purpose", nullable = false)
    private VerificationPurpose purpose;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    public boolean isExpired(Duration ttl) {
        return issuedAt.plus(ttl).isBefore(LocalDateTime.now());
    }

}
