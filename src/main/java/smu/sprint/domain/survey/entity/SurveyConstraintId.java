package smu.sprint.domain.survey.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class SurveyConstraintId implements Serializable {
    // 캡슐화 관점: survey_id + constraint_type 두 컬럼을 하나의 식별자 개념(값 객체)으로 묶어서 관리

    @Column(name = "survey_id")
    private Long surveyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "constraint_type")
    private ConstraintType constraintType;

}
