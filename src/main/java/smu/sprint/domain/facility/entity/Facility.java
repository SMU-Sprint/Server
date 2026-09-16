package smu.sprint.domain.facility.entity;

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
@Table(name = "facility")
public class Facility extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "facility_id")
    private Long facilityId;

    @Column(name = "facility_code", length = 50)
    private String facilityCode;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 100)
    private FacilityType type;

    @Column(name = "address", length = 300)
    private String address;

    // 원본 데이터에 좌표가 없는 시설이 존재할 수 있어 nullable. 좌표가 없으면 거리 계산이 NULL이 되어
    // FacilityRepository.findNearby의 HAVING 절에서 자연스럽게 검색 결과에서 제외된다.
    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "open_time")
    private LocalTime openTime;

    @Column(name = "close_time")
    private LocalTime closeTime;

    // 카카오 지오코딩 백필(FacilityGeocodingRunner)에서 주소로 조회한 좌표를 채워넣을 때 사용
    public void updateCoordinate(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

}
