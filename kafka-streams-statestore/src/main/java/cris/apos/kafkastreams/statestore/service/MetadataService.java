package cris.apos.kafkastreams.statestore.service;

import cris.apos.kafkastreams.statestore.dto.StreamsHostInfoMetadata;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.KeyQueryMetadata;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.StreamsMetadata;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Controller
public class MetadataService {

    @Autowired
    private StreamsBuilderFactoryBean factoryBean;

    public List<StreamsHostInfoMetadata> metadataForAllStreamsClients() {
        final Collection<StreamsMetadata> metadata = factoryBean.getKafkaStreams().metadataForAllStreamsClients();
        return mapInstancesToStreamsHostInfoMetadata(metadata);
    }

    public List<StreamsHostInfoMetadata> streamsMetadataForStore(final String store) {
        final Collection<StreamsMetadata> metadata = factoryBean.getKafkaStreams().streamsMetadataForStore(store);
        return mapInstancesToStreamsHostInfoMetadata(metadata);
    }

    public <K> StreamsHostInfoMetadata streamsMetadataForStoreAndKey(final String store, final K key, final Serializer<K> serializer) {
        final KeyQueryMetadata metadata = factoryBean.getKafkaStreams().queryMetadataForKey(store, key, serializer);
        if (metadata == null) {
            throw new ResourceNotFoundException(String.format("key %s in store %s not found", key, store));
        }
        return new StreamsHostInfoMetadata(metadata.activeHost().host(),
                metadata.activeHost().port(),
                Collections.singleton(store),
                null,
                null,
                null);
    }

    public <K,V> Flux<K> getKeyRange(final String store, final K key) {
        ReadOnlyKeyValueStore<K, V> counts = factoryBean.getKafkaStreams()
                .store(StoreQueryParameters.fromNameAndType(store, QueryableStoreTypes.keyValueStore()));
        KeyValueIterator<K,V> k = counts.range(key, null);
        return Flux.fromStream(StreamSupport.stream(Spliterators.spliteratorUnknownSize(k, Spliterator.ORDERED),false)
                .map(kk -> kk.key));
    }

    private List<StreamsHostInfoMetadata> mapInstancesToStreamsHostInfoMetadata(final Collection<StreamsMetadata> metadatas) {
        return metadatas.stream().map(metadata -> new StreamsHostInfoMetadata(metadata.host(),
                        metadata.port(),
                        metadata.stateStoreNames(),
                        metadata.topicPartitions(),
                        metadata.standbyTopicPartitions(),
                        metadata.standbyStateStoreNames()))
                .collect(Collectors.toList());
    }

}
