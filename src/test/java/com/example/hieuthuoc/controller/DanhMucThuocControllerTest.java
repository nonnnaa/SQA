package com.example.hieuthuoc.controller;

import com.example.hieuthuoc.dto.DanhMucThuocDTO;
import com.example.hieuthuoc.entity.DanhMucThuoc;
import com.example.hieuthuoc.repository.DanhMucThuocRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Rolls back database changes after each test
public class DanhMucThuocControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DanhMucThuocRepo danhMucThuocRepository;

    private DanhMucThuoc dm1, dm2;
    private DanhMucThuocDTO dmDTO_create, dmDTO_update;

    @BeforeEach
    void setUp() {
        // Create DanhMucThuoc entities
        dm1 = new DanhMucThuoc();
        dm1.setTenDanhMuc("Thuốc Kháng Sinh");
        dm1.setMoTa("Nhóm thuốc điều trị nhiễm khuẩn");
        dm1.setLoaiThuocs(Collections.emptyList());
//        dm1 = danhMucThuocRepository.save(dm1);

        dm2 = new DanhMucThuoc();
        dm2.setTenDanhMuc("Thuốc Giảm Đau");
        dm2.setMoTa("Nhóm thuốc giảm các loại đau");
        dm2.setLoaiThuocs(Collections.emptyList());
//        dm2 = danhMucThuocRepository.save(dm2);

        // Create DanhMucThuoc DTOs
        dmDTO_create = new DanhMucThuocDTO();
        dmDTO_create.setId(null);
        dmDTO_create.setTenDanhMuc("Thuốc Tim Mạch");
        dmDTO_create.setMoTa("Nhóm thuốc cho bệnh tim");

        dmDTO_update = new DanhMucThuocDTO();
        dmDTO_update.setId(3);
        dmDTO_update.setTenDanhMuc("Thuốc Kháng Sinh Updated");
        dmDTO_update.setMoTa("Mô tả đã cập nhật");
    }

    @Test
    void getAllDmtController_Success_ReturnsList() throws Exception {
        mockMvc.perform(get("/danhmucthuoc/list")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("Thành công"))
                .andExpect(jsonPath("$.data", hasSize(3)))
                ;

        // Verify database state
        assertEquals(3, danhMucThuocRepository.count());
    }

    @Test
    @DisplayName("getAll - Thành công, trả về danh sách rỗng")
    void getAllDmtController_Success_ReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/danhmucthuoc/list")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("Thành công"))
                .andExpect(jsonPath("$.data", hasSize(0)));

        // Verify database state
        assertEquals(0, danhMucThuocRepository.count());
    }

    @Test
    @DisplayName("getDanhMucAnhLoaiThuoc - Thành công, trả về danh sách")
    void getDanhMucAnhLoaiThuocController_Success_ReturnsList() throws Exception {
        mockMvc.perform(get("/danhmucthuoc/get_danh_muc_and_loai_thuoc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data", hasSize(3)))
                ;
    }

    @Test
    void getDanhMucAnhLoaiThuocController_Success_ReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/danhmucthuoc/get_danh_muc_and_loai_thuoc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data", hasSize(0)));
    }

    @Test
    @DisplayName("searchByTenDanhMuc - Thành công, tìm thấy")
    void searchByTenDanhMucController_Success_Found() throws Exception {
        String searchTerm = "Dược mỹ phẩm";

        mockMvc.perform(get("/danhmucthuoc/search_by_ten_danh_muc")
                        .param("tenDanhMuc", searchTerm)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id").value(3));
    }

    @Test
    @DisplayName("searchByTenDanhMuc - Thành công, không tìm thấy")
    void searchByTenDanhMucController_Success_NotFound() throws Exception {
        String searchTerm = "Không Tồn Tại";

        mockMvc.perform(get("/danhmucthuoc/search_by_ten_danh_muc")
                        .param("tenDanhMuc", searchTerm)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.data", hasSize(0)));

        assertTrue(danhMucThuocRepository.searchByTenDanhMuc(searchTerm).isEmpty());
    }

    @Test
    @DisplayName("searchByTenDanhMuc - Thiếu tham số")
    void searchByTenDanhMucController_MissingParameter() throws Exception {
        mockMvc.perform(get("/danhmucthuoc/search_by_ten_danh_muc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("Vui lòng truyền tham số để tìm kiếm"))
        ;
    }

    @Test
    void createDanhMucController_Success() throws Exception {
        mockMvc.perform(post("/danhmucthuoc/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dmDTO_create))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.msg").value("Tạo danh mục thuốc thành công"))
                .andExpect(jsonPath("$.data.tenDanhMuc").value(dmDTO_create.getTenDanhMuc()))
                .andExpect(jsonPath("$.data.moTa").value(dmDTO_create.getMoTa()));

        // Verify database state
        assertEquals(4, danhMucThuocRepository.count());
    }

    @Test
    void createDanhMucController_Fail_Conflict() throws Exception {
        dmDTO_create.setTenDanhMuc(dm1.getTenDanhMuc()); // Duplicate name

        mockMvc.perform(post("/danhmucthuoc/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dmDTO_create))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.msg").value("Tên danh mục đã tồn tại"));

        // Verify no new record added
        assertEquals(2, danhMucThuocRepository.count());
    }

    @Test
    @DisplayName("update - Thành công")
    void updateDanhMucController_Success() throws Exception {
        mockMvc.perform(put("/danhmucthuoc/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dmDTO_update))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.id").value(dmDTO_update.getId()))
                .andExpect(jsonPath("$.data.tenDanhMuc").value(dmDTO_update.getTenDanhMuc()))
                .andExpect(jsonPath("$.data.moTa").value(dmDTO_update.getMoTa()));

        // Verify database state
        DanhMucThuoc updated = danhMucThuocRepository.findById(3).orElseThrow();
        assertEquals(dmDTO_update.getTenDanhMuc(), updated.getTenDanhMuc());
    }

    @Test
    @DisplayName("update - Lỗi Not Found")
    void updateDanhMucController_Fail_NotFound() throws Exception {
        DanhMucThuocDTO dtoNotFound = new DanhMucThuocDTO();
        dtoNotFound.setId(99);
        dtoNotFound.setTenDanhMuc("Tên không tồn tại");
        dtoNotFound.setMoTa("Mô tả");

        mockMvc.perform(put("/danhmucthuoc/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoNotFound))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.msg").value("Không tìm thấy danh mục"));
    }

    @Test
    @DisplayName("delete - Thành công")
    void deleteDanhMucController_Success() throws Exception {
        mockMvc.perform(delete("/danhmucthuoc/delete")
                        .param("id", String.valueOf(1))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("Xóa danh mục thuốc thành công"));

        // Verify database state
        assertFalse(danhMucThuocRepository.findById(1).isPresent());
    }

    @Test
    @DisplayName("delete - Lỗi Not Found")
    void deleteDanhMucController_Fail_NotFound() throws Exception {
        Integer idToDelete = 99;

        mockMvc.perform(delete("/danhmucthuoc/delete")
                        .param("id", String.valueOf(idToDelete))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.msg").value("Không tìm thấy danh mục thuốc để xóa"));
    }

    @Test
    @DisplayName("delete - Thiếu tham số id")
    void deleteDanhMucController_MissingParameter() throws Exception {
        mockMvc.perform(delete("/danhmucthuoc/delete")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("Vui lòng truyền tham số id để xóa"))
        ;
    }

    @Test
    @DisplayName("delete - Tham số id không hợp lệ (không phải số)")
    void deleteDanhMucController_InvalidParameterType() throws Exception {
        mockMvc.perform(delete("/danhmucthuoc/delete")
                        .param("id", "abc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("Vui lòng truyền tham số id hợp lệ"))
        ;
    }
}