package com.rowland.engineering.ecommerce.repository;

import com.rowland.engineering.ecommerce.model.PromoCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {
    PromoCode findByCodeIgnoreCase(String code);
    Boolean existsByCodeIgnoreCase(String code);
}
