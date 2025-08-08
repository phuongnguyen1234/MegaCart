package com.megacart.repository;

import com.megacart.model.ThongKeThang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThongKeThangRepository extends JpaRepository<ThongKeThang, Integer> {
}