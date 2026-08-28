package com.example.shopingdiscount.config.handler;

import com.example.shopingdiscount.model.ErrorDetailRs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.shopingdiscount.config.handler.ErrorCodes.BE00001;
import static com.example.shopingdiscount.config.handler.ErrorCodes.RE00002;
import static com.example.shopingdiscount.config.handler.ErrorCodes.RE00003;
import static com.example.shopingdiscount.config.handler.ErrorCodes.SE00001;

/**
 * Central place that converts exceptions raised anywhere in the API into a consistent
 * {@link ErrorDetailRs} response shape, instead of leaking stack traces to clients.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleException(MethodArgumentNotValidException ex) {
        ErrorDetailRs errorDetailRs = new ErrorDetailRs();
        List<ErrorRs> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(fieldError -> new ErrorRs(RE00002.name(), fieldError.getDefaultMessage()))
                .collect(Collectors.toList());
        errorDetailRs.setErrors(errors);
        errorDetailRs.setTimestamp(new Timestamp(System.currentTimeMillis()));
        log.error("MethodArgumentNotValidException {}", errorDetailRs);
        return new ResponseEntity<>(errorDetailRs, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleException(HttpMessageNotReadableException ex) {
        log.error(RE00003.name() + " " + ex.getMessage(), ex);
        ErrorDetailRs errorDetailRs = new ErrorDetailRs();
        List<ErrorRs> errorRs = Collections.singletonList(new ErrorRs(RE00003.name(), RE00003.getMessage()));
        errorDetailRs.setErrors(errorRs);
        errorDetailRs.setTimestamp(new Timestamp(System.currentTimeMillis()));
        return new ResponseEntity<>(errorDetailRs, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({InvalidDiscountConfigurationException.class, IllegalArgumentException.class})
    public ResponseEntity<Object> handleException(RuntimeException ex) {
        ErrorDetailRs errorDetailRs = new ErrorDetailRs();
        List<ErrorRs> errorRs = Collections.singletonList(new ErrorRs(BE00001.name(), ex.getMessage()));
        errorDetailRs.setErrors(errorRs);
        errorDetailRs.setTimestamp(new Timestamp(System.currentTimeMillis()));
        log.error(BE00001.name() + " " + ex.getMessage(), ex);
        return new ResponseEntity<>(errorDetailRs, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetailRs> handleException(Exception ex) {
        log.error(SE00001.name() + " " + ex.getMessage(), ex);
        ErrorDetailRs errorDetailRs = new ErrorDetailRs();
        List<ErrorRs> errors = Collections.singletonList(new ErrorRs(SE00001.name(), SE00001.getMessage()));
        errorDetailRs.setErrors(errors);
        errorDetailRs.setTimestamp(new Timestamp(System.currentTimeMillis()));
        return new ResponseEntity<>(errorDetailRs, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
