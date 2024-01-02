package com.zohocrm.repository;

import com.zohocrm.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LeadRepository extends JpaRepository<Lead, String> {
    Optional<Lead> findByMobile(long mobile);
    Optional<Lead> findByEmail(String email);
   boolean existsByEmail(String email);
   boolean existsByMobile(long mobile);

}
