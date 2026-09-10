package smu.sprint.global.security.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import smu.sprint.domain.entity.Member;
import smu.sprint.domain.repository.MemberRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService {

    private final MemberRepository memberRepository;

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Member> userEntity = memberRepository.findByEmail(email);
        if (userEntity.isPresent()) {
            Member member = userEntity.get();
            return new CustomUserDetails(
                    member.getEmail(), member.getPassword(), member.getRole()
            );
        }
        throw new UsernameNotFoundException("사용자가 존재하지 않습니다.");
    }
}
