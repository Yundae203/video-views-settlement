package personal.streaming.application.common.scheduler.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class BatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job job;

    @Scheduled(cron = "0 0 11 * * *")
    public void runJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

        LocalDate yesterday = LocalDate.now().minusDays(1);
        String yesterdayAsString = yesterday.format(DateTimeFormatter.ISO_LOCAL_DATE);

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("today", yesterdayAsString)
                .toJobParameters();

        jobLauncher.run(job, jobParameters);
    }
}
