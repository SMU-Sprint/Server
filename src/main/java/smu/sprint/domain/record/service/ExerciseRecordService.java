package smu.sprint.domain.record.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.sprint.domain.member.entity.Member;
import smu.sprint.domain.member.repository.MemberRepository;
import smu.sprint.domain.record.dto.ExerciseRecordCreateRequest;
import smu.sprint.domain.record.dto.ExerciseRecordCreateResponse;
import smu.sprint.domain.record.entity.ExerciseRecord;
import smu.sprint.domain.record.repository.ExerciseRecordRepository;
import smu.sprint.global.code.MemberErrorCode;
import smu.sprint.global.exception.MemberException;
import smu.sprint.global.security.auth.CustomUserDetails;

@Service
@RequiredArgsConstructor
public class ExerciseRecordService {

    private final ExerciseRecordRepository exerciseRecordRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public ExerciseRecordCreateResponse createRecord(CustomUserDetails customUserDetails, ExerciseRecordCreateRequest request) {
        Member member = memberRepository.findByEmail(customUserDetails.getUsername())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        ExerciseRecord record = ExerciseRecord.builder()
                .memberId(member.getMember_id())
                .exerciseDate(request.exerciseDate())
                .exerciseName(request.exerciseName())
                .durationMinutes(request.durationMinutes())
                .build();

        ExerciseRecord saved = exerciseRecordRepository.save(record);

        return ExerciseRecordCreateResponse.from(saved);
    }

}
