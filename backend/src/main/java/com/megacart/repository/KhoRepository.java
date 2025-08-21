package com.megacart.repository;

import com.megacart.dto.projection.TonKhoProjection;
import com.megacart.model.Kho;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KhoRepository extends JpaRepository<Kho, Integer>, JpaSpecificationExecutor<Kho> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT k FROM Kho k WHERE k.maSanPham = :maSanPham")
    Optional<Kho> findByIdWithPessimisticLock(@Param("maSanPham") Integer maSanPham);

    @Query("SELECT k.sanPham.maSanPham as maSanPham, k.sanPham.tenSanPham as tenSanPham, k.soLuong as soLuongTon " +
           "FROM Kho k ORDER BY k.soLuong DESC")
    List<TonKhoProjection> findTopBySoLuongDesc(Pageable pageable);
}