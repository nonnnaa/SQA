package com.example.hieuthuoc;

import com.example.hieuthuoc.dto.NguoiDungDTO;
import com.example.hieuthuoc.dto.ResponseDTO;
import com.example.hieuthuoc.entity.NguoiDung;
import com.example.hieuthuoc.repository.NguoiDungRepo;
import com.example.hieuthuoc.service.UploadImageService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NguoiDungServiceImplTest {

    // Sử dụng lớp triển khai cụ thể thay vì interface
    @InjectMocks
    private com.example.hieuthuoc.impl.NguoiDungServiceImpl nguoiDungService;

    @Mock
    private NguoiDungRepo nguoiDungRepo;

    @Mock
    private UploadImageService uploadImageService;

    @Mock
    private ModelMapper modelMapper;

    private NguoiDungDTO nguoiDungDTO;
    private NguoiDung nguoiDung;
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
        nguoiDung.setAvatar("old_avatar_url");

        // Khởi tạo MockMultipartFile
        mockFile = new MockMultipartFile("file", "avatar.jpg", "image/jpeg", "mock image content".getBytes());
    }
    
    // TC12
    // Test Case 1: Cập nhật avatar thành công với file hợp lệ (có avatar cũ)
    // Mục tiêu: Kiểm tra rằng avatar được cập nhật khi có file và xóa avatar cũ
    @Test
    void kiemTraChangeAvatarThanhCongCoAvatarCu() throws Exception {
        // Sắp xếp
        nguoiDungDTO.setFile(mockFile);
        when(modelMapper.map(nguoiDungDTO, NguoiDung.class)).thenReturn(nguoiDung);
        when(nguoiDungRepo.findById(1)).thenReturn(Optional.of(nguoiDung));
        doThrow(new RuntimeException("Delete failed")).when(uploadImageService).deleteImage("old_avatar_url");
        when(uploadImageService.uploadImage(mockFile, "NguoiDung_1")).thenReturn("new_avatar_url");
        when(nguoiDungRepo.save(any(NguoiDung.class))).thenReturn(nguoiDung);

        // Thực hiện
        ResponseDTO<NguoiDung> ketQua = nguoiDungService.changeAvatar(nguoiDungDTO);

        // Kiểm tra
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(200, ketQua.getStatus(), "Trạng thái phải là 200");
        assertEquals("Thành công.", ketQua.getMsg(), "Thông điệp phải là 'Thành công.'");
        assertNull(ketQua.getData(), "Dữ liệu trả về phải là null");
        assertEquals("new_avatar_url", nguoiDung.getAvatar(), "Avatar phải được cập nhật");

        // Xác minh
        verify(uploadImageService, times(1)).deleteImage("old_avatar_url");
        verify(uploadImageService, times(1)).uploadImage(mockFile, "NguoiDung_1");
        verify(nguoiDungRepo, times(1)).save(nguoiDung);
    }
    
    // TC13
    // Test Case 2: Cập nhật avatar thành công với file hợp lệ (không có avatar cũ)
    // Mục tiêu: Kiểm tra rằng avatar được cập nhật khi có file và không có avatar cũ
    @Test
    void kiemTraChangeAvatarThanhCongKhongCoAvatarCu() throws Exception {
        // Sắp xếp
        nguoiDung.setAvatar(null); // Không có avatar cũ
        nguoiDungDTO.setFile(mockFile);
        //when(modelMapper.map(nguoiDungDTO, NguoiDung.class)).thenReturn(nguoiDung);
        when(nguoiDungRepo.findById(1)).thenReturn(Optional.of(nguoiDung));
        when(uploadImageService.uploadImage(mockFile, "NguoiDung_1")).thenReturn("new_avatar_url");
        when(nguoiDungRepo.save(any(NguoiDung.class))).thenReturn(nguoiDung);

        // Thực hiện
        ResponseDTO<NguoiDung> ketQua = nguoiDungService.changeAvatar(nguoiDungDTO);

        // Kiểm tra
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(200, ketQua.getStatus(), "Trạng thái phải là 200");
        assertEquals("Thành công.", ketQua.getMsg(), "Thông điệp phải là 'Thành công.'");
        assertNull(ketQua.getData(), "Dữ liệu trả về phải là null");
        assertEquals("new_avatar_url", nguoiDung.getAvatar(), "Avatar phải được cập nhật");

        // Xác minh
        verify(uploadImageService, never()).deleteImage(anyString());
        verify(uploadImageService, times(1)).uploadImage(mockFile, "NguoiDung_1");
        verify(nguoiDungRepo, times(1)).save(nguoiDung);
    }
    
    // TC14
    // Test Case 3: Cập nhật avatar thành công khi không có file
    // Mục tiêu: Kiểm tra rằng không có thay đổi avatar khi file null
    @Test
    void kiemTraChangeAvatarThanhCongKhongCoFile() throws Exception {
        // Sắp xếp
        nguoiDungDTO.setFile(null);
        //when(modelMapper.map(nguoiDungDTO, NguoiDung.class)).thenReturn(nguoiDung);
        when(nguoiDungRepo.findById(1)).thenReturn(Optional.of(nguoiDung));
        when(nguoiDungRepo.save(any(NguoiDung.class))).thenReturn(nguoiDung);

        // Thực hiện
        ResponseDTO<NguoiDung> ketQua = nguoiDungService.changeAvatar(nguoiDungDTO);

        // Kiểm tra
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(200, ketQua.getStatus(), "Trạng thái phải là 200");
        assertEquals("Thành công.", ketQua.getMsg(), "Thông điệp phải là 'Thành công.'");
        assertNull(ketQua.getData(), "Dữ liệu trả về phải là null");
        assertEquals("old_avatar_url", nguoiDung.getAvatar(), "Avatar không được thay đổi");

        // Xác minh
        verify(uploadImageService, never()).deleteImage(anyString());
        verify(uploadImageService, never()).uploadImage(any(MultipartFile.class), anyString());
        verify(nguoiDungRepo, times(1)).save(nguoiDung);
    }
    
    // TC15
    // Test Case 4: Thất bại khi người dùng không tồn tại
    // Mục tiêu: Kiểm tra xử lý khi không tìm thấy người dùng
    @Test
    void kiemTraChangeAvatarThatBaiNguoiDungKhongTonTai() {
        // Sắp xếp
        nguoiDungDTO.setFile(mockFile);
        //when(modelMapper.map(nguoiDungDTO, NguoiDung.class)).thenReturn(nguoiDung);
        when(nguoiDungRepo.findById(1)).thenReturn(Optional.empty());

        // Thực hiện
        ResponseDTO<NguoiDung> ketQua = nguoiDungService.changeAvatar(nguoiDungDTO);

        // Kiểm tra
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(500, ketQua.getStatus(), "Trạng thái phải là 500");
        assertEquals("Đã xảy ra lỗi khi thay đổi hình đại diện.", ketQua.getMsg(), "Thông điệp phải khớp");
        assertNull(ketQua.getData(), "Dữ liệu trả về phải là null");

        // Xác minh
        verify(uploadImageService, never()).deleteImage(anyString());
        verify(uploadImageService, never()).uploadImage(any(MultipartFile.class), anyString());
        verify(nguoiDungRepo, never()).save(any(NguoiDung.class));
    }
    
    // TC16
    // Test Case 5: Thất bại khi uploadImageService ném ra ngoại lệ
    // Mục tiêu: Kiểm tra xử lý lỗi khi upload image thất bại
    @Test
    void kiemTraChangeAvatarThatBaiLoiUpload() {
        // Sắp xếp
        nguoiDungDTO.setFile(mockFile);
        when(modelMapper.map(nguoiDungDTO, NguoiDung.class)).thenReturn(nguoiDung);
        when(nguoiDungRepo.findById(1)).thenReturn(Optional.of(nguoiDung));
        doThrow(new RuntimeException("Delete failed")).when(uploadImageService).deleteImage("old_avatar_url");
        when(uploadImageService.uploadImage(mockFile, "NguoiDung_1")).thenThrow(new RuntimeException("Lỗi upload"));

        // Thực hiện
        ResponseDTO<NguoiDung> ketQua = nguoiDungService.changeAvatar(nguoiDungDTO);

        // Kiểm tra
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(500, ketQua.getStatus(), "Trạng thái phải là 500");
        assertEquals("Đã xảy ra lỗi khi thay đổi hình đại diện.", ketQua.getMsg(), "Thông điệp phải khớp");
        assertNull(ketQua.getData(), "Dữ liệu trả về phải là null");

        // Xác minh
        verify(uploadImageService, times(1)).deleteImage("old_avatar_url");
        verify(uploadImageService, times(1)).uploadImage(mockFile, "NguoiDung_1");
        verify(nguoiDungRepo, never()).save(any(NguoiDung.class));
    }
    
    // TC17
    // Test Case 6: Đầu vào nguoiDungDTO null
    // Mục tiêu: Kiểm tra xử lý khi nguoiDungDTO là null
    @Test
    void kiemTraChangeAvatarDauVaoNull() {
        // Thực hiện
        ResponseDTO<NguoiDung> ketQua = nguoiDungService.changeAvatar(null);

        // Kiểm tra
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(500, ketQua.getStatus(), "Trạng thái phải là 500");
        assertEquals("Đã xảy ra lỗi khi thay đổi hình đại diện.", ketQua.getMsg(), "Thông điệp phải khớp");
        assertNull(ketQua.getData(), "Dữ liệu trả về phải là null");

        // Xác minh
        verify(modelMapper, never()).map(any(), any());
        verify(nguoiDungRepo, never()).findById(anyInt());
        verify(uploadImageService, never()).deleteImage(anyString());
        verify(uploadImageService, never()).uploadImage(any(MultipartFile.class), anyString());
        verify(nguoiDungRepo, never()).save(any(NguoiDung.class));
    }
    
    // TC18
    // Test Case 7: Cập nhật thông tin người dùng thành công
    // Mục tiêu: Kiểm tra rằng thông tin người dùng được cập nhật thành công
    @Test
    void kiemTraUpdateThanhCong() {
        // Sắp xếp
        when(modelMapper.map(nguoiDungDTO, NguoiDung.class)).thenReturn(nguoiDung);
        when(nguoiDungRepo.findById(1)).thenReturn(Optional.of(nguoiDung));
        when(nguoiDungRepo.save(any(NguoiDung.class))).thenReturn(nguoiDung);

        // Thực hiện
        ResponseDTO<NguoiDung> ketQua = nguoiDungService.update(nguoiDungDTO);

        // Kiểm tra
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(200, ketQua.getStatus(), "Trạng thái phải là 200");
        assertEquals("Thành công", ketQua.getMsg(), "Thông điệp phải là 'Thành công'");
        assertEquals(nguoiDung, ketQua.getData(), "Dữ liệu người dùng phải khớp");

        // Xác minh
        verify(nguoiDungRepo, times(1)).findById(1);
        verify(nguoiDungRepo, times(1)).save(nguoiDung);
    }
    
    // TC19
    // Test Case 8: Thất bại khi người dùng không tồn tại
    // Mục tiêu: Kiểm tra xử lý khi không tìm thấy người dùng
    @Test
    void kiemTraUpdateThatBaiNguoiDungKhongTonTai() {
        // Sắp xếp
        when(modelMapper.map(nguoiDungDTO, NguoiDung.class)).thenReturn(nguoiDung);
        when(nguoiDungRepo.findById(1)).thenReturn(Optional.empty());

        // Thực hiện
        ResponseDTO<NguoiDung> ketQua = nguoiDungService.update(nguoiDungDTO);

        // Kiểm tra
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(400, ketQua.getStatus(), "Trạng thái phải là 400");
        assertEquals("Tài khoản không tồn tại.", ketQua.getMsg(), "Thông điệp phải khớp");
        assertNull(ketQua.getData(), "Dữ liệu trả về phải là null");

        // Xác minh
        verify(nguoiDungRepo, times(1)).findById(1);
        verify(nguoiDungRepo, never()).save(any(NguoiDung.class));
    }
    
    // TC20
    // Test Case 9: Đầu vào nguoiDungDTO null
    // Mục tiêu: Kiểm tra xử lý khi nguoiDungDTO là null
    @Test
    void kiemTraUpdateDauVaoNull() {
        // Thực hiện
        ResponseDTO<NguoiDung> ketQua = nguoiDungService.update(null);

        // Kiểm tra
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(400, ketQua.getStatus(), "Trạng thái phải là 400");
        assertEquals("Tài khoản không tồn tại.", ketQua.getMsg(), "Thông điệp phải khớp");
        assertNull(ketQua.getData(), "Dữ liệu trả về phải là null");

        // Xác minh
        verify(modelMapper, never()).map(any(), any());
        verify(nguoiDungRepo, never()).findById(anyInt());
        verify(nguoiDungRepo, never()).save(any(NguoiDung.class));
    }
}