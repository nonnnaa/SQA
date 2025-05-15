package com.example.hieuthuoc.controller;

import com.example.hieuthuoc.controller.DanhGiaController;
import com.example.hieuthuoc.dto.DanhGiaDTO;
import com.example.hieuthuoc.dto.PageDTO;
import com.example.hieuthuoc.dto.ResponseDTO;
import com.example.hieuthuoc.dto.SearchDTO;
import com.example.hieuthuoc.entity.DanhGia;
import com.example.hieuthuoc.service.DanhGiaService;
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
public class DanhGiaControllerTest {

    // Tiêm controller với các phụ thuộc đã được mock
    @InjectMocks
    private DanhGiaController danhGiaController;

    // Mock DanhGiaService
    @Mock
    private DanhGiaService danhGiaService;

    // Biến chứa dữ liệu kiểm thử
    private SearchDTO searchDTOHopLe;
    private DanhGiaDTO danhGiaDTOHopLe;
    private DanhGia danhGia;
    private PageDTO<List<DanhGia>> pageDTO;

    // Phương thức thiết lập dữ liệu kiểm thử trước mỗi test
    @BeforeEach
    void thietLap() {
        // Khởi tạo SearchDTO hợp lệ
        searchDTOHopLe = new SearchDTO();
        searchDTOHopLe.setSortedField("danhGia");
        searchDTOHopLe.setCurrentPage(0);
        searchDTOHopLe.setSize(20);

        // Khởi tạo DanhGiaDTO hợp lệ
        // Lưu ý: @Data từ Lombok tự động cung cấp getter và setter cho tất cả các trường
        // trong DanhGiaDTO (id, thuocId, nguoiDungId, danhGiaGocId, diemSo, danhGia)
        danhGiaDTOHopLe = new DanhGiaDTO();
        danhGiaDTOHopLe.setId(1);
        danhGiaDTOHopLe.setThuocId(1);
        danhGiaDTOHopLe.setNguoiDungId(1);
        danhGiaDTOHopLe.setDanhGiaGocId(null); // Không có đánh giá gốc
        danhGiaDTOHopLe.setDiemSo(5);
        danhGiaDTOHopLe.setDanhGia("Thuốc chất lượng cao");

        // Khởi tạo DanhGia
        danhGia = new DanhGia();
        // Giả định DanhGia cũng có các setter tương ứng
        danhGia.setId(1);
        //danhGia.setThuocId(1);
        //danhGia.setNguoiDungId(1);
        //danhGia.setDanhGiaGocId(null);
        danhGia.setDiemSo(5);
        danhGia.setDanhGia("Thuốc chất lượng cao");

        // Khởi tạo PageDTO
        pageDTO = new PageDTO<>();
        pageDTO.setTotalElements(1);
        pageDTO.setTotalPages(1);
        pageDTO.setData(Collections.singletonList(danhGia));
    }

    // TC85
    // Test Case 1: Lấy danh sách đánh giá thành công với SearchDTO hợp lệ
    // Kết quả mong đợi: Trả về ResponseDTO với trạng thái 200 và dữ liệu PageDTO
    @Test
    void kiemTraGetAllThanhCong() throws Exception {
        // Sắp xếp: Mock DanhGiaService để trả về ResponseDTO thành công
        ResponseDTO<PageDTO<List<DanhGia>>> responseDTO = ResponseDTO.<PageDTO<List<DanhGia>>>builder()
                .status(200)
                .msg("Thành công")
                .data(pageDTO)
                .build();
        when(danhGiaService.getAll(any(SearchDTO.class))).thenReturn(responseDTO);

        // Thực hiện: Gọi phương thức getAll
        ResponseDTO<PageDTO<List<DanhGia>>> ketQua = danhGiaController.getAll(searchDTOHopLe);

        // Kiểm tra: Xác minh kết quả trả về
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(200, ketQua.getStatus(), "Trạng thái phải là 200");
        assertEquals("Thành công", ketQua.getMsg(), "Thông điệp phải là 'Thành công'");
        assertEquals(pageDTO, ketQua.getData(), "Dữ liệu PageDTO phải khớp");
        assertEquals(1, ketQua.getData().getTotalElements(), "Tổng số phần tử phải là 1");
        assertEquals(1, ketQua.getData().getTotalPages(), "Tổng số trang phải là 1");
        assertEquals(1, ketQua.getData().getData().size(), "Danh sách phải chứa 1 đánh giá");
        assertEquals("Thuốc chất lượng cao", ketQua.getData().getData().get(0).getDanhGia(), "Nội dung phải là 'Thuốc chất lượng cao'");
        assertEquals(5, ketQua.getData().getData().get(0).getDiemSo(), "Điểm số phải là 5");

        // Xác minh rằng danhGiaService được gọi
        verify(danhGiaService, times(1)).getAll(any(SearchDTO.class));
    }

    // TC86
    // Test Case 2: Lấy danh sách đánh giá rỗng
    // Kết quả mong đợi: Trả về ResponseDTO với trạng thái 200 và PageDTO rỗng
    @Test
    void kiemTraGetAllKetQuaRong() throws Exception {
        // Sắp xếp: Mock DanhGiaService để trả về ResponseDTO với PageDTO rỗng
        PageDTO<List<DanhGia>> emptyPageDTO = new PageDTO<>();
        emptyPageDTO.setTotalElements(0);
        emptyPageDTO.setTotalPages(0);
        emptyPageDTO.setData(Collections.emptyList());
        ResponseDTO<PageDTO<List<DanhGia>>> responseDTO = ResponseDTO.<PageDTO<List<DanhGia>>>builder()
                .status(200)
                .msg("Thành công")
                .data(emptyPageDTO)
                .build();
        when(danhGiaService.getAll(any(SearchDTO.class))).thenReturn(responseDTO);

        // Thực hiện: Gọi phương thức getAll
        ResponseDTO<PageDTO<List<DanhGia>>> ketQua = danhGiaController.getAll(searchDTOHopLe);

        // Kiểm tra: Xác minh kết quả trả về
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(200, ketQua.getStatus(), "Trạng thái phải là 200");
        assertEquals("Thành công", ketQua.getMsg(), "Thông điệp phải là 'Thành công'");
        assertEquals(emptyPageDTO, ketQua.getData(), "Dữ liệu PageDTO phải khớp");
        assertEquals(0, ketQua.getData().getTotalElements(), "Tổng số phần tử phải là 0");
        assertEquals(0, ketQua.getData().getTotalPages(), "Tổng số trang phải là 0");
        assertTrue(ketQua.getData().getData().isEmpty(), "Danh sách phải rỗng");

        // Xác minh rằng danhGiaService được gọi
        verify(danhGiaService, times(1)).getAll(any(SearchDTO.class));
    }

    // TC87
    // Test Case 3: Đầu vào SearchDTO null
    // Kết quả mong đợi: Ném Exception do @Valid
    @Test
    void kiemTraGetAllDauVaoNull() {
        // Sắp xếp: Mock DanhGiaService để ném Exception
        when(danhGiaService.getAll(null)).thenThrow(new RuntimeException("Dữ liệu đầu vào không hợp lệ"));

        // Thực hiện & Kiểm tra: Xác minh rằng Exception được ném ra
        Exception ngoaiLe = assertThrows(
                Exception.class,
                () -> danhGiaController.getAll(null),
                "Mong đợi Exception khi SearchDTO null"
        );

        // Kiểm tra thông điệp của ngoại lệ
        assertEquals("Dữ liệu đầu vào không hợp lệ", ngoaiLe.getMessage(), "Thông điệp ngoại lệ phải khớp");

        // Xác minh rằng danhGiaService được gọi
        verify(danhGiaService, times(1)).getAll(null);
    }

    // TC88
    // Test Case 4: Lỗi không mong muốn từ DanhGiaService khi lấy danh sách
    // Kết quả mong đợi: Ném Exception khi service gặp lỗi
    @Test
    void kiemTraGetAllLoiService() {
        // Sắp xếp: Mock DanhGiaService để ném Exception
    	when(danhGiaService.getAll(any(SearchDTO.class))).thenThrow(new RuntimeException("Lỗi lấy danh sách đánh giá"));

        // Thực hiện & Kiểm tra: Xác minh rằng Exception được ném ra
        Exception ngoaiLe = assertThrows(
                Exception.class,
                () -> danhGiaController.getAll(searchDTOHopLe),
                "Mong đợi Exception khi DanhGiaService gặp lỗi"
        );

        // Kiểm tra thông điệp của ngoại lệ
        assertEquals("Lỗi lấy danh sách đánh giá", ngoaiLe.getMessage(), "Thông điệp ngoại lệ phải khớp");

        // Xác minh rằng danhGiaService được gọi
        verify(danhGiaService, times(1)).getAll(any(SearchDTO.class));
    }

    // TC89
    // Test Case 5: Tạo đánh giá thành công với DanhGiaDTO hợp lệ
    // Kết quả mong đợi: Trả về ResponseDTO với trạng thái 200 và dữ liệu DanhGia
    @Test
    void kiemTraCreateThanhCong() throws Exception {
        // Sắp xếp: Mock DanhGiaService để trả về ResponseDTO thành công
        ResponseDTO<DanhGia> responseDTO = ResponseDTO.<DanhGia>builder()
                .status(200)
                .msg("Thành công")
                .data(danhGia)
                .build();
        when(danhGiaService.create(any(DanhGiaDTO.class))).thenReturn(responseDTO);

        // Thực hiện: Gọi phương thức create
        ResponseDTO<DanhGia> ketQua = danhGiaController.create(danhGiaDTOHopLe);

        // Kiểm tra: Xác minh kết quả trả về
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(200, ketQua.getStatus(), "Trạng thái phải là 200");
        assertEquals("Thành công", ketQua.getMsg(), "Thông điệp phải là 'Thành công'");
        assertEquals(danhGia, ketQua.getData(), "Dữ liệu DanhGia phải khớp");
        //assertEquals(1, ketQua.getData().getThuocId(), "ThuocId phải là 1");
        //assertEquals(1, ketQua.getData().getNguoiDungId(), "NguoiDungId phải là 1");
        //assertNull(ketQua.getData().getDanhGiaGocId(), "DanhGiaGocId phải là null");
        assertEquals(5, ketQua.getData().getDiemSo(), "Điểm số phải là 5");
        assertEquals("Thuốc chất lượng cao", ketQua.getData().getDanhGia(), "Nội dung phải là 'Thuốc chất lượng cao'");

        // Xác minh rằng danhGiaService được gọi
        verify(danhGiaService, times(1)).create(any(DanhGiaDTO.class));
    }

    // TC90
    // Test Case 6: Tạo đánh giá với DanhGiaDTO null
    // Kết quả mong đợi: Ném Exception do @Valid
    @Test
    void kiemTraCreateDauVaoNull() {
        // Sắp xếp: Mock DanhGiaService để ném Exception
        when(danhGiaService.create(null)).thenThrow(new RuntimeException("Dữ liệu đầu vào không hợp lệ"));

        // Thực hiện & Kiểm tra: Xác minh rằng Exception được ném ra
        Exception ngoaiLe = assertThrows(
                Exception.class,
                () -> danhGiaController.create(null),
                "Mong đợi Exception khi DanhGiaDTO null"
        );

        // Kiểm tra thông điệp của ngoại lệ
        assertEquals("Dữ liệu đầu vào không hợp lệ", ngoaiLe.getMessage(), "Thông điệp ngoại lệ phải khớp");

        // Xác minh rằng danhGiaService được gọi
        verify(danhGiaService, times(1)).create(null);
    }

    // TC91
    // Test Case 7: Tạo đánh giá với thuocId null
    // Kết quả mong đợi: Ném Exception do @Valid
    @Test
    void kiemTraCreateThuocIdNull() {
        // Sắp xếp: Tạo DanhGiaDTO với thuocId null
        // Sử dụng setter do @Data cung cấp
        DanhGiaDTO danhGiaDTOKhongHopLe = new DanhGiaDTO();
        danhGiaDTOKhongHopLe.setId(1);
        danhGiaDTOKhongHopLe.setThuocId(null); // ThuocId null
        danhGiaDTOKhongHopLe.setNguoiDungId(1);
        danhGiaDTOKhongHopLe.setDanhGiaGocId(null);
        danhGiaDTOKhongHopLe.setDiemSo(5);
        danhGiaDTOKhongHopLe.setDanhGia("Thuốc chất lượng cao");

        // Mock DanhGiaService để ném Exception
        when(danhGiaService.create(any(DanhGiaDTO.class))).thenThrow(new RuntimeException("ThuocId không hợp lệ"));

        // Thực hiện & Kiểm tra: Xác minh rằng Exception được ném ra
        Exception ngoaiLe = assertThrows(
                Exception.class,
                () -> danhGiaController.create(danhGiaDTOKhongHopLe),
                "Mong đợi Exception khi ThuocId null"
        );

        // Kiểm tra thông điệp của ngoại lệ
        assertEquals("ThuocId không hợp lệ", ngoaiLe.getMessage(), "Thông điệp ngoại lệ phải khớp");

        // Xác minh rằng danhGiaService được gọi
        verify(danhGiaService, times(1)).create(any(DanhGiaDTO.class));
    }

    // TC92
    // Test Case 8: Lỗi không mong muốn từ DanhGiaService khi tạo
    // Kết quả mong đợi: Ném Exception khi service gặp lỗi
    @Test
    void kiemTraCreateLoiService() {
        // Sắp xếp: Mock DanhGiaService để ném Exception
    	when(danhGiaService.create(any(DanhGiaDTO.class))).thenThrow(new RuntimeException("Lỗi tạo đánh giá"));

        // Thực hiện & Kiểm tra: Xác minh rằng Exception được ném ra
        Exception ngoaiLe = assertThrows(
                Exception.class,
                () -> danhGiaController.create(danhGiaDTOHopLe),
                "Mong đợi Exception khi DanhGiaService gặp lỗi"
        );

        // Kiểm tra thông điệp của ngoại lệ
        assertEquals("Lỗi tạo đánh giá", ngoaiLe.getMessage(), "Thông điệp ngoại lệ phải khớp");

        // Xác minh rằng danhGiaService được gọi
        verify(danhGiaService, times(1)).create(any(DanhGiaDTO.class));
    }

    // TC93
    // Test Case 9: Cập nhật đánh giá thành công với DanhGiaDTO hợp lệ
    // Kết quả mong đợi: Trả về ResponseDTO với trạng thái 200 và dữ liệu DanhGia
    @Test
    void kiemTraUpdateThanhCong() throws Exception {
        // Sắp xếp: Mock DanhGiaService để trả về ResponseDTO thành công
        ResponseDTO<DanhGia> responseDTO = ResponseDTO.<DanhGia>builder()
                .status(200)
                .msg("Thành công")
                .data(danhGia)
                .build();
        when(danhGiaService.update(any(DanhGiaDTO.class))).thenReturn(responseDTO);

        // Thực hiện: Gọi phương thức update
        ResponseDTO<DanhGia> ketQua = danhGiaController.update(danhGiaDTOHopLe);

        // Kiểm tra: Xác minh kết quả trả về
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(200, ketQua.getStatus(), "Trạng thái phải là 200");
        assertEquals("Thành công", ketQua.getMsg(), "Thông điệp phải là 'Thành công'");
        assertEquals(danhGia, ketQua.getData(), "Dữ liệu DanhGia phải khớp");
        //assertEquals(1, ketQua.getData().getThuocId(), "ThuocId phải là 1");
        //assertEquals(1, ketQua.getData().getNguoiDungId(), "NguoiDungId phải là 1");
        //assertNull(ketQua.getData().getDanhGiaGocId(), "DanhGiaGocId phải là null");
        assertEquals(5, ketQua.getData().getDiemSo(), "Điểm số phải là 5");
        assertEquals("Thuốc chất lượng cao", ketQua.getData().getDanhGia(), "Nội dung phải là 'Thuốc chất lượng cao'");

        // Xác minh rằng danhGiaService được gọi
        verify(danhGiaService, times(1)).update(any(DanhGiaDTO.class));
    }

    // TC94
    // Test Case 10: Thất bại khi đánh giá không tồn tại
    // Kết quả mong đợi: Trả về ResponseDTO với trạng thái 404
    @Test
    void kiemTraUpdateThatBaiKhongTonTai() throws Exception {
        // Sắp xếp: Mock DanhGiaService để trả về ResponseDTO với trạng thái 404
        ResponseDTO<DanhGia> responseDTO = ResponseDTO.<DanhGia>builder()
                .status(404)
                .msg("Đánh giá không tồn tại")
                .build();
        when(danhGiaService.update(any(DanhGiaDTO.class))).thenReturn(responseDTO);

        // Thực hiện: Gọi phương thức update
        ResponseDTO<DanhGia> ketQua = danhGiaController.update(danhGiaDTOHopLe);

        // Kiểm tra: Xác minh kết quả trả về
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(404, ketQua.getStatus(), "Trạng thái phải là 404");
        assertEquals("Đánh giá không tồn tại", ketQua.getMsg(), "Thông điệp phải là 'Đánh giá không tồn tại'");
        assertNull(ketQua.getData(), "Dữ liệu phải là null");

        // Xác minh rằng danhGiaService được gọi
        verify(danhGiaService, times(1)).update(any(DanhGiaDTO.class));
    }

    // TC95
    // Test Case 11: Cập nhật với DanhGiaDTO null
    // Kết quả mong đợi: Ném Exception do @Valid
    @Test
    void kiemTraUpdateDauVaoNull() {
        // Sắp xếp: Mock DanhGiaService để ném Exception
        when(danhGiaService.update(null)).thenThrow(new RuntimeException("Dữ liệu đầu vào không hợp lệ"));

        // Thực hiện & Kiểm tra: Xác minh rằng Exception được ném ra
        Exception ngoaiLe = assertThrows(
                Exception.class,
                () -> danhGiaController.update(null),
                "Mong đợi Exception khi DanhGiaDTO null"
        );

        // Kiểm tra thông điệp của ngoại lệ
        assertEquals("Dữ liệu đầu vào không hợp lệ", ngoaiLe.getMessage(), "Thông điệp ngoại lệ phải khớp");

        // Xác minh rằng danhGiaService được gọi
        verify(danhGiaService, times(1)).update(null);
    }

    // TC96
    // Test Case 12: Cập nhật với id null
    // Kết quả mong đợi: Ném Exception do @Valid
    @Test
    void kiemTraUpdateIdNull() {
        // Sắp xếp: Tạo DanhGiaDTO với id null
        // Sử dụng setter do @Data cung cấp
        DanhGiaDTO danhGiaDTOKhongHopLe = new DanhGiaDTO();
        danhGiaDTOKhongHopLe.setId(null); // Id null
        danhGiaDTOKhongHopLe.setThuocId(1);
        danhGiaDTOKhongHopLe.setNguoiDungId(1);
        danhGiaDTOKhongHopLe.setDanhGiaGocId(null);
        danhGiaDTOKhongHopLe.setDiemSo(5);
        danhGiaDTOKhongHopLe.setDanhGia("Thuốc chất lượng cao");

        // Mock DanhGiaService để ném Exception
        when(danhGiaService.update(any(DanhGiaDTO.class))).thenThrow(new RuntimeException("Id không hợp lệ"));
        // Thực hiện & Kiểm tra: Xác minh rằng Exception được ném ra
        Exception ngoaiLe = assertThrows(
                Exception.class,
                () -> danhGiaController.update(danhGiaDTOKhongHopLe),
                "Mong đợi Exception khi Id null"
        );

        // Kiểm tra thông điệp của ngoại lệ
        assertEquals("Id không hợp lệ", ngoaiLe.getMessage(), "Thông điệp ngoại lệ phải khớp");

        // Xác minh rằng danhGiaService được gọi
        verify(danhGiaService, times(1)).update(any(DanhGiaDTO.class));
    }

    // TC97
    // Test Case 13: Lỗi không mong muốn từ DanhGiaService khi cập nhật
    // Kết quả mong đợi: Ném Exception khi service gặp lỗi
    @Test
    void kiemTraUpdateLoiService() {
        // Sắp xếp: Mock DanhGiaService để ném Exception
    	when(danhGiaService.update(any(DanhGiaDTO.class))).thenThrow(new RuntimeException("Lỗi cập nhật đánh giá"));
        
        // Thực hiện & Kiểm tra: Xác minh rằng Exception được ném ra
        Exception ngoaiLe = assertThrows(
                Exception.class,
                () -> danhGiaController.update(danhGiaDTOHopLe),
                "Mong đợi Exception khi DanhGiaService gặp lỗi"
        );

        // Kiểm tra thông điệp của ngoại lệ
        assertEquals("Lỗi cập nhật đánh giá", ngoaiLe.getMessage(), "Thông điệp ngoại lệ phải khớp");

        // Xác minh rằng danhGiaService được gọi
        verify(danhGiaService, times(1)).update(any(DanhGiaDTO.class));
    }

    // TC98
    // Test Case 14: Xóa đánh giá thành công với ID hợp lệ
    // Kết quả mong đợi: Trả về ResponseDTO với trạng thái 200
    @Test
    void kiemTraDeleteThanhCong() {
        // Sắp xếp: Mock DanhGiaService để trả về ResponseDTO thành công
        ResponseDTO<Void> responseDTO = ResponseDTO.<Void>builder()
                .status(200)
                .msg("Thành công")
                .build();
        when(danhGiaService.delete(1)).thenReturn(responseDTO);

        // Thực hiện: Gọi phương thức delete
        ResponseDTO<Void> ketQua = danhGiaController.delete(1);

        // Kiểm tra: Xác minh kết quả trả về
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(200, ketQua.getStatus(), "Trạng thái phải là 200");
        assertEquals("Thành công", ketQua.getMsg(), "Thông điệp phải là 'Thành công'");
        assertNull(ketQua.getData(), "Dữ liệu phải là null");

        // Xác minh rằng danhGiaService được gọi
        verify(danhGiaService, times(1)).delete(1);
    }

    // TC99
    // Test Case 15: Thất bại khi ID không tồn tại
    // Kết quả mong đợi: Trả về ResponseDTO với trạng thái 404
    @Test
    void kiemTraDeleteThatBaiKhongTonTai() {
        // Sắp xếp: Mock DanhGiaService để trả về ResponseDTO với trạng thái 404
        ResponseDTO<Void> responseDTO = ResponseDTO.<Void>builder()
                .status(404)
                .msg("Đánh giá không tồn tại")
                .build();
        when(danhGiaService.delete(999)).thenReturn(responseDTO);

        // Thực hiện: Gọi phương thức delete
        ResponseDTO<Void> ketQua = danhGiaController.delete(999);

        // Kiểm tra: Xác minh kết quả trả về
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(404, ketQua.getStatus(), "Trạng thái phải là 404");
        assertEquals("Đánh giá không tồn tại", ketQua.getMsg(), "Thông điệp phải là 'Đánh giá không tồn tại'");
        assertNull(ketQua.getData(), "Dữ liệu phải là null");

        // Xác minh rằng danhGiaService được gọi
        verify(danhGiaService, times(1)).delete(999);
    }

    // TC100
    // Test Case 16: Xóa với ID không hợp lệ (âm)
    // Kết quả mong đợi: Trả về ResponseDTO với trạng thái 400
    @Test
    void kiemTraDeleteDauVaoKhongHopLe() {
        // Sắp xếp: Mock DanhGiaService để trả về ResponseDTO với trạng thái 400
        ResponseDTO<Void> responseDTO = ResponseDTO.<Void>builder()
                .status(400)
                .msg("ID không hợp lệ")
                .build();
        when(danhGiaService.delete(-1)).thenReturn(responseDTO);

        // Thực hiện: Gọi phương thức delete với ID âm
        ResponseDTO<Void> ketQua = danhGiaController.delete(-1);

        // Kiểm tra: Xác minh kết quả trả về
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(400, ketQua.getStatus(), "Trạng thái phải là 400");
        assertEquals("ID không hợp lệ", ketQua.getMsg(), "Thông điệp phải là 'ID không hợp lệ'");
        assertNull(ketQua.getData(), "Dữ liệu phải là null");

        // Xác minh rằng danhGiaService được gọi
        verify(danhGiaService, times(1)).delete(-1);
    }

    // TC101
    // Test Case 17: Lỗi không mong muốn từ DanhGiaService khi xóa
    // Kết quả mong đợi: Ném RuntimeException khi service gặp lỗi
    @Test
    void kiemTraDeleteLoiService() {
        // Sắp xếp: Mock DanhGiaService để ném RuntimeException
        when(danhGiaService.delete(anyInt())).thenThrow(new RuntimeException("Lỗi xóa đánh giá"));

        // Thực hiện & Kiểm tra: Xác minh rằng RuntimeException được ném ra
        RuntimeException ngoaiLe = assertThrows(
                RuntimeException.class,
                () -> danhGiaController.delete(1),
                "Mong đợi RuntimeException khi DanhGiaService gặp lỗi"
        );

        // Kiểm tra thông điệp của ngoại lệ
        assertEquals("Lỗi xóa đánh giá", ngoaiLe.getMessage(), "Thông điệp ngoại lệ phải khớp");

        // Xác minh rằng danhGiaService được gọi
        verify(danhGiaService, times(1)).delete(1);
    }
}
