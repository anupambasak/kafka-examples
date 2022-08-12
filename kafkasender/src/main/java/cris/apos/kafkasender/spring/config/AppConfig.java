package cris.apos.kafkasender.spring.config;

import lombok.Getter;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.web.reactive.config.EnableWebFlux;

@Configuration
@EnableKafka
@EnableWebFlux
@ComponentScan(basePackages =  "cris.apos.kafkasender")
@Getter
public class AppConfig {

    @Value("${EVENT_TOPIC:teststreamstopic}")
    private String sendTopic;


    @Bean
    public NewTopic topic1() {
        return TopicBuilder.name(sendTopic)
                .partitions(10)
                .replicas(1)
                .build();
    }
}
