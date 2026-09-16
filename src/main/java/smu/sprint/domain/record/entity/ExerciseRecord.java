package smu.sprint.domain.record.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import smu.sprint.global.entity.BaseEntity;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "exercise_record",
        indexes = @Index(name = "idx_exercise_record_member_date", columnList = "member_id, exercise_date"))
public class ExerciseRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long recordId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    // 잔디(히트맵) 집계의 기준이 되는 '일' 단위 날짜. 실제 운동을 수행한 날짜이며, 등록 시각(createdAt)과는 다를 수 있다.
    @Column(name = "exercise_date", nullable = false)
    private LocalDate exerciseDate;

    @Column(name = "exercise_name", nullable = false, length = 100)
    private String exerciseName;

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes;

}
