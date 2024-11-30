package personal.streaming.application.common.batch.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import personal.streaming.application.common.batch.chunk.processor.DailyStatisticsProcessor;
import personal.streaming.application.common.batch.chunk.reader.LoggedContentIdReader;
import personal.streaming.application.common.batch.chunk.writer.DailyStatisticsBatchWriter;
import personal.streaming.application.common.batch.incrementer.LocalDateIncrementer;
import personal.streaming.application.common.batch.listener.DailyStatisticsStepListener;
import personal.streaming.application.common.batch.dto.ContentDailyStatisticsDto;
import personal.streaming.application.common.batch.partitioner.LoggedContentPartitioner;
import personal.streaming.content_post_watch_history.domain.DailyWatchedContentLog;

@Configuration
@RequiredArgsConstructor
public class ContentStatisticsJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final LocalDateIncrementer localDateIncrementer;

    private final LoggedContentPartitioner loggedContentPartitioner;
    private final DailyStatisticsStepListener dailyStatisticsStepListener;

    private final LoggedContentIdReader loggedContentIdReader;
    private final DailyStatisticsProcessor dailyStatisticsProcessor;
    private final DailyStatisticsBatchWriter dailyStatisticsBatchWriter;

    @Value("${spring.chunk.size}")
    private int chunkSize;
    @Value("${spring.pool.size}")
    private int poolSize;

    @Bean
    public Job job() {
        return new JobBuilder("content-statistics-job", jobRepository)
                .start(dailyStatisticisMasterStep())
                .incrementer(localDateIncrementer)
                .build();
    }

    @Bean
    public Step dailyStatisticisMasterStep() {
        return new StepBuilder("daily-statistics-master", jobRepository)
                .partitioner("daily-statistics-partitioner", loggedContentPartitioner)
                .partitionHandler(dailyPartitionHandler())
                .build();
    }

    @Bean
    public TaskExecutorPartitionHandler dailyPartitionHandler() {
        TaskExecutorPartitionHandler partitionHandler = new TaskExecutorPartitionHandler();
        partitionHandler.setStep(dailyStatisticsStep());
        partitionHandler.setTaskExecutor(executor());
        partitionHandler.setGridSize(poolSize);
        return partitionHandler;
    }

    @Bean
    public TaskExecutor executor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolSize);
        executor.setMaxPoolSize(poolSize);
        executor.setThreadNamePrefix("statistics-thread-");
        executor.setWaitForTasksToCompleteOnShutdown(true); // 어플리케이션 종료 시 작업 완료 대기 (ExecutorConfigurationSupport)
        executor.initialize();
        return executor;
    }

    @Bean
    public Step dailyStatisticsStep() {
        return new StepBuilder("daily-statistics-step", jobRepository)
                .<DailyWatchedContentLog, ContentDailyStatisticsDto>chunk(chunkSize, transactionManager)
                .reader(loggedContentIdReader)
                .processor(dailyStatisticsProcessor)
                .writer(dailyStatisticsBatchWriter)
                .build();
    }

    @Bean
    public Step dailyStatisticsSingleStep() {
        return new StepBuilder("daily-statistics-single-thread-step", jobRepository)
                .listener(dailyStatisticsStepListener)
                .<DailyWatchedContentLog, ContentDailyStatisticsDto>chunk(chunkSize, transactionManager)
                .reader(loggedContentIdReader)
                .processor(dailyStatisticsProcessor)
                .writer(dailyStatisticsBatchWriter)
                .build();
    }
}
