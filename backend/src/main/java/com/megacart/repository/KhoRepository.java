package com.megacart.repository;

import com.megacart.model.Kho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface KhoRepository extends JpaRepository<Kho, Integer>, JpaSpecificationExecutor<Kho> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT k FROM Kho k WHERE k.maSanPham = :maSanPham")
    Optional<Kho> findByIdWithPessimisticLock(@Param("maSanPham") Integer maSanPham);
}