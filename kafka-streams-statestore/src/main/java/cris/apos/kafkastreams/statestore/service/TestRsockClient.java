package cris.apos.kafkastreams.statestore.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/client")
@Slf4j
public class TestRsockClient {

    @Autowired
    private RSocketRequester rSocketRequester;


    @GetMapping("/test")
    public String test(){
        SimpleRequest sm = SimpleRequest.newBuilder().setRequestMessage("anupam").build();
        Mono<SimpleResponse> mm = rSocketRequester
                .route("testmsg")
//                .data(sm)
                .retrieveMono(SimpleResponse.class);
        mm.subscribe(s -> {
            log.info("############## {}",s);
        });
        return "OK";
    }
}
