package smu.sprint.domain.survey.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import smu.sprint.global.entity.BaseEntity;

import java.time.LocalTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "survey")
public class Survey extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_id")
    private Long surveyId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(name = "exercise_purpose", nullable = false)
    private ExercisePurpose exercisePurpose;

    @Column(name = "exercise_purpose_etc")
    private String exercisePurposeEtc;

    @Column(name = "exercise_experience_flag", nullable = false)
    private boolean exerciseExperienceFlag;

    @Column(name = "exercise_experience_detail")
    private String exerciseExperienceDetail;

    @Column(name = "preferred_sport", length = 50)
    private String preferredSport;

    @Enumerated(EnumType.STRING)
    @Column(name = "occupation_type", nullable = false)
    private OccupationType occupationType;

    @Column(name = "vigorous_days", nullable = false)
    private int vigorousDays;

    @Column(name = "vigorous_duration_minutes", nullable = false)
    private int vigorousDurationMinutes;

    @Column(name = "moderate_days", nullable = false)
    private int moderateDays;

    @Column(name = "moderate_duration_minutes", nullable = false)
    private int moderateDurationMinutes;

    @Column(name = "walking_days", nullable = false)
    private int walkingDays;

    @Column(name = "walking_duration_minutes", nullable = false)
    private int walkingDurationMinutes;

    @Column(name = "work_start_time")
    private LocalTime workStartTime;

    @Column(name = "work_end_time")
    private LocalTime workEndTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "exercise_spot", nullable = false)
    private ExerciseSpot exerciseSpot;

    @Column(name = "exercise_spot_etc")
    private String exerciseSpotEtc;

    @Column(name = "fatigue_flag", nullable = false)
    private boolean fatigueFlag;

    @Column(name = "stair_climb_flag", nullable = false)
    private boolean stairClimbFlag;

    @Column(name = "walk_300m_flag", nullable = false)
    private boolean walk300mFlag;

    @Column(name = "weight_loss_flag", nullable = false)
    private boolean weightLossFlag;
}