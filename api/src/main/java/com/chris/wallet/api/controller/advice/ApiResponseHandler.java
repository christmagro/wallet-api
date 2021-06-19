package com.chris.wallet.api.controller.advice;

import com.chris.wallet.api.contract.CustomFieldError;
import com.chris.wallet.api.contract.ErrorDto;
import com.chris.wallet.api.contract.WalletApiResponse;
import com.chris.wallet.api.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

import static java.util.stream.Collectors.toList;


@Slf4j
@RequiredArgsConstructor
@ControllerAdvice(basePackages = "com.chris.wallet.api.controller")
class ApiResponseHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        List<CustomFieldError> fieldErrors = ex.getBindingResult()
                                               .getFieldErrors()
                                               .stream()
                                               .map(fieldError -> new CustomFieldError(
                        fieldError.getField(),
                        fieldError.getCode(),
                        fieldError.getRejectedValue())
                )
                                               .collect(toList());
        return new ResponseEntity<>(WalletApiResponse.builder().error(ErrorDto.builder().message(fieldErrors.toString()).code(100).build()).build(), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException() {
        return new ResponseEntity<>(WalletApiResponse.builder().error(ErrorDto.builder().message("Something went wrong").code(100).build()).build(), HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(ExchangeRateServiceUnavailableExceptions.class)
    public ResponseEntity<Object> handleExchangeRateServiceException(ExchangeRateServiceUnavailableExceptions ersu){
        return new ResponseEntity<>(WalletApiResponse.builder()
                                                     .error(ErrorDto.builder()
                                                                    .message(ersu.getErrorCause())
                                                                    .code(ersu.getReturnCode())
                                                                    .build())
                                                     .build(),
                                    HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(InvalidExchangeRateException.class)
    public ResponseEntity<Object> handleInvalidExchangeRateException(InvalidExchangeRateException ier){
        return new ResponseEntity<>(WalletApiResponse.builder()
                                                     .error(ErrorDto.builder()
                                                                    .message(ier.getErrorCause())
                                                                    .code(ier.getReturnCode())
                                                                    .build())
                                                     .build(),
                                    HttpStatus.NOT_ACCEPTABLE);
    }
    @ExceptionHandler(NotEnoughFundsException.class)
    public ResponseEntity<Object> handleNotEnoughFunds(NotEnoughFundsException nef){
        return new ResponseEntity<>(WalletApiResponse.builder()
                                                     .error(ErrorDto.builder()
                                                                    .message(nef.getErrorCause())
                                                                    .code(nef.getReturnCode())
                                                                    .build())
                                                     .build(),
                                    HttpStatus.PAYMENT_REQUIRED);
    }

    @ExceptionHandler(PlayerNotFoundException.class)
    public ResponseEntity<Object> handlePlayerNotFound(PlayerNotFoundException pnf){
        return new ResponseEntity<>(WalletApiResponse.builder()
                                                     .error(ErrorDto.builder()
                                                                    .message(pnf.getErrorCause())
                                                                    .code(pnf.getReturnCode())
                                                                    .build())
                                                     .build(),
                                    HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TransactionAlreadyExistsException.class)
    public ResponseEntity<Object> handleTransactionAlreadyExist(TransactionAlreadyExistsException tae){
        return new ResponseEntity<>(WalletApiResponse.builder()
                                                     .error(ErrorDto.builder()
                                                                    .message(tae.getErrorCause())
                                                                    .code(tae.getReturnCode())
                                                                    .build())
                                                     .build(),
                                    HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<Object> handlePlayerNotFound(UsernameAlreadyExistsException uae){
        return new ResponseEntity<>(WalletApiResponse.builder()
                                                     .error(ErrorDto.builder()
                                                                    .message(uae.getErrorCause())
                                                                    .code(uae.getReturnCode())
                                                                    .build())
                                                     .build(),
                                    HttpStatus.BAD_REQUEST);
    }
}


