package com.megacart.repository;

import com.megacart.model.ThongKeThang;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ThongKeThangRepository extends JpaRepository<ThongKeThang, Integer> {
    // Tìm kiếm bản ghi thống kê theo năm và tháng
    Optional<ThongKeThang> findByNamAndThang(int nam, int thang);

   // Lấy danh sách thống kê, sắp xếp theo tháng mới nhất, có phân trang
    Page<ThongKeThang> findByOrderByNamDescThangDesc(Pageable pageable);
}