package com.megacart.repository;

import com.megacart.model.AnhMinhHoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnhMinhHoaRepository extends JpaRepository<AnhMinhHoa, Integer> {
    Optional<AnhMinhHoa> findByDuongDan(String duongDan);
}