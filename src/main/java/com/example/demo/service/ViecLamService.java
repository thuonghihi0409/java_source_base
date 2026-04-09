package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.vieclam.ViecLamRequest;
import com.example.demo.dto.vieclam.ViecLamResponse;
import com.example.demo.dto.KhuVucSimpleDto;
import com.example.demo.dto.congty.CongTySimpleDto;
import com.example.demo.dto.hinhthuclamviec.HinhThucLamViecSimpleDto;
import com.example.demo.dto.nguoidung.HrSimpleDto;
import com.example.demo.entity.CongTy;
import com.example.demo.entity.HinhThucLamViec;
import com.example.demo.entity.KhuVuc;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.entity.ViecLam;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.UnauthorizedException;
import com.example.demo.repository.CongTyRepository;
import com.example.demo.repository.HinhThucLamViecRepository;
import com.example.demo.repository.KhuVucRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.ViecLamRepository;

@Service
@Transactional
public class ViecLamService {

    private final ViecLamRepository viecLamRepository;
    private final CongTyRepository congTyRepository;
    private final HinhThucLamViecRepository hinhThucLamViecRepository;
    private final KhuVucRepository khuVucRepository;
    private final UserRepository userRepository;

    public ViecLamService(ViecLamRepository viecLamRepository, CongTyRepository congTyRepository,
            HinhThucLamViecRepository hinhThucLamViecRepository, KhuVucRepository khuVucRepository,
            UserRepository userRepository) {
        this.viecLamRepository = viecLamRepository;
        this.congTyRepository = congTyRepository;
        this.hinhThucLamViecRepository = hinhThucLamViecRepository;
        this.khuVucRepository = khuVucRepository;
        this.userRepository = userRepository;
    }

    public ViecLamResponse create(ViecLamRequest request) {
        ViecLam v = new ViecLam();
        apply(v, request);
        return toResponse(viecLamRepository.save(v));
    }

    public ViecLamResponse update(Long id, ViecLamRequest request) {
        ViecLam v = find(id);
        apply(v, request);
        return toResponse(viecLamRepository.save(v));
    }

    public void delete(Long id) {
        viecLamRepository.delete(find(id));
    }

    @Transactional(readOnly = true)
    public ViecLamResponse getById(Long id) {
        return toResponse(find(id));
    }

    @Transactional(readOnly = true)
    public List<ViecLamResponse> getAll() {
        return viecLamRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ViecLamResponse> search(String keyword, Long congTyId, Long hinhThucLamViecId, Long khuVucId,
            Boolean onlyActive) {
        return viecLamRepository.search(keyword, congTyId, hinhThucLamViecId, khuVucId, onlyActive).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ViecLamResponse> byHr(Long hrUserId) {
        return viecLamRepository.findByHrUser_Id(hrUserId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Page<ViecLamResponse> page(String keyword, Long congTyId, Long hinhThucLamViecId, Long khuVucId,
            Boolean onlyActive, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100),
                Sort.by(Sort.Direction.DESC, "createdAt"));
        return viecLamRepository.searchPage(keyword, congTyId, hinhThucLamViecId, khuVucId, onlyActive, pageable)
                .map(this::toResponse);
    }

    private ViecLam find(Long id) {
        return viecLamRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("ViecLam", "id", id));
    }

    private void apply(ViecLam v, ViecLamRequest request) {
        if (request.getMucLuongMin() != null && request.getMucLuongMax() != null
                && request.getMucLuongMin().compareTo(request.getMucLuongMax()) > 0) {
            throw new BadRequestException("mucLuongMin khong duoc lon hon mucLuongMax");
        }
        CongTy congTy = congTyRepository.findById(request.getCongTyId())
                .orElseThrow(() -> new ResourceNotFoundException("CongTy", "id", request.getCongTyId()));
        HinhThucLamViec hinhThucLamViec = hinhThucLamViecRepository.findById(request.getHinhThucLamViecId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("HinhThucLamViec", "id", request.getHinhThucLamViecId()));

        User actor = currentUser();
        User hrUser = resolveHrUser(actor, request.getHrUserId());

        List<KhuVuc> khuVucList = new ArrayList<>(khuVucRepository.findAllById(request.getKhuVucIds()));
        if (khuVucList.size() != request.getKhuVucIds().size()) {
            throw new BadRequestException("Danh sach khuVucIds co gia tri khong ton tai");
        }

        v.setTieuDe(request.getTieuDe());
        v.setMoTa(request.getMoTa());
        v.setMucLuongMin(request.getMucLuongMin());
        v.setMucLuongMax(request.getMucLuongMax());
        v.setCapDoKinhNghiem(request.getCapDoKinhNghiem());
        v.setHanNop(request.getHanNop());
        v.setIsActive(request.getIsActive());
        v.setCongTy(congTy);
        v.setHinhThucLamViec(hinhThucLamViec);
        v.setHrUser(hrUser);
        v.setKhuVucList(khuVucList);
    }

    private User resolveHrUser(User actor, Long hrUserId) {
        if (actor.getRole() == Role.ROLE_HR) {
            if (hrUserId != null && !hrUserId.equals(actor.getId())) {
                throw new UnauthorizedException("HR chi duoc dang tin bang chinh tai khoan cua minh");
            }
            return actor;
        }
        if (actor.getRole() == Role.ROLE_ADMIN) {
            if (hrUserId == null) {
                throw new BadRequestException("Admin can truyen hrUserId khi tao/cap nhat viec lam");
            }
            User hr = userRepository.findById(hrUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", hrUserId));
            if (hr.getRole() != Role.ROLE_HR) {
                throw new BadRequestException("hrUserId phai la tai khoan ROLE_HR");
            }
            return hr;
        }
        throw new UnauthorizedException("Chi ROLE_HR hoac ROLE_ADMIN duoc thao tac viec lam");
    }

    private User currentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.User u) {
            return userRepository.findByEmail(u.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "email", u.getUsername()));
        }
        throw new UnauthorizedException("Chua dang nhap");
    }

    private ViecLamResponse toResponse(ViecLam v) {
        List<KhuVucSimpleDto> khuVucList = v.getKhuVucList().stream()
                .map(kv -> new KhuVucSimpleDto(kv.getId(), kv.getMa(), kv.getTen()))
                .toList();
        return new ViecLamResponse(
                v.getId(), v.getTieuDe(), v.getMoTa(), v.getMucLuongMin(), v.getMucLuongMax(),
                v.getCapDoKinhNghiem(), v.getHanNop(), v.getIsActive(),
                new CongTySimpleDto(v.getCongTy().getId(), v.getCongTy().getTen(), v.getCongTy().getLogoUrl()),
                new HinhThucLamViecSimpleDto(v.getHinhThucLamViec().getId(), v.getHinhThucLamViec().getMa(),
                        v.getHinhThucLamViec().getTen()),
                new HrSimpleDto(v.getHrUser().getId(), v.getHrUser().getEmail(), v.getHrUser().getFullName()),
                khuVucList, v.getCreatedAt(), v.getUpdatedAt());
    }
}
