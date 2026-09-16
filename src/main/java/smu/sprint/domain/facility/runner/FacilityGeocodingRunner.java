package smu.sprint.domain.facility.runner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import smu.sprint.domain.facility.client.KakaoGeocodingClient;
import smu.sprint.domain.facility.entity.Facility;
import smu.sprint.domain.facility.repository.FacilityRepository;

import java.util.List;
import java.util.Optional;

// 위경도가 비어있는 시설의 주소를 카카오 지오코딩 API로 좌표 변환해 채우는 일회성 배치.
// 기본 프로필로는 실행되지 않고, "geocode-backfill" 프로필을 활성화했을 때만 앱 시작 시 1회 실행된다.
// 좌표가 채워진 행은 다음 실행부터 대상에서 빠지므로, 중간에 중단돼도 재실행하면 이어서 처리된다.
@Slf4j
@Component
@Profile("geocode-backfill")
@RequiredArgsConstructor
public class FacilityGeocodingRunner implements ApplicationRunner {

    // 카카오 API 호출 간 최소 간격(ms). 초당 호출 수 제한에 걸리면 이 값을 늘릴 것.
    private static final long DELAY_MS = 150;

    // 이 개수마다 누적 진행 상황을 로그로 남긴다 (성공은 개별 로그가 없어서 진행 상황을 알기 어려웠음)
    private static final int PROGRESS_LOG_INTERVAL = 50;

    private final FacilityRepository facilityRepository;
    private final KakaoGeocodingClient kakaoGeocodingClient;

    @Override
    public void run(ApplicationArguments args) throws InterruptedException {
        List<Facility> targets = facilityRepository.findByLatitudeIsNullOrLongitudeIsNull();
        log.info("[ FacilityGeocodingRunner ]: 좌표 없는 시설 {}건 발견, 지오코딩 시작", targets.size());

        int updated = 0;
        int skipped = 0;
        int failed = 0;
        int processed = 0;

        for (Facility facility : targets) {
            String address = facility.getAddress();
            if (address == null || address.isBlank()) {
                skipped++;
            } else {
                Optional<KakaoGeocodingClient.Coordinate> coordinate = kakaoGeocodingClient.geocode(address, facility.getName());
                if (coordinate.isPresent()) {
                    facility.updateCoordinate(coordinate.get().latitude(), coordinate.get().longitude());
                    facilityRepository.save(facility);
                    updated++;
                } else {
                    log.warn("[ FacilityGeocodingRunner ]: 지오코딩 실패 - facilityId={}, address='{}'", facility.getFacilityId(), address);
                    failed++;
                }
                Thread.sleep(DELAY_MS);
            }

            processed++;
            if (processed % PROGRESS_LOG_INTERVAL == 0) {
                log.info("[ FacilityGeocodingRunner ]: 진행 {}/{} - 성공 {}건, 실패 {}건, 주소 없음(skip) {}건",
                        processed, targets.size(), updated, failed, skipped);
            }
        }

        log.info("[ FacilityGeocodingRunner ]: 완료 - 성공 {}건, 실패 {}건, 주소 없음(skip) {}건", updated, failed, skipped);
    }

}
