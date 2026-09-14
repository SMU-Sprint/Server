package smu.sprint.domain.recommendation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class RecommendedExerciseId implements Serializable {
    // 캡슐화 관점 : survey_id + rank 두 컬럼을 하나의 식별자 개념(값 객체로 만들어)으로 묶어서 관리

    @Column(name = "survey_id")
    private Long surveyId;

    // DB 컬럼명은 exercise_rank로 매핑
    @Column(name = "exercise_rank")
    private Integer rank;

}
