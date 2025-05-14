package com.example.hieuthuoc.controller;

import com.example.hieuthuoc.dto.*;
import com.example.hieuthuoc.entity.*;
import com.example.hieuthuoc.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ThuocControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ThuocRepo thuocRepository;

    @Autowired
    private LoaiThuocRepo loaiThuocRepository;

    @Autowired
    private DanhMucThuocRepo danhMucThuocRepository;

    @Autowired
    private ThanhPhanThuocRepo thanhPhanThuocRepository;

    @Autowired
    private DoiTuongRepo doiTuongRepository;

    private LoaiThuoc loaiThuoc;
    private ThanhPhanThuoc thanhPhanThuoc;
    private DoiTuong doiTuong;
    private Thuoc thuoc;

    @BeforeEach
    public void setup() {
        // Create DanhMucThuoc
        DanhMucThuoc danhMucThuoc = new DanhMucThuoc();
        danhMucThuoc.setTenDanhMuc("Thuốc Giảm Đau");
        danhMucThuoc.setMoTa("Nhóm thuốc giảm đau");
        danhMucThuoc = danhMucThuocRepository.save(danhMucThuoc);

        // Create LoaiThuoc
        loaiThuoc = new LoaiThuoc();
        loaiThuoc.setTenLoai("Thuốc giảm đau hạ sốt");
        loaiThuoc.setMoTa("Giảm đau, hạ sốt");
        loaiThuoc.setDanhMucThuoc(danhMucThuoc);
        loaiThuoc = loaiThuocRepository.save(loaiThuoc);

        // Create ThanhPhanThuoc
        thanhPhanThuoc = new ThanhPhanThuoc();
        thanhPhanThuoc.setTenThanhPhan("Paracetamol");
        thanhPhanThuoc.setHamLuong("500");
        thanhPhanThuoc.setDonVi("mg");
        thanhPhanThuoc = thanhPhanThuocRepository.save(thanhPhanThuoc);

        // Create DoiTuong
        doiTuong = new DoiTuong();
        doiTuong.setTenDoiTuong("Người lớn");
        doiTuong = doiTuongRepository.save(doiTuong);

        // Create Thuoc
        thuoc = new Thuoc();
        thuoc.setTenThuoc("Paracetamol");
        thuoc.setMaThuoc("PARA001");
        thuoc.setLoaiThuoc(loaiThuoc);
        thuoc.setDonVi("Viên");
        thuoc.setGiaNhap(5000.0);
        thuoc.setGiaBan(10000.0);
        thuoc.setSoLuongTon(100);
        thuoc.setHanSuDung(new Date());
        thuoc.setMaVach("8934567890123");
        thuoc.setSoDangKy("DK2023001");
        thuoc.setCheBao("Hộp");
        thuoc.setChiDinh("Giảm đau, hạ sốt");
        thuoc.setThanhPhanThuocs(Collections.singletonList(thanhPhanThuoc));
        thuoc.setDoiTuongs(Collections.singletonList(doiTuong));
        thuoc = thuocRepository.save(thuoc);
    }

    @Test
    public void testGetThuocBanChayController_Success() throws Exception {
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setCurrentPage(0);
        searchDTO.setSize(10);

        mockMvc.perform(post("/thuoc/get_thuoc_ban_chay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("Thành công"))
                .andExpect(jsonPath("$.data.totalElements").value(10))
                .andExpect(jsonPath("$.data.currentPage").value(0))
                .andExpect(jsonPath("$.data.data", hasSize(10)))
                .andDo(print());

//        assertEquals(23, thuocRepository.count());
    }

    @Test
    public void testGetThuocBanChayController_InvalidInput_ValidationFailure() throws Exception {
        SearchDTO invalidSearch = new SearchDTO();
        invalidSearch.setCurrentPage(-1);
        invalidSearch.setSize(10);

        mockMvc.perform(post("/thuoc/get_thuoc_ban_chay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidSearch)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void testSearchThuocController_Success_WithResults() throws Exception {
        SearchThuocDTO searchThuocDTO = new SearchThuocDTO();
        searchThuocDTO.setKeyWord("para");
        searchThuocDTO.setCurrentPage(0);
        searchThuocDTO.setSize(10);

        mockMvc.perform(post("/thuoc/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchThuocDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("Thành công"))
                .andExpect(jsonPath(".data.totalElements").value(2))
                .andExpect(jsonPath(".data.currentPage").value(0))
                .andDo(print());


    }

    @Test
    public void testSearchThuocController_InvalidInput_ValidationFailure() throws Exception {
        SearchThuocDTO invalidSearchDTO = new SearchThuocDTO();
        invalidSearchDTO.setKeyWord("Test");
        invalidSearchDTO.setCurrentPage(-1);
        invalidSearchDTO.setSize(10);

        mockMvc.perform(post("/thuoc/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidSearchDTO)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void testSearchThuocController_Success_NoResults() throws Exception {
        SearchThuocDTO validSearchThuocDTO = new SearchThuocDTO();
        validSearchThuocDTO.setKeyWord("Nonexistent");
        validSearchThuocDTO.setCurrentPage(0);
        validSearchThuocDTO.setSize(10);
        validSearchThuocDTO.setSortedField("tenThuoc");
        validSearchThuocDTO.setMinGiaBan(0.0);

        mockMvc.perform(post("/thuoc/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validSearchThuocDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("Thành công"))
                .andExpect(jsonPath("$.data.totalElements").value(0))
                .andExpect(jsonPath("$.data.data", hasSize(0)));
    }

    @Test
    public void testGetThuocByIdController_Success() throws Exception {
        int id = 9;

        mockMvc.perform(get("/thuoc/get")
                        .param("id", String.valueOf(id)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.id").value(id))
                .andExpect(jsonPath("$.data.tenThuoc").value("Bột hỗn dịch uống Taromentin 457mg/5ml điều trị nhiễm trùng viêm xoang, tai giữa, đường hô hấp (12.6g)"));

        assertTrue(thuocRepository.findById(id).isPresent());
    }

    @Test
    public void testGetThuocByIdControllerNotFound() throws Exception {
        int id = 999;

        mockMvc.perform(get("/thuoc/get")
                        .param("id", String.valueOf(id)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.msg").value("Không tìm thấy thuốc"));
    }

    @Test
    public void testCreateThuocControllerWithFile() throws Exception {
        ThuocDTO thuocDTO = new ThuocDTO();
        thuocDTO.setTenThuoc("Ibuprofen");
        thuocDTO.setMaThuoc("IBU001");
        thuocDTO.setLoaiThuocId(1);
        thuocDTO.setNhaSanXuatId(1);
        thuocDTO.setDonVi("Viên");
        thuocDTO.setGiaNhap(6000.0);
        thuocDTO.setGiaBan(12000.0);
        thuocDTO.setSoLuongTon(50);
        thuocDTO.setHanSuDung(new Date());
        thuocDTO.setMaVach("8934567890124");
        thuocDTO.setSoDangKy("DK2023002");
        thuocDTO.setCheBao("Hộp");
        thuocDTO.setChiDinh("Giảm đau, kháng viêm");
        thuocDTO.setThanhPhanThuocs(new ArrayList<>());
        thuocDTO.setDoiTuongs(new ArrayList<>());

        MockMultipartFile thuocDTOFile = new MockMultipartFile(
                "thuocDTO",
                "test-image.jpg",
                "application/json",
                objectMapper.writeValueAsBytes(thuocDTO));

        BufferedImage bufferedImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", os);
        MockMultipartFile imageFile = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                new ByteArrayInputStream(os.toByteArray()));

        mockMvc.perform(multipart("/thuoc/create")
                        .file(thuocDTOFile)
                        .file(imageFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.tenThuoc").value("Ibuprofen"))
                .andExpect(jsonPath("$.data.maThuoc").value("IBU001"))
                .andExpect(jsonPath("$.data.maVach").value("8934567890124"))
                .andExpect(jsonPath("$.data.soDangKy").value("DK2023002"));

        assertTrue(thuocRepository.existsByMaThuoc("IBU001"));
    }

    @Test
    public void testCreateThuocControllerWithEmptyFile() throws Exception {
        ThuocDTO thuocDTO = new ThuocDTO();
        thuocDTO.setTenThuoc("Ibuprofen");
        thuocDTO.setMaThuoc("IBU001");
        thuocDTO.setLoaiThuocId(1);
        thuocDTO.setNhaSanXuatId(1);
        thuocDTO.setDonVi("Viên");
        thuocDTO.setGiaNhap(6000.0);
        thuocDTO.setGiaBan(12000.0);
        thuocDTO.setSoLuongTon(50);
        thuocDTO.setHanSuDung(new Date());
        thuocDTO.setMaVach("8934567890124");
        thuocDTO.setSoDangKy("DK2023002");
        thuocDTO.setThanhPhanThuocs(new ArrayList<>());
        thuocDTO.setDoiTuongs(new ArrayList<>());

        MockMultipartFile thuocDTOFile = new MockMultipartFile(
                "thuocDTO",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(thuocDTO));

        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty-image.jpg",
                "image/jpeg",
                "".getBytes());

        mockMvc.perform(multipart("/thuoc/create")
                        .file(thuocDTOFile)
                        .file(emptyFile))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("File ảnh tải lên đang rỗng"));

        assertEquals(1, thuocRepository.count());
    }

    @Test
    public void testCreateThuocControllerWithoutFile() throws Exception {
        ThuocDTO thuocDTO = new ThuocDTO();
        thuocDTO.setTenThuoc("Ibuprofen");
        thuocDTO.setMaThuoc("IBU001");
        thuocDTO.setLoaiThuocId(1);
        thuocDTO.setNhaSanXuatId(1);
        thuocDTO.setDonVi("Viên");
        thuocDTO.setGiaNhap(6000.0);
        thuocDTO.setGiaBan(12000.0);
        thuocDTO.setSoLuongTon(50);
        thuocDTO.setHanSuDung(new Date());
        thuocDTO.setMaVach("8934567890124");
        thuocDTO.setSoDangKy("DK2023002");
        thuocDTO.setThanhPhanThuocs(new ArrayList<>());
        thuocDTO.setDoiTuongs(new ArrayList<>());

        MockMultipartFile thuocDTOFile = new MockMultipartFile(
                "thuocDTO",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(thuocDTO));

        mockMvc.perform(multipart("/thuoc/create")
                        .file(thuocDTOFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("Thành công"))
                .andExpect(jsonPath("$.data.tenThuoc").value("Ibuprofen"))
                .andExpect(jsonPath("$.data.maThuoc").value("IBU001"))
                .andExpect(jsonPath("$.data.soDangKy").value("DK2023002"))
                .andExpect(jsonPath("$.data.maVach").value("8934567890124"));

        assertTrue(thuocRepository.existsByMaThuoc("IBU001"));
    }

//    @Test
//    public void testCreateThuocControllerWithInvalidData() throws Exception {
//        ThuocDTO thuocDTO = new ThuocDTO();
//
//
//        MockMultipartFile thuocDTOFile = new MockMultipartFile(
//                "thuocDTO",
//                "",
//                "application/json",
//                objectMapper.writeValueAsBytes(thuocDTO));
//
//        mockMvc.perform(multipart("/thuoc/create")
//                        .file(thuocDTOFile))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.status").value(400))
//                .andDo(print());
//
//        assertEquals(1, thuocRepository.count());
//    }

    @Test
    public void testUpdateThuocControllerWithFile() throws Exception {
        ThuocDTO thuocDTO = new ThuocDTO();
        thuocDTO.setId(9);
        thuocDTO.setTenThuoc("Ibuprofen");
        thuocDTO.setMaThuoc("IBU001");
        thuocDTO.setLoaiThuocId(1);
        thuocDTO.setNhaSanXuatId(1);
        thuocDTO.setDonVi("Viên");
        thuocDTO.setGiaNhap(6000.0);
        thuocDTO.setGiaBan(12000.0);
        thuocDTO.setSoLuongTon(50);
        thuocDTO.setHanSuDung(new Date());
        thuocDTO.setMaVach("8934567890124");
        thuocDTO.setSoDangKy("DK2023002");
        thuocDTO.setThanhPhanThuocs(new ArrayList<>());
        thuocDTO.setDoiTuongs(new ArrayList<>());

        MockMultipartFile thuocDTOFile = new MockMultipartFile(
                "thuocDTO",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(thuocDTO));

        BufferedImage bufferedImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", os);
        MockMultipartFile imageFile = new MockMultipartFile(
                "file",
                "update-image.jpg",
                "image/jpeg",
                new ByteArrayInputStream(os.toByteArray()));

        mockMvc.perform(multipart("/thuoc/update")
                        .file(thuocDTOFile)
                        .file(imageFile)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("Thành công"))
                .andExpect(jsonPath("$.data.id").value(9))
                .andExpect(jsonPath("$.data.tenThuoc").value("Ibuprofen"));

        Thuoc updated = thuocRepository.findById(9).orElseThrow();
        assertEquals("Ibuprofen", updated.getTenThuoc());
    }

    @Test
    public void testUpdateThuocControllerWithEmptyFile() throws Exception {
        ThuocDTO thuocDTO = new ThuocDTO();
        thuocDTO.setId(9);
        thuocDTO.setTenThuoc("Paracetamol 500mg Caplet Update");
        thuocDTO.setMaThuoc("1999887");
        thuocDTO.setLoaiThuocId(1);
        thuocDTO.setNhaSanXuatId(1);
        thuocDTO.setDonVi("Viên");
        thuocDTO.setGiaNhap(5500.0);
        thuocDTO.setGiaBan(11000.0);
        thuocDTO.setSoLuongTon(100);
        thuocDTO.setHanSuDung(new Date());
        thuocDTO.setMaVach("8934567890123");
        thuocDTO.setSoDangKy("DK2023001");
        thuocDTO.setCheBao("Hộp");
        thuocDTO.setChiDinh("Giảm đau, hạ sốt");
        thuocDTO.setThanhPhanThuocs(new ArrayList<>());
        thuocDTO.setDoiTuongs(new ArrayList<>());

        MockMultipartFile thuocDTOFile = new MockMultipartFile(
                "thuocDTO",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(thuocDTO));

        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty-image.jpg",
                "image/jpeg",
                "".getBytes());

        mockMvc.perform(multipart("/thuoc/update")
                        .file(thuocDTOFile)
                        .file(emptyFile)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("File ảnh cập nhật bị rỗng"));


    }

    @Test
    public void testUpdateThuocControllerWithoutFile() throws Exception {
        ThuocDTO thuocDTO = new ThuocDTO();
        thuocDTO.setId(9);
        thuocDTO.setTenThuoc("Paracetamol 500mg");
        thuocDTO.setMaThuoc("19987");
        thuocDTO.setNhaSanXuatId(1);
        thuocDTO.setLoaiThuocId(1);
        thuocDTO.setDonVi("Viên");
        thuocDTO.setGiaNhap(5500.0);
        thuocDTO.setGiaBan(11000.0);
        thuocDTO.setSoLuongTon(100);
        thuocDTO.setHanSuDung(new Date());
        thuocDTO.setMaVach("8934567890123");
        thuocDTO.setSoDangKy("DK2023001");
        thuocDTO.setCheBao("Hộp");
        thuocDTO.setChiDinh("Giảm đau, hạ sốt");
        thuocDTO.setThanhPhanThuocs(new ArrayList<>());
        thuocDTO.setDoiTuongs(new ArrayList<>());

        MockMultipartFile thuocDTOFile = new MockMultipartFile(
                "thuocDTO",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(thuocDTO));

        mockMvc.perform(multipart("/thuoc/update")
                        .file(thuocDTOFile)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("Thành công"))
                .andExpect(jsonPath("$.data.id").value(9))
                .andExpect(jsonPath("$.data.tenThuoc").value("Paracetamol 500mg"));

        Thuoc updated = thuocRepository.findById(9).orElseThrow();
        assertEquals("Paracetamol 500mg", updated.getTenThuoc());
    }

    @Test
    public void testDeleteThuocController_Success() throws Exception {
        int id = 9;

        mockMvc.perform(delete("/thuoc/delete")
                        .param("id", String.valueOf(id)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("Thành công"));

        assertFalse(thuocRepository.findById(id).isPresent());
    }

    @Test
    public void testDeleteThuocController_Failure() throws Exception {
        int id = 999;

        mockMvc.perform(delete("/thuoc/delete")
                        .param("id", String.valueOf(id)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.msg").value("Không tìm thấy thuốc để xóa"));
    }
}