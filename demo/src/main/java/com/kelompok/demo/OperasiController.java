package com.kelompok.demo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class OperasiController {

    private static final List<String> OPERATOR_VALID = List.of("+", "-", "*", ":");
    private static final BigDecimal BATAS = new BigDecimal("999999999");

    @Autowired
    private OperasiRepository operasiRepository;

    // READ: halaman utama (form + riwayat dalam satu halaman)
    @GetMapping("/")
    public String index(Model model) {
        Operasi baru = new Operasi();
        baru.setOpt("+");
        isiHalaman(model, baru);
        return "Kalkulator";
    }

    // UPDATE (bagian 1): muat data lama ke form
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Operasi lama = operasiRepository.findById(id).orElse(null);
        if (lama == null) {
            return "redirect:/";
        }
        // Salin ke objek baru supaya angka tampil rapi di form (tanpa nol di belakang)
        Operasi salinan = new Operasi();
        salinan.setId(lama.getId());
        salinan.setBilangan1(new BigDecimal(Operasi.rapikan(lama.getBilangan1())));
        salinan.setOpt(lama.getOpt());
        salinan.setBilangan2(new BigDecimal(Operasi.rapikan(lama.getBilangan2())));
        isiHalaman(model, salinan);
        return "Kalkulator";
    }

    // CREATE + UPDATE (bagian 2): tombol "="
    // id kosong = data baru, id terisi = memperbarui data lama (hasil dihitung ulang)
    @PostMapping("/simpan")
    public String simpan(@ModelAttribute("operasi") Operasi form,
                         BindingResult binding,
                         RedirectAttributes ra) {

        String kembali = (form.getId() == null) ? "redirect:/" : "redirect:/edit/" + form.getId();

        // --- Validasi di sisi server (HTML saja tidak cukup) ---
        if (binding.hasErrors() || form.getBilangan1() == null || form.getBilangan2() == null) {
            ra.addFlashAttribute("error", "Bilangan 1 dan bilangan 2 harus diisi dengan angka.");
            return kembali;
        }
        if (form.getOpt() == null || !OPERATOR_VALID.contains(form.getOpt())) {
            ra.addFlashAttribute("error", "Operator harus salah satu dari + - * :");
            return kembali;
        }
        if (form.getBilangan1().abs().compareTo(BATAS) > 0 || form.getBilangan2().abs().compareTo(BATAS) > 0) {
            ra.addFlashAttribute("error", "Angka terlalu besar. Batasnya 999.999.999.");
            return kembali;
        }

        BigDecimal a = form.getBilangan1().setScale(6, RoundingMode.HALF_UP);
        BigDecimal b = form.getBilangan2().setScale(6, RoundingMode.HALF_UP);

        if (":".equals(form.getOpt()) && b.signum() == 0) {
            ra.addFlashAttribute("error", "Pembagian dengan nol tidak dapat dilakukan.");
            return kembali;
        }

        // --- Perhitungan dilakukan di Java, bukan di SQL ---
        BigDecimal hasil;
        switch (form.getOpt()) {
            case "+": hasil = a.add(b); break;
            case "-": hasil = a.subtract(b); break;
            case "*": hasil = a.multiply(b); break;
            default:  hasil = a.divide(b, 6, RoundingMode.HALF_UP); break;   // ":"
        }

        boolean dataBaru = (form.getId() == null);
        form.setBilangan1(a);
        form.setBilangan2(b);
        form.setHasil(hasil.setScale(6, RoundingMode.HALF_UP));

        Operasi tersimpan = operasiRepository.save(form);

        ra.addFlashAttribute("pesan", dataBaru ? "Hitungan disimpan." : "Hitungan diperbarui, hasil dihitung ulang.");
        ra.addFlashAttribute("hasilBaru", tersimpan.getHasilTampil());
        return "redirect:/";
    }

    // DELETE
    @PostMapping("/hapus/{id}")
    public String hapus(@PathVariable Long id, RedirectAttributes ra) {
        operasiRepository.deleteById(id);
        ra.addFlashAttribute("pesan", "Hitungan dihapus.");
        return "redirect:/";
    }

    // Data yang dibutuhkan halaman: riwayat (terbaru di atas) + objek untuk form
    private void isiHalaman(Model model, Operasi untukForm) {
        model.addAttribute("daftar", operasiRepository.findAll(Sort.by(Sort.Direction.DESC, "id")));
        model.addAttribute("operasi", untukForm);
    }
}