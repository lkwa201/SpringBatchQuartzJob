### 개발 목표 요약
내가 만든 MyJob.class를
→ Quartz에 등록 (등록 시 파라미터 전달 가능)
→ 나중에 해당 Job을 Quartz에서 삭제

### 개발된 EndPoint List
GET http://localhost:8080/quartz/jobs
→ Job 목록 조회

GET http://localhost:8080/quartz/jobs/state
→ 등록된 모든 잡 상태 조회

GET http://localhost:8080/quartz/jobs/{{jobGroup}}/{{jobName}}
→ 등록한 잡 상세정보 조회

DELETE http://localhost:8080/quartz/jobs/{{jobGroup}}/{{jobName}}
→ 등록한 잡 삭제

POST http://localhost:8080/quartz/jobs/{{jobGroup}}/{{jobName}}/trigger
→ 등록한 잡 수동 실행

POST http://localhost:8080/quartz/register
→ Job 등록

```json
//등록 샘플 Json 형식
{
  "jobId": "myJob",
  "jobClassName": "redwine.me.springbatchquartzjob.jobClasses.myJob",
  "jobGroupName": "myGroup",
  "cron": "* 10 12 * * ?"
}
```

### 참조 사이트

 - https://bigdown.tistory.com/608?category=1308859
 - https://anianidindin.tistory.com/m/22
 - https://homoefficio.github.io/2018/08/12/Java-Quartz-Scheduler-Job-Chaining-%EA%B5%AC%ED%98%84
 - https://velog.io/@hyunsoo730/Spring-Quartz-%EC%82%AC%EC%9A%A9
