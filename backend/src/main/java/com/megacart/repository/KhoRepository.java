package com.megacart.repository;

import com.megacart.model.Kho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface KhoRepository extends JpaRepository<Kho, Integer>, JpaSpecificationExecutor<Kho> {
}