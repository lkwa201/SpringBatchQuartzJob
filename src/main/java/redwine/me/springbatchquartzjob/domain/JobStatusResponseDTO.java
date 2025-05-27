package redwine.me.springbatchquartzjob.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import redwine.me.springbatchquartzjob.define.JobStatus;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JobStatusResponseDTO {

    private String jobId;
    private String jobName;
    private String jobGroup;
    private String triggerName;
    private String triggerGroup;
    private Date nextFireTime;
    private Date previousFireTime;
    //    private String status; // 예: REGISTERED
    private JobStatus status;
    private String message; // 선택: 오류 메시지, 상태 설명 등
}
