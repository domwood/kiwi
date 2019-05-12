package com.github.domwood.kiwi.api.rest.exception;

import com.github.domwood.kiwi.data.error.ApiError;
import com.github.domwood.kiwi.data.error.ImmutableApiError;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.common.errors.UnknownTopicOrPartitionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.PrintWriter;
import java.io.StringWriter;

@ControllerAdvice
public class KiwiApiExceptionHandler extends ResponseEntityExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(KiwiApiExceptionHandler.class);
    private final Integer MAX_DEPTH = 30;

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntime(Exception ex, WebRequest request){
        return handleExceptionInternal(ex, null,  new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Throwable rootCause =  ExceptionUtils.getRootCause(ex);
        if(rootCause == null) rootCause = ex;

        if(rootCause instanceof UnknownTopicOrPartitionException){
            status = HttpStatus.NOT_FOUND;
        }

        ApiError error = ImmutableApiError.builder()
                .error(ex.getClass().getName())
                .rootCause(rootCause.getClass().getName())
                .message(ex.getMessage() != null ? ex.getMessage() : ExceptionUtils.getRootCauseMessage(ex))
                .stackTrace(ExceptionUtils.getStackTrace(ex))
                .build();

        logger.warn("Failed to handle api call, body {} lead to error {}", body, error);

        return new ResponseEntity(error, headers, status);
    }

    private Throwable discoverCause(Throwable ex, int depth){
        if(depth >= MAX_DEPTH){
            return ex;
        }
        if(ex.getCause() == null){
            return ex;
        }
        else if(ex.getCause().getClass().getName().equals(ex.getClass().getName())){
            return ex;
        }
        else{
            return this.discoverCause(ex.getCause(), depth++);
        }
    }

    private String stackAsString(Exception ex) {

        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
