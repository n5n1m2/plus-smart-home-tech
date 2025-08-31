package analyzer;

import analyzer.service.HubEventProcessor;
import analyzer.service.SnapshotProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AnalyzerApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(AnalyzerApplication.class, args);
        final HubEventProcessor processor = context.getBean(HubEventProcessor.class);
        final SnapshotProcessor snapshotProcessor = context.getBean(SnapshotProcessor.class);

        Thread hubEventProcessorThread = new Thread(processor);
        hubEventProcessorThread.start();

        Thread snapshotProcessorThread = new Thread(snapshotProcessor);
        snapshotProcessorThread.start();
    }
}
