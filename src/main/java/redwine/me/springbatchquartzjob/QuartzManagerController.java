package redwine.me.springbatchquartzjob;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redwine.me.springbatchquartzjob.domain.JobRegisterRequestDTO;
import redwine.me.springbatchquartzjob.domain.JobStatusResponseDTO;

import java.util.List;
import java.util.Map;

@Slf4j
@RequestMapping("/quartz")
@RequiredArgsConstructor
@RestController
public class QuartzManagerController {

    private Job job;
    private final QuartzJobManagerService service;

    /**
     * 등록된 모든 잡 상태 조회
     * @return
     * @throws SchedulerException
     */
    @GetMapping("/jobs/state")
    public ResponseEntity<List<Map<String, Object>>> getAllJobsState() throws SchedulerException {
        return ResponseEntity.ok(service.getAllJobsState());
    }


    /**
     * 등록된 잡 삭제
     * @param jobGroup
     * @param jobName
     * @return
     * @throws SchedulerException
     */
    @DeleteMapping("/jobs/{jobGroup}/{jobName}")
    public String deleteJob(@PathVariable("jobGroup")  String jobGroup,
                            @PathVariable("jobName") String jobName) throws SchedulerException {
        if(service.deleteJob(jobName, jobGroup)) {
            return "Job deleted: " + jobName;
        } else {
            return "Job not found or already deleted: " + jobName;   
        }
    }


    /**
     * 잡 상세정보 조회
     * @param jobGroup
     * @param jobName
     * @return
     * @throws SchedulerException
     */
    @GetMapping("/jobs/{jobGroup}/{jobName}")
    public Map<String, Object> getJobDetail(@PathVariable("jobGroup")  String jobGroup, 
                                            @PathVariable("jobName") String jobName) throws SchedulerException {
        return service.getJobDetail(jobGroup, jobName);
    }

    /**
     * 수동 실행
     * @param jobGroup
     * @param jobName
     * @return
     */
    @PostMapping("/jobs/{jobGroup}/{jobName}/trigger")
    public ResponseEntity<String> triggerJob(@PathVariable String jobGroup,
                                             @PathVariable String jobName) {
        try {
            service.triggerJobExecute(jobName, jobGroup);
            return ResponseEntity.ok("Job 실행 완료: " + jobName);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Job 실행 실패: " + e.getMessage());
        }
    }

    /**
     * Job 목록 조회
     * @return
     * @throws SchedulerException
     */
    @GetMapping("/jobs")
    public List<Map<String, Object>> getAllJobList() throws SchedulerException {
        return service.getAllJobList();
    }


    /**
     * Job 등록
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/register")
    public ResponseEntity<JobStatusResponseDTO> registerJob(@RequestBody JobRegisterRequestDTO request) throws Exception {
        JobStatusResponseDTO response = service.registerJob(request);
        return ResponseEntity.ok(response);
    }

}
