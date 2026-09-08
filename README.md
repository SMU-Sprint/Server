## 🛠 Tech Stack

### Backend

| 분류 | 기술 | Version |
| --- | --- | --- |
| Language | Java | 17 |
| Framework | Spring Boot | 4.1.1 |
| Web | Spring Web MVC | Spring Boot Managed |
| ORM | Spring Data JPA | Spring Boot Managed |
| Database | MySQL | Spring Boot Managed |
| Security | Spring Security | Spring Boot Managed |
| Validation | Spring Validation | Spring Boot Managed |
| HTTP Client | Spring RestClient | Spring Boot Managed |
| JWT | JJWT | 0.12.6 |
| API Documentation | SpringDoc OpenAPI / Swagger UI | 3.0.0 |
| Utility | Lombok | Spring Boot Managed |

---

## 📋 Commit Convention

커밋 메시지는 다음 형식을 사용합니다.

```text
[emoji] [type]: [description]
```

### Commit Types

| Type | Emoji | 의미 | 예시 |
| --- | --- | --- | --- |
| `init` | 🎉 | 프로젝트 초기 설정 | `🎉 init: 프로젝트 초기 설정` |
| `feat` | ✨ | 새로운 기능 추가 | `✨ feat: 로그인 API 구현` |
| `fix` | 🐛 | 버그 수정 | `🐛 fix: 비밀번호 검증 오류 수정` |
| `refactor` | ♻️ | 동작 변화 없는 코드 구조 개선 | `♻️ refactor: MemberService 책임 분리` |
| `chore` | 🔧 | 설정, 의존성, 빌드 등 변경 | `🔧 chore: Spring Security 의존성 추가` |
| `docs` | 📝 | 문서 추가 및 수정 | `📝 docs: API 명세 추가` |
| `test` | ✅ | 테스트 코드 추가 및 수정 | `✅ test: 회원가입 서비스 테스트 추가` |
| `style` | 🎨 | 코드 포맷팅, import 정리 등 | `🎨 style: 코드 포맷 정리` |
| `rename` | ✏️ | 파일 또는 패키지명 변경 | `✏️ rename: member 패키지 구조 변경` |
| `remove` | 🗑️ | 불필요한 코드 또는 파일 삭제 | `🗑️ remove: 사용하지 않는 설정 파일 삭제` |

### Commit Rules

- 커밋 메시지는 작업 내용을 명확하게 표현합니다.
- 하나의 커밋에는 하나의 논리적인 변경사항을 포함하는 것을 권장합니다.
- `type`은 소문자로 작성합니다.
- 기능 추가와 코드 구조 개선을 구분하여 `feat`와 `refactor`를 사용합니다.
- 의존성, 빌드 및 환경설정 변경은 `chore`를 사용합니다.
- 단순 코드 포맷팅이나 import 정리는 `style`을 사용합니다.