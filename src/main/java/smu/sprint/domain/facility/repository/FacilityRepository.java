package smu.sprint.domain.facility.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import smu.sprint.domain.facility.entity.Facility;

import java.util.List;

public interface FacilityRepository extends JpaRepository<Facility, Long> {

    // Haversine 공식(ASIN/SQRT 방식)으로 직선 거리(km)를 계산해 반경 내 시설을 거리순으로 조회하기
    @Query(value = """
            SELECT
                f.facility_id AS facilityId,
                f.name AS name,
                f.type AS type,
                f.address AS address,
                f.latitude AS latitude,
                f.longitude AS longitude,
                DATE_FORMAT(f.open_time, '%H:%i') AS openTime,
                DATE_FORMAT(f.close_time, '%H:%i') AS closeTime,
                ROUND(
                    2 * 6371 * ASIN(SQRT(
                        POWER(SIN(RADIANS(f.latitude - :latitude) / 2), 2) +
                        COS(RADIANS(:latitude)) * COS(RADIANS(f.latitude)) *
                        POWER(SIN(RADIANS(f.longitude - :longitude) / 2), 2)
                    )), 2
                ) AS distanceKm
            FROM facility f
            WHERE (:type IS NULL OR f.type = :type)
            HAVING distanceKm <= :radiusKm
            ORDER BY distanceKm ASC
            """, nativeQuery = true)
    List<FacilityNearbyProjection> findNearby(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radiusKm") double radiusKm,
            @Param("type") String type);

    // 카카오 지오코딩 백필(FacilityGeocodingRunner) 대상 조회용
    List<Facility> findByLatitudeIsNullOrLongitudeIsNull();

}
