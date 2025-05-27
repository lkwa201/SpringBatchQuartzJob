package redwine.me.springbatchquartzjob.define;

public enum JobStatus {
    // 등록/스케줄 단계
    REGISTERED,         // Job 등록만 완료 (스케줄링 전)
    SCHEDULED,          // 정상적으로 스케줄 등록됨
    EXISTS,             // 이미 존재하는 Job

    // 실행 후 단계
    COMPLETED,          // 정상 실행 완료됨
    FAILED_EXECUTION,   // 실행 중 예외 발생
    CANCELLED,          // 수동 또는 조건부로 취소됨

    // 에러
    FAILED              // 등록 실패 등 일반 에러
}
