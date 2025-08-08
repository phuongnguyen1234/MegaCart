package com.megacart.repository;

import com.megacart.model.ThongKeNgay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThongKeNgayRepository extends JpaRepository<ThongKeNgay, Integer> {
}