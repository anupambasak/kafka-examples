package cris.apos.kafkastreams.statestore.service;

import cris.apos.kafkastreams.protobuf.WordCountStateMetadataReq;
import cris.apos.kafkastreams.protobuf.WordCountStateReq;
import cris.apos.kafkastreams.protobuf.WordCountStateRes;
import cris.apos.kafkastreams.statestore.dto.CurrentHostInfo;
import cris.apos.kafkastreams.statestore.dto.StreamsHostInfoMetadata;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

@Controller
@Slf4j
public class WordCountStateStoreInteractiveQueries {

    @Autowired
    private MetadataService metadataService;

    @Autowired
    private CurrentHostInfo currentHostInfo;

    @Autowired
    private StreamsBuilderFactoryBean factoryBean;

    @Autowired
    private RSocketRequester.Builder rSBuilder;

    @MessageMapping("wordCount")
    public Mono<WordCountStateRes> getCount(WordCountStateReq wordCountStateReq) {
        StreamsHostInfoMetadata shim = metadataService.streamsMetadataForStoreAndKey(wordCountStateReq.getStoreName(),wordCountStateReq.getKey(),
                new StringSerializer());

        WordCountStateRes.Builder sm = WordCountStateRes.newBuilder();

        if(isCurrentHost(shim)) {
            final ReadOnlyKeyValueStore<String, Long> store =
                    factoryBean.getKafkaStreams().
                            store(StoreQueryParameters.fromNameAndType(wordCountStateReq.getStoreName(),
                                    QueryableStoreTypes.keyValueStore()));
            if (store == null) {
                throw new ResourceNotFoundException("store not found");
            }
            Long count = store.get(wordCountStateReq.getKey());
            return Mono.just(sm.setCount(count)
                    .setKey(wordCountStateReq.getKey())
                    .build());
        } else {
            return fetchFromRemoteByKey(shim, wordCountStateReq);
        }
    }

    @MessageMapping("keyList")
    public Flux<String> getKeyRangForStore(WordCountStateMetadataReq wordCountStateMetadataReq) {
        Collection<StreamsHostInfoMetadata> collection =  metadataService.streamsMetadataForStore(wordCountStateMetadataReq.getStore());
        return Flux.fromStream(collection.stream())
                .flatMap(s -> {
                    if(isCurrentHost(s))
                        return metadataService.getKeyRange(wordCountStateMetadataReq.getStore(), wordCountStateMetadataReq.getKeyRange());
                    else {
                        return rSBuilder.tcp(s.getHost(),s.getPort())
                        .route("keyListRemote")
                        .data(wordCountStateMetadataReq)
                        .retrieveFlux(String.class);
                    }
                });
    }

    @MessageMapping("keyListRemote")
    public Flux<String> getRemoteKeyRangForStore(WordCountStateMetadataReq wordCountStateMetadataReq) {
        return metadataService.getKeyRange(wordCountStateMetadataReq.getStore(), wordCountStateMetadataReq.getKeyRange());
    }

    private boolean isCurrentHost(final StreamsHostInfoMetadata host) {
        return host.getHost().equals(currentHostInfo.getHost()) &&
                host.getPort() == currentHostInfo.getPort();
    }

    private Mono<WordCountStateRes> fetchFromRemoteByKey(StreamsHostInfoMetadata streamsHostInfoMetadata,
                                                         WordCountStateReq wordCountStateReq) {
        Mono<WordCountStateRes> mm = rSBuilder.tcp(streamsHostInfoMetadata.getHost(),streamsHostInfoMetadata.getPort())
                .route("wordCount")
                .data(wordCountStateReq)
                .retrieveMono(WordCountStateRes.class);
        return mm;
    }
}
