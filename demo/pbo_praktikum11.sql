-- =====================================================
-- PBO Praktikum 11 : Kalkulator CRUD
-- Aman dijalankan berulang: TIDAK ada DROP TABLE dan TIDAK ada TRUNCATE,
-- jadi data yang sudah ada tidak akan hilang atau tertimpa.
-- =====================================================

-- LANGKAH 1 (sekali saja) - buat database.
-- Jalankan di Query Tool pada database "postgres", atau lewat menu:
--   klik kanan Databases > Create > Database > nama: pbo_praktikum11
--
--   CREATE DATABASE pbo_praktikum11;

-- LANGKAH 2 - buka Query Tool pada database pbo_praktikum11, lalu jalankan skrip di bawah.

CREATE TABLE IF NOT EXISTS operasi_aritmatika (
    id         BIGSERIAL PRIMARY KEY,
    bilangan1  NUMERIC(30,6) NOT NULL,
    opt        VARCHAR(1)    NOT NULL CHECK (opt IN ('+', '-', '*', ':')),
    bilangan2  NUMERIC(30,6) NOT NULL,
    hasil      NUMERIC(30,6) NOT NULL
);

-- Data contoh: hanya masuk jika tabel masih KOSONG (data lama tidak tersentuh).
-- Nilai hasil di sini hanya contoh awal; perhitungan aplikasi dilakukan di Java.
INSERT INTO operasi_aritmatika (bilangan1, opt, bilangan2, hasil)
SELECT a, o, b, h
FROM (VALUES
        (12,  '+', 5, 17),
        (20,  '-', 8, 12),
        (6,   '*', 7, 42),
        (15,  ':', 4, 3.75)
     ) AS contoh(a, o, b, h)
WHERE NOT EXISTS (SELECT 1 FROM operasi_aritmatika);

-- Cek isi tabel
SELECT * FROM operasi_aritmatika ORDER BY id;