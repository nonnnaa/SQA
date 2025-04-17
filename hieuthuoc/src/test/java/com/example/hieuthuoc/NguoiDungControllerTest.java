package com.example.hieuthuoc;

import com.example.hieuthuoc.controller.NguoiDungController;
import com.example.hieuthuoc.dto.NguoiDungDTO;
import com.example.hieuthuoc.dto.ResponseDTO;
import com.example.hieuthuoc.entity.NguoiDung;
import com.example.hieuthuoc.service.NguoiDungService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NguoiDungControllerTest {

    @InjectMocks
    private NguoiDungController nguoiDungController;

    @Mock
    private NguoiDungService nguoiDungService;

    private NguoiDungDTO nguoiDungDTO;
    private NguoiDung nguoiDung;
    private ResponseDTO<NguoiDung> responseDTO;
    private MockMultipartFile mockFile;

    @BeforeEach
    void thietLap() {
        // Khởi tạo NguoiDungDTO
        nguoiDungDTO = new NguoiDungDTO();
        nguoiDungDTO.setId(1);
        nguoiDungDTO.setHoTen("Nguyen Van A");
        nguoiDungDTO.setEmail("nguyenvana@example.com");

        // Khởi tạo NguoiDung
        nguoiDung = new NguoiDung();
        nguoiDung.setId(1);
        nguoiDung.setHoTen("Nguyen Van A");
        nguoiDung.setEmail("nguyenvana@example.com");

        // Khởi tạo ResponseDTO
        responseDTO = ResponseDTO.<NguoiDung>builder()
                .status(200)
                .data(nguoiDung)
                .msg("ok")
                .build();

        // Khởi tạo MockMultipartFile
        mockFile = new MockMultipartFile("file", "avatar.jpg", "image/jpeg", "mock image content".getBytes());
    }
    
    // TC5
    // Test Case 1: Cập nhật avatar thành công với file hợp lệ
    // Mục tiêu: Kiểm tra rằng avatar được cập nhật khi cung cấp file hợp lệ
    @Test
    void kiemTraChangeAvatarThanhCongCoFile() throws Exception {
        // Sắp xếp: Mock NguoiDungService trả về ResponseDTO
        when(nguoiDungService.changeAvatar(any(NguoiDungDTO.class))).thenReturn(responseDTO);

        // Thực hiện: Gọi phương thức changeAvatar với file
        ResponseDTO<NguoiDung> ketQua = nguoiDungController.changeAvatar(nguoiDungDTO, mockFile);

        // Kiểm tra: Xác minh kết quả
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(200, ketQua.getStatus(), "Trạng thái phải là 200");
        assertEquals(nguoiDung, ketQua.getData(), "Dữ liệu người dùng phải khớp");
        assertEquals("ok", ketQua.getMsg(), "Thông điệp phải là 'ok'");

        // Xác minh rằng file đã được gán vào nguoiDungDTO
        verify(nguoiDungService, times(1)).changeAvatar(nguoiDungDTO);
    }
    
    // TC6
    // Test Case 2: Cập nhật avatar thành công không có file
    // Mục tiêu: Kiểm tra rằng avatar được cập nhật khi không cung cấp file
    @Test
    void kiemTraChangeAvatarThanhCongKhongCoFile() throws Exception {
        // Sắp xếp: Mock NguoiDungService trả về ResponseDTO
        when(nguoiDungService.changeAvatar(any(NguoiDungDTO.class))).thenReturn(responseDTO);

        // Thực hiện: Gọi phương thức changeAvatar với file null
        ResponseDTO<NguoiDung> ketQua = nguoiDungController.changeAvatar(nguoiDungDTO, null);

        // Kiểm tra: Xác minh kết quả
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(200, ketQua.getStatus(), "Trạng thái phải là 200");
        assertEquals(nguoiDung, ketQua.getData(), "Dữ liệu người dùng phải khớp");
        assertEquals("ok", ketQua.getMsg(), "Thông điệp phải là 'ok'");

        // Xác minh rằng service được gọi mà không gán file
        verify(nguoiDungService, times(1)).changeAvatar(nguoiDungDTO);
    }

    // TC7
    // Test Case 3: Cập nhật avatar thất bại do lỗi từ NguoiDungService
    // Mục tiêu: Kiểm tra xử lý lỗi khi NguoiDungService ném ra ngoại lệ
    @Test
    void kiemTraChangeAvatarThatBai() {
        // Sắp xếp: Mock NguoiDungService ném ra Exception
        when(nguoiDungService.changeAvatar(any(NguoiDungDTO.class)))
                .thenThrow(new RuntimeException("Lỗi cập nhật avatar"));

        // Thực hiện & Kiểm tra: Xác minh rằng ngoại lệ được ném ra
        RuntimeException ngoaiLe = assertThrows(
                RuntimeException.class,
                () -> nguoiDungController.changeAvatar(nguoiDungDTO, mockFile),
                "Mong đợi RuntimeException khi cập nhật avatar thất bại"
        );

        // Kiểm tra thông điệp ngoại lệ
        assertEquals("Lỗi cập nhật avatar", ngoaiLe.getMessage(), "Thông điệp ngoại lệ phải khớp");

        // Xác minh rằng service được gọi
        verify(nguoiDungService, times(1)).changeAvatar(nguoiDungDTO);
    }

    // TC8
    // Test Case 4: Đầu vào nguoiDungDTO null khi cập nhật avatar
    // Mục tiêu: Kiểm tra xử lý khi nguoiDungDTO là null
    @Test
    void kiemTraChangeAvatarDauVaoNull() {
        // Sắp xếp: Mock NguoiDungService ném ra Exception khi DTO null
        when(nguoiDungService.changeAvatar(isNull())).thenThrow(new RuntimeException("DTO không hợp lệ"));

        // Thực hiện & Kiểm tra: Xác minh rằng ngoại lệ được ném ra
        RuntimeException ngoaiLe = assertThrows(
                RuntimeException.class,
                () -> nguoiDungController.changeAvatar(null, null),
                "Mong đợi RuntimeException khi nguoiDungDTO là null"
        );

        // Kiểm tra thông điệp ngoại lệ
        assertEquals("DTO không hợp lệ", ngoaiLe.getMessage(), "Thông điệp ngoại lệ phải khớp");

        // Xác minh rằng service được gọi
        verify(nguoiDungService, times(1)).changeAvatar(null);
    }

    // TC9
    // Test Case 5: Cập nhật thông tin người dùng thành công
    // Mục tiêu: Kiểm tra rằng thông tin người dùng được cập nhật thành công
    @Test
    void kiemTraUpdateThanhCong() throws Exception {
        // Sắp xếp: Mock NguoiDungService trả về ResponseDTO
        when(nguoiDungService.update(any(NguoiDungDTO.class))).thenReturn(responseDTO);

        // Thực hiện: Gọi phương thức update
        ResponseDTO<NguoiDung> ketQua = nguoiDungController.update(nguoiDungDTO);

        // Kiểm tra: Xác minh kết quả
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(200, ketQua.getStatus(), "Trạng thái phải là 200");
        assertEquals(nguoiDung, ketQua.getData(), "Dữ liệu người dùng phải khớp");
        assertEquals("ok", ketQua.getMsg(), "Thông điệp phải là 'ok'");

        // Xác minh rằng service được gọi
        verify(nguoiDungService, times(1)).update(nguoiDungDTO);
    }

    // TC10
    // Test Case 6: Cập nhật thất bại do lỗi từ NguoiDungService
    // Mục tiêu: Kiểm tra xử lý lỗi khi NguoiDungService ném ra ngoại lệ
    @Test
    void kiemTraUpdateThatBai() {
        // Sắp xếp: Mock NguoiDungService ném ra Exception
        when(nguoiDungService.update(any(NguoiDungDTO.class)))
                .thenThrow(new RuntimeException("Lỗi cập nhật người dùng"));

        // Thực hiện & Kiểm tra: Xác minh rằng ngoại lệ được ném ra
        RuntimeException ngoaiLe = assertThrows(
                RuntimeException.class,
                () -> nguoiDungController.update(nguoiDungDTO),
                "Mong đợi RuntimeException khi cập nhật thất bại"
        );

        // Kiểm tra thông điệp ngoại lệ
        assertEquals("Lỗi cập nhật người dùng", ngoaiLe.getMessage(), "Thông điệp ngoại lệ phải khớp");

        // Xác minh rằng service được gọi
        verify(nguoiDungService, times(1)).update(nguoiDungDTO);
    }

    // TC11
    // Test Case 7: Đầu vào nguoiDungDTO null khi cập nhật
    // Mục tiêu: Kiểm tra xử lý khi nguoiDungDTO là null
    @Test
    void kiemTraUpdateDauVaoNull() {
        // Sắp xếp: Mock NguoiDungService ném ra Exception khi DTO null
        when(nguoiDungService.update(isNull())).thenThrow(new RuntimeException("DTO không hợp lệ"));

        // Thực hiện & Kiểm tra: Xác minh rằng ngoại lệ được ném ra
        RuntimeException ngoaiLe = assertThrows(
                RuntimeException.class,
                () -> nguoiDungController.update(null),
                "Mong đợi RuntimeException khi nguoiDungDTO là null"
        );

        // Kiểm tra thông điệp ngoại lệ
        assertEquals("DTO không hợp lệ", ngoaiLe.getMessage(), "Thông điệp ngoại lệ phải khớp");

        // Xác minh rằng service được gọi
        verify(nguoiDungService, times(1)).update(null);
    }
}
