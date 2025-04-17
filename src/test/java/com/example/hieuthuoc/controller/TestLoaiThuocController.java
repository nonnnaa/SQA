package com.example.hieuthuoc.controller;

import com.example.hieuthuoc.dto.LoaiThuocDTO;
import com.example.hieuthuoc.dto.ResponseDTO;
import com.example.hieuthuoc.entity.DanhMucThuoc;
import com.example.hieuthuoc.entity.LoaiThuoc;
import com.example.hieuthuoc.service.LoaiThuocService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class TestLoaiThuocController {

    private MockMvc mockMvc; // Giả lập HTTP request

    @Mock // Mock service dependency
    private LoaiThuocService loaiThuocService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private LoaiThuocController loaiThuocController;

    private LoaiThuoc lt1, lt2;
    private LoaiThuocDTO ltDTO_create, ltDTO_update;
    private DanhMucThuoc danhMucThuocSample;
    private ResponseDTO<List<LoaiThuoc>> responseListOK;
    private ResponseDTO<List<LoaiThuoc>> responseListEmpty;
    private ResponseDTO<LoaiThuoc> responseSingleOK;
    private ResponseDTO<LoaiThuoc> responseErrorConflict;
    private ResponseDTO<LoaiThuoc> responseErrorNotFound_Single;
    private ResponseDTO<LoaiThuoc> responseErrorBadRequest_Single; // Ví dụ: danhMucThuocId không hợp lệ
    private ResponseDTO<Void> responseVoidOK;
    private ResponseDTO<Void> responseErrorNotFound_Void;
    private ResponseDTO<Void> responseErrorConflict_Void;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(loaiThuocController).build();

        // Danh mục thuốc mẫu (cha của LoaiThuoc)
        danhMucThuocSample = new DanhMucThuoc();
        danhMucThuocSample.setId(10);
        danhMucThuocSample.setTenDanhMuc("Nhóm Thuốc Cơ Bản");

        // LoaiThuoc entities
        lt1 = new LoaiThuoc();
        lt1.setId(1);
        lt1.setTenLoai("Thuốc kháng sinh nhóm Beta-lactam");
        lt1.setMoTa("Diệt khuẩn");
        lt1.setDanhMucThuoc(danhMucThuocSample);

        lt2 = new LoaiThuoc();
        lt2.setId(2);
        lt2.setTenLoai("Thuốc giảm đau hạ sốt");
        lt2.setMoTa("Giảm đau, hạ sốt thông thường");
        lt2.setDanhMucThuoc(danhMucThuocSample);

        // LoaiThuoc DTOs
        ltDTO_create = new LoaiThuocDTO(); // ID null khi tạo
        ltDTO_create.setTenLoai("Thuốc chống nấm");
        ltDTO_create.setMoTa("Điều trị nhiễm nấm");
        ltDTO_create.setDanhMucThuocId(10); // ID của danh mục cha

        ltDTO_update = new LoaiThuocDTO(); // ID cho update
        ltDTO_update.setId(1);
        ltDTO_update.setTenLoai("Thuốc kháng sinh nhóm Beta-lactam (Updated)");
        ltDTO_update.setMoTa("Diệt khuẩn updated");
        ltDTO_update.setDanhMucThuocId(10);

        // ResponseDTOs mẫu
        responseListOK = ResponseDTO.<List<LoaiThuoc>>builder().status(200).msg("OK").data(Arrays.asList(lt1, lt2)).build();
        responseListEmpty = ResponseDTO.<List<LoaiThuoc>>builder().status(200).msg("OK").data(Collections.emptyList()).build();
        // Giả sử create/update trả về lt1 (đã cập nhật nếu là update)
        responseSingleOK = ResponseDTO.<LoaiThuoc>builder().status(200).msg("Thành công").data(lt1).build();
        responseErrorConflict = ResponseDTO.<LoaiThuoc>builder().status(409).msg("Tên loại thuốc đã tồn tại").data(null).build();
        responseErrorNotFound_Single = ResponseDTO.<LoaiThuoc>builder().status(404).msg("Không tìm thấy loại thuốc").data(null).build();
        responseErrorBadRequest_Single = ResponseDTO.<LoaiThuoc>builder().status(400).msg("ID Danh mục thuốc không hợp lệ").data(null).build();
        responseVoidOK = ResponseDTO.<Void>builder().status(200).msg("Thành công").build();
        responseErrorNotFound_Void = ResponseDTO.<Void>builder().status(404).msg("Không tìm thấy loại thuốc để xóa").build();
        responseErrorConflict_Void = ResponseDTO.<Void>builder().status(409).msg("Không thể xóa loại thuốc do ràng buộc").build();
    }

    // --- Test cases ---

    // GET /list
    @Test
    @DisplayName("getAll - Thành công, trả về danh sách")
    void getAll_Success_ReturnsList() throws Exception {
        when(loaiThuocService.getAllLoaiThuocs()).thenReturn(responseListOK);

        mockMvc.perform(get("/loaithuoc/list")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].id").value(lt1.getId()))
                .andExpect(jsonPath("$.data[1].id").value(lt2.getId()));

        verify(loaiThuocService).getAllLoaiThuocs();
    }

    @Test
    @DisplayName("getAll - Thành công, trả về danh sách rỗng")
    void getAll_Success_ReturnsEmptyList() throws Exception {
        when(loaiThuocService.getAllLoaiThuocs()).thenReturn(responseListEmpty);

        mockMvc.perform(get("/loaithuoc/list")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data", hasSize(0)));

        verify(loaiThuocService).getAllLoaiThuocs();
    }

    // GET /search_by_ten_loai
    @Test
    @DisplayName("searchByTenLoai - Thành công, tìm thấy")
    void searchByTenLoai_Success_Found() throws Exception {
        String searchTerm = "Beta-lactam";
        // Giả sử chỉ trả về lt1
        ResponseDTO<List<LoaiThuoc>> responseFound = ResponseDTO.<List<LoaiThuoc>>builder()
                .status(200).msg("OK").data(Collections.singletonList(lt1)).build();
        when(loaiThuocService.searchByTenLoai(eq(searchTerm))).thenReturn(responseFound);

        mockMvc.perform(get("/loaithuoc/search_by_ten_loai")
                        .param("tenLoai", searchTerm)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id").value(lt1.getId()));

        verify(loaiThuocService).searchByTenLoai(eq(searchTerm));
    }

    @Test
    @DisplayName("searchByTenLoai - Thành công, không tìm thấy")
    void searchByTenLoai_Success_NotFound() throws Exception {
        String searchTerm = "Không có";
        when(loaiThuocService.searchByTenLoai(eq(searchTerm))).thenReturn(responseListEmpty);

        mockMvc.perform(get("/loaithuoc/search_by_ten_loai")
                        .param("tenLoai", searchTerm)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data", hasSize(0)));

        verify(loaiThuocService).searchByTenLoai(eq(searchTerm));
    }

    @Test
    @DisplayName("searchByTenLoai - Thiếu tham số")
    void searchByTenLoai_MissingParameter() throws Exception {
        mockMvc.perform(get("/loaithuoc/search_by_ten_loai") // Thiếu param "tenLoai"
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(loaiThuocService, never()).searchByTenLoai(anyString());
    }

    // POST /create
    @Test
    @DisplayName("create - Thành công")
    void create_Success() throws Exception {
        // Giả sử service trả về lt1 với id mới gán
        LoaiThuoc createdLT = new LoaiThuoc();
        createdLT.setId(3); // ID mới
        createdLT.setTenLoai(ltDTO_create.getTenLoai());
        createdLT.setMoTa(ltDTO_create.getMoTa());
        createdLT.setDanhMucThuoc(danhMucThuocSample);
        ResponseDTO<LoaiThuoc> responseCreated = ResponseDTO.<LoaiThuoc>builder()
                .status(200).msg("OK").data(createdLT).build(); // Nên là 201 Created

        when(loaiThuocService.create(any(LoaiThuocDTO.class))).thenReturn(responseCreated);

        mockMvc.perform(post("/loaithuoc/create") // Endpoint không có / ở đầu
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ltDTO_create))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Hoặc isCreated() nếu trả về 201
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.id").value(3))
                .andExpect(jsonPath("$.data.tenLoai").value(ltDTO_create.getTenLoai()));

        // Verify service được gọi với DTO đúng
        ArgumentCaptor<LoaiThuocDTO> dtoCaptor = ArgumentCaptor.forClass(LoaiThuocDTO.class);
        verify(loaiThuocService).create(dtoCaptor.capture());
        assertEquals(ltDTO_create.getTenLoai(), dtoCaptor.getValue().getTenLoai());
        assertEquals(ltDTO_create.getDanhMucThuocId(), dtoCaptor.getValue().getDanhMucThuocId());
    }

    @Test
    @DisplayName("create - Lỗi Conflict (Tên đã tồn tại)")
    void create_Fail_Conflict() throws Exception {
        when(loaiThuocService.create(any(LoaiThuocDTO.class))).thenReturn(responseErrorConflict);

        mockMvc.perform(post("/loaithuoc/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ltDTO_create))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict()) // Mong đợi 409
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.msg").value("Tên loại thuốc đã tồn tại"));

        verify(loaiThuocService).create(any(LoaiThuocDTO.class));
    }

    @Test
    @DisplayName("create - Lỗi Bad Request (DanhMucThuocId không hợp lệ)")
    void create_Fail_InvalidDanhMucId() throws Exception {
        when(loaiThuocService.create(any(LoaiThuocDTO.class))).thenReturn(responseErrorBadRequest_Single);
        ltDTO_create.setId(999);
        mockMvc.perform(post("/loaithuoc/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ltDTO_create)) // DTO này có ID danh mục hợp lệ, nhưng mock trả về lỗi
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("ID Danh mục thuốc không hợp lệ"));

        verify(loaiThuocService).create(any(LoaiThuocDTO.class));
    }

    // PUT /update
    @Test
    @DisplayName("update - Thành công")
    void update_Success() throws Exception {
        // Giả sử service trả về lt1 với thông tin đã cập nhật từ ltDTO_update
        lt1.setTenLoai(ltDTO_update.getTenLoai()); // Cập nhật đối tượng gốc để khớp response mock
        lt1.setMoTa(ltDTO_update.getMoTa());
        responseSingleOK.setData(lt1); // Response trả về đối tượng đã update

        when(loaiThuocService.update(any(LoaiThuocDTO.class))).thenReturn(responseSingleOK);

        mockMvc.perform(put("/loaithuoc/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ltDTO_update)) // Gửi DTO update (có ID=1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.id").value(ltDTO_update.getId()))
                .andExpect(jsonPath("$.data.tenLoai").value(ltDTO_update.getTenLoai()))
                .andExpect(jsonPath("$.data.moTa").value(ltDTO_update.getMoTa()));

        verify(loaiThuocService).update(ArgumentMatchers.refEq(ltDTO_update));
    }

    @Test
    @DisplayName("update - Lỗi Not Found")
    void update_Fail_NotFound() throws Exception {
        LoaiThuocDTO dtoNotFound = new LoaiThuocDTO();
        dtoNotFound.setId(99); // ID không tồn tại
        dtoNotFound.setTenLoai("Không quan trọng");
        dtoNotFound.setDanhMucThuocId(10);

        when(loaiThuocService.update(any(LoaiThuocDTO.class))).thenReturn(responseErrorNotFound_Single);

        mockMvc.perform(put("/loaithuoc/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoNotFound))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()) // Mong đợi 404
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.msg").value("Không tìm thấy loại thuốc"));

        verify(loaiThuocService).update(ArgumentMatchers.refEq(dtoNotFound));
    }


    // DELETE /delete
    @Test
    @DisplayName("delete - Thành công")
    void delete_Success() throws Exception {
        int idToDelete = 1;
        when(loaiThuocService.delete(eq(idToDelete))).thenReturn(responseVoidOK);

        mockMvc.perform(delete("/loaithuoc/delete")
                        .param("id", String.valueOf(idToDelete))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("OK"))
                ;

        verify(loaiThuocService).delete(eq(idToDelete));
    }

    @Test
    @DisplayName("delete - Lỗi Not Found")
    void delete_Fail_NotFound() throws Exception {
        int idToDelete = 99; // ID không tồn tại
        when(loaiThuocService.delete(eq(idToDelete))).thenReturn(responseErrorNotFound_Void);

        mockMvc.perform(delete("/loaithuoc/delete")
                        .param("id", String.valueOf(idToDelete))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()) // Mong đợi 404
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.msg").value("Không tìm thấy loại thuốc để xóa"));

        verify(loaiThuocService).delete(eq(idToDelete));
    }

    @Test
    @DisplayName("delete - Thiếu tham số id")
    void delete_MissingParameter() throws Exception {
        mockMvc.perform(delete("/loaithuoc/delete") // Không có param id
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // Spring xử lý

        verify(loaiThuocService, never()).delete(anyInt());
    }

    @Test
    void delete_InvalidParameterType() throws Exception {
        mockMvc.perform(delete("/loaithuoc/delete")
                        .param("id", "xyz") // id là chữ
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(loaiThuocService, never()).delete(anyInt());
    }

}
