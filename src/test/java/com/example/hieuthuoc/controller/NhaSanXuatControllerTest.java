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
     * TC34 - Mục tiêu: Test GET /nhasanxuat/list trả về danh sách NSX
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
     * TC35 - Mục tiêu: Test GET /nhasanxuat/search_by_ten_nha_san_xuat với tên hợp lệ
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
     * TC36 - Mục tiêu: Tìm kiếm với tên không tồn tại
     * Input: tenNhaSanXuat = "Không Tồn Tại"
     * Expected output: 200 OK, data là mảng rỗng
     * Output: 200 OK
     * Ghi chú: kiểm tra hệ thống xử lý khi không có kết quả
     */
    @Test
    @WithMockUser
    void searchByTenNhaSanXuat_ShouldReturnEmptyList_WhenNotFound() throws Exception {
        when(nhaSanXuatService.searchByTenNhaSanXuat("Không Tồn Tại"))
                .thenReturn(new ResponseDTO<>(200, "OK", Collections.emptyList()));

        mockMvc.perform(get("/nhasanxuat/search_by_ten_nha_san_xuat")
                        .param("tenNhaSanXuat", "Không Tồn Tại"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    /**
     * TC37 - Mục tiêu: Tìm kiếm với tên rỗng
         * Input: tenNhaSanXuat = ""
     * Expected output: 400 Bad Request nếu có validate
     * Output: tùy hệ thống
     * Ghi chú: kiểm tra phản hồi khi param rỗng
     */
    @Test
    @WithMockUser
    void searchByTenNhaSanXuat_ShouldReturnBadRequest_WhenBlank() throws Exception {
        mockMvc.perform(get("/nhasanxuat/search_by_ten_nha_san_xuat")
                        .param("tenNhaSanXuat", ""))
                .andExpect(status().isBadRequest());
    }


    /**
     * TC38 - Mục tiêu: Test POST /nhasanxuat/create tạo NSX thành công
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
     * TC39 - Mục tiêu: Tạo NSX với tên null
     * Input: NhaSanXuatDTO có tenNhaSanXuat = null
     * Expected output: 400 Bad Request nếu validate tồn tại tên
     * Output: Có thể là 500 nếu backend không validate
     * Ghi chú: Kiểm tra xử lý null khi tạo mới
     */
    @Test
    @WithMockUser
    void createNhaSanXuat_ShouldFail_WhenTenNhaSanXuatIsNull() throws Exception {
        NhaSanXuatDTO dto = new NhaSanXuatDTO();
        dto.setTenNhaSanXuat(null);

        mockMvc.perform(post("/nhasanxuat/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    /**
     * TC40 - Mục tiêu: Tạo NSX trùng tên (giả lập tồn tại sẵn trong hệ thống)
     * Input: tenNhaSanXuat = "Test NSX" (đã có)
     * Expected output: 409 Conflict nếu có logic kiểm tra trùng tên
     * Output: tùy hệ thống
     * Ghi chú: Cần mock response trả về 409
     */
    @Test
    @WithMockUser
    void createNhaSanXuat_ShouldFail_WhenTenNhaSanXuatIsDuplicate() throws Exception {
        when(nhaSanXuatService.create(any())).thenReturn(
                new ResponseDTO<>(409, "Tên nhà sản xuất đã tồn tại", null));

        mockMvc.perform(post("/nhasanxuat/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.msg").value("Tên nhà sản xuất đã tồn tại"));
    }
        /**
         * TC41 - Mục tiêu: Test PUT /nhasanxuat/update cập nhật NSX thành công
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
     * TC42 - Mục tiêu: Cập nhật NSX với ID không tồn tại
     * Input: NhaSanXuatDTO có ID = 999
     * Expected output: 409 Conflict, msg = "Không tìm thấy nhà sản xuất"
     * Output: 409 Conflict
     * Ghi chú: Kiểm tra logic khi cập nhật vào ID không có
     */
    @Test
    @WithMockUser
    void updateNhaSanXuat_ShouldReturnConflict_WhenIdNotFound() throws Exception {
        NhaSanXuatDTO dto = new NhaSanXuatDTO();
        dto.setId(999);
        dto.setTenNhaSanXuat("Không có");

        when(nhaSanXuatService.update(any())).thenReturn(
                new ResponseDTO<>(409, "Không tìm thấy nhà sản xuất", null));

        mockMvc.perform(put("/nhasanxuat/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.msg").value("Không tìm thấy nhà sản xuất"));
    }

    /**
     * TC43 - Mục tiêu: Cập nhật NSX với tên null
     * Input: NhaSanXuatDTO có ID = 1, tenNhaSanXuat = null
     * Expected output: 400 Bad Request nếu có validate
     * Output: tùy logic hệ thống
     * Ghi chú: Test field bắt buộc bị null
     */
    @Test
    @WithMockUser
    void updateNhaSanXuat_ShouldFail_WhenTenNhaSanXuatIsNull() throws Exception {
        NhaSanXuatDTO dto = new NhaSanXuatDTO();
        dto.setId(1);
        dto.setTenNhaSanXuat(null);

        mockMvc.perform(put("/nhasanxuat/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    /**
     * TC44 - Mục tiêu: Cập nhật NSX với ID âm
     * Input: ID = -10
     * Expected output: 400 hoặc 200 tùy validate
     * Output: 200 OK nếu không validate
     * Ghi chú: Xác minh hệ thống phản hồi với ID không hợp lệ
     */
    @Test
    @WithMockUser
    void updateNhaSanXuat_ShouldSucceed_WhenIdIsNegative() throws Exception {
        NhaSanXuatDTO dto = new NhaSanXuatDTO();
        dto.setId(-10);
        dto.setTenNhaSanXuat("Tên âm");

        NhaSanXuat nsx = new NhaSanXuat();
        nsx.setId(-10);
        nsx.setTenNhaSanXuat("Tên âm");

        when(nhaSanXuatService.update(any())).thenReturn(
                new ResponseDTO<>(200, "OK", nsx));

        mockMvc.perform(put("/nhasanxuat/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(-10));
    }

    /**
     * TC45 - Mục tiêu: Cập nhật NSX trùng tên
     * Input: tenNhaSanXuat = "Tên Trùng" đã tồn tại trong hệ thống
     * Expected output: 409 Conflict nếu có kiểm tra trùng tên
     * Output: 409 Conflict
     * Ghi chú: Kiểm tra duplicate name khi update
     */
    @Test
    @WithMockUser
    void updateNhaSanXuat_ShouldFail_WhenTenNhaSanXuatIsDuplicate() throws Exception {
        NhaSanXuatDTO dto = new NhaSanXuatDTO();
        dto.setId(1);
        dto.setTenNhaSanXuat("Tên Trùng");

        when(nhaSanXuatService.update(any())).thenReturn(
                new ResponseDTO<>(409, "Tên nhà sản xuất đã tồn tại", null));

        mockMvc.perform(put("/nhasanxuat/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.msg").value("Tên nhà sản xuất đã tồn tại"));
    }
    /**
     * TC46 - Mục tiêu: Test DELETE /nhasanxuat/delete?id=1 xóa NSX thành công
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
     * TC47 - Mục tiêu: Test DELETE /nhasanxuat/delete không có param id
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
}
