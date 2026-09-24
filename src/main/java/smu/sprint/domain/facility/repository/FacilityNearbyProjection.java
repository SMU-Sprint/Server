package smu.sprint.domain.facility.repository;

/* FacilityRepository.findNearby는 네이티브 쿼리라 결과가 Facility 엔티티 그대로가 아니다.
 distanceKm은 입력 좌표마다 매번 계산되는 값이라 엔티티에 없는 필드이고,
 openTime/closeTime도 엔티티의 LocalTime이 아니라 SQL에서 이미 "HH:mm" 문자열로 포맷되어 내려온다.
 그래서 결과 모양에 맞는 이 인터페이스를 따로 정의했고, getter 이름이 SQL의 컬럼 별칭(AS ...)과
 일치해야 Spring Data가 각 행을 이 인터페이스의 구현체로 자동 매핑해준다. */
public interface FacilityNearbyProjection {

    Long getFacilityId();

    String getName();

    String getType();

    String getExerciseName();

    String getAddress();

    Double getLatitude();

    Double getLongitude();

    String getOpenTime();

    String getCloseTime();

    Double getDistanceKm();

}
