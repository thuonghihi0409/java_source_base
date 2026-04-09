package com.example.demo.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.hinhthuclamviec.HinhThucLamViecRequest;
import com.example.demo.dto.hinhthuclamviec.HinhThucLamViecResponse;
import com.example.demo.entity.HinhThucLamViec;
import com.example.demo.exception.ConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.HinhThucLamViecRepository;

@Service
@Transactional
public class HinhThucLamViecService {

    private final HinhThucLamViecRepository repository;

    public HinhThucLamViecService(HinhThucLamViecRepository repository) {
        this.repository = repository;
    }

    public HinhThucLamViecResponse create(HinhThucLamViecRequest request) {
        validateUnique(null, request);
        HinhThucLamViec item = new HinhThucLamViec();
        apply(item, request);
        return toResponse(repository.save(item));
    }

    @Transactional(readOnly = true)
    public HinhThucLamViecResponse getById(Long id) {
        return toResponse(find(id));
    }

    @Transactional(readOnly = true)
    public List<HinhThucLamViecResponse> getAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<HinhThucLamViecResponse> search(String keyword) {
        return repository.findByTenContainingIgnoreCase(keyword).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Page<HinhThucLamViecResponse> page(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100),
                Sort.by(Sort.Direction.DESC, "createdAt"));
        if (keyword == null || keyword.isBlank()) {
            return repository.findAll(pageable).map(this::toResponse);
        }
        return repository.findByTenContainingIgnoreCase(keyword.trim(), pageable).map(this::toResponse);
    }

    public HinhThucLamViecResponse update(Long id, HinhThucLamViecRequest request) {
        HinhThucLamViec item = find(id);
        validateUnique(item, request);
        apply(item, request);
        return toResponse(repository.save(item));
    }

    public void delete(Long id) {
        repository.delete(find(id));
    }

    private HinhThucLamViec find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HinhThucLamViec", "id", id));
    }

    private void validateUnique(HinhThucLamViec current, HinhThucLamViecRequest request) {
        if (current == null || !current.getMa().equalsIgnoreCase(request.getMa())) {
            if (repository.existsByMaIgnoreCase(request.getMa())) {
                throw new ConflictException("Ma hinh thuc lam viec da ton tai");
            }
        }
        if (current == null || !current.getTen().equalsIgnoreCase(request.getTen())) {
            if (repository.existsByTenIgnoreCase(request.getTen())) {
                throw new ConflictException("Ten hinh thuc lam viec da ton tai");
            }
        }
    }

    private static void apply(HinhThucLamViec item, HinhThucLamViecRequest request) {
        item.setMa(request.getMa());
        item.setTen(request.getTen());
        item.setMoTa(request.getMoTa());
        item.setIsActive(request.getIsActive());
    }

    private HinhThucLamViecResponse toResponse(HinhThucLamViec item) {
        return new HinhThucLamViecResponse(item.getId(), item.getMa(), item.getTen(), item.getMoTa(),
                item.getIsActive(), item.getCreatedAt(), item.getUpdatedAt());
    }
}
