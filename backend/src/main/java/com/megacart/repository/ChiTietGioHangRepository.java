package com.megacart.repository;

import com.megacart.model.ChiTietGioHang;
import com.megacart.model.ChiTietGioHangId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChiTietGioHangRepository extends JpaRepository<ChiTietGioHang, ChiTietGioHangId> {
}