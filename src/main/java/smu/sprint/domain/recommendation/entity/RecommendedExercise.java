package smu.sprint.domain.recommendation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import smu.sprint.global.entity.BaseEntity;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "recommended_exercise")
public class RecommendedExercise extends BaseEntity {

    @EmbeddedId
    private RecommendedExerciseId id;

    @Column(name = "exercise_name", nullable = false, length = 100)
    private String exerciseName;

    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(name = "improvements", columnDefinition = "TEXT")
    private String improvements;

}
