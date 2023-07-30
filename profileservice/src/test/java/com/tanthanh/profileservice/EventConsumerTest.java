package com.tanthanh.profileservice;

import com.google.gson.Gson;
import com.tanthanh.commonservice.utils.Constant;
import com.tanthanh.profileservice.event.EventConsumer;
import com.tanthanh.profileservice.model.ProfileDTO;
import com.tanthanh.profileservice.service.ProfileService;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class EventConsumerTest {
    @InjectMocks
    private EventConsumer eventConsumer;

    @Mock
    private ProfileService profileService;

    @Mock
    ReceiverRecord<String,String> receiverRecord;

    @Mock
    ReceiverOptions<String, String> receiverOptions;

    Gson gson = new Gson();
    private ProfileDTO profileDTO;
    @BeforeEach
    void setUp(){
        Map<String, Object> propsReceiver = new HashMap<>();
        propsReceiver.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        propsReceiver.put(ConsumerConfig.GROUP_ID_CONFIG, "sample-group");
        propsReceiver.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        propsReceiver.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        receiverOptions = ReceiverOptions.create(propsReceiver);
        profileDTO = new ProfileDTO(1,"test@gmail.com", Constant.STATUS_PROFILE_ACTIVE,200,"name","ADMIN");
        ReflectionTestUtils.setField(eventConsumer, "profileService", profileService);
    }
    @Test
    void profileOnboarded(){
        when(receiverRecord.value()).thenReturn(gson.toJson(profileDTO));
        when(profileService.updateStatusProfile(profileDTO)).thenReturn(Mono.just(profileDTO));
        eventConsumer.profileOnboarded(receiverRecord);
        verify(profileService,times(1)).updateStatusProfile(profileDTO);
    }
}
