package com.example.hieuthuoc.controller;

import com.example.hieuthuoc.dto.LoaiThuocDTO;
import com.example.hieuthuoc.entity.DanhMucThuoc;
import com.example.hieuthuoc.entity.LoaiThuoc;
import com.example.hieuthuoc.repository.DanhMucThuocRepo;
import com.example.hieuthuoc.repository.LoaiThuocRepo;
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

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Rolls back database changes after each test
public class LoaiThuocControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LoaiThuocRepo loaiThuocRepository;

    @Autowired
    private DanhMucThuocRepo danhMucThuocRepository;

    private LoaiThuoc lt1, lt2;
    private LoaiThuocDTO ltDTO_create, ltDTO_update;
    private DanhMucThuoc danhMucThuocSample;

    @BeforeEach
    void setUp() {

        // Create DanhMucThuoc
        danhMucThuocSample = new DanhMucThuoc();
        danhMucThuocSample.setTenDanhMuc("Nhóm Thuốc Cơ Bản");
        danhMucThuocSample = danhMucThuocRepository.save(danhMucThuocSample); // Save to get ID

        // Create LoaiThuoc entities
        lt1 = new LoaiThuoc();
        lt1.setTenLoai("Thuốc kháng sinh nhóm Beta-lactam");
        lt1.setMoTa("Diệt khuẩn");
        lt1.setDanhMucThuoc(danhMucThuocSample);
        lt1 = loaiThuocRepository.save(lt1);

        lt2 = new LoaiThuoc();
        lt2.setTenLoai("Thuốc giảm đau hạ sốt");
        lt2.setMoTa("Giảm đau, hạ sốt thông thường");
        lt2.setDanhMucThuoc(danhMucThuocSample);
        lt2 = loaiThuocRepository.save(lt2);

        // LoaiThuoc DTOs
        ltDTO_create = new LoaiThuocDTO();
        ltDTO_create.setTenLoai("Thuốc chống nấm");
        ltDTO_create.setMoTa("Điều trị nhiễm nấm");
        ltDTO_create.setDanhMucThuocId(danhMucThuocSample.getId());

        ltDTO_update = new LoaiThuocDTO();
        ltDTO_update.setId(lt1.getId());
        ltDTO_update.setTenLoai("Thuốc kháng sinh nhóm Beta-lactam (Updated)");
        ltDTO_update.setMoTa("Diệt khuẩn updated");
        ltDTO_update.setDanhMucThuocId(danhMucThuocSample.getId());
    }

    // GET /list
    @Test
    @DisplayName("getAll - Thành công, trả về danh sách")
    void getAllLoaiThuocController_Success_ReturnsList() throws Exception {
        mockMvc.perform(get("/loaithuoc/list")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data", hasSize((int) loaiThuocRepository.count())))
        ;

    }

    // GET /search_by_ten_loai
    @Test
    @DisplayName("searchByTenLoai - Thành công, tìm thấy")
    void searchByTenLoaiController_Success_Found() throws Exception {
        String searchTerm = "Beta-lactam";

        List<LoaiThuoc> loaiThuocs = loaiThuocRepository.findAll();
        int cnt = 0;
        for(LoaiThuoc lt : loaiThuocs) {
            if(lt.getTenLoai().contains(searchTerm)) {
                cnt++;
            }
        }

        mockMvc.perform(get("/loaithuoc/search_by_ten_loai")
                        .param("tenLoai", searchTerm)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data", hasSize(cnt)))
                .andExpect(jsonPath("$.msg").value("Thành công"))
                .andExpect(jsonPath("$.data[0].id").value(lt1.getId()));
    }

    @Test
    @DisplayName("searchByTenLoai - Thành công, không tìm thấy")
    void searchByTenLoaiController_Success_NotFound() throws Exception {
        String searchTerm = "Không có";

        mockMvc.perform(get("/loaithuoc/search_by_ten_loai")
                        .param("tenLoai", searchTerm)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.data", hasSize(0)));

    }

    @Test
    @DisplayName("searchByTenLoai - Thiếu tham số")
    void searchByTenLoaiController_MissingParameter() throws Exception {
        mockMvc.perform(get("/loaithuoc/search_by_ten_loai")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // POST /create
    @Test
    @DisplayName("create - Thành công")
    void createLoaiThuocController_Success() throws Exception {
        mockMvc.perform(post("/loaithuoc/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ltDTO_create))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.tenLoai").value(ltDTO_create.getTenLoai()))
                .andExpect(jsonPath("$.data.moTa").value(ltDTO_create.getMoTa()));

        // Verify database state
        assertEquals(12, loaiThuocRepository.count());
    }

    @Test
    @DisplayName("create - Lỗi Conflict (Tên đã tồn tại)")
    void createLoaiThuocController_Fail_Conflict() throws Exception {
        ltDTO_create.setTenLoai(lt1.getTenLoai()); // Duplicate name

        mockMvc.perform(post("/loaithuoc/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ltDTO_create))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.msg").value("Loại thuốc đã tồn tại"));

        // Verify no new record added
        assertEquals(11, loaiThuocRepository.count());
    }

    @Test
    @DisplayName("create - Lỗi Bad Request (DanhMucThuocId không hợp lệ)")
    void createLoaiThuocController_Fail_InvalidDanhMucId() throws Exception {
        ltDTO_create.setDanhMucThuocId(999); // Invalid ID

        mockMvc.perform(post("/loaithuoc/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ltDTO_create))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("Danh mục thuốc không tồn tại"));

        // Verify no new record added
        assertEquals(2, loaiThuocRepository.count());
    }

    // PUT /update
    @Test
    @DisplayName("update - Thành công")
    void updateLoaiThuocController_Success() throws Exception {
        mockMvc.perform(put("/loaithuoc/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ltDTO_update))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.id").value(ltDTO_update.getId()))
                .andExpect(jsonPath("$.data.tenLoai").value(ltDTO_update.getTenLoai()))
                .andExpect(jsonPath("$.data.moTa").value(ltDTO_update.getMoTa()));

        // Verify database state
        LoaiThuoc updated = loaiThuocRepository.findById(lt1.getId()).orElseThrow();
        assertEquals(ltDTO_update.getTenLoai(), updated.getTenLoai());
    }

    @Test
    @DisplayName("update - Lỗi Not Found")
    void updateLoaiThuocController_Fail_NotFound() throws Exception {
        LoaiThuocDTO dtoNotFound = new LoaiThuocDTO();
        dtoNotFound.setId(99);
        dtoNotFound.setTenLoai("Không quan trọng");
        dtoNotFound.setDanhMucThuocId(danhMucThuocSample.getId());

        mockMvc.perform(put("/loaithuoc/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoNotFound))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.msg").value("Không tìm thấy loại thuốc"));
    }

    // DELETE /delete
    @Test
    @DisplayName("delete - Thành công")
    void deleteLoaiThuocController_Success() throws Exception {
        mockMvc.perform(delete("/loaithuoc/delete")
                        .param("id", String.valueOf(lt1.getId()))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("Xóa loại thuốc thành công"));

        // Verify database state
        assertFalse(loaiThuocRepository.findById(lt1.getId()).isPresent());
    }

    @Test
    @DisplayName("delete - Lỗi Not Found")
    void deleteLoaiThuocController_Fail_NotFound() throws Exception {
        int idToDelete = 99;

        mockMvc.perform(delete("/loaithuoc/delete")
                        .param("id", String.valueOf(idToDelete))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.msg").value("Không tìm thấy loại thuốc để xóa"));

        assertFalse(loaiThuocRepository.findById(99).isEmpty());
    }

    @Test
    @DisplayName("delete - Thiếu tham số id")
    void deleteLoaiThuocController_MissingParameter() throws Exception {
        mockMvc.perform(delete("/loaithuoc/delete")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("Vui lòng truyền id để xóa"))
        ;
    }

    @Test
    @DisplayName("delete - Tham số id không hợp lệ")
    void deleteLoaiThuocController_InvalidParameterType() throws Exception {
        mockMvc.perform(delete("/loaithuoc/delete")
                        .param("id", "xyz") // Invalid ID
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("Tham số id không hợp lệ"))
        ;
    }
}