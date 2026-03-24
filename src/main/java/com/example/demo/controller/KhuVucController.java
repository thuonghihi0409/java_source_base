package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.KhuVucRequest;
import com.example.demo.dto.KhuVucResponse;
import com.example.demo.service.KhuVucService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/khu-vuc")
@CrossOrigin(origins = "*")
public class KhuVucController {

    @Autowired
    private KhuVucService khuVucService;

    @PostMapping
    public ResponseEntity<ApiResponse<KhuVucResponse>> create(@Valid @RequestBody KhuVucRequest request) {
        KhuVucResponse response = khuVucService.create(request);
        return ResponseEntity.ok(ApiResponse.success(response, "KhuVuc created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<KhuVucResponse>> getById(@PathVariable Long id) {
        KhuVucResponse response = khuVucService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "KhuVuc retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<KhuVucResponse>>> getAll() {
        List<KhuVucResponse> responses = khuVucService.getAll();
        return ResponseEntity.ok(ApiResponse.success(responses, "KhuVuc list retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<KhuVucResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody KhuVucRequest request) {
        KhuVucResponse response = khuVucService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "KhuVuc updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        khuVucService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("KhuVuc deleted successfully", "KhuVuc deleted successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<KhuVucResponse>>> search(@RequestParam String keyword) {
        List<KhuVucResponse> responses = khuVucService.search(keyword);
        return ResponseEntity.ok(ApiResponse.success(responses, "Search results retrieved successfully"));
    }
}
