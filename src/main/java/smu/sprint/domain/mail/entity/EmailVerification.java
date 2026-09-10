package smu.sprint.domain.mail.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "email_verification")
public class EmailVerification {

    @Id
    @Column(name = "email")
    private String email;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    public void reissue(String code, LocalDateTime issuedAt) {
        this.code = code;
        this.issuedAt = issuedAt;
    }

    public boolean isExpired(Duration ttl) {
        return issuedAt.plus(ttl).isBefore(LocalDateTime.now());
    }

    public boolean matches(String inputCode) {
        return this.code.equals(inputCode);
    }

}
