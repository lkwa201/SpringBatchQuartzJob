package redwine.me.springbatchquartzjob.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class JobRegisterRequestDTO {
    private String jobId;
    private String jobClassName; // 동적으로 등록할 클래스 이름
    private String jobGroupName; // 동적으로 Job 그룹 명
    private String cron; // 스케줄링 방식 표현) <초> <분> <시> <일> <월> <요일> <년> ->  * 10 12 * * ?

    private Map<String, Object> parameters;
}
