package com.example.hieuthuoc.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.hieuthuoc.dto.DoanhThuDTO;
import com.example.hieuthuoc.repository.DonHangRepo;

@WebMvcTest(BaoCaoController.class)
public class TestBaoCaoController {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DonHangRepo donHangRepo;

    private List<DoanhThuDTO> mockDoanhThuDTOs;

    @BeforeEach
    void setUp() {
        // Create mock data using the constructor with all required parameters
        DoanhThuDTO doanhThuDTO = new DoanhThuDTO(
                1, // thoiGian
                1000.0, // tongDoanhThu
                5L, // tongDonHang
                1L  // tongDonHangTraLai
        );
        mockDoanhThuDTOs = Arrays.asList(doanhThuDTO);
    }

    @Test
    void doanhThuTheoNgay_ShouldReturnDoanhThuList() throws Exception {
        // Given
        Date testDate = new Date();
        when(donHangRepo.doanhThuTheoNgay(any(Date.class))).thenReturn(mockDoanhThuDTOs);

        // When & Then
        mockMvc.perform(get("/baocao/doanhthutheongay")
                        .param("ngay", "2024-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("Thành công."))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void doanhThuTheoThang_ShouldReturnDoanhThuList() throws Exception {
        // Given
        when(donHangRepo.doanhThuTheoThang(anyInt(), anyInt())).thenReturn(mockDoanhThuDTOs);

        // When & Then
        mockMvc.perform(get("/baocao/doanhthutheothang")
                        .param("nam", "2024")
                        .param("thang", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("Thành công."))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void doanhThuTheoNam_ShouldReturnDoanhThuList() throws Exception {
        // Given
        when(donHangRepo.doanhThuTheoNam(anyInt())).thenReturn(mockDoanhThuDTOs);

        // When & Then
        mockMvc.perform(get("/baocao/doanhthutheonam")
                        .param("nam", "2024"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("Thành công."))
                .andExpect(jsonPath("$.data").exists());
    }
}