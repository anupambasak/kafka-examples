package cris.apos.kafkastreams.statestore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.kafka.common.TopicPartition;

import java.util.Set;

@Data
@AllArgsConstructor
public class StreamsHostInfoMetadata{
    private String host;
    private int port;
    private Set<String> stateStoreNames;
    private Set<TopicPartition> topicPartitions;
    private Set<TopicPartition> standbyTopicPartitions;
    private Set<String> standbyStateStoreNames;

}
