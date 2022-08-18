package cris.apos.kafkastreams.statestore.service;

import cris.apos.kafkastreams.protobuf.SimpleRequest;
import cris.apos.kafkastreams.protobuf.SimpleResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
@Slf4j
public class TestRSockService {


    @MessageMapping("testmsg")
//    public Mono<SimpleResponse> hello(SimpleRequest req) {
    public Mono<SimpleResponse> hello() {

//        log.info("@@@@@@@@@@@@@@@@@@@ {}",req);
        String t = "Hello " ;//+ req.getRequestMessage();
        SimpleResponse sm = SimpleResponse.newBuilder().setResponseMessage(t).build();
        return Mono.just(sm);
    }
}
