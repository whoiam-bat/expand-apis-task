package com.example.task.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class ExceptionResponse {

    private HttpStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;

    private Map<String, String> causes;


    public ExceptionResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ExceptionResponse(HttpStatus status) {
        this();
        this.status = status;
    }

    public ExceptionResponse(HttpStatus status, Map<String, String> causes) {
        this(status);
        this.causes = causes;
    }
}
