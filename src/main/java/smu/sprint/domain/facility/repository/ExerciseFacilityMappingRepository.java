package smu.sprint.domain.facility.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.sprint.domain.facility.entity.ExerciseFacilityMapping;

import java.util.Optional;

// exercise_facility_mapping(운동명 -> 시설 종목 매핑) 테이블 조회용 리포지토리
// FacilityService가 요청받은 exerciseName에 대응하는 시설 종목 필터 값을 찾을 때 findByExerciseName을 사용
public interface ExerciseFacilityMappingRepository extends JpaRepository<ExerciseFacilityMapping, Long> {

    Optional<ExerciseFacilityMapping> findByExerciseName(String exerciseName);

}
