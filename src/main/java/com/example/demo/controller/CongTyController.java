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
import com.example.demo.dto.congty.CongTyRequest;
import com.example.demo.dto.congty.CongTyResponse;
import com.example.demo.service.CongTyService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cong-ty")
@CrossOrigin(origins = "*")
public class CongTyController {

    private final CongTyService congTyService;

    public CongTyController(CongTyService congTyService) {
        this.congTyService = congTyService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CongTyResponse>> create(@Valid @RequestBody CongTyRequest request) {
        return ResponseEntity.ok(ApiResponse.success(congTyService.create(request), "CongTy created"));
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CongTyResponse>> createAction(@Valid @RequestBody CongTyRequest request) {
        return create(request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CongTyResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(congTyService.getById(id), "OK"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CongTyResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(congTyService.getAll(), "OK"));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<CongTyResponse>>> getAllAction() {
        return getAll();
    }

    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Page<CongTyResponse>>> page(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(congTyService.page(keyword, page, size), "CongTy paged list"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CongTyResponse>>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(ApiResponse.success(congTyService.search(keyword), "OK"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CongTyResponse>> update(@PathVariable Long id,
            @Valid @RequestBody CongTyRequest request) {
        return ResponseEntity.ok(ApiResponse.success(congTyService.update(id, request), "CongTy updated"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<CongTyResponse>> updateAction(@PathVariable Long id,
            @Valid @RequestBody CongTyRequest request) {
        return update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        congTyService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("CongTy deleted", "CongTy deleted"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<String>> deleteAction(@PathVariable Long id) {
        return delete(id);
    }
}
