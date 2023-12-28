package com.example.task.controller.exception;

import com.example.task.exception.DbException;
import com.example.task.exception.EntityNotFound;
import com.example.task.model.dto.ExceptionResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RestExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(EntityNotFound.class)
    protected ResponseEntity<Object> handleEntityNotFound(EntityNotFound ex) {
        ExceptionResponse response = new ExceptionResponse(HttpStatus.NOT_FOUND,
                Map.of(ex.getClass().getSimpleName(), ex.getLocalizedMessage()));

        return buildResponseEntity(response);
    }

    @ExceptionHandler(DbException.class)
    protected ResponseEntity<Object> handleDbException(EntityNotFound ex) {
        ExceptionResponse response = new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                Map.of(ex.getClass().getSimpleName(), ex.getLocalizedMessage()));

        return buildResponseEntity(response);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(HttpStatus.BAD_REQUEST,
                Map.of(ex.getClass().getSimpleName(), ex.getLocalizedMessage()));

        return buildResponseEntity(response);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                          HttpHeaders headers,
                                                                          HttpStatusCode status,
                                                                          WebRequest request) {
        String exceptionMessage = ex.getParameterName() + " request parameter is missing";

        Map<String, String> error = new HashMap<>();
        error.put(ex.getClass().getSimpleName(), exceptionMessage);

        ExceptionResponse response = new ExceptionResponse(HttpStatus.resolve(status.value()), error);

        return buildResponseEntity(response);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
                                                                     HttpHeaders headers,
                                                                     HttpStatusCode status,
                                                                     WebRequest request) {
        String exceptionMessage = String.format("",
                ex.getContentType(),
                " content type is not supported.\nSupported media types: ",
                ex.getSupportedMediaTypes()
        );

        Map<String, String> error = new HashMap<>();
        error.put(ex.getClass().getSimpleName(), exceptionMessage);

        ExceptionResponse response = new ExceptionResponse(HttpStatus.resolve(status.value()), error);

        return buildResponseEntity(response);
    }

    private ResponseEntity<Object> buildResponseEntity(ExceptionResponse exceptionResponse) {
        return new ResponseEntity<>(exceptionResponse, exceptionResponse.getStatus());
    }
}
