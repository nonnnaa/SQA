package com.example.hieuthuoc.controller;

import com.example.hieuthuoc.controller.NhaCungCapController;
import com.example.hieuthuoc.dto.NhaCungCapDTO;
import com.example.hieuthuoc.dto.ResponseDTO;
import com.example.hieuthuoc.entity.NhaCungCap;
import com.example.hieuthuoc.service.NhaCungCapService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

// Kích hoạt Mockito để hỗ trợ tiêm phụ thuộc và mock
@ExtendWith(MockitoExtension.class)
public class NhaCungCapControllerTest {

    // Tiêm controller với các phụ thuộc đã được mock
    @InjectMocks
    private NhaCungCapController nhaCungCapController;

    // Mock NhaCungCapService
    @Mock
    private NhaCungCapService nhaCungCapService;

    // Biến chứa dữ liệu kiểm thử
    private NhaCungCapDTO nhaCungCapDTOHopLe;
    private NhaCungCap nhaCungCap;
    private List<NhaCungCap> danhSachNhaCungCap;

    // Phương thức thiết lập dữ liệu kiểm thử trước mỗi test
    @BeforeEach
    void thietLap() {
        // Khởi tạo NhaCungCapDTO hợp lệ
        nhaCungCapDTOHopLe = new NhaCungCapDTO();
        nhaCungCapDTOHopLe.setId(1);
        nhaCungCapDTOHopLe.setTenNhaCungCap("PharmaCorp");
        nhaCungCapDTOHopLe.setDiaChi("123 Đường Láng, Hà Nội");

        // Khởi tạo NhaCungCap
        nhaCungCap = new NhaCungCap();
        nhaCungCap.setId(1);
        nhaCungCap.setTenNhaCungCap("PharmaCorp");
        nhaCungCap.setDiaChi("123 Đường Láng, Hà Nội");

        // Khởi tạo danh sách nhà cung cấp
        danhSachNhaCungCap = Collections.singletonList(nhaCungCap);
    }

    // TC67
    // Test Case 1: Lấy danh sách nhà cung cấp thành công
    // Kết quả mong đợi: Trả về ResponseDTO với trạng thái 200 và danh sách nhà cung cấp
    @Test
    void kiemTraGetAllThanhCong() {
        // Sắp xếp: Mock NhaCungCapService để trả về ResponseDTO thành công
        ResponseDTO<List<NhaCungCap>> responseDTO = ResponseDTO.<List<NhaCungCap>>builder()
                .status(200)
                .msg("Thành công")
                .data(danhSachNhaCungCap)
                .build();
        when(nhaCungCapService.getAll()).thenReturn(responseDTO);

        // Thực hiện: Gọi phương thức getAll
        ResponseDTO<List<NhaCungCap>> ketQua = nhaCungCapController.getAll();

        // Kiểm tra: Xác minh kết quả trả về
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(200, ketQua.getStatus(), "Trạng thái phải là 200");
        assertEquals("Thành công", ketQua.getMsg(), "Thông điệp phải là 'Thành công'");
        assertEquals(danhSachNhaCungCap, ketQua.getData(), "Danh sách nhà cung cấp phải khớp");
        assertEquals(1, ketQua.getData().size(), "Danh sách phải chứa 1 nhà cung cấp");
        assertEquals("PharmaCorp", ketQua.getData().get(0).getTenNhaCungCap(), "Tên nhà cung cấp phải là 'PharmaCorp'");

        // Xác minh rằng nhaCungCapService được gọi
        verify(nhaCungCapService, times(1)).getAll();
    }

    // TC68
    // Test Case 2: Lấy danh sách nhà cung cấp rỗng
    // Kết quả mong đợi: Trả về ResponseDTO với trạng thái 200 và danh sách rỗng
    @Test
    void kiemTraGetAllKetQuaRong() {
        // Sắp xếp: Mock NhaCungCapService để trả về ResponseDTO với danh sách rỗng
        ResponseDTO<List<NhaCungCap>> responseDTO = ResponseDTO.<List<NhaCungCap>>builder()
                .status(200)
                .msg("Thành công")
                .data(Collections.emptyList())
                .build();
        when(nhaCungCapService.getAll()).thenReturn(responseDTO);

        // Thực hiện: Gọi phương thức getAll
        ResponseDTO<List<NhaCungCap>> ketQua = nhaCungCapController.getAll();

        // Kiểm tra: Xác minh kết quả trả về
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(200, ketQua.getStatus(), "Trạng thái phải là 200");
        assertEquals("Thành công", ketQua.getMsg(), "Thông điệp phải là 'Thành công'");
        assertTrue(ketQua.getData().isEmpty(), "Danh sách nhà cung cấp phải rỗng");

        // Xác minh rằng nhaCungCapService được gọi
        verify(nhaCungCapService, times(1)).getAll();
    }

    // TC69
    // Test Case 3: Lỗi không mong muốn từ NhaCungCapService khi lấy danh sách
    // Kết quả mong đợi: Ném ra RuntimeException khi service gặp lỗi
    @Test
    void kiemTraGetAllLoiService() {
        // Sắp xếp: Mock NhaCungCapService để ném ra RuntimeException
        when(nhaCungCapService.getAll()).thenThrow(new RuntimeException("Lỗi lấy danh sách nhà cung cấp"));

        // Thực hiện & Kiểm tra: Xác minh rằng RuntimeException được ném ra
        RuntimeException ngoaiLe = assertThrows(
                RuntimeException.class,
                () -> nhaCungCapController.getAll(),
                "Mong đợi RuntimeException khi NhaCungCapService gặp lỗi"
        );

        // Kiểm tra thông điệp của ngoại lệ
        assertEquals("Lỗi lấy danh sách nhà cung cấp", ngoaiLe.getMessage(), "Thông điệp ngoại lệ phải khớp");

        // Xác minh rằng nhaCungCapService được gọi
        verify(nhaCungCapService, times(1)).getAll();
    }

    // TC70
    // Test Case 4: Tìm kiếm nhà cung cấp thành công theo tên
    // Kết quả mong đợi: Trả về ResponseDTO với trạng thái 200 và danh sách nhà cung cấp
    @Test
    void kiemTraSearchByTenNhaCungCapThanhCong() {
        // Sắp xếp: Mock NhaCungCapService để trả về ResponseDTO thành công
        ResponseDTO<List<NhaCungCap>> responseDTO = ResponseDTO.<List<NhaCungCap>>builder()
                .status(200)
                .msg("Thành công")
                .data(danhSachNhaCungCap)
                .build();
        when(nhaCungCapService.searchByTenNhaCungCap("PharmaCorp")).thenReturn(responseDTO);

        // Thực hiện: Gọi phương thức searchByTenNhaCungCap
        ResponseDTO<List<NhaCungCap>> ketQua = nhaCungCapController.searchByTenNhaCungCap("PharmaCorp");

        // Kiểm tra: Xác minh kết quả trả về
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(200, ketQua.getStatus(), "Trạng thái phải là 200");
        assertEquals("Thành công", ketQua.getMsg(), "Thông điệp phải là 'Thành công'");
        assertEquals(danhSachNhaCungCap, ketQua.getData(), "Danh sách nhà cung cấp phải khớp");
        assertEquals(1, ketQua.getData().size(), "Danh sách phải chứa 1 nhà cung cấp");
        assertEquals("PharmaCorp", ketQua.getData().get(0).getTenNhaCungCap(), "Tên nhà cung cấp phải là 'PharmaCorp'");

        // Xác minh rằng nhaCungCapService được gọi
        verify(nhaCungCapService, times(1)).searchByTenNhaCungCap("PharmaCorp");
    }

    // TC71
    // Test Case 5: Tìm kiếm nhà cung cấp với tên không tồn tại
    // Kết quả mong đợi: Trả về ResponseDTO với trạng thái 200 và danh sách rỗng
    @Test
    void kiemTraSearchByTenNhaCungCapKetQuaRong() {
        // Sắp xếp: Mock NhaCungCapService để trả về ResponseDTO với danh sách rỗng
        ResponseDTO<List<NhaCungCap>> responseDTO = ResponseDTO.<List<NhaCungCap>>builder()
                .status(200)
                .msg("Thành công")
                .data(Collections.emptyList())
                .build();
        when(nhaCungCapService.searchByTenNhaCungCap("NonExistent")).thenReturn(responseDTO);

        // Thực hiện: Gọi phương thức searchByTenNhaCungCap
        ResponseDTO<List<NhaCungCap>> ketQua = nhaCungCapController.searchByTenNhaCungCap("NonExistent");

        // Kiểm tra: Xác minh kết quả trả về
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(200, ketQua.getStatus(), "Trạng thái phải là 200");
        assertEquals("Thành công", ketQua.getMsg(), "Thông điệp phải là 'Thành công'");
        assertTrue(ketQua.getData().isEmpty(), "Danh sách nhà cung cấp phải rỗng");

        // Xác minh rằng nhaCungCapService được gọi
        verify(nhaCungCapService, times(1)).searchByTenNhaCungCap("NonExistent");
    }

    // TC72
    // Test Case 6: Tìm kiếm với tên nhà cung cấp null hoặc rỗng
    // Kết quả mong đợi: Trả về ResponseDTO với trạng thái 400
    @Test
    void kiemTraSearchByTenNhaCungCapDauVaoNull() {
        // Sắp xếp: Mock NhaCungCapService để trả về ResponseDTO với trạng thái 400
        ResponseDTO<List<NhaCungCap>> responseDTO = ResponseDTO.<List<NhaCungCap>>builder()
                .status(400)
                .msg("Tên nhà cung cấp không hợp lệ")
                .build();
        when(nhaCungCapService.searchByTenNhaCungCap(null)).thenReturn(responseDTO);

        // Thực hiện: Gọi phương thức searchByTenNhaCungCap với tên null
        ResponseDTO<List<NhaCungCap>> ketQua = nhaCungCapController.searchByTenNhaCungCap(null);

        // Kiểm tra: Xác minh kết quả trả về
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(400, ketQua.getStatus(), "Trạng thái phải là 400");
        assertEquals("Tên nhà cung cấp không hợp lệ", ketQua.getMsg(), "Thông điệp phải là 'Tên nhà cung cấp không hợp lệ'");
        assertNull(ketQua.getData(), "Dữ liệu phải là null");

        // Xác minh rằng nhaCungCapService được gọi
        verify(nhaCungCapService, times(1)).searchByTenNhaCungCap(null);
    }

    // TC73
    // Test Case 7: Lỗi không mong muốn từ NhaCungCapService khi tìm kiếm
    // Kết quả mong đợi: Ném ra RuntimeException khi service gặp lỗi
    @Test
    void kiemTraSearchByTenNhaCungCapLoiService() {
        // Sắp xếp: Mock NhaCungCapService để ném ra RuntimeException
        when(nhaCungCapService.searchByTenNhaCungCap(anyString()))
                .thenThrow(new RuntimeException("Lỗi tìm kiếm nhà cung cấp"));

        // Thực hiện & Kiểm tra: Xác minh rằng RuntimeException được ném ra
        RuntimeException ngoaiLe = assertThrows(
                RuntimeException.class,
                () -> nhaCungCapController.searchByTenNhaCungCap("PharmaCorp"),
                "Mong đợi RuntimeException khi NhaCungCapService gặp lỗi"
        );

        // Kiểm tra thông điệp của ngoại lệ
        assertEquals("Lỗi tìm kiếm nhà cung cấp", ngoaiLe.getMessage(), "Thông điệp ngoại lệ phải khớp");

        // Xác minh rằng nhaCungCapService được gọi
        verify(nhaCungCapService, times(1)).searchByTenNhaCungCap("PharmaCorp");
    }

    // TC74
    // Test Case 8: Tạo nhà cung cấp thành công với DTO hợp lệ
    // Kết quả mong đợi: Trả về ResponseDTO với trạng thái 200 và dữ liệu NhaCungCap
    @Test
    void kiemTraCreateThanhCong() {
        // Sắp xếp: Mock NhaCungCapService để trả về ResponseDTO thành công
        ResponseDTO<NhaCungCap> responseDTO = ResponseDTO.<NhaCungCap>builder()
                .status(200)
                .msg("Thành công")
                .data(nhaCungCap)
                .build();
        when(nhaCungCapService.create(any(NhaCungCapDTO.class))).thenReturn(responseDTO);

        // Thực hiện: Gọi phương thức create
        ResponseDTO<NhaCungCap> ketQua = nhaCungCapController.create(nhaCungCapDTOHopLe);

        // Kiểm tra: Xác minh kết quả trả về
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(200, ketQua.getStatus(), "Trạng thái phải là 200");
        assertEquals("Thành công", ketQua.getMsg(), "Thông điệp phải là 'Thành công'");
        assertEquals(nhaCungCap, ketQua.getData(), "Dữ liệu NhaCungCap phải khớp");
        assertEquals("PharmaCorp", ketQua.getData().getTenNhaCungCap(), "Tên nhà cung cấp phải là 'PharmaCorp'");

        // Xác minh rằng nhaCungCapService được gọi
        verify(nhaCungCapService, times(1)).create(any(NhaCungCapDTO.class));
    }

    // TC75
    // Test Case 9: Tạo nhà cung cấp với DTO null
    // Kết quả mong đợi: Trả về ResponseDTO với trạng thái 400
    @Test
    void kiemTraCreateDauVaoNull() {
        // Sắp xếp: Mock NhaCungCapService để trả về ResponseDTO với trạng thái 400
        ResponseDTO<NhaCungCap> responseDTO = ResponseDTO.<NhaCungCap>builder()
                .status(400)
                .msg("Dữ liệu đầu vào không hợp lệ")
                .build();
        when(nhaCungCapService.create(null)).thenReturn(responseDTO);

        // Thực hiện: Gọi phương thức create với DTO null
        ResponseDTO<NhaCungCap> ketQua = nhaCungCapController.create(null);

        // Kiểm tra: Xác minh kết quả trả về
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(400, ketQua.getStatus(), "Trạng thái phải là 400");
        assertEquals("Dữ liệu đầu vào không hợp lệ", ketQua.getMsg(), "Thông điệp phải là 'Dữ liệu đầu vào không hợp lệ'");
        assertNull(ketQua.getData(), "Dữ liệu phải là null");

        // Xác minh rằng nhaCungCapService được gọi
        verify(nhaCungCapService, times(1)).create(null);
    }

    // TC76
    // Test Case 10: Lỗi không mong muốn từ NhaCungCapService khi tạo
    // Kết quả mong đợi: Ném ra RuntimeException khi service gặp lỗi
    @Test
    void kiemTraCreateLoiService() {
        // Sắp xếp: Mock NhaCungCapService để ném ra RuntimeException
        when(nhaCungCapService.create(any(NhaCungCapDTO.class)))
                .thenThrow(new RuntimeException("Lỗi tạo nhà cung cấp"));

        // Thực hiện & Kiểm tra: Xác minh rằng RuntimeException được ném ra
        RuntimeException ngoaiLe = assertThrows(
                RuntimeException.class,
                () -> nhaCungCapController.create(nhaCungCapDTOHopLe),
                "Mong đợi RuntimeException khi NhaCungCapService gặp lỗi"
        );

        // Kiểm tra thông điệp của ngoại lệ
        assertEquals("Lỗi tạo nhà cung cấp", ngoaiLe.getMessage(), "Thông điệp ngoại lệ phải khớp");

        // Xác minh rằng nhaCungCapService được gọi
        verify(nhaCungCapService, times(1)).create(any(NhaCungCapDTO.class));
    }

    // TC77
    // Test Case 11: Cập nhật nhà cung cấp thành công với DTO hợp lệ
    // Kết quả mong đợi: Trả về ResponseDTO với trạng thái 200 và dữ liệu NhaCungCap
    @Test
    void kiemTraUpdateThanhCong() {
        // Sắp xếp: Mock NhaCungCapService để trả về ResponseDTO thành công
        ResponseDTO<NhaCungCap> responseDTO = ResponseDTO.<NhaCungCap>builder()
                .status(200)
                .msg("Thành công")
                .data(nhaCungCap)
                .build();
        when(nhaCungCapService.update(any(NhaCungCapDTO.class))).thenReturn(responseDTO);

        // Thực hiện: Gọi phương thức update
        ResponseDTO<NhaCungCap> ketQua = nhaCungCapController.update(nhaCungCapDTOHopLe);

        // Kiểm tra: Xác minh kết quả trả về
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(200, ketQua.getStatus(), "Trạng thái phải là 200");
        assertEquals("Thành công", ketQua.getMsg(), "Thông điệp phải là 'Thành công'");
        assertEquals(nhaCungCap, ketQua.getData(), "Dữ liệu NhaCungCap phải khớp");
        assertEquals("PharmaCorp", ketQua.getData().getTenNhaCungCap(), "Tên nhà cung cấp phải là 'PharmaCorp'");

        // Xác minh rằng nhaCungCapService được gọi
        verify(nhaCungCapService, times(1)).update(any(NhaCungCapDTO.class));
    }

    // TC78
    // Test Case 12: Thất bại khi nhà cung cấp không tồn tại
    // Kết quả mong đợi: Trả về ResponseDTO với trạng thái 404
    @Test
    void kiemTraUpdateThatBaiKhongTonTai() {
        // Sắp xếp: Mock NhaCungCapService để trả về ResponseDTO với trạng thái 404
        ResponseDTO<NhaCungCap> responseDTO = ResponseDTO.<NhaCungCap>builder()
                .status(404)
                .msg("Nhà cung cấp không tồn tại")
                .build();
        when(nhaCungCapService.update(any(NhaCungCapDTO.class))).thenReturn(responseDTO);

        // Thực hiện: Gọi phương thức update
        ResponseDTO<NhaCungCap> ketQua = nhaCungCapController.update(nhaCungCapDTOHopLe);

        // Kiểm tra: Xác minh kết quả trả về
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(404, ketQua.getStatus(), "Trạng thái phải là 404");
        assertEquals("Nhà cung cấp không tồn tại", ketQua.getMsg(), "Thông điệp phải là 'Nhà cung cấp không tồn tại'");
        assertNull(ketQua.getData(), "Dữ liệu phải là null");

        // Xác minh rằng nhaCungCapService được gọi
        verify(nhaCungCapService, times(1)).update(any(NhaCungCapDTO.class));
    }

    // TC79
    // Test Case 13: Cập nhật với DTO null
    // Kết quả mong đợi: Trả về ResponseDTO với trạng thái 400
    @Test
    void kiemTraUpdateDauVaoNull() {
        // Sắp xếp: Mock NhaCungCapService để trả về ResponseDTO với trạng thái 400
        ResponseDTO<NhaCungCap> responseDTO = ResponseDTO.<NhaCungCap>builder()
                .status(400)
                .msg("Dữ liệu đầu vào không hợp lệ")
                .build();
        when(nhaCungCapService.update(null)).thenReturn(responseDTO);

        // Thực hiện: Gọi phương thức update với DTO null
        ResponseDTO<NhaCungCap> ketQua = nhaCungCapController.update(null);

        // Kiểm tra: Xác minh kết quả trả về
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(400, ketQua.getStatus(), "Trạng thái phải là 400");
        assertEquals("Dữ liệu đầu vào không hợp lệ", ketQua.getMsg(), "Thông điệp phải là 'Dữ liệu đầu vào không hợp lệ'");
        assertNull(ketQua.getData(), "Dữ liệu phải là null");

        // Xác minh rằng nhaCungCapService được gọi
        verify(nhaCungCapService, times(1)).update(null);
    }

    // TC80
    // Test Case 14: Lỗi không mong muốn từ NhaCungCapService khi cập nhật
    // Kết quả mong đợi: Ném ra RuntimeException khi service gặp lỗi
    @Test
    void kiemTraUpdateLoiService() {
        // Sắp xếp: Mock NhaCungCapService để ném ra RuntimeException
        when(nhaCungCapService.update(any(NhaCungCapDTO.class)))
                .thenThrow(new RuntimeException("Lỗi cập nhật nhà cung cấp"));

        // Thực hiện & Kiểm tra: Xác minh rằng RuntimeException được ném ra
        RuntimeException ngoaiLe = assertThrows(
                RuntimeException.class,
                () -> nhaCungCapController.update(nhaCungCapDTOHopLe),
                "Mong đợi RuntimeException khi NhaCungCapService gặp lỗi"
        );

        // Kiểm tra thông điệp của ngoại lệ
        assertEquals("Lỗi cập nhật nhà cung cấp", ngoaiLe.getMessage(), "Thông điệp ngoại lệ phải khớp");

        // Xác minh rằng nhaCungCapService được gọi
        verify(nhaCungCapService, times(1)).update(any(NhaCungCapDTO.class));
    }

    // TC81
    // Test Case 15: Xóa nhà cung cấp thành công với ID hợp lệ
    // Kết quả mong đợi: Trả về ResponseDTO với trạng thái 200
    @Test
    void kiemTraDeleteThanhCong() {
        // Sắp xếp: Mock NhaCungCapService để trả về ResponseDTO thành công
        ResponseDTO<Void> responseDTO = ResponseDTO.<Void>builder()
                .status(200)
                .msg("Thành công")
                .build();
        when(nhaCungCapService.delete(1)).thenReturn(responseDTO);

        // Thực hiện: Gọi phương thức delete
        ResponseDTO<Void> ketQua = nhaCungCapController.delete(1);

        // Kiểm tra: Xác minh kết quả trả về
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(200, ketQua.getStatus(), "Trạng thái phải là 200");
        assertEquals("Thành công", ketQua.getMsg(), "Thông điệp phải là 'Thành công'");
        assertNull(ketQua.getData(), "Dữ liệu phải là null");

        // Xác minh rằng nhaCungCapService được gọi
        verify(nhaCungCapService, times(1)).delete(1);
    }

    // TC82
    // Test Case 16: Thất bại khi ID không tồn tại
    // Kết quả mong đợi: Trả về ResponseDTO với trạng thái 404
    @Test
    void kiemTraDeleteThatBaiKhongTonTai() {
        // Sắp xếp: Mock NhaCungCapService để trả về ResponseDTO với trạng thái 404
        ResponseDTO<Void> responseDTO = ResponseDTO.<Void>builder()
                .status(404)
                .msg("Nhà cung cấp không tồn tại")
                .build();
        when(nhaCungCapService.delete(999)).thenReturn(responseDTO);

        // Thực hiện: Gọi phương thức delete
        ResponseDTO<Void> ketQua = nhaCungCapController.delete(999);

        // Kiểm tra: Xác minh kết quả trả về
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(404, ketQua.getStatus(), "Trạng thái phải là 404");
        assertEquals("Nhà cung cấp không tồn tại", ketQua.getMsg(), "Thông điệp phải là 'Nhà cung cấp không tồn tại'");
        assertNull(ketQua.getData(), "Dữ liệu phải là null");

        // Xác minh rằng nhaCungCapService được gọi
        verify(nhaCungCapService, times(1)).delete(999);
    }

    // TC83
    // Test Case 17: Xóa với ID null
    // Kết quả mong đợi: Trả về ResponseDTO với trạng thái 400
    @Test
    void kiemTraDeleteDauVaoNull() {
        // Sắp xếp: Mock NhaCungCapService để trả về ResponseDTO với trạng thái 400
        ResponseDTO<Void> responseDTO = ResponseDTO.<Void>builder()
                .status(400)
                .msg("ID không hợp lệ")
                .build();
        when(nhaCungCapService.delete(null)).thenReturn(responseDTO);

        // Thực hiện: Gọi phương thức delete với ID null
        ResponseDTO<Void> ketQua = nhaCungCapController.delete(null);

        // Kiểm tra: Xác minh kết quả trả về
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(400, ketQua.getStatus(), "Trạng thái phải là 400");
        assertEquals("ID không hợp lệ", ketQua.getMsg(), "Thông điệp phải là 'ID không hợp lệ'");
        assertNull(ketQua.getData(), "Dữ liệu phải là null");

        // Xác minh rằng nhaCungCapService được gọi
        verify(nhaCungCapService, times(1)).delete(null);
    }

    // TC84
    // Test Case 18: Lỗi không mong muốn từ NhaCungCapService khi xóa
    // Kết quả mong đợi: Ném ra RuntimeException khi service gặp lỗi
    @Test
    void kiemTraDeleteLoiService() {
        // Sắp xếp: Mock NhaCungCapService để ném ra RuntimeException
        when(nhaCungCapService.delete(anyInt())).thenThrow(new RuntimeException("Lỗi xóa nhà cung cấp"));

        // Thực hiện & Kiểm tra: Xác minh rằng RuntimeException được ném ra
        RuntimeException ngoaiLe = assertThrows(
                RuntimeException.class,
                () -> nhaCungCapController.delete(1),
                "Mong đợi RuntimeException khi NhaCungCapService gặp lỗi"
        );

        // Kiểm tra thông điệp của ngoại lệ
        assertEquals("Lỗi xóa nhà cung cấp", ngoaiLe.getMessage(), "Thông điệp ngoại lệ phải khớp");

        // Xác minh rằng nhaCungCapService được gọi
        verify(nhaCungCapService, times(1)).delete(1);
    }
}
