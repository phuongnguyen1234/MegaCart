package com.megacart.service.impl;

import com.megacart.dto.response.BreadcrumbItem;
import com.megacart.enumeration.NhanSanPham;
import com.megacart.enumeration.TrangThaiSanPham;
import com.megacart.enumeration.TrangThaiTonKho;
import com.megacart.exception.ResourceNotFoundException;
import com.megacart.dto.response.AnhMinhHoaResponse;
import com.megacart.dto.response.ChiTietSanPhamResponse;
import com.megacart.dto.response.PagedResponse;
import com.megacart.dto.response.SanPhamResponse;
import com.megacart.model.DanhMuc;
import com.megacart.model.SanPham;
import com.megacart.repository.SanPhamRepository;
import com.megacart.repository.DanhMucRepository;
import com.megacart.service.DanhMucService;
import com.megacart.service.SanPhamService;
import com.megacart.utils.ImageUtils;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SanPhamServiceImpl implements SanPhamService {

    private final SanPhamRepository sanPhamRepository;
    private final DanhMucRepository danhMucRepository;
    private final DanhMucService danhMucService;
    private static final int GOI_Y_LIMIT = 10; // Giới hạn 10 gợi ý

    @Override
    @Transactional(readOnly = true)
    public List<String> goiYTimKiem(String tuKhoa) {
        Pageable limit = PageRequest.of(0, GOI_Y_LIMIT);
        // Chuẩn bị pattern tìm kiếm ngay tại tầng service
        String searchPattern = tuKhoa + "%";
        System.out.println("Pattern tìm kiếm: " + searchPattern);
        return sanPhamRepository.findTenSanPhamByPrefixAndStatus(searchPattern, TrangThaiSanPham.BAN, limit);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<SanPhamResponse> timKiemVaLocSanPham(
            String tuKhoa,
            Integer maDanhMuc,
            Integer giaToiThieu,
            Integer giaToiDa,
            String nhaSanXuat,
            NhanSanPham nhan,
            Boolean banChay,
            Pageable pageable
    ) {
        // 1. Build the dynamic query using Specification
        Specification<SanPham> spec = (root, query, cb) -> {
            // Để tránh vấn đề N+1 query, chúng ta sử dụng fetch join.
            // Điều này đặc biệt quan trọng đối với các kết quả được phân trang.
            // Nó sẽ gom các query lấy ảnh và kho vào cùng 1 query lấy sản phẩm.
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("anhMinhHoas", jakarta.persistence.criteria.JoinType.LEFT);
                root.fetch("kho", jakarta.persistence.criteria.JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();

            // Luôn lọc các sản phẩm đang được bán
            predicates.add(cb.equal(root.get("trangThai"), TrangThaiSanPham.BAN));

            // Lọc theo từ khóa
            if (StringUtils.hasText(tuKhoa)) {
                predicates.add(cb.like(cb.lower(root.get("tenSanPham")), "%" + tuKhoa.toLowerCase() + "%"));
            }

            // Lọc theo danh mục (bao gồm cả các danh mục con)
            if (maDanhMuc != null) {
                List<Integer> categoryIdsToSearch = danhMucService.getAllSubCategoryIds(maDanhMuc);
                if (categoryIdsToSearch != null && !categoryIdsToSearch.isEmpty()) {
                    predicates.add(root.get("danhMuc").get("maDanhMuc").in(categoryIdsToSearch));
                }
            }

            // Lọc theo khoảng giá
            if (giaToiThieu != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("donGia"), giaToiThieu));
            }
            if (giaToiDa != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("donGia"), giaToiDa));
            }

            // Lọc theo nhà sản xuất
            if (StringUtils.hasText(nhaSanXuat)) {
                // Sử dụng 'like' thay vì 'equal' để tìm kiếm linh hoạt hơn (ví dụ: "Xiaomi" sẽ khớp với "Xiaomi Corporation")
                predicates.add(cb.like(cb.lower(root.get("nhaSanXuat")), "%" + nhaSanXuat.toLowerCase() + "%"));
            }

            // Lọc theo nhãn (ví dụ: Mới)
            if (nhan != null) {
                predicates.add(cb.equal(root.get("nhan"), nhan));
            }

            // Lọc theo sản phẩm bán chạy
            if (banChay != null && banChay) {
                predicates.add(cb.isTrue(root.get("banChay")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // 2. Thực thi truy vấn
        Page<SanPham> sanPhamPage = sanPhamRepository.findAll(spec, pageable);

        // 3. Xây dựng breadcrumbs dựa trên ngữ cảnh
        List<BreadcrumbItem> breadcrumbs = null;
        if (maDanhMuc != null) {
            breadcrumbs = danhMucRepository.findById(maDanhMuc)
                    .map(this::buildBreadcrumbs)
                    .orElse(null);
        } else if (nhan != null) {
            breadcrumbs = buildStaticBreadcrumbs(nhan.getTenHienThi());
        } else if (banChay != null && banChay) {
            breadcrumbs = buildStaticBreadcrumbs("Bán chạy nhất");
        }
        // 4. Chuyển đổi sang DTO và trả về
        return convertPageToPagedResponse(sanPhamPage, breadcrumbs);
    }

    @Override
    @Transactional(readOnly = true)
    public ChiTietSanPhamResponse getSanPhamByMaSanPham(Integer maSanPham) {
        SanPham sanPham = sanPhamRepository.findById(maSanPham)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + maSanPham));

        // Chỉ cho phép xem chi tiết các sản phẩm đang được bán.
        // Nếu sản phẩm không còn bán, trả về lỗi 404 để thân thiện với người dùng và SEO.
        if (sanPham.getTrangThai() != TrangThaiSanPham.BAN) {
            throw new ResourceNotFoundException("Sản phẩm này không tồn tại hoặc đã ngừng kinh doanh.");
        }

        ChiTietSanPhamResponse response = mapToChiTietSanPhamResponse(sanPham);

        // Xây dựng và thêm breadcrumbs vào response
        if (sanPham.getDanhMuc() != null) {
            List<BreadcrumbItem> breadcrumbs = buildBreadcrumbs(sanPham.getDanhMuc());
            // Thêm tên sản phẩm hiện tại vào cuối breadcrumbs (không có link)
            breadcrumbs.add(BreadcrumbItem.builder().text(sanPham.getTenSanPham()).to(null).build());
            response.setBreadcrumbs(breadcrumbs);
        }

        return response;
    }

    /**
     * Phương thức private helper để chuyển đổi từ Page<SanPham> sang PagedResponse<SanPhamResponse>.
     * Tái sử dụng logic mapping để tránh lặp code.
     * @param sanPhamPage Đối tượng Page chứa các sản phẩm.
     * @return Đối tượng PagedResponse chứa DTO sản phẩm.
     */
    private SanPhamResponse mapToSanPhamResponse(SanPham sanPham) {
        String anhChinhUrl = ImageUtils.getAnhMinhHoaChinhUrl(sanPham.getAnhMinhHoas());
        // Suy ra trạng thái tồn kho từ số lượng trong bảng Kho
        TrangThaiTonKho trangThaiTonKho = (sanPham.getKho() != null && sanPham.getKho().getSoLuong() > 0)
                ? TrangThaiTonKho.CON_HANG
                : TrangThaiTonKho.HET_HANG;

        return SanPhamResponse.builder()
                .maSanPham(sanPham.getMaSanPham())
                .tenSanPham(sanPham.getTenSanPham())
                .donGia(sanPham.getDonGia())
                .donVi(sanPham.getDonVi())
                .nhaSanXuat(sanPham.getNhaSanXuat())
                .nhan(sanPham.getNhan())
                .trangThaiTonKho(trangThaiTonKho)
                .anhMinhHoaChinh(anhChinhUrl)
                .banChay(sanPham.isBanChay()).build();
    }

    private PagedResponse<SanPhamResponse> convertPageToPagedResponse(Page<SanPham> sanPhamPage, List<BreadcrumbItem> breadcrumbs) {
        List<SanPhamResponse> sanPhamResponses = sanPhamPage.getContent().stream()
                .map(this::mapToSanPhamResponse)
                .toList();

        return new PagedResponse<>(
                sanPhamResponses,
                sanPhamPage.getNumber(),
                sanPhamPage.getSize(),
                sanPhamPage.getTotalElements(),
                sanPhamPage.getTotalPages(),
                breadcrumbs
        );
    }

    private ChiTietSanPhamResponse mapToChiTietSanPhamResponse(SanPham sanPham) {
        TrangThaiTonKho trangThaiTonKho = (sanPham.getKho() != null && sanPham.getKho().getSoLuong() > 0)
                ? TrangThaiTonKho.CON_HANG
                : TrangThaiTonKho.HET_HANG;

        // Sắp xếp ảnh theo số thứ tự trước khi trả về
        List<AnhMinhHoaResponse> anhMinhHoas = sanPham.getAnhMinhHoas().stream()
                .sorted(Comparator.comparing(com.megacart.model.AnhMinhHoa::getSoThuTu, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(amh -> AnhMinhHoaResponse.builder().duongDan(amh.getDuongDan()).laAnhChinh(amh.isLaAnhChinh()).soThuTu(amh.getSoThuTu()).build())
                .collect(Collectors.toList());

        return ChiTietSanPhamResponse.builder()
                .maSanPham(sanPham.getMaSanPham())
                .tenSanPham(sanPham.getTenSanPham())
                .donGia(sanPham.getDonGia())
                .donVi(sanPham.getDonVi())
                .nhaSanXuat(sanPham.getNhaSanXuat())
                .moTa(sanPham.getMoTa())
                .ghiChu(sanPham.getGhiChu())
                .trangThaiTonKho(trangThaiTonKho)
                .anhMinhHoas(anhMinhHoas)
                .banChay(sanPham.isBanChay()).build();
               }

    /**
     * Xây dựng danh sách breadcrumbs bằng cách duyệt ngược từ danh mục con lên danh mục cha.
     * @param danhMuc Danh mục hiện tại của sản phẩm hoặc trang.
     * @return Một danh sách các BreadcrumbItem.
     */
    private List<BreadcrumbItem> buildBreadcrumbs(DanhMuc danhMuc) {
        LinkedList<BreadcrumbItem> breadcrumbs = new LinkedList<>();
        // Thêm "Trang chủ" làm mục đầu tiên
        breadcrumbs.add(BreadcrumbItem.builder().text("Trang chủ").to("/trang-chu").build());

        DanhMuc current = danhMuc;
        LinkedList<DanhMuc> path = new LinkedList<>();
        while (current != null) {
            path.addFirst(current);
            current = current.getDanhMucCha();
        }

        // Xây dựng đường dẫn phân cấp dựa trên slug 
        // Thêm tiền tố /danh-muc/ để khớp với cấu trúc route mới của frontend
        StringBuilder pathBuilder = new StringBuilder("/danh-muc");
        for (DanhMuc dm : path) {
            // Nối slug của mục hiện tại vào đường dẫn chung
            pathBuilder.append("/").append(dm.getSlug());
            breadcrumbs.add(BreadcrumbItem.builder()
                    .text(dm.getTenDanhMuc())
                    // Trả về đường dẫn đầy đủ mà frontend mong đợi
                    .to(pathBuilder.toString())
                    .build());
        }

        return breadcrumbs;
    }

    /**
     * Xây dựng breadcrumbs cho các trang tĩnh không dựa vào danh mục (ví dụ: Bán chạy, Khuyến mãi).
     * @param currentPageName Tên của trang hiện tại.
     * @return Một danh sách các BreadcrumbItem.
     */
    private List<BreadcrumbItem> buildStaticBreadcrumbs(String currentPageName) {
        List<BreadcrumbItem> breadcrumbs = new LinkedList<>();
        breadcrumbs.add(BreadcrumbItem.builder()
                .text("Trang chủ")
                .to("/trang-chu") // Giữ nguyên link trang chủ
                .build());
        breadcrumbs.add(BreadcrumbItem.builder()
                .text(currentPageName)
                .to(null) // Trang tĩnh hiện tại không cần link, khớp với yêu cầu
                .build());
        return breadcrumbs;
    }
}