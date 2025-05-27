package redwine.me.springbatchquartzjob;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Service;
import redwine.me.springbatchquartzjob.define.JobStatus;
import redwine.me.springbatchquartzjob.domain.JobRegisterRequestDTO;
import redwine.me.springbatchquartzjob.domain.JobStatusResponseDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class QuartzJobManagerService {

    private static final String JOB_GROUP = "Jobs";
    private static final String TRIGGER_GROUP = "Triggers";

    private final Scheduler scheduler;

    /**
     * (등록된) 모든 Job 상태 조회
     * @return
     * @throws SchedulerException
     */
    public List<Map<String, Object>> getAllJobsState() throws SchedulerException {
        List<Map<String, Object>> result = new ArrayList<>();

        for (String group : scheduler.getJobGroupNames()) {
            for (JobKey jobKeys : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(group))) {
                List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKeys);
                Trigger.TriggerState state = triggers.isEmpty()
                        ? Trigger.TriggerState.NONE
                        : scheduler.getTriggerState(triggers.get(0).getKey());
                
                Map<String, Object> jobInfo = new HashMap<>();
                jobInfo.put("jobName", jobKeys.getName());
                jobInfo.put("jobGroup", jobKeys.getGroup());
                jobInfo.put("state", state.name());
                
                if(!triggers.isEmpty()) {
                    jobInfo.put("nextFireTime", triggers.get(0).getNextFireTime());
                } else {
                    jobInfo.put("jobName", "");
                    jobInfo.put("jobGroup", "");
                    jobInfo.put("state", "");
                }

                result.add(jobInfo);
            }
        }
        return result;
    }

    /**
     * Job 상세정보 조회
     * @param jobGroup
     * @param jobName
     * @return
     * @throws SchedulerException
     */
    public Map<String, Object> getJobDetail(String jobGroup, String jobName) throws SchedulerException {
        JobKey jobKey = new JobKey(jobName, jobGroup);
        if(!scheduler.checkExists(jobKey)) {
            throw new SchedulerException("Job not found: " + jobName);
        }

        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);

        Map<String, Object> detail = new HashMap<>();
        detail.put("jobName", jobKey.getName());
        detail.put("jobClass", jobDetail.getJobClass().getName());

        List<Map<String, Object>> triggerInfo = new ArrayList<>();
        for(Trigger trigger : triggers) {
            Map<String, Object> t = new HashMap<>();
            t.put("triggerKey", trigger.getKey().toString());
            t.put("nextFireTime", trigger.getNextFireTime());
            t.put("previousFireTime", trigger.getPreviousFireTime());
            triggerInfo.add(t);
        }

        detail.put("triggers", triggerInfo);
        return detail;
    }

    /**
     * 등록된 Job Delete
     * @param jobGroup
     * @param jobName
     * @return
     * @throws SchedulerException
     */
    public boolean deleteJob(String jobGroup, String jobName) throws SchedulerException {
        System.out.println("jobName = " + jobName);
        JobKey jobKey = new JobKey(jobGroup, jobName);
        System.out.println("jobKey.getName() = " + jobKey.getName());

        if(scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 등록된 잡을 수동 트리거로 실행.
     * @param jobGroup
     * @param jobName
     * @throws SchedulerException
     */
    public void triggerJobExecute(String jobGroup, String jobName) throws SchedulerException {
        JobKey jobKey = new JobKey(jobGroup, jobName);
        if(scheduler.checkExists(jobKey)) {
            scheduler.triggerJob(jobKey);
        } else {
            throw new SchedulerException("Job not found: " + jobKey);
        }
    }

    /**
     * 등록된 모든 job 목록 조회
     * @return
     * @throws SchedulerException
     */
    public List<Map<String, Object>> getAllJobList() throws SchedulerException {
        List<Map<String, Object>> jobList = new ArrayList<>();
        for(String group : scheduler.getJobGroupNames()) {
            for(JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(group))) {
                Map<String, Object> jobMap = new HashMap<>();
                jobMap.put("jobGroup", jobKey.getGroup());
                jobMap.put("jobName", jobKey.getName());
                jobList.add(jobMap);
            }
        }
        return jobList;
    }


    /**
     * 잡 등록
     * @param request
     * @return
     * @throws Exception
     */
    public JobStatusResponseDTO registerJob(JobRegisterRequestDTO request) throws Exception {
        Class<? extends Job> jobClass = (Class<? extends Job>) Class.forName(request.getJobClassName());
        String jobGroupName = request.getJobGroupName() + JOB_GROUP;
        String triggerGroupName = request.getJobGroupName() + TRIGGER_GROUP;

        JobKey jobKey = new JobKey(request.getJobId(), jobGroupName);
        TriggerKey triggerKey = new TriggerKey("trigger_"+request.getJobId(), triggerGroupName);

        try {
            if(scheduler.checkExists(jobKey)) {
                return new JobStatusResponseDTO(
                        request.getJobId(),
                        jobKey.getName(),
                        jobKey.getGroup(),
                        triggerKey.getName(),
                        triggerKey.getName(),
                        null,
                        null,
                        JobStatus.EXISTS,
                        "이미 등록된 Job 입니다."
                );
            }

            JobDataMap dataMap = new JobDataMap();
            dataMap.put("JobId", request.getJobId());
            dataMap.put("retryCount", 1);
            dataMap.put("maxRetry", 3);
            dataMap.put("retryIntervalSec", 60);

            JobDetail jobDetail = JobBuilder.newJob(jobClass)
                    .withIdentity(jobKey)
                    .usingJobData(dataMap)
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("trigger_" + request.getJobId(), jobGroupName)
                    .withSchedule(CronScheduleBuilder.cronSchedule(request.getCron()))
                    .forJob(jobDetail)
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);

            return new JobStatusResponseDTO(
                    request.getJobId(),
                    jobKey.getName(),
                    jobKey.getGroup(),
                    triggerKey.getName(),
                    triggerKey.getGroup(),
                    trigger.getNextFireTime(),
                    trigger.getPreviousFireTime(),
                    JobStatus.SCHEDULED,
                    "Job이 정상적으로 등록되어 스케줄링되었습니다."
            );


        } catch (Exception e) {
            return new JobStatusResponseDTO(
                    request.getJobId(),
                    jobKey.getName(),
                    jobKey.getGroup(),
                    triggerKey.getName(),
                    triggerKey.getGroup(),
                    null,
                    null,
                    JobStatus.FAILED,
                    "잡 등록 실패: " + e.getMessage()
            );
        }

    }
}
