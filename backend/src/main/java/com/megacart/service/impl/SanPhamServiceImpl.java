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
import com.megacart.model.AnhMinhHoa;
import com.megacart.model.SanPham;
import com.megacart.repository.SanPhamRepository;
import com.megacart.repository.DanhMucRepository;
import com.megacart.service.DanhMucService;
import com.megacart.repository.specification.SanPhamSpecification;
import com.megacart.service.SanPhamService;
import com.megacart.utils.ImageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SanPhamServiceImpl implements SanPhamService {

    private final SanPhamRepository sanPhamRepository;
    private final SanPhamSpecification sanPhamSpecification;
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
            Pageable pageable
    ) {
        List<Integer> categoryIdsToSearch = null;
        if (maDanhMuc != null) {
            // Lấy ID của danh mục cha và tất cả các danh mục con cháu của nó
            categoryIdsToSearch = danhMucService.getAllSubCategoryIds(maDanhMuc);
        }

        // Truyền danh sách ID vào Specification để lọc theo mệnh đề IN
        Specification<SanPham> spec = sanPhamSpecification.filterBy(tuKhoa, categoryIdsToSearch, giaToiThieu, giaToiDa, nhaSanXuat, null, false);
        Page<SanPham> sanPhamPage = sanPhamRepository.findAll(spec, pageable);

        List<BreadcrumbItem> breadcrumbs = null;
        if (maDanhMuc != null) {
            breadcrumbs = danhMucRepository.findById(maDanhMuc)
                    .map(this::buildBreadcrumbs)
                    .orElse(null);
        }

        return convertPageToPagedResponse(sanPhamPage, breadcrumbs);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<SanPhamResponse> getSanPhamTheoNhan(
            NhanSanPham nhan,
            Integer maDanhMuc,
            Integer giaToiThieu,
            Integer giaToiDa,
            String nhaSanXuat,
            Pageable pageable) {

        List<Integer> categoryIdsToSearch = null;
        if (maDanhMuc != null) {
            categoryIdsToSearch = danhMucService.getAllSubCategoryIds(maDanhMuc);
        }

        // Sử dụng Specification để lọc động, truyền nhãn vào tham số cuối cùng
        Specification<SanPham> spec = sanPhamSpecification.filterBy(null, categoryIdsToSearch, giaToiThieu, giaToiDa, nhaSanXuat, nhan, false);
        Page<SanPham> sanPhamPage = sanPhamRepository.findAll(spec, pageable);

        // Lấy tên hiển thị từ enum để code sạch hơn và dễ mở rộng
        List<BreadcrumbItem> breadcrumbs = buildStaticBreadcrumbs(nhan.getTenHienThi());
        return convertPageToPagedResponse(sanPhamPage, breadcrumbs);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<SanPhamResponse> getSanPhamBanChay(
            Integer maDanhMuc,
            Integer giaToiThieu,
            Integer giaToiDa,
            String nhaSanXuat,
            Pageable pageable) {

        List<BreadcrumbItem> breadcrumbs = buildStaticBreadcrumbs("Bán chạy nhất");

        List<Integer> categoryIdsToSearch = null;
        if (maDanhMuc != null) {
            // Lấy ID của danh mục cha và tất cả các danh mục con cháu của nó
            categoryIdsToSearch = danhMucService.getAllSubCategoryIds(maDanhMuc);
        }
        // Sử dụng Specification để lọc các sản phẩm được đánh dấu là bán chạy
        Specification<SanPham> spec = sanPhamSpecification.filterBy(null, categoryIdsToSearch, giaToiThieu, giaToiDa, nhaSanXuat, null, true);
        Page<SanPham> sanPhamPage = sanPhamRepository.findAll(spec, pageable);
        return convertPageToPagedResponse(sanPhamPage, breadcrumbs);
    }

    @Override
    @Transactional(readOnly = true)
    public ChiTietSanPhamResponse getSanPhamByMaSanPham(Integer maSanPham) {
        SanPham sanPham = sanPhamRepository.findById(maSanPham)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + maSanPham));

        // Chỉ cho phép xem sản phẩm đang được bán
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
                .nhan(sanPham.getNhan() != null ? sanPham.getNhan().getTenHienThi() : null)
                .trangThaiTonKho(trangThaiTonKho.getTenHienThi())
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

        List<AnhMinhHoaResponse> anhMinhHoas = sanPham.getAnhMinhHoas().stream()
                .map(amh -> AnhMinhHoaResponse.builder().duongDan(amh.getDuongDan()).laAnhChinh(amh.isLaAnhChinh()).build())
                .collect(Collectors.toList());

        return ChiTietSanPhamResponse.builder()
                .maSanPham(sanPham.getMaSanPham())
                .tenSanPham(sanPham.getTenSanPham())
                .donGia(sanPham.getDonGia())
                .donVi(sanPham.getDonVi())
                .nhaSanXuat(sanPham.getNhaSanXuat())
                .moTa(sanPham.getMoTa())
                .ghiChu(sanPham.getGhiChu())
                .trangThaiTonKho(trangThaiTonKho.getTenHienThi())
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
        StringBuilder pathBuilder = new StringBuilder();
        for (DanhMuc dm : path) {
            // Nối slug của mục hiện tại vào đường dẫn chung
            pathBuilder.append("/").append(dm.getSlug());
            breadcrumbs.add(BreadcrumbItem.builder()
                    .text(dm.getTenDanhMuc())
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