package com.example.hieuthuoc.service;

import com.example.hieuthuoc.dto.NguoiDungDTO;
import com.example.hieuthuoc.dto.ResponseDTO;
import com.example.hieuthuoc.entity.NguoiDung;
import com.example.hieuthuoc.repository.NguoiDungRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NguoiDungServiceImplTest {

    @InjectMocks
    private NguoiDungServiceImpl nguoiDungService;

    @Mock
    private NguoiDungRepo nguoiDungRepo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * TC01 - Mục tiêu: Đổi mật khẩu thành công
     * Input: ID tồn tại, mật khẩu hiện tại đúng
     * Expected Output: 200 OK, msg = "Đổi mật khẩu thành công."
     * Ghi chú: Mật khẩu mới được mã hóa và lưu thành công
     */
    @Test
    void changeMatKhau_ShouldSucceed_WhenPasswordMatches() {
        NguoiDung nguoiDung = new NguoiDung();
        nguoiDung.setId(1);
        nguoiDung.setMatKhau(new BCryptPasswordEncoder().encode("oldpass"));

        NguoiDungDTO dto = new NguoiDungDTO();
        dto.setId(1);
        dto.setMatKhau("oldpass");
        dto.setMatKhauMoi("newpass");

        when(nguoiDungRepo.findById(1)).thenReturn(Optional.of(nguoiDung));
        when(nguoiDungRepo.save(any())).thenReturn(nguoiDung);

        ResponseDTO<NguoiDung> response = nguoiDungService.changeMatKhau(dto);

        assertEquals(200, response.getStatus());
        assertEquals("Đổi mật khẩu thành công.", response.getMsg());
        assertNotNull(response.getData());
    }

    /**
     * TC02 - Mục tiêu: Đổi mật khẩu thất bại do không tìm thấy người dùng
     * Input: ID không tồn tại
     * Expected Output: 400 Bad Request, msg = "Tài khoản không tồn tại."
     * Ghi chú: Không có người dùng nào trong database
     */
    @Test
    void changeMatKhau_ShouldFail_WhenUserNotFound() {
        NguoiDungDTO dto = new NguoiDungDTO();
        dto.setId(999);
        dto.setMatKhau("anypass");
        dto.setMatKhauMoi("newpass");

        when(nguoiDungRepo.findById(999)).thenReturn(Optional.empty());

        ResponseDTO<NguoiDung> response = nguoiDungService.changeMatKhau(dto);

        assertEquals(400, response.getStatus());
        assertEquals("Tài khoản không tồn tại.", response.getMsg());
        assertNull(response.getData());
    }

    /**
     * TC03 - Mục tiêu: Đổi mật khẩu thất bại do mật khẩu hiện tại không khớp
     * Input: ID đúng, mật khẩu hiện tại sai
     * Expected Output: 400 Bad Request, msg = "Mật khẩu không chính xác."
     * Ghi chú: So sánh mật khẩu thất bại
     */
    @Test
    void changeMatKhau_ShouldFail_WhenCurrentPasswordWrong() {
        NguoiDung nguoiDung = new NguoiDung();
        nguoiDung.setId(1);
        nguoiDung.setMatKhau(new BCryptPasswordEncoder().encode("correctPass"));

        NguoiDungDTO dto = new NguoiDungDTO();
        dto.setId(1);
        dto.setMatKhau("wrongPass");
        dto.setMatKhauMoi("newpass");

        when(nguoiDungRepo.findById(1)).thenReturn(Optional.of(nguoiDung));

        ResponseDTO<NguoiDung> response = nguoiDungService.changeMatKhau(dto);

        assertEquals(400, response.getStatus());
        assertEquals("Mật khẩu không chính xác.", response.getMsg());
        assertNull(response.getData());
    }
}