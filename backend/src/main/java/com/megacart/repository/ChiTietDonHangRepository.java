package com.megacart.repository;

import com.megacart.model.ChiTietDonHang;
import com.megacart.model.ChiTietDonHangId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChiTietDonHangRepository extends JpaRepository<ChiTietDonHang, ChiTietDonHangId> {
}