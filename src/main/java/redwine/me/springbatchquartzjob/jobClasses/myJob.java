package redwine.me.springbatchquartzjob.jobClasses;

import org.quartz.Job;
import org.springframework.stereotype.Component;

@Component
public class myJob implements Job {

    @Override
    public void execute(org.quartz.JobExecutionContext context) {
        System.out.println("myJob is running");
    }
}
