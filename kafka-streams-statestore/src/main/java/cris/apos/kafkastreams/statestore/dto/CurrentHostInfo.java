package cris.apos.kafkastreams.statestore.dto;

import lombok.Data;

@Data
public class CurrentHostInfo {

    private String host;

    private int port;

}
