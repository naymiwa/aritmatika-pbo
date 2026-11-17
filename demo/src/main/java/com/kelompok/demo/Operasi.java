package com.kelompok.demo;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "operasi_aritmatika")
public class Operasi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bilangan1", nullable = false, precision = 30, scale = 6)
    private BigDecimal bilangan1;

    // Simbol operasi: + - * :   (dinamai "opt" agar tidak bentrok dengan kata kunci SQL)
    @Column(name = "opt", nullable = false, length = 1)
    private String opt;

    @Column(name = "bilangan2", nullable = false, precision = 30, scale = 6)
    private BigDecimal bilangan2;

    // Catatan: "hasil" SENGAJA TIDAK disimpan di database.
    // Sesuai soal tugas, PostgreSQL hanya menyimpan bilangan1, operator (opt), dan bilangan2.
    // Hasil dihitung ulang setiap kali data dibaca/ditampilkan (lihat getHasil() di bawah),
    // karena field ini tidak dianotasi @Column/@Id maka Hibernate (access type FIELD,
    // ditentukan dari lokasi @Id) otomatis mengabaikannya saat INSERT/UPDATE.

    // --- Getter dan Setter ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getBilangan1() { return bilangan1; }
    public void setBilangan1(BigDecimal bilangan1) { this.bilangan1 = bilangan1; }

    public String getOpt() { return opt; }
    public void setOpt(String opt) { this.opt = opt; }

    public BigDecimal getBilangan2() { return bilangan2; }
    public void setBilangan2(BigDecimal bilangan2) { this.bilangan2 = bilangan2; }

    // Dihitung on-the-fly dari bilangan1, opt, bilangan2 — bukan dibaca dari kolom database.
    public BigDecimal getHasil() {
        if (bilangan1 == null || bilangan2 == null || opt == null) {
            return null;
        }
        switch (opt) {
            case "+": return bilangan1.add(bilangan2);
            case "-": return bilangan1.subtract(bilangan2);
            case "*": return bilangan1.multiply(bilangan2);
            case ":":
                if (bilangan2.signum() == 0) {
                    return null; // hindari ArithmeticException; validasi pembagi nol sudah dicegah saat simpan
                }
                return bilangan1.divide(bilangan2, 6, RoundingMode.HALF_UP);
            default: return null;
        }
    }

    // --- Format tampilan: 17.000000 menjadi 17, 3.750000 menjadi 3.75 ---
    // (bukan kolom database, hanya dipakai oleh halaman HTML)

    public String getBilangan1Tampil() { return rapikan(bilangan1); }
    public String getBilangan2Tampil() { return rapikan(bilangan2); }
    public String getHasilTampil()     { return rapikan(getHasil()); }

    public static String rapikan(BigDecimal nilai) {
        if (nilai == null) {
            return "";
        }
        return nilai.stripTrailingZeros().toPlainString();
    }
}