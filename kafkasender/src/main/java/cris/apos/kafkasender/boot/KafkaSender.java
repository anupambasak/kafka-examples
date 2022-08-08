package cris.apos.kafkasender.boot;

import cris.apos.kafkasender.spring.config.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackageClasses = {AppConfig.class})
public class KafkaSender {

    public static void main(String args[]) {
        SpringApplication.run(KafkaSender.class);
    }
}
