package cris.apos.kafkastreams.statestore.spring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.protobuf.ProtobufDecoder;
import org.springframework.http.codec.protobuf.ProtobufEncoder;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.web.util.pattern.PathPatternRouteMatcher;
import reactor.util.retry.Retry;

import java.time.Duration;

@Configuration
//@ComponentScan(basePackages =  "cris.apos.kafkastreams.statestore")
public class MetadataStoreAppConfig {

    @Bean
    public RSocketRequester getRSocketRequester(@Autowired RSocketStrategies rSocketStrategies){
        RSocketRequester.Builder builder = RSocketRequester.builder();
        return builder
                .rsocketStrategies(rSocketStrategies)
                .rsocketConnector(
                        rSocketConnector ->
                                rSocketConnector.reconnect(Retry.fixedDelay(2, Duration.ofSeconds(2)))
                )
                .tcp("localhost", 9898);
    }

    @Bean
    public RSocketMessageHandler rsocketMessageHandler() {
        RSocketMessageHandler handler = new RSocketMessageHandler();
        handler.setRSocketStrategies(rsocketStrategies());
        return handler;
    }

    @Bean
    public RSocketStrategies rsocketStrategies() {
        return RSocketStrategies.builder()
                .encoders(encoders -> encoders.add(new ProtobufEncoder()))
                .decoders(decoders -> decoders.add(new ProtobufDecoder()))
                .routeMatcher(new PathPatternRouteMatcher())
                .build();
    }
}
