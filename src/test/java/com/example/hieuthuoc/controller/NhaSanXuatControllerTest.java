package com.example.hieuthuoc.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;

import com.example.hieuthuoc.dto.NhaSanXuatDTO;
import com.example.hieuthuoc.dto.ResponseDTO;
import com.example.hieuthuoc.entity.NhaSanXuat;
import com.example.hieuthuoc.service.NhaSanXuatService;
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
class NhaSanXuatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NhaSanXuatService nhaSanXuatService;

    private NhaSanXuatDTO mockDTO;
    private NhaSanXuat mockEntity;
    private ResponseDTO<NhaSanXuat> mockEntityResponse;
    private ResponseDTO<List<NhaSanXuat>> mockListResponse;
    private ResponseDTO<Void> mockVoidResponse;

    @BeforeEach
    void setup() {
        mockDTO = new NhaSanXuatDTO();
        mockEntity = new NhaSanXuat();
        mockEntityResponse = new ResponseDTO<>(200, "OK", mockEntity);
        mockListResponse = new ResponseDTO<>(200, "OK", Collections.singletonList(mockEntity));
        mockVoidResponse = new ResponseDTO<>(200, "Deleted", null);
    }

    /**
     * TC01 - Mục tiêu: Test GET /nhasanxuat/list trả về danh sách NSX
     * Input: none
     * Output: status 200, data là mảng
     * Ghi chú: mock danh sách có 1 phần tử
     */
    @Test
    @WithMockUser
    void getAll_ShouldReturnList() throws Exception {
        when(nhaSanXuatService.getAll()).thenReturn(mockListResponse);

        mockMvc.perform(get("/nhasanxuat/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    /**
     * TC02 - Mục tiêu: Test GET /nhasanxuat/search_by_ten_nha_san_xuat với tên hợp lệ
     * Input: tenNhaSanXuat = "Pfizer"
     * Output: status 200, data mảng có ít nhất 1 phần tử
     * Ghi chú: test tìm kiếm theo tên
     */
    @Test
    @WithMockUser
    void searchByTenNhaSanXuat_ShouldReturnList() throws Exception {
        when(nhaSanXuatService.searchByTenNhaSanXuat("Pfizer")).thenReturn(mockListResponse);

        mockMvc.perform(get("/nhasanxuat/search_by_ten_nha_san_xuat")
                        .param("tenNhaSanXuat", "Pfizer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    /**
     * TC03 - Mục tiêu: Test POST /nhasanxuat/create tạo NSX thành công
     * Input: JSON từ NhaSanXuatDTO
     * Output: status 200, data != null
     * Ghi chú: mock response create thành công
     */
    @Test
    @WithMockUser
    void create_ShouldReturnCreatedEntity() throws Exception {
        when(nhaSanXuatService.create(any())).thenReturn(mockEntityResponse);

        mockMvc.perform(post("/nhasanxuat/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    /**
     * TC04 - Mục tiêu: Test PUT /nhasanxuat/update cập nhật NSX thành công
     * Input: JSON từ NhaSanXuatDTO
     * Output: status 200, data != null
     * Ghi chú: test cập nhật thành công
     */
    @Test
    @WithMockUser
    void update_ShouldReturnUpdatedEntity() throws Exception {
        when(nhaSanXuatService.update(any())).thenReturn(mockEntityResponse);

        mockMvc.perform(put("/nhasanxuat/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    /**
     * TC05 - Mục tiêu: Test DELETE /nhasanxuat/delete?id=1 xóa NSX thành công
     * Input: id = 1
     * Output: status 200, msg = "Deleted"
     * Ghi chú: test xóa hợp lệ
     */
    @Test
    @WithMockUser
    void delete_ShouldReturnSuccess() throws Exception {
        when(nhaSanXuatService.delete(anyInt())).thenReturn(mockVoidResponse);

        mockMvc.perform(delete("/nhasanxuat/delete").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Deleted"));
    }

    /**
     * TC06 - Mục tiêu: Test DELETE /nhasanxuat/delete không có param id
     * Input: thiếu param
     * Output: status 400
     * Ghi chú: kiểm tra validate thiếu ID
     */
    @Test
    @WithMockUser
    void delete_MissingParam_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(delete("/nhasanxuat/delete"))
                .andExpect(status().isBadRequest());
    }

    /**
     * TC07 - Mục tiêu: Test GET /nhasanxuat/search_by_ten_nha_san_xuat thiếu param
     * Input: không có tenNhaSanXuat
     * Output: 400 Bad Request
     * Ghi chú: test validation query param
     */
    @Test
    @WithMockUser
    void searchByTenNhaSanXuat_MissingParam_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/nhasanxuat/search_by_ten_nha_san_xuat"))
                .andExpect(status().isBadRequest());
    }
}
