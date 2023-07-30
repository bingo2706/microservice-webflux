package com.tanthanh.profileservice;

import com.tanthanh.profileservice.event.EventProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(SpringExtension.class)
class EventProducerTest {
    @InjectMocks
    private EventProducer eventProducer;

    @Mock
    private KafkaSender<String,String> sender;

    @BeforeEach
    void setUp(){
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        SenderOptions<String, String> senderOptions = SenderOptions.create(props);
        sender = KafkaSender.create(senderOptions);
        ReflectionTestUtils.setField(eventProducer, "sender", sender);
    }
    @Test
    void send(){
        eventProducer.send("test","test").doOnNext(it -> Assertions.assertEquals("ok",it))
                .doOnSuccess(Assertions::assertNotNull)
                .doOnError(Assertions::assertNotNull)
                .subscribe();
    }
}
