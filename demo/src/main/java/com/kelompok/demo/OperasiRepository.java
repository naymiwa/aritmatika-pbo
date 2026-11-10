package com.kelompok.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperasiRepository extends JpaRepository<Operasi, Long> {
    // findAll(), save(), findById(), deleteById() sudah disediakan otomatis
}