package smu.sprint.domain.survey.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "survey_constraint")
public class SurveyConstraint {

    @EmbeddedId
    private SurveyConstraintId id;

    @Column(name = "constraint_etc")
    private String constraintEtc;

}
