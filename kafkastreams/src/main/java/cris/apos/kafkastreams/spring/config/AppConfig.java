package cris.apos.kafkastreams.spring.config;

import lombok.Getter;
import static org.apache.kafka.streams.StreamsConfig.*;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBeanConfigurer;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.web.reactive.config.EnableWebFlux;


import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@EnableKafkaStreams
@EnableWebFlux
@ComponentScan(basePackages =  "cris.apos.kafkastreams")
@Getter
@Slf4j
public class AppConfig {

    @Value("${EVENT_TOPIC:teststreamstopic}")
    private String sendTopic;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value("${MY_POD_IP:localhost}")
    private String applicationIp;


    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    KafkaStreamsConfiguration kStreamsConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(APPLICATION_ID_CONFIG, "kafkastreams");
        props.put(APPLICATION_SERVER_CONFIG, applicationIp+":8080");
        props.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        return new KafkaStreamsConfiguration(props);
    }

    @Bean
    public StreamsBuilderFactoryBeanConfigurer configurer() {
        return fb -> fb.setStateListener((newState, oldState) -> {
            log.info("State transition from " + oldState + " to " + newState);
        });
    }

    @Bean
    public NewTopic topic1() {
        return TopicBuilder.name(sendTopic)
                .partitions(10)
                .replicas(1)
                .build();
    }


}
