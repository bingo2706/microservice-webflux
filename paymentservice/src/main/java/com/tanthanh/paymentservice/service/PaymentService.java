package com.tanthanh.paymentservice.service;

import com.tanthanh.commonservice.common.CommonException;
import com.tanthanh.commonservice.model.AccountDTO;
import com.tanthanh.commonservice.utils.Constant;
import com.tanthanh.paymentservice.model.PaymentDTO;
import com.tanthanh.paymentservice.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class PaymentService {
    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    WebClient webClientAccount;


    public Flux<PaymentDTO> getAllPayment(String id){
        return paymentRepository.findByAccountId(id)
                .map(PaymentDTO::entityToDto)
                .switchIfEmpty(Mono.error(new CommonException("P02", "Account don't have payment", HttpStatus.NOT_FOUND)));
    }
    public Mono<PaymentDTO> makePayment(PaymentDTO paymentDTO){
        return webClientAccount.get()
                .uri("/checkBalance/"+paymentDTO.getAccountId())
                .retrieve()
                .bodyToMono(AccountDTO.class)
                .flatMap(accountDTO -> {
                    if(paymentDTO.getAmount() <= accountDTO.getBalance()){
                        paymentDTO.setStatus(Constant.STATUS_PAYMENT_CREATING);
                    }else{
                        throw new CommonException("P01", "Balance not enough", HttpStatus.BAD_REQUEST);
                    }
                    return createNewPayment(paymentDTO);
                });
    }
    public Mono<PaymentDTO> createNewPayment(PaymentDTO paymentDTO){
        return Mono.just(paymentDTO)
                .map(PaymentDTO::dtoToEntity)
                .flatMap(payment -> paymentRepository.save(payment))
                .map(PaymentDTO::entityToDto)
                .doOnError(throwable -> log.error(throwable.getMessage()));
    }
}
