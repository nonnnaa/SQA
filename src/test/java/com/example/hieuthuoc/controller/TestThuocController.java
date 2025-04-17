package com.example.hieuthuoc.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.hieuthuoc.dto.PageDTO;
import com.example.hieuthuoc.dto.ResponseDTO;
import com.example.hieuthuoc.dto.SearchDTO;
import com.example.hieuthuoc.dto.SearchThuocDTO;
import com.example.hieuthuoc.dto.ThuocDTO;
import com.example.hieuthuoc.entity.Thuoc;
import com.example.hieuthuoc.service.ThuocService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class TestThuocController {

    private MockMvc mockMvc;

    @Mock
    private ThuocService thuocService;

    @InjectMocks
    private ThuocController thuocController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(thuocController).build();
    }

    /**
     * Test phương thức getThuocBanChay
     * Kiểm tra khi gửi SearchDTO hợp lệ thì API trả về status 200 và kết quả đúng
     */
    @Test
    public void testGetThuocBanChay_Success() throws Exception {
        // Chuẩn bị dữ liệu test
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setCurrentPage(0);
        searchDTO.setSize(10);

        List<Thuoc> thuocList = new ArrayList<>();
        Thuoc thuoc = new Thuoc();
        thuoc.setId(1);
        thuoc.setTenThuoc("Paracetamol");
        thuocList.add(thuoc);

        PageDTO<List<Thuoc>> pageDTO = new PageDTO<>();
        pageDTO.setData(thuocList);
        pageDTO.setTotalElements(1);

        ResponseDTO<PageDTO<List<Thuoc>>> responseDTO = new ResponseDTO<>(200, "Thành công", pageDTO);

        // Mock service
        when(thuocService.getThuocBanChay(any(SearchDTO.class))).thenReturn(responseDTO);

        // Gọi API và kiểm tra kết quả
        mockMvc.perform(post("/thuoc/get_thuoc_ban_chay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("Thành công"))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.currentPage").value(0))
                .andExpect(jsonPath("$.data.data", hasSize(1)))
                .andDo(print())
                ;

        // Xác minh service được gọi
        verify(thuocService).getThuocBanChay(any(SearchDTO.class));
    }

    @Test
    public void testGetThuocBanChay_InvalidInput_ValidationFailure() throws Exception {
        SearchDTO invalidSearch = new SearchDTO();
        invalidSearch.setCurrentPage(-1);   //Thử 1 giá trị không hợp lệ
        invalidSearch.setSize(10);

        mockMvc.perform(post("/thuoc/get_thuoc_ban_chay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidSearch)))
                .andExpect(status().isBadRequest())

                .andDo(print())
            ;
    }

    @Test
    public void testGetThuocBanChay_ReturnEmptyData() throws Exception{
        PageDTO<List<Thuoc>> emptyPage = new PageDTO<>();
        emptyPage.setTotalPages(0);
        emptyPage.setTotalElements(0L);
        emptyPage.setCurrentPage(0);
        emptyPage.setData(Collections.emptyList());
        ResponseDTO<PageDTO<List<Thuoc>>> emptyResponse = new ResponseDTO<>(200, "Thành công", emptyPage);

        SearchDTO validSearchDTO = new SearchDTO();
        validSearchDTO.setCurrentPage(0);
        validSearchDTO.setSize(10);
        validSearchDTO.setSortedField("tenThuoc");

        when(thuocService.getThuocBanChay(any(SearchDTO.class))).thenReturn(emptyResponse);

        mockMvc.perform(post("/thuoc/get_thuoc_ban_chay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validSearchDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("Thành công"))
                .andExpect(jsonPath("$.data.totalElements").value(0))
                .andExpect(jsonPath("$.data.data", hasSize(0)));
    }

    /**
     * Test phương thức search
     * Kiểm tra khi gửi SearchThuocDTO hợp lệ thì API trả về status 200 và kết quả tìm kiếm đúng
     */
    @Test
    public void testSearch_Success_WithResults() throws Exception {
        // Chuẩn bị dữ liệu test
        SearchThuocDTO searchThuocDTO = new SearchThuocDTO();
        searchThuocDTO.setKeyWord("para");
        searchThuocDTO.setCurrentPage(0);
        searchThuocDTO.setSize(10);

        List<Thuoc> thuocList = new ArrayList<>();
        Thuoc thuoc = new Thuoc();
        thuoc.setId(1);
        thuoc.setTenThuoc("Paracetamol");
        thuocList.add(thuoc);

        PageDTO<List<Thuoc>> pageDTO = new PageDTO<>();
        pageDTO.setData(thuocList);
        pageDTO.setTotalElements(1);

        ResponseDTO<PageDTO<List<Thuoc>>> responseDTO = new ResponseDTO<>(200, "Tìm kiếm thành công", pageDTO);

        // Mock service
        when(thuocService.search(any(SearchThuocDTO.class))).thenReturn(responseDTO);

        // Gọi API và kiểm tra kết quả
        mockMvc.perform(post("/thuoc/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchThuocDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("Tìm kiếm thành công"))
                .andExpect(jsonPath(".data.totalPages").value(1))
                .andExpect(jsonPath(".data.totalElements").value(1))
                .andExpect(jsonPath(".data.currentPage").value(0))
                .andExpect(jsonPath(".data.data", hasSize(1)))
                .andDo(print())
                ;

        // Xác minh service được gọi
        verify(thuocService).search(any(SearchThuocDTO.class));
    }

    @Test
    public void testSearch_InvalidInput_ValidationFailure() throws Exception {
        SearchThuocDTO invalidSearchDTO = new SearchThuocDTO();
        invalidSearchDTO.setKeyWord("Test");
        invalidSearchDTO.setCurrentPage(-1); // Giá trị không hợp lệ
        invalidSearchDTO.setSize(10);

        mockMvc.perform(post("/thuoc/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidSearchDTO)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void testSearch_Success_NoResults() throws Exception{
        // Input: validSearchThuocDTO
        // Expected Output from Mocked Service: searchEmptyResponsePage (không có data)
        PageDTO<List<Thuoc>> emptySearchPageDTO = new PageDTO<>();
        emptySearchPageDTO.setTotalPages(0);
        emptySearchPageDTO.setTotalElements(0L);
        emptySearchPageDTO.setCurrentPage(0);
        emptySearchPageDTO.setData(Collections.emptyList());

        ResponseDTO searchEmptyResponsePage = new ResponseDTO<>(200, "Thành công", emptySearchPageDTO);

        SearchThuocDTO validSearchThuocDTO = new SearchThuocDTO();
        validSearchThuocDTO.setKeyWord("Para11");
        validSearchThuocDTO.setCurrentPage(0);
        validSearchThuocDTO.setSize(10);
        validSearchThuocDTO.setSortedField("tenThuoc");
        validSearchThuocDTO.setMinGiaBan(0.0);
        // --- Mocking ---
        when(thuocService.search(any(SearchThuocDTO.class))).thenReturn(searchEmptyResponsePage);

        // --- Perform Request and Assertions ---
        mockMvc.perform(post("/thuoc/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validSearchThuocDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("Thành công"))
                .andExpect(jsonPath("$.data.totalElements").value(0))
                .andExpect(jsonPath("$.data.data", hasSize(0)));
    }

    /**
     * Test phương thức getById
     * Kiểm tra khi truyền ID hợp lệ thì API trả về thông tin thuốc đúng
     */
    @Test
    public void testGetById_Success() throws Exception {
        // Chuẩn bị dữ liệu test
        int id = 1;
        Thuoc thuoc = new Thuoc();
        thuoc.setId(id);
        thuoc.setTenThuoc("Paracetamol");

        ResponseDTO<Thuoc> responseDTO = new ResponseDTO<>(200, "Lấy thông tin thuốc thành công", thuoc);

        // Mock service
        when(thuocService.getById(id)).thenReturn(responseDTO);

        // Gọi API và kiểm tra kết quả
        mockMvc.perform(get("/thuoc/get")
                        .param("id", String.valueOf(id)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("Lấy thông tin thuốc thành công"))
                .andExpect(jsonPath("$.data.id").value(id))
                .andExpect(jsonPath("$.data.tenThuoc").value("Paracetamol"));

        // Xác minh service được gọi
        verify(thuocService).getById(id);
    }

    /**
     * Test phương thức getById với trường hợp không tìm thấy thuốc
     * Kiểm tra khi không tìm thấy thuốc theo ID thì API trả về thông báo lỗi
     */
    @Test
    public void testGetByIdNotFound() throws Exception {
        // Chuẩn bị dữ liệu test
        int id = 999;
        ResponseDTO<Thuoc> responseDTO = new ResponseDTO<>(404, "Không tìm thấy thuốc", null);

        // Mock service
        when(thuocService.getById(id)).thenReturn(responseDTO);

        // Gọi API và kiểm tra kết quả
        mockMvc.perform(get("/thuoc/get")
                        .param("id", String.valueOf(id)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.msg").value("Không tìm thấy thuốc"))
                .andExpect(jsonPath("$.data").doesNotExist());

        // Xác minh service được gọi
        verify(thuocService).getById(id);
    }

    /**
     * Test phương thức create với file
     * Kiểm tra việc tạo mới thuốc kèm file ảnh
     */
    @Test
    public void testCreateWithFile() throws Exception {
        // Chuẩn bị dữ liệu test
        ThuocDTO thuocDTO = new ThuocDTO();
        thuocDTO.setTenThuoc("Paracetamol");
        thuocDTO.setMaThuoc("PARA001");
        thuocDTO.setDonVi("Viên");
        thuocDTO.setGiaNhap(5000.0);
        thuocDTO.setGiaBan(10000.0);
        thuocDTO.setHanSuDung(new Date());

        Thuoc thuoc = new Thuoc();
        thuoc.setId(1);
        thuoc.setTenThuoc("Paracetamol");

        ResponseDTO<Thuoc> responseDTO = new ResponseDTO<>(200, "Tạo mới thuốc thành công", thuoc);

        // Mock service
        when(thuocService.create(any(ThuocDTO.class))).thenReturn(responseDTO);

        // Tạo mock file
        MockMultipartFile thuocDTOFile = new MockMultipartFile(
                "thuocDTO",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(thuocDTO));

        MockMultipartFile imageFile = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                "test image content".getBytes());

        // Gọi API và kiểm tra kết quả
        mockMvc.perform(multipart("/thuoc/create")
                        .file(thuocDTOFile)
                        .file(imageFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("Tạo mới thuốc thành công"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.tenThuoc").value("Paracetamol"));

        // Xác minh service được gọi
        verify(thuocService).create(any(ThuocDTO.class));
    }

    @Test
    public void testCreateWithEmptyFile() throws Exception {
        // Chuẩn bị dữ liệu test
        ThuocDTO thuocDTO = new ThuocDTO();
        thuocDTO.setTenThuoc("Paracetamol");
        thuocDTO.setMaThuoc("PARA001");
        thuocDTO.setDonVi("Viên");
        thuocDTO.setGiaNhap(5000.0);
        thuocDTO.setGiaBan(10000.0);

        Thuoc thuoc = new Thuoc();
        thuoc.setId(1);
        thuoc.setTenThuoc("Paracetamol");

        ResponseDTO<Thuoc> responseDTO = new ResponseDTO<>(200, "Tạo mới thuốc thành công", thuoc);

        // Mock service
        when(thuocService.create(any(ThuocDTO.class))).thenReturn(responseDTO);

        // Tạo mock file rỗng
        MockMultipartFile thuocDTOFile = new MockMultipartFile(
                "thuocDTO",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(thuocDTO));

        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty-image.jpg",
                "image/jpeg",
                "".getBytes()); // Nội dung rỗng

        // Gọi API và kiểm tra kết quả
        mockMvc.perform(multipart("/thuoc/create")
                        .file(thuocDTOFile)
                        .file(emptyFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("Tạo mới thuốc thành công"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.tenThuoc").value("Paracetamol"));

        // Xác minh service được gọi
        verify(thuocService).create(any(ThuocDTO.class));
    }

    /**
     * Mã Testcase:
     * Objective: Kiểm tra API tạo mới thuốc thành công khi không có file ảnh nào được tải lên.
     * Input: MockMultipartFile cho thuocDTO (dữ liệu thuốc dưới dạng JSON).
     * Expected Output:
     * - HTTP Status 200 OK.
     * - JSON response có:
     * - status: 200
     * - msg: "Tạo mới thuốc thành công"
     * - data:
     * - id: 1
     * - tenThuoc: "Paracetamol"
     */
    @Test
    public void testCreateWithoutFile() throws Exception {
        // Chuẩn bị dữ liệu test
        ThuocDTO thuocDTO = new ThuocDTO();
        thuocDTO.setTenThuoc("Paracetamol");
        thuocDTO.setMaThuoc("PARA001");
        thuocDTO.setDonVi("Viên");
        thuocDTO.setGiaNhap(5000.0);
        thuocDTO.setGiaBan(10000.0);

        Thuoc thuoc = new Thuoc();
        thuoc.setId(1);
        thuoc.setTenThuoc("Paracetamol");

        ResponseDTO<Thuoc> responseDTO = new ResponseDTO<>(200, "Tạo mới thuốc thành công", thuoc);

        // Mock service
        when(thuocService.create(any(ThuocDTO.class))).thenReturn(responseDTO);

        // Tạo mock file
        MockMultipartFile thuocDTOFile = new MockMultipartFile(
                "thuocDTO",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(thuocDTO));

        // Gọi API và kiểm tra kết quả
        mockMvc.perform(multipart("/thuoc/create")
                        .file(thuocDTOFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("Tạo mới thuốc thành công"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.tenThuoc").value("Paracetamol"));

        // Xác minh service được gọi
        verify(thuocService).create(any(ThuocDTO.class));
    }


    @Test
    public void testCreateWithInvalidData() throws Exception {
        // Chuẩn bị dữ liệu test
        ThuocDTO thuocDTO = new ThuocDTO();
        // Không cung cấp tenThuoc và các thông tin bắt buộc khác

        ResponseDTO<Thuoc> responseDTO = new ResponseDTO<>(400, "Dữ liệu không hợp lệ", null);

        // Mock service - giả lập lỗi từ service
        when(thuocService.create(any(ThuocDTO.class))).thenReturn(responseDTO);

        // Tạo mock file
        MockMultipartFile thuocDTOFile = new MockMultipartFile(
                "thuocDTO",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(thuocDTO));

        // Gọi API và kiểm tra kết quả
        mockMvc.perform(multipart("/thuoc/create")
                        .file(thuocDTOFile))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("Dữ liệu không hợp lệ"));

        // Xác minh service được gọi
        verify(thuocService).create(any(ThuocDTO.class));
    }

    @Test
    public void testUpdateWithFile() throws Exception {
        // Chuẩn bị dữ liệu test
        ThuocDTO thuocDTO = new ThuocDTO();
        thuocDTO.setId(1);
        thuocDTO.setTenThuoc("Paracetamol 500mg");
        thuocDTO.setMaThuoc("PARA001");
        thuocDTO.setDonVi("Viên");
        thuocDTO.setGiaNhap(5500.0);
        thuocDTO.setGiaBan(11000.0);

        Thuoc thuoc = new Thuoc();
        thuoc.setId(1);
        thuoc.setTenThuoc("Paracetamol 500mg");

        ResponseDTO<Thuoc> responseDTO = new ResponseDTO<>(200, "Cập nhật thuốc thành công", thuoc);

        // Mock service
        when(thuocService.update(any(ThuocDTO.class))).thenReturn(responseDTO);

        // Tạo mock file
        MockMultipartFile thuocDTOFile = new MockMultipartFile(
                "thuocDTO",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(thuocDTO));

        MockMultipartFile imageFile = new MockMultipartFile(
                "file",
                "update-image.jpg",
                "image/jpeg",
                "update image content".getBytes());

        // Gọi API và kiểm tra kết quả
        mockMvc.perform(multipart("/thuoc/update")
                        .file(thuocDTOFile)
                        .file(imageFile)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("Cập nhật thuốc thành công"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.tenThuoc").value("Paracetamol 500mg"));

        // Xác minh service được gọi
        verify(thuocService).update(any(ThuocDTO.class));
    }

    @Test
    public void testUpdateWithEmptyFile() throws Exception {
        // Chuẩn bị dữ liệu test
        ThuocDTO thuocDTO = new ThuocDTO();
        thuocDTO.setId(1);
        thuocDTO.setTenThuoc("Paracetamol 500mg");
        thuocDTO.setMaThuoc("PARA001");
        thuocDTO.setDonVi("Viên");
        thuocDTO.setGiaNhap(5500.0);
        thuocDTO.setGiaBan(11000.0);

        Thuoc thuoc = new Thuoc();
        thuoc.setId(1);
        thuoc.setTenThuoc("Paracetamol 500mg");

        ResponseDTO<Thuoc> responseDTO = new ResponseDTO<>(200, "Cập nhật thuốc thành công", thuoc);

        // Mock service
        when(thuocService.update(any(ThuocDTO.class))).thenReturn(responseDTO);

        // Tạo mock file rỗng
        MockMultipartFile thuocDTOFile = new MockMultipartFile(
                "thuocDTO",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(thuocDTO));

        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty-image.jpg",
                "image/jpeg",
                "".getBytes()); // Nội dung rỗng

        // Gọi API và kiểm tra kết quả
        mockMvc.perform(multipart("/thuoc/update")
                        .file(thuocDTOFile)
                        .file(emptyFile)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("Cập nhật thuốc thành công"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.tenThuoc").value("Paracetamol 500mg"));

        // Xác minh service được gọi
        verify(thuocService).update(any(ThuocDTO.class));
    }

    /**
     * Test phương thức update không có file
     * Kiểm tra việc cập nhật thông tin thuốc không kèm file ảnh
     */
    @Test
    public void testUpdateWithoutFile() throws Exception {
        // Chuẩn bị dữ liệu test
        ThuocDTO thuocDTO = new ThuocDTO();
        thuocDTO.setId(1);
        thuocDTO.setTenThuoc("Paracetamol 500mg");
        thuocDTO.setMaThuoc("PARA001");
        thuocDTO.setDonVi("Viên");
        thuocDTO.setGiaNhap(5500.0);
        thuocDTO.setGiaBan(11000.0);

        Thuoc thuoc = new Thuoc();
        thuoc.setId(1);
        thuoc.setTenThuoc("Paracetamol 500mg");

        ResponseDTO<Thuoc> responseDTO = new ResponseDTO<>(200, "Cập nhật thuốc thành công", thuoc);

        // Mock service
        when(thuocService.update(any(ThuocDTO.class))).thenReturn(responseDTO);

        // Tạo mock file
        MockMultipartFile thuocDTOFile = new MockMultipartFile(
                "thuocDTO",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(thuocDTO));

        // Gọi API và kiểm tra kết quả
        mockMvc.perform(multipart("/thuoc/update")
                        .file(thuocDTOFile)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("Cập nhật thuốc thành công"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.tenThuoc").value("Paracetamol 500mg"));

        // Xác minh service được gọi
        verify(thuocService).update(any(ThuocDTO.class));
    }

    /**
     * Test phương thức delete
     * Kiểm tra việc xóa thuốc theo ID
     */
    @Test
    public void testDelete_Success() throws Exception {
        // Chuẩn bị dữ liệu test
        int id = 1;
        ResponseDTO<Void> responseDTO = new ResponseDTO<>(200, "Xóa thuốc thành công");

        // Mock service
        when(thuocService.delete(id)).thenReturn(responseDTO);

        // Gọi API và kiểm tra kết quả
        mockMvc.perform(delete("/thuoc/delete")
                        .param("id", String.valueOf(id)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("Xóa thuốc thành công"));

        // Xác minh service được gọi
        verify(thuocService).delete(id);
    }

    /**
     * Mã Testcase:
     * Objective: Kiểm tra API xóa thuốc thất bại khi truyền ID không tồn tại.
     * Input: ID = 999 được truyền như một tham số query.
     * Expected Output:
     * - HTTP Status 404 Not Found (hoặc mã lỗi phù hợp với logic ứng dụng).
     * - JSON response có:
     * - status: 404 (hoặc mã lỗi khác)
     * - msg: "Không tìm thấy thuốc để xóa" (hoặc thông báo lỗi phù hợp).
     */
    @Test
    public void testDelete_Failure() throws Exception {
        // Chuẩn bị dữ liệu test
        int id = 999;
        ResponseDTO<Void> responseDTO = new ResponseDTO<>(404, "Không tìm thấy thuốc để xóa");

        // Mock service
        when(thuocService.delete(id)).thenReturn(responseDTO);

        // Gọi API và kiểm tra kết quả
        mockMvc.perform(delete("/thuoc/delete")
                        .param("id", String.valueOf(id)))
                .andExpect(status().isNotFound()) // Kiểm tra status code
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.msg").value("Không tìm thấy thuốc để xóa"));

        // Xác minh service được gọi
        verify(thuocService).delete(id);
    }
}