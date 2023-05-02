package com.tanthanh.commonservice.advide;

import com.tanthanh.commonservice.common.CommonException;
import com.tanthanh.commonservice.common.ErrorMessage;
import com.tanthanh.commonservice.common.ValidateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
@Slf4j
public class ExceptionAdvide {
    @ExceptionHandler
    public ResponseEntity<ErrorMessage> handleException(Exception ex){
        log.error("Unknown internal server error: "+ex.getMessage());
        log.error("Exception class: "+ex.getClass());
        log.error("Exception cause: "+ex.getCause());
        return new ResponseEntity(new ErrorMessage("9999", ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler
    public ResponseEntity<ErrorMessage> handleCommonException(CommonException ex){
        log.error(String.format("Common error: %s %s %s", ex.getCode(), ex.getStatus(),ex.getMessage()));
        return new ResponseEntity(new ErrorMessage(ex.getCode(), ex.getMessage(),ex.getStatus()),ex.getStatus());
    }
    @ExceptionHandler
    public ResponseEntity<Map<String,String>> handleValidateException(ValidateException ex){
        return new ResponseEntity(ex.getMessageMap(),ex.getStatus());
    }
}
