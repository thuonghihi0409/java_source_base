package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.KhuVucRequest;
import com.example.demo.dto.KhuVucResponse;
import com.example.demo.entity.KhuVuc;
import com.example.demo.exception.ConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.KhuVucRepository;
import com.example.demo.repository.ViecLamRepository;

@Service
@Transactional
public class KhuVucService {

    @Autowired
    private KhuVucRepository khuVucRepository;

    @Autowired
    private ViecLamRepository viecLamRepository;

    public KhuVucResponse create(KhuVucRequest request) {
        if (khuVucRepository.existsByMaIgnoreCase(request.getMa())) {
            throw new ConflictException("KhuVuc with ma '" + request.getMa() + "' already exists");
        }
        if (khuVucRepository.existsByTenIgnoreCase(request.getTen())) {
            throw new ConflictException("KhuVuc with ten '" + request.getTen() + "' already exists");
        }

        KhuVuc khuVuc = new KhuVuc();
        khuVuc.setMa(request.getMa());
        khuVuc.setTen(request.getTen());
        khuVuc.setMoTa(request.getMoTa());
        khuVuc.setIsActive(request.getIsActive());

        return toResponse(khuVucRepository.save(khuVuc));
    }

    @Transactional(readOnly = true)
    public KhuVucResponse getById(Long id) {
        KhuVuc khuVuc = khuVucRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("KhuVuc", "id", id));
        return toResponse(khuVuc);
    }

    @Transactional(readOnly = true)
    public List<KhuVucResponse> getAll() {
        return khuVucRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public KhuVucResponse update(Long id, KhuVucRequest request) {
        KhuVuc khuVuc = khuVucRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("KhuVuc", "id", id));

        if (!khuVuc.getMa().equalsIgnoreCase(request.getMa())
                && khuVucRepository.existsByMaIgnoreCase(request.getMa())) {
            throw new ConflictException("KhuVuc with ma '" + request.getMa() + "' already exists");
        }
        if (!khuVuc.getTen().equalsIgnoreCase(request.getTen())
                && khuVucRepository.existsByTenIgnoreCase(request.getTen())) {
            throw new ConflictException("KhuVuc with ten '" + request.getTen() + "' already exists");
        }

        khuVuc.setMa(request.getMa());
        khuVuc.setTen(request.getTen());
        khuVuc.setMoTa(request.getMoTa());
        khuVuc.setIsActive(request.getIsActive());

        return toResponse(khuVucRepository.save(khuVuc));
    }

    public void delete(Long id) {
        KhuVuc khuVuc = khuVucRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("KhuVuc", "id", id));
        if (viecLamRepository.existsByKhuVucList_Id(id)) {
            throw new ConflictException("Khong the xoa khu vuc dang duoc gan vao viec lam");
        }
        khuVucRepository.delete(khuVuc);
    }

    @Transactional(readOnly = true)
    public List<KhuVucResponse> search(String keyword) {
        return khuVucRepository.findByTenContainingIgnoreCase(keyword).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<KhuVucResponse> page(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100),
                Sort.by(Sort.Direction.DESC, "createdAt"));
        if (keyword == null || keyword.isBlank()) {
            return khuVucRepository.findAll(pageable).map(this::toResponse);
        }
        return khuVucRepository.findByTenContainingIgnoreCase(keyword.trim(), pageable).map(this::toResponse);
    }

    private KhuVucResponse toResponse(KhuVuc khuVuc) {
        return new KhuVucResponse(
                khuVuc.getId(),
                khuVuc.getMa(),
                khuVuc.getTen(),
                khuVuc.getMoTa(),
                khuVuc.getIsActive(),
                khuVuc.getCreatedAt(),
                khuVuc.getUpdatedAt());
    }
}
