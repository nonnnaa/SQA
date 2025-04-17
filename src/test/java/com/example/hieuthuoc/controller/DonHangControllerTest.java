package com.example.hieuthuoc.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;

import com.example.hieuthuoc.dto.*;
import com.example.hieuthuoc.entity.DonHang;
import com.example.hieuthuoc.service.DonHangService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class DonHangControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DonHangService donHangService;

    @Autowired
    private ObjectMapper objectMapper;

    private DonHangDTO mockDonHangDTO;
    private DonHang mockDonHang;
    private ResponseDTO<DonHang> mockResponse;
    private ResponseDTO<Void> mockVoidResponse;

    @BeforeEach
    void setup() {
        mockDonHangDTO = new DonHangDTO(); // có thể setup thêm field nếu cần
        mockDonHang = new DonHang();
        mockResponse = new ResponseDTO<>(200, "OK", mockDonHang);
        mockVoidResponse = new ResponseDTO<>(200, "Deleted", null);
    }

    /**
     * TC01 - Mục tiêu: Test /donhang/get?id=1 trả về đơn hàng đúng
     * Input: id = 1
     * Output: 200 OK, data != null
     * Ghi chú: mock donHangService.getById()
     */
    @Test
    @WithMockUser
    void getById_ShouldReturnDonHang() throws Exception {
        when(donHangService.getById(anyInt())).thenReturn(mockResponse);

        mockMvc.perform(get("/donhang/get").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").exists());
    }

    /**
     * TC02 - Mục tiêu: Test /donhang/list trả về danh sách đơn hàng
     * Input: SearchDTO rỗng (page = 0, size = 10,...)
     * Output: 200 OK, data là list rỗng
     * Ghi chú: kiểm tra trả về PageDTO
     */
    @Test
    @WithMockUser
    void getByTrangThaiGiaoHang_ShouldReturnPageDTO() throws Exception {
        SearchDTO searchDTO = new SearchDTO();
        PageDTO<List<DonHang>> pageDTO = new PageDTO<>();
        searchDTO.setKeyWord("thuốc cảm");
        searchDTO.setId(1);
        searchDTO.setCurrentPage(0);
        searchDTO.setSize(10);
        searchDTO.setSortedField("ngayTao");
        ResponseDTO<PageDTO<List<DonHang>>> response = new ResponseDTO<>(200, "OK", pageDTO);

        when(donHangService.getByTrangThaiGiaoHang(any())).thenReturn(response);

        mockMvc.perform(post("/donhang/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.list").isArray());
    }

    /**
     * TC03 - Mục tiêu: Test đổi trạng thái giao hàng
     * Input: DonHangDTO mock
     * Output: 200 OK, data đơn hàng
     */
    @Test
    @WithMockUser
    void changTrangThaiGiaoHang_ShouldReturnDonHang() throws Exception {
        when(donHangService.changTrangThaiGiaoHang(any())).thenReturn(mockResponse);

        mockMvc.perform(post("/donhang/changTrangThaiGiaoHang")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockDonHangDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    /**
     * TC04 - Mục tiêu: Test đổi trạng thái thanh toán
     * Input: DonHangDTO mock
     * Output: 200 OK, data đơn hàng
     */
    @Test
    @WithMockUser
    void changTrangThaiThanhToan_ShouldReturnDonHang() throws Exception {
        when(donHangService.changTrangThaiThanhToan(any())).thenReturn(mockResponse);

        mockMvc.perform(post("/donhang/changTrangThaiThanhToan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockDonHangDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    /**
     * TC05 - Mục tiêu: Test tạo mới đơn hàng
     * Input: DonHangDTO mock
     * Output: 200 OK, data đơn hàng
     */
    @Test
    @WithMockUser
    void createDonHang_ShouldReturnDonHang() throws Exception {
        when(donHangService.create(any())).thenReturn(mockResponse);

        mockMvc.perform(post("/donhang/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockDonHangDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    /**
     * TC06 - Mục tiêu: Test cập nhật đơn hàng
     * Input: DonHangDTO mock
     * Output: 200 OK, data đơn hàng
     */
    @Test
    @WithMockUser
    void updateDonHang_ShouldReturnDonHang() throws Exception {
        when(donHangService.update(any())).thenReturn(mockResponse);

        mockMvc.perform(put("/donhang/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockDonHangDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    /**
     * TC07 - Mục tiêu: Test xóa đơn hàng
     * Input: id = 1
     * Output: 200 OK, data = null
     */
    @Test
    @WithMockUser
    void deleteDonHang_ShouldReturnSuccess() throws Exception {
        when(donHangService.delete(anyInt())).thenReturn(mockVoidResponse);

        mockMvc.perform(delete("/donhang/delete").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Deleted"));
    }
}
