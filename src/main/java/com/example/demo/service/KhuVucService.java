package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.KhuVucRequest;
import com.example.demo.dto.KhuVucResponse;
import com.example.demo.entity.KhuVuc;
import com.example.demo.exception.ConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.KhuVucRepository;

@Service
@Transactional
public class KhuVucService {

    @Autowired
    private KhuVucRepository khuVucRepository;

    public KhuVucResponse create(KhuVucRequest request) {
        if (khuVucRepository.existsByTenIgnoreCase(request.getTen())) {
            throw new ConflictException("KhuVuc with ten '" + request.getTen() + "' already exists");
        }

        KhuVuc khuVuc = new KhuVuc();
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

        if (!khuVuc.getTen().equalsIgnoreCase(request.getTen())
                && khuVucRepository.existsByTenIgnoreCase(request.getTen())) {
            throw new ConflictException("KhuVuc with ten '" + request.getTen() + "' already exists");
        }

        khuVuc.setTen(request.getTen());
        khuVuc.setMoTa(request.getMoTa());
        khuVuc.setIsActive(request.getIsActive());

        return toResponse(khuVucRepository.save(khuVuc));
    }

    public void delete(Long id) {
        KhuVuc khuVuc = khuVucRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("KhuVuc", "id", id));
        khuVucRepository.delete(khuVuc);
    }

    @Transactional(readOnly = true)
    public List<KhuVucResponse> search(String keyword) {
        return khuVucRepository.findByTenContainingIgnoreCase(keyword).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private KhuVucResponse toResponse(KhuVuc khuVuc) {
        return new KhuVucResponse(
                khuVuc.getId(),
                khuVuc.getTen(),
                khuVuc.getMoTa(),
                khuVuc.getIsActive(),
                khuVuc.getCreatedAt(),
                khuVuc.getUpdatedAt());
    }
}
