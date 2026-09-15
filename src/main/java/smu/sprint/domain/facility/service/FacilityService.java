package smu.sprint.domain.facility.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.sprint.domain.facility.dto.FacilitySearchResponse;
import smu.sprint.domain.facility.entity.ExerciseFacilityMapping;
import smu.sprint.domain.facility.entity.FacilityType;
import smu.sprint.domain.facility.repository.ExerciseFacilityMappingRepository;
import smu.sprint.domain.facility.repository.FacilityNearbyProjection;
import smu.sprint.domain.facility.repository.FacilityRepository;
import smu.sprint.global.code.FacilityErrorCode;
import smu.sprint.global.exception.FacilityException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FacilityService {

    private final FacilityRepository facilityRepository;
    private final ExerciseFacilityMappingRepository exerciseFacilityMappingRepository;

    @Transactional(readOnly = true)
    public FacilitySearchResponse searchNearbyFacilities(Double latitude, Double longitude, Double radiusKm, String exerciseName) {
        if (latitude == null || longitude == null) {
            throw new FacilityException(FacilityErrorCode.INVALID_COORDINATE);
        }

        FacilityType facilityType = resolveFacilityType(exerciseName);
        // 네이티브 쿼리는 DB 컬럼(문자열)과 직접 비교하므로 enum을 그 이름 문자열로 변환해서 넘긴다.
        String type = facilityType == null ? null : facilityType.name();
        List<FacilityNearbyProjection> rows = facilityRepository.findNearby(latitude, longitude, radiusKm, type);
        return FacilitySearchResponse.from(rows);
    }

    // exerciseName에 매핑된 시설 종목이 있으면 그 값으로, 매핑이 없거나 exerciseName이 없으면 null(필터 없음)을 반환한다.
    private FacilityType resolveFacilityType(String exerciseName) {
        if (exerciseName == null || exerciseName.isBlank()) {
            return null;
        }
        return exerciseFacilityMappingRepository.findByExerciseName(exerciseName)
                .map(ExerciseFacilityMapping::getFacilityType)
                .orElse(null);
    }

}
