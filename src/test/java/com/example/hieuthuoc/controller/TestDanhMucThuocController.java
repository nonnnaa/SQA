package com.example.hieuthuoc.controller;

import com.example.hieuthuoc.dto.DanhMucThuocDTO;
import com.example.hieuthuoc.dto.ResponseDTO;
import com.example.hieuthuoc.entity.DanhMucThuoc;
import com.example.hieuthuoc.service.DanhMucThuocService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class TestDanhMucThuocController {

    private MockMvc mockMvc; // Đối tượng giả lập HTTP request

    @Mock // Tạo mock bean cho service dependency
    private DanhMucThuocService danhMucThuocService;

    private ObjectMapper objectMapper = new ObjectMapper(); // Dùng để chuyển đổi object <-> JSON

    @InjectMocks
    private DanhMucThuocController danhMucThuocController;

    // --- Dữ liệu Test Mẫu ---
    private DanhMucThuoc dm1, dm2;
    private DanhMucThuocDTO dmDTO_create, dmDTO_update;
    private ResponseDTO<List<DanhMucThuoc>> responseListOK;
    private ResponseDTO<List<DanhMucThuoc>> responseListEmpty;
    private ResponseDTO<DanhMucThuoc> responseSingleOK;
    private ResponseDTO<DanhMucThuoc> responseErrorConflict;
    private ResponseDTO<DanhMucThuoc> responseErrorNotFound_Single; // Cho update
    private ResponseDTO<Void> responseVoidOK;
    private ResponseDTO<Void> responseErrorNotFound_Void; // Cho delete
    private ResponseDTO<Void> responseErrorConflict_Void; // Cho delete

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(danhMucThuocController).build();

        // --- Khởi tạo DanhMucThuoc entities ---
        dm1 = new DanhMucThuoc();
        dm1.setId(1);
        dm1.setTenDanhMuc("Thuốc Kháng Sinh");
        dm1.setMoTa("Nhóm thuốc điều trị nhiễm khuẩn");
        dm1.setLoaiThuocs(Collections.emptyList()); // Giả sử không cần load list này trong test controller

        dm2 = new DanhMucThuoc();
        dm2.setId(2);
        dm2.setTenDanhMuc("Thuốc Giảm Đau");
        dm2.setMoTa("Nhóm thuốc giảm các loại đau");
        dm2.setLoaiThuocs(Collections.emptyList());

        // --- Khởi tạo DanhMucThuoc DTOs ---
        dmDTO_create = new DanhMucThuocDTO();
        dmDTO_create.setId(null);
        dmDTO_create.setTenDanhMuc("Thuốc Tim Mạch");
        dmDTO_create.setMoTa("Nhóm thuốc cho bệnh tim");

        dmDTO_update = new DanhMucThuocDTO();
        dmDTO_update.setId(1);
        dmDTO_update.setTenDanhMuc("Thuốc Kháng Sinh Updated");
        dmDTO_update.setMoTa("Mô tả đã cập nhật");
        // --- Khởi tạo ResponseDTOs mẫu ---
        responseListOK = ResponseDTO.<List<DanhMucThuoc>>builder()
                .status(200).msg("Thành công").data(Arrays.asList(dm1, dm2)).build();
        responseListEmpty = ResponseDTO.<List<DanhMucThuoc>>builder()
                .status(200).msg("Thành công").data(Collections.emptyList()).build();
        responseSingleOK = ResponseDTO.<DanhMucThuoc>builder()
                .status(200).msg("Thành công").data(dm1).build(); // Trả về dm1 sau khi create/update thành công
        responseErrorConflict = ResponseDTO.<DanhMucThuoc>builder()
                .status(409).msg("Tên danh mục đã tồn tại").data(null).build();
        responseErrorNotFound_Single = ResponseDTO.<DanhMucThuoc>builder()
                .status(404).msg("Không tìm thấy danh mục").data(null).build();
        responseVoidOK = ResponseDTO.<Void>builder()
                .status(200).msg("Thành công").data(null).build();
        responseErrorNotFound_Void = ResponseDTO.<Void>builder()
                .status(404).msg("Không tìm thấy danh mục để xóa").data(null).build();
    }

    // --- Test cases ---

    // GET /list
    @Test
    void getAll_Success_ReturnsList() throws Exception {
        when(danhMucThuocService.getAll()).thenReturn(responseListOK);

        mockMvc.perform(get("/danhmucthuoc/list")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("Thành công"))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].id").value(dm1.getId()))
                .andExpect(jsonPath("$.data[0].tenDanhMuc").value(dm1.getTenDanhMuc()))
                .andExpect(jsonPath("$.data[1].id").value(dm2.getId()));

        verify(danhMucThuocService).getAll();
    }

    @Test
    @DisplayName("getAll - Thành công, trả về danh sách rỗng")
    void getAll_Success_ReturnsEmptyList() throws Exception {
        when(danhMucThuocService.getAll()).thenReturn(responseListEmpty);

        mockMvc.perform(get("/danhmucthuoc/list")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("Thành công"))
                .andExpect(jsonPath("$.data", hasSize(0)));

        verify(danhMucThuocService).getAll();
    }

    // GET /get_danh_muc_and_loai_thuoc (Giả định tương tự getAll về mặt controller)
    @Test
    @DisplayName("getDanhMucAnhLoaiThuoc - Thành công, trả về danh sách")
    void getDanhMucAnhLoaiThuoc_Success_ReturnsList() throws Exception {
        when(danhMucThuocService.getDanhMucAnhLoaiThuoc()).thenReturn(responseListOK);

        mockMvc.perform(get("/danhmucthuoc/get_danh_muc_and_loai_thuoc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].id").value(dm1.getId()));

        verify(danhMucThuocService).getDanhMucAnhLoaiThuoc();
    }

    @Test
    void getDanhMucAnhLoaiThuoc_Success_ReturnsEmptyList() throws Exception {
        when(danhMucThuocService.getDanhMucAnhLoaiThuoc()).thenReturn(responseListEmpty);

        mockMvc.perform(get("/danhmucthuoc/get_danh_muc_and_loai_thuoc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data", hasSize(0)));

        verify(danhMucThuocService).getDanhMucAnhLoaiThuoc();
    }

    // GET /search_by_ten_danh_muc
    @Test
    @DisplayName("searchByTenDanhMuc - Thành công, tìm thấy")
    void searchByTenDanhMuc_Success_Found() throws Exception {
        String searchTerm = "Kháng Sinh";
        // Giả sử chỉ trả về dm1
        ResponseDTO<List<DanhMucThuoc>> responseFound = ResponseDTO.<List<DanhMucThuoc>>builder()
                .status(200).msg("Success").data(Collections.singletonList(dm1)).build();
        when(danhMucThuocService.searchByTenDanhMuc(eq(searchTerm))).thenReturn(responseFound);

        mockMvc.perform(get("/danhmucthuoc/search_by_ten_danh_muc")
                        .param("tenDanhMuc", searchTerm)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id").value(dm1.getId()));

        verify(danhMucThuocService).searchByTenDanhMuc(eq(searchTerm));
    }

    @Test
    @DisplayName("searchByTenDanhMuc - Thành công, không tìm thấy")
    void searchByTenDanhMuc_Success_NotFound() throws Exception {
        String searchTerm = "Không Tồn Tại";
        when(danhMucThuocService.searchByTenDanhMuc(eq(searchTerm))).thenReturn(responseListEmpty);

        mockMvc.perform(get("/danhmucthuoc/search_by_ten_danh_muc")
                        .param("tenDanhMuc", searchTerm)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data", hasSize(0)));

        verify(danhMucThuocService).searchByTenDanhMuc(eq(searchTerm));
    }

    @Test
    @DisplayName("searchByTenDanhMuc - Thiếu tham số")
    void searchByTenDanhMuc_MissingParameter() throws Exception {
        mockMvc.perform(get("/danhmucthuoc/search_by_ten_danh_muc") // Không có param tenDanhMuc
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // Spring sẽ trả về 400
        verify(danhMucThuocService, never()).searchByTenDanhMuc(anyString()); // Service không được gọi
    }


    // POST /create
    @Test
    @DisplayName("create - Thành công")
    void create_Success() throws Exception {
        // Giả sử service trả về dm1 sau khi gán ID (mặc dù DTO là "Thuốc Tim Mạch")
        // Để test chính xác hơn, service nên trả về object tương ứng với DTO
        DanhMucThuoc createdDM = new DanhMucThuoc();
        createdDM.setId(3); // ID mới được gán
        createdDM.setTenDanhMuc(dmDTO_create.getTenDanhMuc());
        createdDM.setMoTa(dmDTO_create.getMoTa());
        ResponseDTO<DanhMucThuoc> responseCreated = ResponseDTO.<DanhMucThuoc>builder()
                .status(200).msg("Thành công").data(createdDM).build(); // Nên là status 201 Created

        when(danhMucThuocService.create(any(DanhMucThuocDTO.class))).thenReturn(responseCreated);

        mockMvc.perform(post("/danhmucthuoc/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dmDTO_create)) // Gửi DTO đi
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("Thành công"))
                .andExpect(jsonPath("$.data.id").value(3))
                .andExpect(jsonPath("$.data.tenDanhMuc").value(dmDTO_create.getTenDanhMuc()));

        // Kiểm tra service được gọi với đúng DTO (hoặc dùng ArgumentCaptor)
        verify(danhMucThuocService).create(ArgumentMatchers.refEq(dmDTO_create));
    }

    @Test
    void create_Fail_Conflict() throws Exception {
        when(danhMucThuocService.create(any(DanhMucThuocDTO.class))).thenReturn(responseErrorConflict);

        mockMvc.perform(post("/danhmucthuoc/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dmDTO_create))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.msg").value("Tên danh mục đã tồn tại"))
                .andExpect(jsonPath("$.data").value(nullValue()));

        verify(danhMucThuocService).create(any(DanhMucThuocDTO.class));
    }

    // PUT /update
    @Test
    @DisplayName("update - Thành công")
    void update_Success() throws Exception {
        // dmDTO_update có id = 1
        // Giả sử service trả về dm1 với thông tin đã được cập nhật
        DanhMucThuoc updatedDM = dm1; // Lấy dm1 làm mẫu
        updatedDM.setTenDanhMuc(dmDTO_update.getTenDanhMuc()); // Cập nhật tên
        updatedDM.setMoTa(dmDTO_update.getMoTa()); // Cập nhật mô tả
        ResponseDTO<DanhMucThuoc> responseUpdated = ResponseDTO.<DanhMucThuoc>builder()
                .status(200).msg("Success").data(updatedDM).build();

        when(danhMucThuocService.update(any(DanhMucThuocDTO.class))).thenReturn(responseUpdated);

        mockMvc.perform(put("/danhmucthuoc/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dmDTO_update)) // Gửi DTO update
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.id").value(dmDTO_update.getId()))
                .andExpect(jsonPath("$.data.tenDanhMuc").value(dmDTO_update.getTenDanhMuc()))
                .andExpect(jsonPath("$.data.moTa").value(dmDTO_update.getMoTa()));

        verify(danhMucThuocService).update(ArgumentMatchers.refEq(dmDTO_update));
    }

    @Test
    @DisplayName("update - Lỗi Not Found")
    void update_Fail_NotFound() throws Exception {
        // Giả sử DTO update có ID không tồn tại
        DanhMucThuocDTO dtoNotFound = new DanhMucThuocDTO();
        dtoNotFound.setId(99);
        dtoNotFound.setTenDanhMuc("Tên ko tồn tại");
        dtoNotFound.setMoTa("Mô tả");
        when(danhMucThuocService.update(any(DanhMucThuocDTO.class))).thenReturn(responseErrorNotFound_Single);

        mockMvc.perform(put("/danhmucthuoc/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoNotFound))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()) // Mong đợi 404
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.msg").value("Không tìm thấy danh mục"))
                .andExpect(jsonPath("$.data").value(nullValue()));

        verify(danhMucThuocService).update(ArgumentMatchers.refEq(dtoNotFound));
    }


    // DELETE /delete
    @Test
    @DisplayName("delete - Thành công")
    void delete_Success() throws Exception {
        Integer idToDelete = 1;
        when(danhMucThuocService.delete(eq(idToDelete))).thenReturn(responseVoidOK);

        mockMvc.perform(delete("/danhmucthuoc/delete")
                        .param("id", String.valueOf(idToDelete)) // Tham số dạng String
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Service trả về 200 OK
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("Thành công"))
                ;

        verify(danhMucThuocService).delete(eq(idToDelete));
    }

    @Test
    @DisplayName("delete - Lỗi Not Found")
    void delete_Fail_NotFound() throws Exception {
        Integer idToDelete = 99; // ID không tồn tại
        when(danhMucThuocService.delete(eq(idToDelete))).thenReturn(responseErrorNotFound_Void);

        mockMvc.perform(delete("/danhmucthuoc/delete")
                        .param("id", String.valueOf(idToDelete))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.msg").value("Không tìm thấy danh mục để xóa"))
                ;

        verify(danhMucThuocService).delete(eq(idToDelete));
    }

    @Test
    @DisplayName("delete - Thiếu tham số id")
    void delete_MissingParameter() throws Exception {
        mockMvc.perform(delete("/danhmucthuoc/delete") // Không có param id
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // Spring xử lý

        verify(danhMucThuocService, never()).delete(anyInt());
    }

    @Test
    @DisplayName("delete - Tham số id không hợp lệ (không phải số)")
    void delete_InvalidParameterType() throws Exception {
        mockMvc.perform(delete("/danhmucthuoc/delete")
                        .param("id", "abc") // id là chữ
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // Spring xử lý lỗi type mismatch

        verify(danhMucThuocService, never()).delete(anyInt());
    }

}
