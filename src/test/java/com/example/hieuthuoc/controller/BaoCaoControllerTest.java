package com.example.hieuthuoc.controller;

import com.example.hieuthuoc.dto.DoanhThuDTO;
import com.example.hieuthuoc.repository.DonHangRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BaoCaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DonHangRepo donHangRepo;

    private Date ngayTest;

    @BeforeEach
    void setup() throws Exception {
        ngayTest = new SimpleDateFormat("yyyy-MM-dd").parse("2025-04-01");
    }

    @Test
    void testDoanhThuTheoNgay_NgayKhongHopLe() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/baocao/doanhthutheongay")
                        .param("ngay", "2025-02-29"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDoanhThuTheoNgay_CoDon() throws Exception {
        DoanhThuDTO dto = new DoanhThuDTO(1, 100000.0, 2L, 0L);
        when(donHangRepo.doanhThuTheoNgay(ngayTest)).thenReturn(Arrays.asList(dto));

        mockMvc.perform(MockMvcRequestBuilders.get("/baocao/doanhthutheongay")
                        .param("ngay", "2025-04-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].thoiGian").value(1))
                .andExpect(jsonPath("$.data[0].tongDoanhThu").value(100000.0));
    }

    @Test
    void testDoanhThuTheoNgay_KhongCoDon() throws Exception {
        when(donHangRepo.doanhThuTheoNgay(ngayTest)).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/baocao/doanhthutheongay")
                        .param("ngay", "2025-04-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }
    
    @Test
    void testDoanhThuTheoThang_ThangKhongHopLe() throws Exception {
        // Arrange
        int nam = 2025;
        int thang = 13;  // Tháng không hợp lệ

        // Act
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/doanhthutheothang")
                        .param("nam", String.valueOf(nam))
                        .param("thang", String.valueOf(thang)))
                .andExpect(status().isBadRequest()) // Kiểm tra mã lỗi HTTP 400
                .andReturn();

        // Assert
        String responseBody = result.getResponse().getContentAsString();

        // Kiểm tra thông báo lỗi trong response
        assertTrue(responseBody.contains("Tháng không hợp lệ"));
    }


    @Test
    void testDoanhThuTheoThang_CoDon() throws Exception {
        DoanhThuDTO dto = new DoanhThuDTO(4, 500000.0, 5L, 1L);
        when(donHangRepo.doanhThuTheoThang(2025, 4)).thenReturn(Arrays.asList(dto));

        mockMvc.perform(MockMvcRequestBuilders.get("/baocao/doanhthutheothang")
                        .param("nam", "2025")
                        .param("thang", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].tongDoanhThu").value(500000.0));
    }

    @Test
    void testDoanhThuTheoThang_KhongCoDon() throws Exception {
        when(donHangRepo.doanhThuTheoThang(2025, 3)).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/baocao/doanhthutheothang")
                        .param("nam", "2025")
                        .param("thang", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }


    @Test
    void testDoanhThuTheoNam_NamTrongTuongLai() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/doanhthutheonam")
                        .param("nam", "2026")) // Năm trong tương lai
                .andExpect(status().isBadRequest()) // Kiểm tra mã trạng thái 400
                .andExpect(content().string("Năm không hợp lệ")); // Kiểm tra thông báo lỗi
    }

    @Test
    void testDoanhThuTheoNam_NamKhongTonTai() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/doanhthutheonam")
                        .param("nam", "2021")) // Năm không tồn tại trong hệ thống
                .andExpect(status().isBadRequest()) // Kiểm tra mã trạng thái 400
                .andExpect(content().string("Năm không tồn tại trong hệ thống")); // Kiểm tra thông báo lỗi
    }



    @Test
    void testDoanhThuTheoNam_CoDon() throws Exception {
        DoanhThuDTO dto = new DoanhThuDTO(4, 1200000.0, 10L, 1L); // tháng 4 có đơn
        when(donHangRepo.doanhThuTheoNam(2025)).thenReturn(Arrays.asList(dto));

        mockMvc.perform(MockMvcRequestBuilders.get("/baocao/doanhthutheonam")
                        .param("nam", "2025"))
                .andDo(print()) // ✅ In toàn bộ request/response ra console
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].thoiGian").value(4))
                .andExpect(jsonPath("$.data[0].tongDoanhThu").value(1200000.0));
    }


    @Test
    void testDoanhThuTheoNam_KhongDon() throws Exception {
        when(donHangRepo.doanhThuTheoNam(2023)).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/baocao/doanhthutheonam")
                        .param("nam", "2023"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
