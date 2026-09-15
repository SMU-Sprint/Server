package smu.sprint.domain.facility.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import smu.sprint.global.entity.BaseEntity;

// AI 추천 운동명(exerciseName, 자유 텍스트)을 시설 종목(Facility.type)에 연결하는 룩업 테이블.
// 매핑이 없는 exerciseName은 시설 조회 시 필터 없이 전체 반환된다.
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "exercise_facility_mapping")
public class ExerciseFacilityMapping extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "exercise_name", nullable = false, unique = true, length = 100)
    private String exerciseName;

    @Enumerated(EnumType.STRING)
    @Column(name = "facility_type", nullable = false, length = 100)
    private FacilityType facilityType;

}
