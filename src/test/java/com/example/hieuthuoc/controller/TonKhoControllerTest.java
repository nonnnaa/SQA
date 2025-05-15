package com.example.hieuthuoc.controller;

import com.example.hieuthuoc.controller.TonKhoController;
import com.example.hieuthuoc.dto.PageDTO;
import com.example.hieuthuoc.dto.ResponseDTO;
import com.example.hieuthuoc.dto.SearchTonKhoDTO;
import com.example.hieuthuoc.dto.TonKhoDTO;
import com.example.hieuthuoc.entity.TonKho;
import com.example.hieuthuoc.service.TonKhoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Kích hoạt Mockito để hỗ trợ tiêm phụ thuộc và mock
@ExtendWith(MockitoExtension.class)
public class TonKhoControllerTest {

    // Tiêm controller với các phụ thuộc đã được mock
    @InjectMocks
    private TonKhoController tonKhoController;

    // Mock TonKhoService
    @Mock
    private TonKhoService tonKhoService;

    // Biến chứa dữ liệu kiểm thử
    private TonKhoDTO tonKhoDTOHopLe;
    private TonKho tonKho;
    private SearchTonKhoDTO searchTonKhoDTOHopLe;
    private PageDTO<List<TonKho>> pageDTO;

    // Phương thức thiết lập dữ liệu kiểm thử trước mỗi test
    @BeforeEach
    void thietLap() {
        // Khởi tạo TonKhoDTO hợp lệ
        tonKhoDTOHopLe = new TonKhoDTO();
        tonKhoDTOHopLe.setId(1);
        tonKhoDTOHopLe.setThuocId(1);
        tonKhoDTOHopLe.setSoLuong(100);

        // Khởi tạo TonKho
        tonKho = new TonKho();
        tonKho.setId(1);
        tonKho.setSoLuong(100);

        // Khởi tạo SearchTonKhoDTO hợp lệ
        searchTonKhoDTOHopLe = new SearchTonKhoDTO();
        searchTonKhoDTOHopLe.setTenThuoc("Paracetamol");
        searchTonKhoDTOHopLe.setSoLo("LOT123");
        searchTonKhoDTOHopLe.setTenNhaSanXuat("Pharma");
        searchTonKhoDTOHopLe.setSortedField("tenThuoc");
        searchTonKhoDTOHopLe.setCurrentPage(0);
        searchTonKhoDTOHopLe.setSize(20);

        // Khởi tạo PageDTO
        pageDTO = new PageDTO<>();
        pageDTO.setTotalElements(1);
        pageDTO.setTotalPages(1);
        pageDTO.setData(Collections.singletonList(tonKho));
    }

    // TC59
    // Test Case 1: Cập nhật tồn kho thành công với DTO hợp lệ
    // Kết quả mong đợi: Trả về ResponseDTO với trạng thái 200 và dữ liệu TonKho
    @Test
    void kiemTraUpdateTonKhoThanhCong() {
        // Sắp xếp: Mock TonKhoService để trả về ResponseDTO thành công
        ResponseDTO<TonKho> responseDTO = ResponseDTO.<TonKho>builder()
                .status(200)
                .msg("Thành công")
                .data(tonKho)
                .build();
        when(tonKhoService.update(any(TonKhoDTO.class))).thenReturn(responseDTO);

        // Thực hiện: Gọi phương thức updateTonKho
        ResponseDTO<TonKho> ketQua = tonKhoController.updateTonKho(tonKhoDTOHopLe);

        // Kiểm tra: Xác minh kết quả trả về
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(200, ketQua.getStatus(), "Trạng thái phải là 200");
        assertEquals("Thành công", ketQua.getMsg(), "Thông điệp phải là 'Thành công'");
        assertEquals(tonKho, ketQua.getData(), "Dữ liệu TonKho phải khớp");
        assertEquals(100, ketQua.getData().getSoLuong(), "Số lượng phải là 100");

        // Xác minh rằng tonKhoService được gọi
        verify(tonKhoService, times(1)).update(any(TonKhoDTO.class));
    }

    // TC60
    // Test Case 2: Thất bại khi tồn kho không tồn tại
    // Kết quả mong đợi: Trả về ResponseDTO với trạng thái 404
    @Test
    void kiemTraUpdateTonKhoThatBaiKhongTonTai() {
        // Sắp xếp: Mock TonKhoService để trả về ResponseDTO với trạng thái 404
        ResponseDTO<TonKho> responseDTO = ResponseDTO.<TonKho>builder()
                .status(404)
                .msg("Tồn kho không tồn tại")
                .build();
        when(tonKhoService.update(any(TonKhoDTO.class))).thenReturn(responseDTO);

        // Thực hiện: Gọi phương thức updateTonKho
        ResponseDTO<TonKho> ketQua = tonKhoController.updateTonKho(tonKhoDTOHopLe);

        // Kiểm tra: Xác minh kết quả trả về
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(404, ketQua.getStatus(), "Trạng thái phải là 404");
        assertEquals("Tồn kho không tồn tại", ketQua.getMsg(), "Thông điệp phải là 'Tồn kho không tồn tại'");
        assertNull(ketQua.getData(), "Dữ liệu phải là null");

        // Xác minh rằng tonKhoService được gọi
        verify(tonKhoService, times(1)).update(any(TonKhoDTO.class));
    }

    // TC61
    // Test Case 3: Đầu vào TonKhoDTO null
    // Kết quả mong đợi: Trả về ResponseDTO với trạng thái 400
    @Test
    void kiemTraUpdateTonKhoDauVaoNull() {
        // Sắp xếp: Mock TonKhoService để trả về ResponseDTO với trạng thái 400
        ResponseDTO<TonKho> responseDTO = ResponseDTO.<TonKho>builder()
                .status(400)
                .msg("Dữ liệu đầu vào không hợp lệ")
                .build();
        when(tonKhoService.update(null)).thenReturn(responseDTO);

        // Thực hiện: Gọi phương thức updateTonKho với DTO null
        ResponseDTO<TonKho> ketQua = tonKhoController.updateTonKho(null);

        // Kiểm tra: Xác minh kết quả trả về
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(400, ketQua.getStatus(), "Trạng thái phải là 400");
        assertEquals("Dữ liệu đầu vào không hợp lệ", ketQua.getMsg(), "Thông điệp phải là 'Dữ liệu đầu vào không hợp lệ'");
        assertNull(ketQua.getData(), "Dữ liệu phải là null");

        // Xác minh rằng tonKhoService được gọi
        verify(tonKhoService, times(1)).update(null);
    }

    // TC62
    // Test Case 4: Lỗi không mong muốn từ TonKhoService
    // Kết quả mong đợi: Ném ra RuntimeException khi TonKhoService gặp lỗi
    @Test
    void kiemTraUpdateTonKhoLoiService() {
        // Sắp xếp: Mock TonKhoService để ném ra RuntimeException
        when(tonKhoService.update(any(TonKhoDTO.class)))
                .thenThrow(new RuntimeException("Lỗi xử lý cập nhật tồn kho"));

        // Thực hiện & Kiểm tra: Xác minh rằng RuntimeException được ném ra
        RuntimeException ngoaiLe = assertThrows(
                RuntimeException.class,
                () -> tonKhoController.updateTonKho(tonKhoDTOHopLe),
                "Mong đợi RuntimeException khi TonKhoService gặp lỗi"
        );

        // Kiểm tra thông điệp của ngoại lệ
        assertEquals("Lỗi xử lý cập nhật tồn kho", ngoaiLe.getMessage(), "Thông điệp ngoại lệ phải khớp");

        // Xác minh rằng tonKhoService được gọi
        verify(tonKhoService, times(1)).update(any(TonKhoDTO.class));
    }

    // TC63
    // Test Case 5: Tìm kiếm tồn kho thành công với DTO hợp lệ
    // Kết quả mong đợi: Trả về ResponseDTO với trạng thái 200 và dữ liệu PageDTO
    @Test
    void kiemTraSearchThanhCong() {
        // Sắp xếp: Mock TonKhoService để trả về ResponseDTO thành công
        ResponseDTO<PageDTO<List<TonKho>>> responseDTO = ResponseDTO.<PageDTO<List<TonKho>>>builder()
                .status(200)
                .msg("Thành công")
                .data(pageDTO)
                .build();
        when(tonKhoService.search(any(SearchTonKhoDTO.class))).thenReturn(responseDTO);

        // Thực hiện: Gọi phương thức search
        ResponseDTO<PageDTO<List<TonKho>>> ketQua = tonKhoController.search(searchTonKhoDTOHopLe);

        // Kiểm tra: Xác minh kết quả trả về
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(200, ketQua.getStatus(), "Trạng thái phải là 200");
        assertEquals("Thành công", ketQua.getMsg(), "Thông điệp phải là 'Thành công'");
        assertEquals(pageDTO, ketQua.getData(), "Dữ liệu PageDTO phải khớp");
        assertEquals(1, ketQua.getData().getTotalElements(), "Tổng số phần tử phải là 1");
        assertEquals(1, ketQua.getData().getTotalPages(), "Tổng số trang phải là 1");
        assertEquals(1, ketQua.getData().getData().size(), "Danh sách phải chứa 1 phần tử");
        assertEquals(100, ketQua.getData().getData().get(0).getSoLuong(), "Số lượng phải là 100");

        // Xác minh rằng tonKhoService được gọi
        verify(tonKhoService, times(1)).search(any(SearchTonKhoDTO.class));
    }

    // TC64
    // Test Case 6: Tìm kiếm với kết quả rỗng
    // Kết quả mong đợi: Trả về ResponseDTO với trạng thái 200 và PageDTO rỗng
    @Test
    void kiemTraSearchKetQuaRong() {
        // Sắp xếp: Mock TonKhoService để trả về ResponseDTO với PageDTO rỗng
        PageDTO<List<TonKho>> emptyPageDTO = new PageDTO<>();
        emptyPageDTO.setTotalElements(0);
        emptyPageDTO.setTotalPages(0);
        emptyPageDTO.setData(Collections.emptyList());
        ResponseDTO<PageDTO<List<TonKho>>> responseDTO = ResponseDTO.<PageDTO<List<TonKho>>>builder()
                .status(200)
                .msg("Thành công")
                .data(emptyPageDTO)
                .build();
        when(tonKhoService.search(any(SearchTonKhoDTO.class))).thenReturn(responseDTO);

        // Thực hiện: Gọi phương thức search
        ResponseDTO<PageDTO<List<TonKho>>> ketQua = tonKhoController.search(searchTonKhoDTOHopLe);

        // Kiểm tra: Xác minh kết quả trả về
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(200, ketQua.getStatus(), "Trạng thái phải là 200");
        assertEquals("Thành công", ketQua.getMsg(), "Thông điệp phải là 'Thành công'");
        assertEquals(emptyPageDTO, ketQua.getData(), "Dữ liệu PageDTO phải khớp");
        assertEquals(0, ketQua.getData().getTotalElements(), "Tổng số phần tử phải là 0");
        assertEquals(0, ketQua.getData().getTotalPages(), "Tổng số trang phải là 0");
        assertTrue(ketQua.getData().getData().isEmpty(), "Danh sách phải rỗng");

        // Xác minh rằng tonKhoService được gọi
        verify(tonKhoService, times(1)).search(any(SearchTonKhoDTO.class));
    }

    // TC65
    // Test Case 7: Đầu vào SearchTonKhoDTO null
    // Kết quả mong đợi: Trả về ResponseDTO với trạng thái 400
    @Test
    void kiemTraSearchDauVaoNull() {
        // Sắp xếp: Mock TonKhoService để trả về ResponseDTO với trạng thái 400
        ResponseDTO<PageDTO<List<TonKho>>> responseDTO = ResponseDTO.<PageDTO<List<TonKho>>>builder()
                .status(400)
                .msg("Dữ liệu đầu vào không hợp lệ")
                .build();
        when(tonKhoService.search(null)).thenReturn(responseDTO);

        // Thực hiện: Gọi phương thức search với DTO null
        ResponseDTO<PageDTO<List<TonKho>>> ketQua = tonKhoController.search(null);

        // Kiểm tra: Xác minh kết quả trả về
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(400, ketQua.getStatus(), "Trạng thái phải là 400");
        assertEquals("Dữ liệu đầu vào không hợp lệ", ketQua.getMsg(), "Thông điệp phải là 'Dữ liệu đầu vào không hợp lệ'");
        assertNull(ketQua.getData(), "Dữ liệu phải là null");

        // Xác minh rằng tonKhoService được gọi
        verify(tonKhoService, times(1)).search(null);
    }

    // TC66
    // Test Case 8: Lỗi không mong muốn từ TonKhoService
    // Kết quả mong đợi: Ném ra RuntimeException khi TonKhoService gặp lỗi
    @Test
    void kiemTraSearchLoiService() {
        // Sắp xếp: Mock TonKhoService để ném ra RuntimeException
        when(tonKhoService.search(any(SearchTonKhoDTO.class)))
                .thenThrow(new RuntimeException("Lỗi xử lý tìm kiếm tồn kho"));

        // Thực hiện & Kiểm tra: Xác minh rằng RuntimeException được ném ra
        RuntimeException ngoaiLe = assertThrows(
                RuntimeException.class,
                () -> tonKhoController.search(searchTonKhoDTOHopLe),
                "Mong đợi RuntimeException khi TonKhoService gặp lỗi"
        );

        // Kiểm tra thông điệp của ngoại lệ
        assertEquals("Lỗi xử lý tìm kiếm tồn kho", ngoaiLe.getMessage(), "Thông điệp ngoại lệ phải khớp");

        // Xác minh rằng tonKhoService được gọi
        verify(tonKhoService, times(1)).search(any(SearchTonKhoDTO.class));
    }
}