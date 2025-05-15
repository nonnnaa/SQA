package com.example.hieuthuoc.controller;
import static com.example.hieuthuoc.entity.ThongBao.LoaiThongBao.KHUYEN_MAI;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.hieuthuoc.dto.PageDTO;
import com.example.hieuthuoc.dto.ResponseDTO;
import com.example.hieuthuoc.dto.SearchDTO;
import com.example.hieuthuoc.dto.ThongBaoDTO;
import com.example.hieuthuoc.entity.ThongBao;
import com.example.hieuthuoc.service.ThongBaoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// ThongBaoControllerTest.java

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class ThongBaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private ThongBaoController thongBaoController;

    @Mock
    private ThongBaoService thongBaoService;


    @Test
    void testGetByNguoiDungId_Success() {
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setId(1);
        searchDTO.setCurrentPage(0);
        searchDTO.setSize(10);

        List<ThongBao> thongBaos = List.of(new ThongBao(), new ThongBao());

        PageDTO<List<ThongBao>> pageDTO = new PageDTO<>();
        pageDTO.setTotalPages(1);
        pageDTO.setTotalElements(2);
        pageDTO.setCurrentPage(0);
        pageDTO.setData(thongBaos);

        // Giả sử status là 200 cho thành công
        ResponseDTO<PageDTO<List<ThongBao>>> response = new ResponseDTO<>(200, "Success", pageDTO);

        Mockito.when(thongBaoService.getByNguoiDungId(searchDTO)).thenReturn(response);

        ResponseDTO<PageDTO<List<ThongBao>>> result = thongBaoController.getByNguoiDungId(searchDTO);

        // Kiểm tra status là 200
        Assertions.assertEquals(200, result.getStatus());
        // Kiểm tra dữ liệu trả về có chính xác
        Assertions.assertEquals(2, result.getData().getData().size());
        Assertions.assertEquals(1, result.getData().getTotalPages());
        Assertions.assertEquals(2, result.getData().getTotalElements());
        Assertions.assertEquals(0, result.getData().getCurrentPage());
        Assertions.assertEquals("Success", result.getMsg());
    }




    @Test
    void testGetByNguoiDungId_emptyList() throws Exception {
        // Arrange
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setCurrentPage(0);
        searchDTO.setSize(5);
        searchDTO.setKeyWord("Khẩn cấp"); // nếu có
        searchDTO.setId(999); // ID không tồn tại

        String requestJson = new ObjectMapper().writeValueAsString(searchDTO);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/thongbao/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isEmpty())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("Thành công."));
    }


    @Test
    void testGetByNguoiDungId_invalid() throws Exception {
        // Tạo searchDTO thiếu nguoiDungId hoặc có page < 0
        SearchDTO invalidSearchDTO = new SearchDTO();
        invalidSearchDTO.setId(-1);  // input không hợp lệ
        invalidSearchDTO.setCurrentPage(5);
        invalidSearchDTO.setSize(10);
        invalidSearchDTO.setKeyWord("test");

        // Convert to JSON
        String requestJson = new ObjectMapper().writeValueAsString(invalidSearchDTO);
        // Gửi POST request
        mockMvc.perform(MockMvcRequestBuilders.post("/thongbao/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest()) // Kỳ vọng lỗi 400
                .andExpect(content().string(Matchers.containsString("Input không hợp lệ"))); // Kiểm tra thông báo lỗi
    }

    @Test
    void testSearchByLoaiThongBao_success() {
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setKeyWord("Khẩn cấp");
        searchDTO.setCurrentPage(0);
        searchDTO.setSize(5);

        ThongBao tb = new ThongBao();
        tb.setId(1);
        tb.setTieuDe("Cảnh báo nhiệt độ cao");

        PageDTO<List<ThongBao>> pageDTO = new PageDTO<>();
        pageDTO.setTotalPages(1);
        pageDTO.setTotalElements(1L);
        pageDTO.setData(List.of(tb));


        ResponseDTO<PageDTO<List<ThongBao>>> expected = new ResponseDTO<>(200, "Thành công", pageDTO);

        when(thongBaoService.getByLoaiThongBao(searchDTO)).thenReturn(expected);

        ResponseDTO<PageDTO<List<ThongBao>>> actual = thongBaoController.searchByLoaiThongBao(searchDTO);

        System.out.println("✅ Expected Output: " + expected);
        System.out.println("🎯 Actual Output: " + actual);

        assertEquals(200, actual.getStatus());
        assertEquals("Thành công", actual.getMsg());
        assertEquals(1, actual.getData().getData().size());
    }

    @Test
    void testSearchByLoaiThongBao_notFound() {
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setKeyWord("Không tồn tại");
        searchDTO.setCurrentPage(0);
        searchDTO.setSize(5);

        PageDTO<List<ThongBao>> emptyPage = new PageDTO<>();
        emptyPage.setTotalPages(0);
        emptyPage.setTotalElements(0L);
        emptyPage.setData(List.of());

        ResponseDTO<PageDTO<List<ThongBao>>> expected = new ResponseDTO<>(200, "Không có kết quả", emptyPage);

        when(thongBaoService.getByLoaiThongBao(searchDTO)).thenReturn(expected);

        ResponseDTO<PageDTO<List<ThongBao>>> actual = thongBaoController.searchByLoaiThongBao(searchDTO);

        System.out.println("✅ Expected Output: " + expected);
        System.out.println("🎯 Actual Output: " + actual);

        assertEquals(404, actual.getStatus());
        assertEquals("Không có kết quả", actual.getMsg());
        assertTrue(actual.getData().getData().isEmpty());
    }

    @Test
    void testSearchByLoaiThongBao_nullKeyword() {
        // Arrange
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setKeyWord(null); // Không có keyword
        searchDTO.setCurrentPage(0);
        searchDTO.setSize(5);

        List<ThongBao> thongBaoList = Arrays.asList(
                new ThongBao(), new ThongBao()
        );

        PageDTO<List<ThongBao>> pageDTO = new PageDTO<>();
        pageDTO.setTotalPages(1);
        pageDTO.setTotalElements(2);
        pageDTO.setCurrentPage(0);
        pageDTO.setData(thongBaoList);

        ResponseDTO<PageDTO<List<ThongBao>>> expected = new ResponseDTO<>(200, "Thành công", pageDTO);

        when(thongBaoService.getByLoaiThongBao(searchDTO)).thenReturn(expected);

        // Act
        ResponseDTO<PageDTO<List<ThongBao>>> actual = thongBaoController.searchByLoaiThongBao(searchDTO);

        // Assert
        assertEquals(200, actual.getStatus());
        assertEquals("Thành công", actual.getMsg());
        assertNotNull(actual.getData());
        assertEquals(2, actual.getData().getData().size());
    }





    @Test
    void testSearchByLoaiThongBao_emptyKeyword() {
        // Arrange
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setKeyWord(""); // Chuỗi rỗng
        searchDTO.setCurrentPage(0);
        searchDTO.setSize(5);

        // Expected: Trả về ResponseDTO với mã lỗi 400 và thông báo "Từ khóa tìm kiếm không hợp lệ"
        ResponseDTO<PageDTO<List<ThongBao>>> expected = new ResponseDTO<>(400, "Từ khóa tìm kiếm không hợp lệ", null);

        // Mock phương thức searchByLoaiThongBao trong controller trả về expected
        when(thongBaoController.searchByLoaiThongBao(searchDTO)).thenReturn(expected);

        // Act: Call controller method
        ResponseDTO<PageDTO<List<ThongBao>>> actual = thongBaoController.searchByLoaiThongBao(searchDTO);

        // Assert
        assertNotNull(actual); // Đảm bảo actual không null
        assertEquals(400, actual.getStatus());
        assertEquals("Từ khóa tìm kiếm không hợp lệ", actual.getMsg());
        assertNull(actual.getData());
    }




    @Test
    void testGetById_success() {
        // Tạo giả thông báo
        ThongBao thongBao = new ThongBao();
        thongBao.setId(1);
        thongBao.setTieuDe("Thông báo khẩn");

        // Tạo response giả từ service
        ResponseDTO<ThongBao> expected = new ResponseDTO<>(200, "Lấy thành công", thongBao);

        // Khi gọi service.getById(1) sẽ trả về expected
        when(thongBaoService.getById(1)).thenReturn(expected);

        // Gọi controller
        ResponseDTO<ThongBao> actual = thongBaoController.getById(1);

        // In kết quả
        System.out.println("Expected Output: " + expected);
        System.out.println("Actual Output: " + actual);

        // So sánh kết quả
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getMsg(), actual.getMsg());
        assertEquals(expected.getData(), actual.getData());
    }

    @Test
    void testGetById_notFound() {
        // Giả lập response không tìm thấy
        ResponseDTO<ThongBao> expected = new ResponseDTO<>(404, "Không tìm thấy thông báo", null);

        when(thongBaoService.getById(999)).thenReturn(expected);

        ResponseDTO<ThongBao> actual = thongBaoController.getById(999);

        System.out.println("Expected Output: " + expected);
        System.out.println("Actual Output: " + actual);

        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getMsg(), actual.getMsg());
        assertNull(actual.getData());
    }

    @Test
    void create_success() {
        // Arrange
        ThongBaoDTO thongBaoDTO = new ThongBaoDTO();
        thongBaoDTO.setTieuDe("Thông báo mới");
        thongBaoDTO.setNoiDung("Nội dung thông báo");
        thongBaoDTO.setLoaiThongBao("HE_THONG");
        thongBaoDTO.setNguoiDungId(List.of(1, 2));

        ThongBao thongBao = new ThongBao();
        thongBao.setId(1);
        thongBao.setTieuDe(thongBaoDTO.getTieuDe());
        thongBao.setNoiDung(thongBaoDTO.getNoiDung());

        ResponseDTO<ThongBao> expected = new ResponseDTO<>();
        expected.setStatus(200);
        expected.setMsg("Success");
        expected.setData(thongBao);

        when(thongBaoService.create(thongBaoDTO)).thenReturn(expected);

        // Act
        ResponseDTO<ThongBao> actual = thongBaoController.create(thongBaoDTO);

        // Console Output
        System.out.println("Expected Output: " + expected);
        System.out.println("Actual Output: " + actual);

        // Assert
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getMsg(), actual.getMsg());
        assertNotNull(actual.getData());
        assertEquals("Thông báo mới", actual.getData().getTieuDe());
    }

    @Test
    void create_missingTitle() {
        // Arrange
        ThongBaoDTO thongBaoDTO = new ThongBaoDTO();
        thongBaoDTO.setTieuDe(null);
        thongBaoDTO.setNoiDung("Nội dung test");
        thongBaoDTO.setLoaiThongBao("KHUYEN_MAI");

        ResponseDTO<ThongBao> expected = new ResponseDTO<>();
        expected.setStatus(400);
        expected.setMsg("Tiêu đề không được để trống");
        expected.setData(null);

        when(thongBaoService.create(thongBaoDTO)).thenReturn(expected);

        // Act
        ResponseDTO<ThongBao> actual = thongBaoController.create(thongBaoDTO);

        // Console Output
        System.out.println("Expected Output: " + expected);
        System.out.println("Actual Output: " + actual);

        // Assert
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getMsg(), actual.getMsg());
        assertNull(actual.getData());
    }

    @Test
    void update_success() {
        ThongBaoDTO dto = new ThongBaoDTO();
        dto.setId(1);
        dto.setTieuDe("Cập nhật");
        dto.setNoiDung("Nội dung mới");
        dto.setLoaiThongBao("SU_KIEN");

        ThongBao thongBao = new ThongBao();
        thongBao.setId(1);
        thongBao.setTieuDe("Cập nhật");
        thongBao.setNoiDung("Nội dung mới");

        ResponseDTO<ThongBao> expected = new ResponseDTO<>(200, "Cập nhật thành công", thongBao);

        when(thongBaoService.update(dto)).thenReturn(expected);

        ResponseDTO<ThongBao> actual = thongBaoController.update(dto);

        System.out.println("Expected Output: " + expected);
        System.out.println("Actual Output: " + actual);

        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getMsg(), actual.getMsg());
        assertNotNull(actual.getData());
    }

    //
    @Test
    void update_idNotFound() {
        ThongBaoDTO dto = new ThongBaoDTO();
        dto.setId(999);
        dto.setTieuDe("Không tồn tại");
        dto.setNoiDung("Không tìm thấy");

        ResponseDTO<ThongBao> expected = new ResponseDTO<>(404, "Không tìm thấy thông báo", null);

        when(thongBaoService.update(dto)).thenReturn(expected);

        ResponseDTO<ThongBao> actual = thongBaoController.update(dto);

        System.out.println("Expected Output: " + expected);
        System.out.println("Actual Output: " + actual);

        assertEquals(404, actual.getStatus());
        assertEquals("Không tìm thấy thông báo", actual.getMsg());
        assertNull(actual.getData());
    }

    //
    @Test
    void update_missingTitle() {
        ThongBaoDTO dto = new ThongBaoDTO();
        dto.setId(1);
        dto.setTieuDe(null); // thiếu tiêu đề
        dto.setNoiDung("Nội dung vẫn có");

        ResponseDTO<ThongBao> expected = new ResponseDTO<>(400, "Tiêu đề không được để trống", null);

        when(thongBaoService.update(dto)).thenReturn(expected);

        ResponseDTO<ThongBao> actual = thongBaoController.update(dto);

        System.out.println("Expected Output: " + expected);
        System.out.println("Actual Output: " + actual);

        assertEquals(400, actual.getStatus());
        assertEquals("Tiêu đề không được để trống", actual.getMsg());
        assertNull(actual.getData());
    }

    @Test
    void delete_success() {
        Integer id = 1;
        ResponseDTO<Void> expected = new ResponseDTO<>(200, "Xóa thành công", null);

        when(thongBaoService.delete(id)).thenReturn(expected);

        ResponseDTO<Void> actual = thongBaoController.delete(id);

        System.out.println("Expected Output: " + expected);
        System.out.println("Actual Output: " + actual);

        assertEquals(200, actual.getStatus());
        assertEquals("Xóa thành công", actual.getMsg());
        assertNull(actual.getData());
    }

    @Test
    void delete_notFound() {
        Integer id = 999;
        ResponseDTO<Void> expected = new ResponseDTO<>(404, "Không tìm thấy thông báo", null);

        when(thongBaoService.delete(id)).thenReturn(expected);

        ResponseDTO<Void> actual = thongBaoController.delete(id);

        System.out.println("Expected Output: " + expected);
        System.out.println("Actual Output: " + actual);

        assertEquals(404, actual.getStatus());
        assertEquals("Không tìm thấy thông báo", actual.getMsg());
        assertNull(actual.getData());
    }

}

