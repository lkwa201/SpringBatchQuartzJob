package redwine.me.springbatchquartzjob;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling

public class SpringBatchQuartzJobApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchQuartzJobApplication.class, args);
    }

}
