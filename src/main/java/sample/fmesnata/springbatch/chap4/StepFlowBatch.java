package sample.fmesnata.springbatch.chap4;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StepFlowBatch {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Tasklet passTask() {
        return ((contribution, chunkContext) -> {
            System.out.println("Pass");
            return RepeatStatus.FINISHED;
        });
    }

    @Bean
    public Tasklet errorTask() {
        return ((contribution, chunkContext) -> {
            System.out.println("Error");
            throw new RuntimeException("error");
        });
    }

    @Bean
    public Tasklet successTask() {
        return ((contribution, chunkContext) -> {
            System.out.println("Success");
            return RepeatStatus.FINISHED;
        });
    }

    @Bean
    public Tasklet failTask() {
        return ((contribution, chunkContext) -> {
            System.out.println("Failure");
            return RepeatStatus.FINISHED;
        });
    }

    @Bean
    public Step firstStep() {
        return stepBuilderFactory.get("firstStep")
                .tasklet(passTask())
                .build();
    }

    @Bean
    public Step firstStepError() {
        return stepBuilderFactory.get("firstStepError")
                .tasklet(errorTask())
                .build();
    }

    @Bean
    public Step successStep() {
        return stepBuilderFactory.get("successStep")
                .tasklet(successTask())
                .build();
    }

    @Bean
    public Step failStep() {
        return stepBuilderFactory.get("failStep")
                .tasklet(failTask())
                .build();
    }

    @Bean
    public Job job() {
        return jobBuilderFactory.get("conditionalJob")
                .start(firstStep())
                .on("FAILED").to(failStep())
                .from(firstStep()).on("*").to(successStep())
                .end()
                .build();
    }

//    @Bean
//    public Job job(RandomDecider decider) {
//        return jobBuilderFactory.get("conditionalJob")
//                .start(firstStep())
//                .next(decider)
//                .from(decider)
//                .on("FAILED").to(failStep())
//                .from(decider)
//                .on("*").to(successStep())
//                .end()
//                .build();
//    }
}
