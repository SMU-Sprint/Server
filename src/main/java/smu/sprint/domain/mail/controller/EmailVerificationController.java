package smu.sprint.domain.mail.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import smu.sprint.domain.mail.dto.EmailVerificationRequest;
import smu.sprint.domain.mail.service.EmailVerificationService;
import smu.sprint.global.response.CustomResponse;

@Tag(name = "Mail", description = "이메일 인증 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mail")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @Operation(
            summary = "회원가입 이메일 인증 코드 발급",
            description = "입력한 이메일로 인증 코드를 발송합니다. 이미 가입된 이메일이면 발급되지 않습니다. " +
                    "코드는 10분간 유효하며, 재발급은 60초에 한 번만 가능합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 코드 발송 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값 검증 실패"),
            @ApiResponse(responseCode = "409", description = "이미 가입된 이메일"),
            @ApiResponse(responseCode = "429", description = "인증 코드 재요청이 너무 잦음")
    })
    @PostMapping("/verification")
    public CustomResponse<Void> issueCode(@Valid @RequestBody EmailVerificationRequest request) {
        emailVerificationService.issueCode(request);
        return CustomResponse.onSuccess(null);
    }

}
