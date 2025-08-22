package com.megacart.service.impl;

import com.megacart.dto.request.ThemSanPhamRequest;
import com.megacart.dto.response.AnhMinhHoaResponse;
import com.megacart.dto.request.CapNhatSanPhamRequest;
import com.megacart.dto.response.ChiTietSanPhamQuanLyResponse;
import com.megacart.dto.response.MessageResponse;
import com.megacart.dto.response.ThemSanPhamAsyncResponse;
import com.megacart.dto.response.PagedResponse;
import com.megacart.dto.response.SanPhamQuanLyResponse;
import com.megacart.enumeration.TrangThaiDanhMuc;
import com.megacart.enumeration.TrangThaiSanPham;
import com.megacart.exception.ResourceNotFoundException;
import com.megacart.model.AnhMinhHoa;
import com.megacart.model.DanhMuc;
import com.megacart.model.Kho;
import com.megacart.model.SanPham;
import com.megacart.repository.projection.AnhChinhProjection;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
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
    public PagedResponse<SanPhamQuanLyResponse> getDSSanPham(String searchField, String searchValue, Integer maDanhMuc, TrangThaiSanPham trangThai, Pageable pageable) {

        Specification<SanPham> spec = (root, query, cb) -> {
            // Để tránh N+1 queries, chúng ta sử dụng fetch joins.
            // Điều này đặc biệt quan trọng đối với các kết quả được phân trang.
            // Tối ưu: Không fetch ảnh ở đây nữa để tránh data over-fetching.
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("danhMuc", JoinType.LEFT).fetch("danhMucCha", JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();

            // Logic mới: Nếu tìm kiếm theo mã, bỏ qua các bộ lọc khác
            if ("maSanPham".equals(searchField) && StringUtils.hasText(searchValue)) {
                if (searchValue.matches("\\d+")) {
                    predicates.add(cb.equal(root.get("maSanPham"), Integer.parseInt(searchValue)));
                    // Trả về ngay lập tức, không cần các bộ lọc khác
                    return cb.and(predicates.toArray(new Predicate[0]));
                } else {
                    return cb.disjunction(); // Mã không hợp lệ, trả về rỗng
                }
            }

            // Lọc theo các trường khác nếu không phải tìm theo mã
            if ("tenSanPham".equals(searchField) && StringUtils.hasText(searchValue)) {
                predicates.add(cb.like(cb.lower(root.get("tenSanPham")), "%" + searchValue.toLowerCase() + "%"));
            }

            if (maDanhMuc != null) {
                List<Integer> categoryIdsToSearch = danhMucService.getAllSubCategoryIds(maDanhMuc);
                if (categoryIdsToSearch != null && !categoryIdsToSearch.isEmpty()) {
                    predicates.add(root.get("danhMuc").get("maDanhMuc").in(categoryIdsToSearch));
                }
            }

            if (trangThai != null) {
                predicates.add(cb.equal(root.get("trangThai"), trangThai));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<SanPham> sanPhamPage = sanPhamRepository.findAll(spec, pageable);

        List<SanPham> sanPhamsOnPage = sanPhamPage.getContent();
        if (sanPhamsOnPage.isEmpty()) {
            return new PagedResponse<>(Collections.emptyList(), sanPhamPage.getNumber(), sanPhamPage.getSize(), sanPhamPage.getTotalElements(), sanPhamPage.getTotalPages(), null);
        }

        // Tối ưu: Lấy tất cả ảnh chính trong 1 query
        List<Integer> sanPhamIds = sanPhamsOnPage.stream()
                .map(SanPham::getMaSanPham)
                .collect(Collectors.toList());

        // Thêm bước kiểm tra để tránh lỗi khi sanPhamIds rỗng
        Map<Integer, String> anhChinhUrlMap;
        if (sanPhamIds.isEmpty()) {
            anhChinhUrlMap = Collections.emptyMap();
        } else {
            anhChinhUrlMap = sanPhamRepository.findAnhMinhHoaChinhProjectionBySanPhamIds(sanPhamIds)
                    .stream()
                    .collect(Collectors.toMap(AnhChinhProjection::getMaSanPham, AnhChinhProjection::getDuongDan));
        }

        List<SanPhamQuanLyResponse> responses = sanPhamsOnPage.stream()
                .map(sanPham -> mapToSanPhamQuanLyResponse(sanPham, anhChinhUrlMap.get(sanPham.getMaSanPham())))
                .collect(Collectors.toList());

        return new PagedResponse<>(responses, sanPhamPage.getNumber(), sanPhamPage.getSize(), sanPhamPage.getTotalElements(), sanPhamPage.getTotalPages(), null);
    }

    @Override
    @Transactional
    public ThemSanPhamAsyncResponse themSanPham(ThemSanPhamRequest request, List<MultipartFile> files) {
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
        if (danhMuc.getTrangThai() == TrangThaiDanhMuc.KHONG_HOAT_DONG) {
            throw new IllegalArgumentException("Không thể thêm sản phẩm vào một danh mục đang không hoạt động.");
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

        // 4. Tạo kho hàng cho sản phẩm mới với số lượng ban đầu là 0
        Kho kho = Kho.builder()
                .sanPham(savedSanPham)
                .soLuong(0) // Mặc định số lượng là 0, sẽ được cập nhật khi nhập kho
                .build();
        khoRepository.save(kho);

        // 5. Xử lý tải ảnh bất đồng bộ
        xuLyTaiAnhBatDongBo(savedSanPham, files, request.getAnhChinhIndex());

        // 6. Trả về response ngay lập tức cho người dùng
        return ThemSanPhamAsyncResponse.builder()
                .maSanPham(savedSanPham.getMaSanPham())
                .thongBao("Thêm sản phẩm thành công. Ảnh đang được xử lý và sẽ sớm được cập nhật.")
                .build();
    }

    @Async // Chạy toàn bộ logic này trên một luồng khác
    @Transactional // Tạo một transaction mới cho việc lưu ảnh
    public void xuLyTaiAnhBatDongBo(SanPham sanPham, List<MultipartFile> files, int anhChinhIndex) {
        String subDir = "sanpham/" + sanPham.getMaSanPham();

        // Tạo một danh sách các tác vụ tải ảnh lên
        List<CompletableFuture<AnhMinhHoa>> uploadTasks = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            final int index = i;
            MultipartFile file = files.get(i);

            CompletableFuture<AnhMinhHoa> task = fileStorageService.storeFileAsync(file, subDir)
                    .thenApply(filePath -> AnhMinhHoa.builder()
                            .duongDan(filePath)
                            .sanPham(sanPham)
                            .soThuTu(index)
                            .laAnhChinh(index == anhChinhIndex)
                            .build());
            uploadTasks.add(task);
        }

        // Chờ tất cả các tác vụ tải lên hoàn thành
        List<AnhMinhHoa> anhMinhHoas = uploadTasks.stream()
                .map(CompletableFuture::join) // Lấy kết quả từ mỗi CompletableFuture
                .collect(Collectors.toList());

        // Lưu tất cả các đối tượng AnhMinhHoa vào CSDL trong một lần
        anhMinhHoaRepository.saveAll(anhMinhHoas);
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
    public MessageResponse capNhatSanPham(Integer maSanPham, CapNhatSanPhamRequest request, List<MultipartFile> files) {
        SanPham sanPham = sanPhamRepository.findById(maSanPham)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với mã: " + maSanPham));

        // 1. Cập nhật các trường thông tin cơ bản (đồng bộ)
        updateSanPhamFields(sanPham, request);

        // 2. Xử lý xóa ảnh cũ (đồng bộ)
        if (request.getUrlsAnhXoa() != null && !request.getUrlsAnhXoa().isEmpty()) {
            List<AnhMinhHoa> anhCanXoa = sanPham.getAnhMinhHoas().stream()
                    .filter(anh -> request.getUrlsAnhXoa().contains(anh.getDuongDan()))
                    .toList();

            if (!anhCanXoa.isEmpty()) {
                // Xóa khỏi quan hệ
                sanPham.getAnhMinhHoas().removeAll(anhCanXoa);
                // Xóa khỏi DB
                anhMinhHoaRepository.deleteAll(anhCanXoa);
                // Xóa khỏi Cloud Storage bất đồng bộ để không làm chậm response
                anhCanXoa.forEach(anh -> fileStorageService.deleteFileAsync(anh.getDuongDan()));
            }
        }

        // 3. Xử lý thêm ảnh mới và cập nhật ảnh chính (bất đồng bộ)
        xuLyCapNhatAnhBatDongBo(maSanPham, files, request.getAnhChinhIdentifier());

        // 4. Trả về response ngay lập tức
        return new MessageResponse("Yêu cầu cập nhật sản phẩm đã được tiếp nhận. Các thay đổi sẽ sớm được áp dụng.");
    }

    @Async
    @Transactional
    public void xuLyCapNhatAnhBatDongBo(Integer maSanPham, List<MultipartFile> files, String anhChinhIdentifier) {
        // Bắt buộc phải lấy lại entity trong transaction mới này
        SanPham sanPham = sanPhamRepository.findById(maSanPham)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với mã: " + maSanPham + " trong tác vụ bất đồng bộ."));

        List<AnhMinhHoa> anhMoiList = new ArrayList<>();

        // 1. Xử lý tải lên các file ảnh mới
        if (files != null && !files.isEmpty()) {
            String subDir = "sanpham/" + sanPham.getMaSanPham();

            // Tìm số thứ tự lớn nhất hiện có để gán cho các ảnh mới
            final int maxSoThuTuHienTai = sanPham.getAnhMinhHoas().stream()
                    .mapToInt(AnhMinhHoa::getSoThuTu)
                    .max()
                    .orElse(-1); // Nếu chưa có ảnh nào, bắt đầu từ -1

            List<CompletableFuture<AnhMinhHoa>> uploadTasks = new ArrayList<>();
            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);
                final int soThuTuMoi = maxSoThuTuHienTai + 1 + i;

                CompletableFuture<AnhMinhHoa> task = fileStorageService.storeFileAsync(file, subDir)
                            .thenApply(filePath -> AnhMinhHoa.builder()
                                    .duongDan(filePath)
                                    .sanPham(sanPham)
                                    .laAnhChinh(false) // Mặc định là false
                                    .soThuTu(soThuTuMoi) // Gán số thứ tự ngay khi tạo
                                    .originalFileName(file.getOriginalFilename()) // Lưu tên file gốc để định danh
                                    .build());
                uploadTasks.add(task);
            }

            // Chờ tất cả các tác vụ hoàn thành và thu thập kết quả
            anhMoiList = uploadTasks.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());

            // Lưu các ảnh mới vào CSDL và thêm vào danh sách của sản phẩm
            if (!anhMoiList.isEmpty()) {
                anhMinhHoaRepository.saveAll(anhMoiList);
                sanPham.getAnhMinhHoas().addAll(anhMoiList);
            }
        }

        // 2. Xử lý thay đổi ảnh chính
        if (StringUtils.hasText(anhChinhIdentifier)) {
            // Bỏ đánh dấu ảnh chính hiện tại
            sanPham.getAnhMinhHoas().stream()
                    .filter(AnhMinhHoa::isLaAnhChinh)
                    .findFirst()
                    .ifPresent(currentMain -> currentMain.setLaAnhChinh(false));

            // Tìm ảnh chính mới trong số các ảnh đã có (dựa vào URL) hoặc ảnh mới (dựa vào tên file)
            sanPham.getAnhMinhHoas().stream()
                    .filter(anh -> anhChinhIdentifier.equals(anh.getDuongDan()) || anhChinhIdentifier.equals(anh.getOriginalFileName()))
                    .findFirst()
                    .ifPresent(newMain -> newMain.setLaAnhChinh(true));
        }

        // 3. Sắp xếp lại thứ tự của tất cả các ảnh
        reorderImages(sanPham);

        // Transaction sẽ tự động commit các thay đổi trên các entity đã được quản lý.
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

        // Xử lý thay đổi danh mục
        if (request.getMaDanhMuc() != null && !request.getMaDanhMuc().equals(sanPham.getDanhMuc().getMaDanhMuc())) {
            DanhMuc danhMucMoi = danhMucRepository.findById(request.getMaDanhMuc())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với mã: " + request.getMaDanhMuc()));
            if (danhMucMoi.getDanhMucCha() == null) {
                throw new IllegalArgumentException("Không thể gán sản phẩm vào danh mục cha.");
            }
            // Ràng buộc: Không cho phép chuyển sản phẩm vào danh mục không hoạt động.
            if (danhMucMoi.getTrangThai() == TrangThaiDanhMuc.KHONG_HOAT_DONG) {
                throw new IllegalArgumentException("Không thể di chuyển sản phẩm vào một danh mục đang không hoạt động.");
            }
            sanPham.setDanhMuc(danhMucMoi);
        }

        // Xử lý thay đổi trạng thái sản phẩm
        if (request.getTrangThai() != null) {
            // Ràng buộc: Không cho phép đặt trạng thái "Bán" nếu danh mục của nó không hoạt động.
            if (request.getTrangThai() == TrangThaiSanPham.BAN && sanPham.getDanhMuc().getTrangThai() == TrangThaiDanhMuc.KHONG_HOAT_DONG) {
                throw new IllegalArgumentException("Không thể bán sản phẩm thuộc danh mục đang không hoạt động.");
            }
            sanPham.setTrangThai(request.getTrangThai());
        }
    }

    private SanPhamQuanLyResponse mapToSanPhamQuanLyResponse(SanPham sanPham, String anhChinhUrl) {
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
                .nhan(sanPham.getNhan())
                .anhMinhHoaChinh(anhChinhUrl)
                .build();
    }
}