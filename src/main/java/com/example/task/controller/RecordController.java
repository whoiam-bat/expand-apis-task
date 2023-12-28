package com.example.task.controller;

import com.example.task.model.dto.Request;
import com.example.task.model.dto.RequestRecord;
import com.example.task.service.RecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;

    @PostMapping("/add")
    public ResponseEntity<String> addRecords(@RequestBody Request request) {
        recordService.addRecords(request);

        return ResponseEntity.ok("Successfully added!");
    }

    @GetMapping("/all")
    public ResponseEntity<List<RequestRecord>> findAll(@RequestBody Request request) {
        return ResponseEntity.ok(recordService.findAll());
    }
}
