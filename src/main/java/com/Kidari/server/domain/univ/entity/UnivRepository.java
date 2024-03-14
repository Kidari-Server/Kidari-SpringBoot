package com.Kidari.server.domain.univ.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UnivRepository extends JpaRepository<Univ, Long> {
    Optional<Univ> findById(Long id);
    Optional<Univ> findByUnivName(String name);
}
