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
import com.example.demo.dto.vieclam.ViecLamRequest;
import com.example.demo.dto.vieclam.ViecLamResponse;
import com.example.demo.service.ViecLamService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/viec-lam")
@CrossOrigin(origins = "*")
public class ViecLamController {

    private final ViecLamService viecLamService;

    public ViecLamController(ViecLamService viecLamService) {
        this.viecLamService = viecLamService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ViecLamResponse>> create(@Valid @RequestBody ViecLamRequest request) {
        return ResponseEntity.ok(ApiResponse.success(viecLamService.create(request), "ViecLam created"));
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ViecLamResponse>> createAction(@Valid @RequestBody ViecLamRequest request) {
        return create(request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ViecLamResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(viecLamService.getById(id), "OK"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ViecLamResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(viecLamService.getAll(), "OK"));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<ViecLamResponse>>> getAllAction() {
        return getAll();
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ViecLamResponse>>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long congTyId,
            @RequestParam(required = false) Long hinhThucLamViecId,
            @RequestParam(required = false) Long khuVucId,
            @RequestParam(required = false) Boolean onlyActive) {
        return ResponseEntity.ok(ApiResponse.success(
                viecLamService.search(keyword, congTyId, hinhThucLamViecId, khuVucId, onlyActive), "OK"));
    }

    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Page<ViecLamResponse>>> page(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long congTyId,
            @RequestParam(required = false) Long hinhThucLamViecId,
            @RequestParam(required = false) Long khuVucId,
            @RequestParam(required = false) Boolean onlyActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                viecLamService.page(keyword, congTyId, hinhThucLamViecId, khuVucId, onlyActive, page, size),
                "ViecLam paged list"));
    }

    @GetMapping("/hr/{hrUserId}")
    public ResponseEntity<ApiResponse<List<ViecLamResponse>>> byHr(@PathVariable Long hrUserId) {
        return ResponseEntity.ok(ApiResponse.success(viecLamService.byHr(hrUserId), "OK"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ViecLamResponse>> update(@PathVariable Long id,
            @Valid @RequestBody ViecLamRequest request) {
        return ResponseEntity.ok(ApiResponse.success(viecLamService.update(id, request), "ViecLam updated"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<ViecLamResponse>> updateAction(@PathVariable Long id,
            @Valid @RequestBody ViecLamRequest request) {
        return update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        viecLamService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("ViecLam deleted", "ViecLam deleted"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<String>> deleteAction(@PathVariable Long id) {
        return delete(id);
    }
}
