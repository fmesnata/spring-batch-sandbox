package sample.fmesnata.springbatch.chap4;

import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
@Profile("ignore")
public class SimpleChunkBatch {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public ListItemReader<String> strings() {
        List<String> nums = List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
        return new ListItemReader<>(nums);
    }

    @Bean
    public ItemProcessor<String, String> processor() {
        return item -> {
            System.out.println("processing " + item);
            return "processed " + item;
        };
    }

    @Bean
    public ItemWriter<String> stringWriter() {
        return items -> {
            for (String i : items) {
                System.out.println(i);
            }
            System.out.println("fin du writer");
        };
    }

    @Bean
    public Step chunkStep() {
        return stepBuilderFactory.get("step")
                .<String, String>chunk(new SimpleCompletionPolicy(4))
                .reader(strings())
                .processor(processor())
                .writer(stringWriter())
                .listener(stepListener())
                .listener(chunkListener())
                .build();
    }

    @Bean
    public Job chunkJob() {
        return jobBuilderFactory.get("chunk")
                .start(chunkStep()).build();
    }

    @Bean
    public StepExecutionListener stepListener() {
        return new StepExecutionListener() {
            @Override
            public void beforeStep(StepExecution stepExecution) {
                System.out.println("Step start");
            }

            @Override
            public ExitStatus afterStep(StepExecution stepExecution) {
                System.out.println("Step end");
                return stepExecution.getExitStatus();
            }
        };
    }

    @Bean
    public ChunkListener chunkListener() {
        return new ChunkListener() {
            @Override
            public void beforeChunk(ChunkContext context) {
                System.out.println("Chunk start");
            }

            @Override
            public void afterChunk(ChunkContext context) {
                System.out.println("Chunk end");
            }

            @Override
            public void afterChunkError(ChunkContext context) {
                System.out.println("Chunk error");
            }
        };
    }

}
