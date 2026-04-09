package com.example.demo.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.congty.CongTyRequest;
import com.example.demo.dto.congty.CongTyResponse;
import com.example.demo.entity.CongTy;
import com.example.demo.exception.ConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.CongTyRepository;
import com.example.demo.repository.ViecLamRepository;

@Service
@Transactional
public class CongTyService {

    private final CongTyRepository congTyRepository;
    private final ViecLamRepository viecLamRepository;

    public CongTyService(CongTyRepository congTyRepository, ViecLamRepository viecLamRepository) {
        this.congTyRepository = congTyRepository;
        this.viecLamRepository = viecLamRepository;
    }

    public CongTyResponse create(CongTyRequest request) {
        if (congTyRepository.existsByTenIgnoreCase(request.getTen())) {
            throw new ConflictException("CongTy voi ten '" + request.getTen() + "' da ton tai");
        }
        CongTy c = new CongTy();
        apply(c, request);
        return toResponse(congTyRepository.save(c));
    }

    @Transactional(readOnly = true)
    public CongTyResponse getById(Long id) {
        return toResponse(find(id));
    }

    @Transactional(readOnly = true)
    public List<CongTyResponse> getAll() {
        return congTyRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<CongTyResponse> search(String keyword) {
        return congTyRepository.findByTenContainingIgnoreCase(keyword).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Page<CongTyResponse> page(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100),
                Sort.by(Sort.Direction.DESC, "createdAt"));
        if (keyword == null || keyword.isBlank()) {
            return congTyRepository.findAll(pageable).map(this::toResponse);
        }
        return congTyRepository.findByTenContainingIgnoreCase(keyword.trim(), pageable).map(this::toResponse);
    }

    public CongTyResponse update(Long id, CongTyRequest request) {
        CongTy c = find(id);
        if (!c.getTen().equalsIgnoreCase(request.getTen())
                && congTyRepository.existsByTenIgnoreCase(request.getTen())) {
            throw new ConflictException("CongTy voi ten '" + request.getTen() + "' da ton tai");
        }
        apply(c, request);
        return toResponse(congTyRepository.save(c));
    }

    public void delete(Long id) {
        CongTy c = find(id);
        if (viecLamRepository.existsByCongTy_Id(id)) {
            throw new ConflictException("Khong the xoa cong ty dang co viec lam");
        }
        congTyRepository.delete(c);
    }

    private CongTy find(Long id) {
        return congTyRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("CongTy", "id", id));
    }

    private static void apply(CongTy c, CongTyRequest request) {
        c.setTen(request.getTen());
        c.setMoTa(request.getMoTa());
        c.setWebsite(request.getWebsite());
        c.setDiaChi(request.getDiaChi());
        c.setLogoUrl(request.getLogoUrl());
        c.setIsActive(request.getIsActive());
    }

    private CongTyResponse toResponse(CongTy c) {
        return new CongTyResponse(c.getId(), c.getTen(), c.getMoTa(), c.getWebsite(), c.getDiaChi(), c.getLogoUrl(),
                c.getIsActive(),
                c.getCreatedAt(), c.getUpdatedAt());
    }
}
