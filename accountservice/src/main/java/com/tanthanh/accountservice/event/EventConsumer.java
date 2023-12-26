package com.tanthanh.accountservice.event;

import com.google.gson.Gson;
import com.tanthanh.accountservice.model.AccountDTO;
import com.tanthanh.accountservice.service.AccountService;
import com.tanthanh.commonservice.common.CommonException;
import com.tanthanh.commonservice.model.PaymentDTO;
import com.tanthanh.commonservice.model.ProfileDTO;
import com.tanthanh.commonservice.utils.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;

import java.util.Collections;
import java.util.Objects;

@Service
@Slf4j
public class EventConsumer {
    Gson gson = new Gson();
    @Autowired
    AccountService accountService;

    @Autowired
    EventProducer eventProducer;

    public EventConsumer(ReceiverOptions<String,String> receiverOptions){
        KafkaReceiver.create(receiverOptions.subscription(Collections.singleton(Constant.PROFILE_ONBOARDING_TOPIC)))
                .receive().subscribe(this::profileOnboarding);
        KafkaReceiver.create(receiverOptions.subscription(Collections.singleton(Constant.PAYMENT_REQUEST_TOPIC)))
                .receive().subscribe(this::paymentRequest);
        KafkaReceiver.create(receiverOptions.subscription(Collections.singleton(Constant.PAYMENT_COMPLETED_TOPIC)))
                .receive().subscribe(this::paymentComplete);
    }
    public void profileOnboarding(ReceiverRecord<String,String> receiverRecord){
        log.info("Profile Onboarding event");
        ProfileDTO dto = gson.fromJson(receiverRecord.value(),ProfileDTO.class);
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setEmail(dto.getEmail());
        accountDTO.setReserved(0);
        accountDTO.setBalance(dto.getInitialBalance());
        accountDTO.setCurrency("USD");
        accountService.createNewAccount(accountDTO).subscribe(res ->{
            dto.setStatus(Constant.STATUS_PROFILE_ACTIVE);
            eventProducer.send(Constant.PROFILE_ONBOARDED_TOPIC,gson.toJson(dto)).subscribe();
        });
    }
    public void paymentRequest(ReceiverRecord<String,String> receiverRecord){
        PaymentDTO paymentDTO = gson.fromJson(receiverRecord.value(),PaymentDTO.class);
        accountService.bookAmount(paymentDTO.getAmount(),paymentDTO.getAccountId()).subscribe(result ->{
            if(result){
                paymentDTO.setStatus(Constant.STATUS_PAYMENT_PROCESSING);
                eventProducer.send(Constant.PAYMENT_CREATED_TOPIC,gson.toJson(paymentDTO)).subscribe();
            }
            else{
                throw new CommonException("A02","Balance not enough", HttpStatus.BAD_REQUEST);
            }
        });
    }
    public void paymentComplete(ReceiverRecord <String,String> receiverRecord){
        log.info("Payment Complete event");
        PaymentDTO paymentDTO = gson.fromJson(receiverRecord.value(),PaymentDTO.class);
        if(Objects.equals(paymentDTO.getStatus(),Constant.STATUS_PAYMENT_SUCCESSFUL)){
            accountService.subtract(paymentDTO.getAmount(),paymentDTO.getAccountId()).subscribe();
        }else{
            accountService.rollbackReserved(paymentDTO.getAmount(), paymentDTO.getAccountId()).subscribe();
        }
    }
}
