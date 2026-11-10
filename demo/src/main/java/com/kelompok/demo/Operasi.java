package com.kelompok.demo;

import jakarta.persistence.*;
import java.math.BigDecimal;

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

    @Column(name = "hasil", nullable = false, precision = 30, scale = 6)
    private BigDecimal hasil;

    // --- Getter dan Setter ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getBilangan1() { return bilangan1; }
    public void setBilangan1(BigDecimal bilangan1) { this.bilangan1 = bilangan1; }

    public String getOpt() { return opt; }
    public void setOpt(String opt) { this.opt = opt; }

    public BigDecimal getBilangan2() { return bilangan2; }
    public void setBilangan2(BigDecimal bilangan2) { this.bilangan2 = bilangan2; }

    public BigDecimal getHasil() { return hasil; }
    public void setHasil(BigDecimal hasil) { this.hasil = hasil; }

    // --- Format tampilan: 17.000000 menjadi 17, 3.750000 menjadi 3.75 ---
    // (bukan kolom database, hanya dipakai oleh halaman HTML)

    public String getBilangan1Tampil() { return rapikan(bilangan1); }
    public String getBilangan2Tampil() { return rapikan(bilangan2); }
    public String getHasilTampil()     { return rapikan(hasil); }

    public static String rapikan(BigDecimal nilai) {
        if (nilai == null) {
            return "";
        }
        return nilai.stripTrailingZeros().toPlainString();
    }
}