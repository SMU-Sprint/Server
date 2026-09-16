package smu.sprint.domain.record.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import smu.sprint.domain.record.entity.ExerciseRecord;

import java.time.LocalDate;
import java.util.List;

public interface ExerciseRecordRepository extends JpaRepository<ExerciseRecord, Long> {

    @Query("select r.exerciseDate as exerciseDate, count(r) as count from ExerciseRecord r " +
            "where r.memberId = :memberId and r.exerciseDate between :start and :end " +
            "group by r.exerciseDate")
    List<DailyExerciseCountProjection> countByMemberIdAndExerciseDateBetween(
            @Param("memberId") Long memberId, @Param("start") LocalDate start, @Param("end") LocalDate end);

    List<ExerciseRecord> findByMemberIdAndExerciseDateOrderByCreatedAtAsc(Long memberId, LocalDate exerciseDate);

}
