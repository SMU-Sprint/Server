package smu.sprint.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.sprint.domain.member.entity.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String Email);
}
