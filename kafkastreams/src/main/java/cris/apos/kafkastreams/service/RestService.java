package cris.apos.kafkastreams.service;

import cris.apos.kafkastreams.spring.config.AppConfig;
import cris.apos.kafkastreams.statestore.service.MetadataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/service")
public class RestService {

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private StreamsBuilderFactoryBean factoryBean;

    @Autowired
    private MetadataService metadataService;

    @GetMapping("/count/{word}")
    public String get(@PathVariable String word){
        KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();
        ReadOnlyKeyValueStore<String, Long> counts = kafkaStreams
                .store(StoreQueryParameters.fromNameAndType("count", QueryableStoreTypes.keyValueStore()));

        log.info("{}",counts.get(word));

        KeyValueIterator<String, Long> range = counts.range("a","z");
        while (range.hasNext()) {
            KeyValue<String, Long> next = range.next();
            log.info("count for #" + next.key + ": " + next.value);
        }
        // close the iterator to release resources
        range.close();

        // Get the values for all of the keys available in this application instance
        range = counts.all();
        while (range.hasNext()) {
            KeyValue<String, Long> next = range.next();
            System.out.println("count for ##" + next.key + ": " + next.value);
        }
        // close the iterator to release resources
        range.close();

        return "OK";
    }

    @GetMapping("/metadata")
    public String getMetadata(){
//        KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();
//
//        Collection<StreamsMetadata> collection = kafkaStreams.streamsMetadataForStore("count");
//        log.info("collection:::{}",collection);
//        for(StreamsMetadata sm: collection) {
//            log.info("Metadata::::{}",sm);
//        }
//
//        KeyQueryMetadata kqm = kafkaStreams.queryMetadataForKey("count","fox", new StringSerializer());
//
//        log.info("{}",kqm);

//        log.info("{}",metadataService.metadataForAllStreamsClients());

        return "OK";
    }
}
