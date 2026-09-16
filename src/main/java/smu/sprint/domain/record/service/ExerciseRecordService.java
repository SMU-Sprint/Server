package smu.sprint.domain.record.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.sprint.domain.member.entity.Member;
import smu.sprint.domain.member.repository.MemberRepository;
import smu.sprint.domain.record.dto.ExerciseHeatmapDayResponse;
import smu.sprint.domain.record.dto.ExerciseHeatmapResponse;
import smu.sprint.domain.record.dto.ExerciseRecordCreateRequest;
import smu.sprint.domain.record.dto.ExerciseRecordCreateResponse;
import smu.sprint.domain.record.entity.ExerciseRecord;
import smu.sprint.domain.record.repository.DailyExerciseCountProjection;
import smu.sprint.domain.record.repository.ExerciseRecordRepository;
import smu.sprint.global.code.MemberErrorCode;
import smu.sprint.global.exception.MemberException;
import smu.sprint.global.security.auth.CustomUserDetails;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExerciseRecordService {

    // 잔디 한 화면에 보여줄 기간(개월)
    private static final int HEATMAP_WINDOW_MONTHS = 3;

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

    @Transactional(readOnly = true)
    public ExerciseHeatmapResponse getHeatmap(CustomUserDetails customUserDetails, LocalDate endDate) {
        Member member = memberRepository.findByEmail(customUserDetails.getUsername())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        LocalDate end = endDate != null ? endDate : LocalDate.now();
        LocalDate start = end.minusMonths(HEATMAP_WINDOW_MONTHS).plusDays(1);

        Map<LocalDate, Long> countsByDate = exerciseRecordRepository
                .countByMemberIdAndExerciseDateBetween(member.getMember_id(), start, end)
                .stream()
                .collect(Collectors.toMap(DailyExerciseCountProjection::getExerciseDate, DailyExerciseCountProjection::getCount));

        List<ExerciseHeatmapDayResponse> days = start.datesUntil(end.plusDays(1))
                .map(date -> {
                    long count = countsByDate.getOrDefault(date, 0L);
                    return new ExerciseHeatmapDayResponse(date, count, calculateLevel(count));
                })
                .toList();

        return new ExerciseHeatmapResponse(start, end, days);
    }

    // GitHub 잔디처럼 기록 횟수를 0~4 단계로 나눠 프론트가 색상만 매핑하면 되도록 한다.
    private int calculateLevel(long count) {
        if (count <= 0) return 0;
        if (count == 1) return 1;
        if (count == 2) return 2;
        if (count <= 4) return 3;
        return 4;
    }

}
