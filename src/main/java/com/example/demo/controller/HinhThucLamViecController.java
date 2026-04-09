package com.example.demo.controller;

import java.util.List;

import org.springframework.data.domain.Page;
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
import com.example.demo.dto.hinhthuclamviec.HinhThucLamViecRequest;
import com.example.demo.dto.hinhthuclamviec.HinhThucLamViecResponse;
import com.example.demo.service.HinhThucLamViecService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/hinh-thuc-lam-viec")
@CrossOrigin(origins = "*")
public class HinhThucLamViecController {

    private final HinhThucLamViecService service;

    public HinhThucLamViecController(HinhThucLamViecService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<HinhThucLamViecResponse>> create(
            @Valid @RequestBody HinhThucLamViecRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.create(request), "Created"));
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<HinhThucLamViecResponse>> createAction(
            @Valid @RequestBody HinhThucLamViecRequest request) {
        return create(request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HinhThucLamViecResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(service.getById(id), "OK"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<HinhThucLamViecResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(service.getAll(), "OK"));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<HinhThucLamViecResponse>>> getAllAction() {
        return getAll();
    }

    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Page<HinhThucLamViecResponse>>> page(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(service.page(keyword, page, size), "Paged list"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<HinhThucLamViecResponse>>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(ApiResponse.success(service.search(keyword), "OK"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<HinhThucLamViecResponse>> update(@PathVariable Long id,
            @Valid @RequestBody HinhThucLamViecRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.update(id, request), "Updated"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<HinhThucLamViecResponse>> updateAction(@PathVariable Long id,
            @Valid @RequestBody HinhThucLamViecRequest request) {
        return update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted", "Deleted"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<String>> deleteAction(@PathVariable Long id) {
        return delete(id);
    }
}
