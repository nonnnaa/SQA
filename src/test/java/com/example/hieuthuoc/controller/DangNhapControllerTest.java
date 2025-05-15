package com.example.hieuthuoc.controller;

import com.example.hieuthuoc.controller.DangNhapController;
import com.example.hieuthuoc.dto.ResponseDTO;
import com.example.hieuthuoc.jwt.JwtService;
import com.example.hieuthuoc.security.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Kích hoạt Mockito để hỗ trợ tiêm phụ thuộc và mock
@ExtendWith(MockitoExtension.class)
public class DangNhapControllerTest {

    // Tiêm controller với các phụ thuộc đã được mock
    @InjectMocks
    private DangNhapController dangNhapController;

    // Mock AuthenticationManager và JwtService
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    // Biến chứa dữ liệu kiểm thử
    private LoginRequest yeuCauDangNhapHopLe;
    private String tokenJwtHopLe;

    // Phương thức thiết lập dữ liệu kiểm thử trước mỗi test
    @BeforeEach
    void thietLap() {
        // Khởi tạo yêu cầu đăng nhập hợp lệ với tên đăng nhập và mật khẩu
        yeuCauDangNhapHopLe = new LoginRequest();
        yeuCauDangNhapHopLe.setTenDangNhap("user1");
        yeuCauDangNhapHopLe.setMatKhau("password123");

        // Xác định một token JWT mẫu cho kiểm thử
        tokenJwtHopLe = "mocked-jwt-token";
    }
    
    // TC1
    // Test Case 1: Đăng nhập thành công với thông tin hợp lệ
    // Kết quả mong đợi: Trả về ResponseDTO với trạng thái 200 và token JWT
    @Test
    void kiemTraDangNhapThanhCong() {
        // Sắp xếp: Mock AuthenticationManager để trả về xác thực thành công
        Authentication xacThuc = new UsernamePasswordAuthenticationToken(
                yeuCauDangNhapHopLe.getTenDangNhap(), yeuCauDangNhapHopLe.getMatKhau());
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(xacThuc);

        // Mock JwtService để trả về token hợp lệ
        when(jwtService.generateToken(yeuCauDangNhapHopLe.getTenDangNhap())).thenReturn(tokenJwtHopLe);

        // Thực hiện: Gọi phương thức đăng nhập
        ResponseDTO<String> ketQua = dangNhapController.login(yeuCauDangNhapHopLe);

        // Kiểm tra: Xác minh kết quả trả về
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(200, ketQua.getStatus(), "Trạng thái phải là 200");
        assertEquals(tokenJwtHopLe, ketQua.getData(), "Token JWT phải khớp với token đã mock");

        // Xác minh rằng authenticationManager và jwtService được gọi
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, times(1)).generateToken(yeuCauDangNhapHopLe.getTenDangNhap());
    }
    
    // TC2
    // Test Case 2: Thông tin đăng nhập không hợp lệ ném ra AuthenticationException
    // Kết quả mong đợi: AuthenticationException được ném ra khi thông tin không hợp lệ
    @Test
    void kiemTraDangNhapThatBai() {
        // Sắp xếp: Mock AuthenticationManager để ném ra AuthenticationException
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new AuthenticationException("Thông tin đăng nhập không hợp lệ") {});

        // Thực hiện & Kiểm tra: Xác minh rằng AuthenticationException được ném ra
        AuthenticationException ngoaiLe = assertThrows(
                AuthenticationException.class,
                () -> dangNhapController.login(yeuCauDangNhapHopLe),
                "Mong đợi AuthenticationException khi thông tin đăng nhập không hợp lệ"
        );

        // Kiểm tra thông điệp của ngoại lệ
        assertEquals("Thông tin đăng nhập không hợp lệ", ngoaiLe.getMessage(), "Thông điệp ngoại lệ phải khớp");

        // Xác minh rằng authenticationManager được gọi, nhưng jwtService không được gọi
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, never()).generateToken(anyString());
    }
  // TC3
 // Test Case 3: Đầu vào không hợp lệ (tên đăng nhập hoặc mật khẩu null/trống)
    // Kết quả mong đợi: Ném ra AuthenticationException khi đầu vào không hợp lệ
    @Test
    void kiemTraDangNhapDauVaoKhongHopLe() {
        // Sắp xếp: Tạo yêu cầu đăng nhập với tên đăng nhập null
        LoginRequest yeuCauDangNhapKhongHopLe = new LoginRequest();
        yeuCauDangNhapKhongHopLe.setTenDangNhap(null);
        yeuCauDangNhapKhongHopLe.setMatKhau("password123");

        // Mock AuthenticationManager để ném ra AuthenticationException
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new AuthenticationException("Thông tin đăng nhập không hợp lệ") {});

        // Thực hiện & Kiểm tra: Xác minh rằng AuthenticationException được ném ra
        AuthenticationException ngoaiLe = assertThrows(
                AuthenticationException.class,
                () -> dangNhapController.login(yeuCauDangNhapKhongHopLe),
                "Mong đợi AuthenticationException khi tên đăng nhập null"
        );

        // Kiểm tra thông điệp của ngoại lệ
        assertEquals("Thông tin đăng nhập không hợp lệ", ngoaiLe.getMessage(), "Thông điệp ngoại lệ phải khớp");

        // Xác minh rằng authenticationManager được gọi, nhưng jwtService không được gọi
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, never()).generateToken(anyString());
    }
    
    // TC4
    // Test Case 4: Lỗi không mong muốn từ JwtService
    // Kết quả mong đợi: Ném ra ngoại lệ khi JwtService gặp lỗi
    @Test
    void kiemTraDangNhapLoiJwtService() {
        // Sắp xếp: Mock AuthenticationManager để trả về xác thực thành công
        Authentication xacThuc = new UsernamePasswordAuthenticationToken(
                yeuCauDangNhapHopLe.getTenDangNhap(), yeuCauDangNhapHopLe.getMatKhau());
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(xacThuc);

        // Mock JwtService để ném ra RuntimeException
        when(jwtService.generateToken(yeuCauDangNhapHopLe.getTenDangNhap()))
                .thenThrow(new RuntimeException("Lỗi tạo token"));

        // Thực hiện & Kiểm tra: Xác minh rằng RuntimeException được ném ra
        RuntimeException ngoaiLe = assertThrows(
                RuntimeException.class,
                () -> dangNhapController.login(yeuCauDangNhapHopLe),
                "Mong đợi RuntimeException khi JwtService gặp lỗi"
        );

        // Kiểm tra thông điệp của ngoại lệ
        assertEquals("Lỗi tạo token", ngoaiLe.getMessage(), "Thông điệp ngoại lệ phải khớp");

        // Xác minh rằng authenticationManager được gọi, và jwtService được gọi
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, times(1)).generateToken(yeuCauDangNhapHopLe.getTenDangNhap());
    }
}


