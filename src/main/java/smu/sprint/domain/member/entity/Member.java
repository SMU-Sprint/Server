package smu.sprint.domain.member.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import smu.sprint.global.entity.BaseEntity;
import smu.sprint.global.security.auth.Roles;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "member")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long member_id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Roles role;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // 탈퇴 후에도 email의 unique 제약은 유지되므로, 원래 이메일을 비워 재가입이 가능하도록 변형해서 보존한다.
    public void withdraw() {
        this.deletedAt = LocalDateTime.now();
        this.email = this.email + "_DELETED_" + this.member_id;
    }

}
