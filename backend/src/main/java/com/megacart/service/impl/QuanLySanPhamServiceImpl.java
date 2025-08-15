package com.megacart.service.impl;

import com.megacart.dto.request.ThemSanPhamRequest;
import com.megacart.dto.response.AnhMinhHoaResponse;
import com.megacart.dto.request.CapNhatSanPhamRequest;
import com.megacart.dto.response.ChiTietSanPhamQuanLyResponse;
import com.megacart.dto.response.PagedResponse;
import com.megacart.dto.response.SanPhamQuanLyResponse;
import com.megacart.enumeration.TrangThaiSanPham;
import com.megacart.exception.ResourceNotFoundException;
import com.megacart.model.AnhMinhHoa;
import com.megacart.model.DanhMuc;
import com.megacart.model.Kho;
import com.megacart.model.SanPham;
import com.megacart.repository.AnhMinhHoaRepository;
import com.megacart.repository.DanhMucRepository;
import com.megacart.repository.KhoRepository;
import com.megacart.repository.SanPhamRepository;
import com.megacart.service.DanhMucService;
import com.megacart.service.FileStorageService;
import com.megacart.service.QuanLySanPhamService;
import com.megacart.utils.ImageUtils;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuanLySanPhamServiceImpl implements QuanLySanPhamService {

    private final SanPhamRepository sanPhamRepository;
    private final DanhMucService danhMucService;
    private final DanhMucRepository danhMucRepository;
    private final FileStorageService fileStorageService;
    private final KhoRepository khoRepository;
    private final AnhMinhHoaRepository anhMinhHoaRepository;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<SanPhamQuanLyResponse> getDSSanPham(String tuKhoa, Integer maDanhMuc, TrangThaiSanPham trangThai, Pageable pageable) {

        Specification<SanPham> spec = (root, query, cb) -> {
            // Để tránh N+1 queries, chúng ta sử dụng fetch joins.
            // Điều này đặc biệt quan trọng đối với các kết quả được phân trang.
            if (Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("anhMinhHoas", JoinType.LEFT);
                root.fetch("danhMuc", JoinType.LEFT).fetch("danhMucCha", JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();

            // 1. Lọc theo từ khóa (tên hoặc mã sản phẩm)
            if (StringUtils.hasText(tuKhoa)) {
                Predicate tenPredicate = cb.like(cb.lower(root.get("tenSanPham")), "%" + tuKhoa.toLowerCase() + "%");
                Predicate maPredicate = cb.equal(root.get("maSanPham"), tuKhoa.matches("\\d+") ? Integer.parseInt(tuKhoa) : -1);
                predicates.add(cb.or(tenPredicate, maPredicate));
            }

            // 2. Lọc theo danh mục (bao gồm cả các danh mục con)
            if (maDanhMuc != null) {
                List<Integer> categoryIdsToSearch = danhMucService.getAllSubCategoryIds(maDanhMuc);
                if (categoryIdsToSearch != null && !categoryIdsToSearch.isEmpty()) {
                    predicates.add(root.get("danhMuc").get("maDanhMuc").in(categoryIdsToSearch));
                }
            }

            // 3. Lọc theo trạng thái
            if (trangThai != null) {
                predicates.add(cb.equal(root.get("trangThai"), trangThai));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<SanPham> sanPhamPage = sanPhamRepository.findAll(spec, pageable);

        List<SanPhamQuanLyResponse> responses = sanPhamPage.getContent().stream()
                .map(this::mapToSanPhamQuanLyResponse)
                .collect(Collectors.toList());

        return new PagedResponse<>(responses, sanPhamPage.getNumber(), sanPhamPage.getSize(), sanPhamPage.getTotalElements(), sanPhamPage.getTotalPages(), null);
    }

    @Override
    @Transactional
    public SanPhamQuanLyResponse themSanPham(ThemSanPhamRequest request, List<MultipartFile> files) {
        // 1. Validate input
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("Cần ít nhất một ảnh minh họa.");
        }
        if (request.getAnhChinhIndex() >= files.size()) {
            throw new IllegalArgumentException("Chỉ số ảnh chính không hợp lệ.");
        }

        // 2. Kiểm tra danh mục có tồn tại và hợp lệ không (phải là danh mục con)
        DanhMuc danhMuc = danhMucRepository.findById(request.getMaDanhMuc())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với mã: " + request.getMaDanhMuc()));
        if (danhMuc.getDanhMucCha() == null) {
            throw new IllegalArgumentException("Không thể thêm sản phẩm vào danh mục cha. Vui lòng chọn một danh mục con.");
        }

        // 3. Tạo và lưu đối tượng SanPham trước để có ID
        SanPham sanPhamMoi = SanPham.builder()
                .tenSanPham(request.getTenSanPham())
                .danhMuc(danhMuc)
                .moTa(request.getMoTa())
                .nhaSanXuat(request.getNhaSanXuat())
                .donGia(request.getDonGia())
                .donVi(request.getDonVi())
                .nhan(request.getNhan())
                .ghiChu(request.getGhiChu())
                .trangThai(request.getTrangThai() != null ? request.getTrangThai() : TrangThaiSanPham.BAN) // Mặc định là Bán
                .banChay(false) // Mặc định sản phẩm mới không phải là bán chạy
                .build();
        SanPham savedSanPham = sanPhamRepository.save(sanPhamMoi);

        // 4. Lưu ảnh và tạo đối tượng AnhMinhHoa
        List<AnhMinhHoa> anhMinhHoas = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            // Tạo thư mục con cho mỗi sản phẩm để dễ quản lý
            String subDir = "sanpham/" + savedSanPham.getMaSanPham();
            String filePath = fileStorageService.storeFile(file, subDir);

            AnhMinhHoa anhMinhHoa = AnhMinhHoa.builder()
                    .duongDan(filePath)
                    .sanPham(savedSanPham)
                    .soThuTu(i) // Gán số thứ tự dựa trên vị trí trong danh sách tải lên
                    .laAnhChinh(i == request.getAnhChinhIndex())
                    .build();
            anhMinhHoas.add(anhMinhHoa);
        }
        // Lưu tất cả ảnh vào CSDL
        anhMinhHoaRepository.saveAll(anhMinhHoas);
        savedSanPham.setAnhMinhHoas(anhMinhHoas); // Cập nhật lại list ảnh cho entity

        // 5. Tạo kho hàng cho sản phẩm mới với số lượng ban đầu là 0
        Kho kho = Kho.builder()
                .sanPham(savedSanPham)
                .soLuong(0) // Mặc định số lượng là 0, sẽ được cập nhật khi nhập kho
                .build();
        khoRepository.save(kho);
        savedSanPham.setKho(kho); // Cập nhật lại kho cho entity

        // 6. Trả về response
        return mapToSanPhamQuanLyResponse(savedSanPham);
    }

    @Override
    @Transactional(readOnly = true)
    public ChiTietSanPhamQuanLyResponse getChiTietSanPham(Integer maSanPham) {
        // Dùng EntityGraph để fetch sẵn các quan hệ, tránh N+1 query
        SanPham sanPham = sanPhamRepository.findById(maSanPham)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với mã: " + maSanPham));
        return mapToChiTietSanPhamQuanLyResponse(sanPham);
    }

    private ChiTietSanPhamQuanLyResponse mapToChiTietSanPhamQuanLyResponse(SanPham sanPham) {
        // Sắp xếp ảnh theo số thứ tự trước khi trả về
        List<AnhMinhHoaResponse> anhMinhHoaResponses = sanPham.getAnhMinhHoas().stream()
                .sorted(Comparator.comparing(AnhMinhHoa::getSoThuTu, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(anh -> AnhMinhHoaResponse.builder()
                        .duongDan(anh.getDuongDan())
                        .laAnhChinh(anh.isLaAnhChinh())
                        .soThuTu(anh.getSoThuTu())
                        .build())
                .collect(Collectors.toList());

        return ChiTietSanPhamQuanLyResponse.builder()
                .maSanPham(sanPham.getMaSanPham())
                .tenSanPham(sanPham.getTenSanPham())
                .maDanhMuc(sanPham.getDanhMuc() != null ? sanPham.getDanhMuc().getMaDanhMuc() : null)
                .moTa(sanPham.getMoTa())
                .nhaSanXuat(sanPham.getNhaSanXuat())
                .donGia(sanPham.getDonGia())
                .donVi(sanPham.getDonVi())
                .nhan(sanPham.getNhan())
                .ghiChu(sanPham.getGhiChu())
                .trangThai(sanPham.getTrangThai())
                .anhMinhHoas(anhMinhHoaResponses)
                .build();
    }

    @Override
    @Transactional
    public ChiTietSanPhamQuanLyResponse capNhatSanPham(Integer maSanPham, CapNhatSanPhamRequest request, List<MultipartFile> files) {
        SanPham sanPham = sanPhamRepository.findById(maSanPham)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với mã: " + maSanPham));

        // 1. Cập nhật các trường thông tin cơ bản
        updateSanPhamFields(sanPham, request);

        // 2. Xử lý xóa ảnh cũ
        if (request.getUrlsAnhXoa() != null && !request.getUrlsAnhXoa().isEmpty()) {
            List<AnhMinhHoa> anhCanXoa = sanPham.getAnhMinhHoas().stream()
                    .filter(anh -> request.getUrlsAnhXoa().contains(anh.getDuongDan()))
                    .toList();

            if (!anhCanXoa.isEmpty()) {
                sanPham.getAnhMinhHoas().removeAll(anhCanXoa);
                anhMinhHoaRepository.deleteAll(anhCanXoa);
                anhCanXoa.forEach(anh -> fileStorageService.deleteFile(anh.getDuongDan()));
            }
        }

        // 3. Xử lý thêm ảnh mới (tạo object trong bộ nhớ, chưa lưu vào DB)
        List<AnhMinhHoa> anhMoiList = new ArrayList<>();
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                String subDir = "sanpham/" + sanPham.getMaSanPham();
                String filePath = fileStorageService.storeFile(file, subDir);
                AnhMinhHoa anhMinhHoaMoi = AnhMinhHoa.builder()
                        .sanPham(sanPham)
                        .duongDan(filePath)
                        // soThuTu sẽ được gán lại ở bước sau
                        .laAnhChinh(false) // Tạm thời đặt là false, sẽ xử lý ở bước sau
                        .build();
                anhMoiList.add(anhMinhHoaMoi);
            }
        }

        // 4. Xử lý thay đổi ảnh chính
        if (StringUtils.hasText(request.getAnhChinhIdentifier())) {
            String identifier = request.getAnhChinhIdentifier();

            // Bỏ đánh dấu ảnh chính hiện tại (nếu có)
            sanPham.getAnhMinhHoas().stream()
                    .filter(AnhMinhHoa::isLaAnhChinh)
                    .findFirst()
                    .ifPresent(currentMain -> currentMain.setLaAnhChinh(false));

            // Thử tìm ảnh chính mới trong số các ảnh đã có (dựa vào URL)
            Optional<AnhMinhHoa> newMainFromExisting = sanPham.getAnhMinhHoas().stream()
                    .filter(anh -> identifier.equals(anh.getDuongDan()))
                    .findFirst();

            if (newMainFromExisting.isPresent()) {
                newMainFromExisting.get().setLaAnhChinh(true);
            } else {
                // Nếu không, thử tìm trong số các ảnh mới tải lên (dựa vào tên file gốc)
                boolean foundInNewFiles = false;
                if (files != null) {
                    for (int i = 0; i < files.size(); i++) {
                        if (identifier.equals(files.get(i).getOriginalFilename())) {
                            anhMoiList.get(i).setLaAnhChinh(true);
                            foundInNewFiles = true;
                            break;
                        }
                    }
                }
                if (!foundInNewFiles) {
                    throw new IllegalArgumentException("Không tìm thấy ảnh chính được chỉ định: " + identifier);
                }
            }
        }

        // 5. Lưu các ảnh mới vào DB và cập nhật quan hệ
        if (!anhMoiList.isEmpty()) {
            anhMinhHoaRepository.saveAll(anhMoiList);
            sanPham.getAnhMinhHoas().addAll(anhMoiList);
        }

        // 6. Chuẩn hóa lại số thứ tự của tất cả ảnh
        reorderImages(sanPham);

        // 7. Lưu lại sản phẩm và trả về response
        SanPham updatedSanPham = sanPhamRepository.save(sanPham);
        return mapToChiTietSanPhamQuanLyResponse(updatedSanPham);
    }

    private void reorderImages(SanPham sanPham) {
        List<AnhMinhHoa> images = sanPham.getAnhMinhHoas();
        images.sort(Comparator.comparing(AnhMinhHoa::getSoThuTu, Comparator.nullsLast(Comparator.naturalOrder())));
        for (int i = 0; i < images.size(); i++) {
            images.get(i).setSoThuTu(i);
        }
    }

    private void updateSanPhamFields(SanPham sanPham, CapNhatSanPhamRequest request) {
        if (StringUtils.hasText(request.getTenSanPham())) sanPham.setTenSanPham(request.getTenSanPham());
        if (request.getMoTa() != null) sanPham.setMoTa(request.getMoTa());
        if (request.getNhaSanXuat() != null) sanPham.setNhaSanXuat(request.getNhaSanXuat());
        if (request.getDonGia() != null) sanPham.setDonGia(request.getDonGia());
        if (request.getDonVi() != null) sanPham.setDonVi(request.getDonVi());
        if (request.getNhan() != null) sanPham.setNhan(request.getNhan());
        if (request.getGhiChu() != null) sanPham.setGhiChu(request.getGhiChu());
        if (request.getTrangThai() != null) sanPham.setTrangThai(request.getTrangThai());

        if (request.getMaDanhMuc() != null && !request.getMaDanhMuc().equals(sanPham.getDanhMuc().getMaDanhMuc())) {
            DanhMuc danhMuc = danhMucRepository.findById(request.getMaDanhMuc())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với mã: " + request.getMaDanhMuc()));
            if (danhMuc.getDanhMucCha() == null) {
                throw new IllegalArgumentException("Không thể gán sản phẩm vào danh mục cha.");
            }
            sanPham.setDanhMuc(danhMuc);
        }
    }

    private SanPhamQuanLyResponse mapToSanPhamQuanLyResponse(SanPham sanPham) {
        String danhMucCha = null;
        String danhMucCon = null;
        DanhMuc danhMuc = sanPham.getDanhMuc();

        if (danhMuc != null) {
            if (danhMuc.getDanhMucCha() != null) {
                danhMucCha = danhMuc.getDanhMucCha().getTenDanhMuc();
                danhMucCon = danhMuc.getTenDanhMuc();
            } else {
                danhMucCha = danhMuc.getTenDanhMuc();
            }
        }

        return SanPhamQuanLyResponse.builder()
                .maSanPham(sanPham.getMaSanPham())
                .tenSanPham(sanPham.getTenSanPham())
                .danhMucCha(danhMucCha)
                .danhMucCon(danhMucCon)
                .donGia(sanPham.getDonGia())
                .trangThai(sanPham.getTrangThai())
                .build();
    }
}