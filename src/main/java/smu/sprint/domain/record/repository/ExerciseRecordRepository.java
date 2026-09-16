package smu.sprint.domain.record.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.sprint.domain.record.entity.ExerciseRecord;

public interface ExerciseRecordRepository extends JpaRepository<ExerciseRecord, Long> {
}
