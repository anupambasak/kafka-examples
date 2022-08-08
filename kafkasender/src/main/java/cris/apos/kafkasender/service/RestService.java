package cris.apos.kafkasender.service;

import cris.apos.kafkasender.spring.config.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/service")
public class RestService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private AppConfig appConfig;

    @GetMapping("/post")
    public String post(){
        kafkaTemplate.send(appConfig.getSendTopic(),"test","A quick brown fox jumps over the lazy dog");
        return "OK";
    }
}
