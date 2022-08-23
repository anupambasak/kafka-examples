package cris.apos.kafkastreams.statestore.service;

import cris.apos.kafkastreams.protobuf.WordCountStateMetadataReq;
import cris.apos.kafkastreams.protobuf.WordCountStateReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/client")
@Slf4j
public class TestRsockClient {

    @Autowired
    private RSocketRequester.Builder builder;

    @Autowired
    WordCountStateStoreInteractiveQueries wordCountStateStoreInteractiveQueries;

    @Value("${INTERACTIVE_QUERY_HOST:localhost}")
    private String interactiveQueryHost;


    @GetMapping("/test/{word}")
    public String test(@PathVariable String word){
        log.info("inside test");
        WordCountStateMetadataReq w = WordCountStateMetadataReq.newBuilder().setKeyRange(word)
                .setStore("count")
                .build();
        builder.tcp(interactiveQueryHost,9898)
                .route("keyList")
                .data(w)
                .retrieveFlux(String.class)
                .flatMap(s -> {
                    return wordCountStateStoreInteractiveQueries.getCount(WordCountStateReq.newBuilder()
                            .setKey(s)
                            .setStoreName("count")
                            .build()).flux();
                }).subscribe(ss -> {
                    log.info("key:{} count:{}",ss.getKey(),ss.getCount());
                });

//        wordCountStateStoreInteractiveQueries.getKeyRangForStore(w);
        return "OK";
    }
}
