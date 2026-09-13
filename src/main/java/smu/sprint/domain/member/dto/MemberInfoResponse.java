package smu.sprint.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import smu.sprint.domain.member.entity.Gender;
import smu.sprint.domain.member.entity.Member;

public record MemberInfoResponse(

        @Schema(description = "이메일", example = "user@example.com")
        String email,

        @Schema(description = "이름", example = "홍길동")
        String name,

        @Schema(description = "신장(cm)", example = "170.5")
        Double height,

        @Schema(description = "몸무게(kg)", example = "65.0")
        Double weight,

        @Schema(description = "나이", example = "25")
        Integer age,

        @Schema(description = "성별")
        Gender gender

) {
    public static MemberInfoResponse from(Member member) {
        return new MemberInfoResponse(
                member.getEmail(),
                member.getName(),
                member.getHeight(),
                member.getWeight(),
                member.getAge(),
                member.getGender()
        );
    }
}
