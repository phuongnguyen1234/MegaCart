package com.megacart.repository;

import com.megacart.model.AnhMinhHoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnhMinhHoaRepository extends JpaRepository<AnhMinhHoa, Integer> {
}