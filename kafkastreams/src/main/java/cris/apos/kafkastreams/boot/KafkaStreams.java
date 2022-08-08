package cris.apos.kafkastreams.boot;

import cris.apos.kafkastreams.spring.config.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackageClasses = {AppConfig.class})
public class KafkaStreams {

    public static void main(String args[]) {
        SpringApplication.run(KafkaStreams.class);
    }
}
