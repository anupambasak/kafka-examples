package cris.apos.kafkastreams.statestore.service;

import cris.apos.kafkastreams.statestore.dto.StreamsHostInfoMetadata;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.KeyQueryMetadata;
import org.apache.kafka.streams.StreamsMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MetadataService {

    @Autowired
    private StreamsBuilderFactoryBean factoryBean;

    public List<StreamsHostInfoMetadata> metadataForAllStreamsClients() {
        final Collection<StreamsMetadata> metadata = factoryBean.getKafkaStreams().metadataForAllStreamsClients();
        return mapInstancesToHostStoreInfo(metadata);
    }

    public List<StreamsHostInfoMetadata> streamsMetadataForStore(final String store) {
        final Collection<StreamsMetadata> metadata = factoryBean.getKafkaStreams().streamsMetadataForStore(store);
        return mapInstancesToHostStoreInfo(metadata);
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

    private List<StreamsHostInfoMetadata> mapInstancesToHostStoreInfo(final Collection<StreamsMetadata> metadatas) {
        return metadatas.stream().map(metadata -> new StreamsHostInfoMetadata(metadata.host(),
                        metadata.port(),
                        metadata.stateStoreNames(),
                        metadata.topicPartitions(),
                        metadata.standbyTopicPartitions(),
                        metadata.standbyStateStoreNames()))
                .collect(Collectors.toList());
    }

}
