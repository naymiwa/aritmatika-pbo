-- =====================================================
-- PBO Praktikum 11 : Kalkulator CRUD
-- Aman dijalankan berulang: TIDAK ada DROP TABLE dan TIDAK ada TRUNCATE,
-- jadi data yang sudah ada tidak akan hilang atau tertimpa.
--
-- Catatan: sesuai soal tugas, PostgreSQL HANYA menyimpan bilangan1, opt (operator),
-- dan bilangan2. Kolom "hasil" TIDAK disimpan di database — hasil dihitung ulang
-- oleh aplikasi (Java) setiap kali data ditampilkan.
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
    bilangan2  NUMERIC(30,6) NOT NULL
);

-- Migrasi untuk database yang sudah pernah dibuat SEBELUM perubahan ini
-- (dulu kolom "hasil" ikut disimpan). Aman dijalankan berulang: kalau kolom
-- "hasil" sudah tidak ada, baris ini tidak melakukan apa-apa.
ALTER TABLE operasi_aritmatika DROP COLUMN IF EXISTS hasil;

-- Data contoh: hanya masuk jika tabel masih KOSONG (data lama tidak tersentuh).
-- Hasil TIDAK dimasukkan di sini karena tidak lagi disimpan di database;
-- aplikasi menghitungnya sendiri dari bilangan1, opt, bilangan2.
INSERT INTO operasi_aritmatika (bilangan1, opt, bilangan2)
SELECT a, o, b
FROM (VALUES
        (12,  '+', 5),
        (20,  '-', 8),
        (6,   '*', 7),
        (15,  ':', 4)
     ) AS contoh(a, o, b)
WHERE NOT EXISTS (SELECT 1 FROM operasi_aritmatika);

-- Cek isi tabel
SELECT * FROM operasi_aritmatika ORDER BY id;
