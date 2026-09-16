package smu.sprint.domain.record.repository;

import java.time.LocalDate;

public interface DailyExerciseCountProjection {

    LocalDate getExerciseDate();

    Long getCount();

}
