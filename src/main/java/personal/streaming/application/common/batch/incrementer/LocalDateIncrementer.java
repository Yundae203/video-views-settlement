package personal.streaming.application.common.batch.incrementer;


import net.bytebuddy.asm.Advice;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class LocalDateIncrementer implements JobParametersIncrementer {

    @Override
    public JobParameters getNext(JobParameters parameters) {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String yesterdayAsString = yesterday.format(DateTimeFormatter.ISO_LOCAL_DATE);

        return new JobParametersBuilder(parameters)
                .addString("today", yesterdayAsString)
                .toJobParameters();
    }
}
