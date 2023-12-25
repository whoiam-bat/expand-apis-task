package com.example.task.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestRecord {

    private LocalDate entryDate;

    private String itemCode;

    private String itemName;

    private int itemQuantity;

    private String status;

}
