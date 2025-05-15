package com.example.hieuthuoc.controller;

import com.example.hieuthuoc.dto.*;
import com.example.hieuthuoc.entity.*;
import com.example.hieuthuoc.service.GioHangService;
import com.example.hieuthuoc.service.GioHangServiceTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;




@ExtendWith(MockitoExtension.class)
class GioHangControllerTest {

    @InjectMocks
    private GioHangController gioHangController;

    @Mock
    private GioHangService gioHangService;

    @Test
    void getByNguoiDungId_Found() {
        GioHang gioHang = new GioHang();
        gioHang.setId(1);
        ResponseDTO<GioHang> response = new ResponseDTO<>(200, "Thành công", gioHang);

        when(gioHangService.getByNguoiDungId(1)).thenReturn(response);

        ResponseDTO<GioHang> result = gioHangController.getByNguoiDungId(1);

        assertEquals(200, result.getStatus());
        assertEquals("Thành công", result.getMsg());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getId());
    }

    @Test
    void getByNguoiDungId_NotFound() {
        ResponseDTO<GioHang> response = new ResponseDTO<>(404, "Không tìm thấy", null);

        when(gioHangService.getByNguoiDungId(999)).thenReturn(response);

        ResponseDTO<GioHang> result = gioHangController.getByNguoiDungId(999);

        assertEquals(404, result.getStatus());
        assertEquals("Không tìm thấy", result.getMsg());
        assertNull(result.getData());
    }


    @Test
    void createThuoc_Success() {
        ChiTietGioHangDTO dto = new ChiTietGioHangDTO();
        dto.setGioHangId(1);
        dto.setThuocId(10);
        dto.setSoLuong(2);
        dto.setDonGia(50000.0);

        ChiTietGioHang chiTiet = new ChiTietGioHang();
        chiTiet.setId(100);

        ResponseDTO<ChiTietGioHang> mockResponse = new ResponseDTO<>(200, "Thêm thành công", chiTiet);
        when(gioHangService.createThuoc(dto)).thenReturn(mockResponse);

        ResponseDTO<ChiTietGioHang> result = gioHangController.createThuoc(dto);

        assertEquals(200, result.getStatus());
        assertEquals("Thêm thành công", result.getMsg());
        assertNotNull(result.getData());
        assertEquals(100, result.getData().getId());
    }

    @Test
    void createThuoc_InvalidQuantity() {
        ChiTietGioHangDTO dto = new ChiTietGioHangDTO();
        dto.setGioHangId(1);
        dto.setThuocId(10);
        dto.setSoLuong(-1);  // invalid
        dto.setDonGia(50000.0);

        ResponseDTO<ChiTietGioHang> mockResponse = new ResponseDTO<>(400, "Số lượng không hợp lệ");
        when(gioHangService.createThuoc(dto)).thenReturn(mockResponse);

        ResponseDTO<ChiTietGioHang> result = gioHangController.createThuoc(dto);

        assertEquals(400, result.getStatus());
        assertEquals("Số lượng không hợp lệ", result.getMsg());
        assertNull(result.getData());
    }

    @Test
    void createThuoc_CartNotFound() {
        ChiTietGioHangDTO dto = new ChiTietGioHangDTO();
        dto.setGioHangId(999); // không tồn tại
        dto.setThuocId(10);
        dto.setSoLuong(1);
        dto.setDonGia(50000.0);

        ResponseDTO<ChiTietGioHang> mockResponse = new ResponseDTO<>(404, "Giỏ hàng không tồn tại");
        when(gioHangService.createThuoc(dto)).thenReturn(mockResponse);

        ResponseDTO<ChiTietGioHang> result = gioHangController.createThuoc(dto);

        assertEquals(404, result.getStatus());
        assertEquals("Giỏ hàng không tồn tại", result.getMsg());
        assertNull(result.getData());
    }

    @Test
    void createThuoc_ThuocNotFound() {
        ChiTietGioHangDTO dto = new ChiTietGioHangDTO();
        dto.setGioHangId(1); // không tồn tại
        dto.setThuocId(999);
        dto.setSoLuong(1);
        dto.setDonGia(50000.0);

        ResponseDTO<ChiTietGioHang> mockResponse = new ResponseDTO<>(404, "Thuốc không tồn tại");
        when(gioHangService.createThuoc(dto)).thenReturn(mockResponse);

        ResponseDTO<ChiTietGioHang> result = gioHangController.createThuoc(dto);

        assertEquals(404, result.getStatus());
        assertEquals("Thuốc không tồn tại", result.getMsg());
        assertNull(result.getData());
    }

    @Test
    void updateThuoc_Success() {
        ChiTietGioHangDTO dto = new ChiTietGioHangDTO();
        dto.setId(101);
        dto.setGioHangId(1);
        dto.setThuocId(10);
        dto.setSoLuong(5);
        dto.setDonGia(50000.0);

        ChiTietGioHang updated = new ChiTietGioHang();
        updated.setId(101);

        ResponseDTO<ChiTietGioHang> response = new ResponseDTO<>(200, "Cập nhật thành công", updated);
        when(gioHangService.updateThuoc(dto)).thenReturn(response);

        ResponseDTO<ChiTietGioHang> result = gioHangController.updateThuoc(dto);

        assertEquals(200, result.getStatus());
        assertEquals("Cập nhật thành công", result.getMsg());
        assertNotNull(result.getData());
        assertEquals(101, result.getData().getId());
    }

    @Test
    void updateThuoc_NotFound() {
        ChiTietGioHangDTO dto = new ChiTietGioHangDTO();
        dto.setId(999);
        dto.setGioHangId(1);
        dto.setThuocId(10);
        dto.setSoLuong(2);
        dto.setDonGia(50000.0);

        ResponseDTO<ChiTietGioHang> response = new ResponseDTO<>(404, "Không tìm thấy chi tiết");
        when(gioHangService.updateThuoc(dto)).thenReturn(response);

        ResponseDTO<ChiTietGioHang> result = gioHangController.updateThuoc(dto);

        assertEquals(404, result.getStatus());
        assertEquals("Không tìm thấy chi tiết", result.getMsg());
        assertNull(result.getData());
    }

    @Test
    void updateThuoc_InvalidQuantity() {
        ChiTietGioHangDTO dto = new ChiTietGioHangDTO();
        dto.setId(101);
        dto.setGioHangId(1);
        dto.setThuocId(10);
        dto.setSoLuong(-3); // invalid
        dto.setDonGia(50000.0);

        ResponseDTO<ChiTietGioHang> response = new ResponseDTO<>(400, "Số lượng không hợp lệ");
        when(gioHangService.updateThuoc(dto)).thenReturn(response);

        ResponseDTO<ChiTietGioHang> result = gioHangController.updateThuoc(dto);

        assertEquals(400, result.getStatus());
        assertEquals("Số lượng không hợp lệ", result.getMsg());
        assertNull(result.getData());
    }

    @Test
    void deleteThuoc_Success() {
        int productId = 101;

        ResponseDTO<Void> response = new ResponseDTO<>(200, "Xóa thành công", null);
        when(gioHangService.deleteThuoc(productId)).thenReturn(response);

        ResponseDTO<Void> result = gioHangController.deleteThuoc(productId);

        assertEquals(200, result.getStatus());
        assertEquals("Xóa thành công", result.getMsg());
        assertNull(result.getData());
    }

    @Test
    void deleteThuoc_NotFound() {
        int productId = 999;

        ResponseDTO<Void> response = new ResponseDTO<>(404, "Không tìm thấy sản phẩm");
        when(gioHangService.deleteThuoc(productId)).thenReturn(response);

        ResponseDTO<Void> result = gioHangController.deleteThuoc(productId);

        assertEquals(404, result.getStatus());
        assertEquals("Không tìm thấy sản phẩm", result.getMsg());
        assertNull(result.getData());
    }

    @Test
    void deleteThuoc_InvalidId() {
        int productId = 0; // invalid

        ResponseDTO<Void> response = new ResponseDTO<>(400, "ID không hợp lệ");
        when(gioHangService.deleteThuoc(productId)).thenReturn(response);

        ResponseDTO<Void> result = gioHangController.deleteThuoc(productId);

        assertEquals(400, result.getStatus());
        assertEquals("ID không hợp lệ", result.getMsg());
        assertNull(result.getData());
    }

    @Test
    void deleteThuoc_ExceptionThrown() {
        int productId = 101;

        when(gioHangService.deleteThuoc(productId)).thenThrow(new RuntimeException("Lỗi hệ thống"));

        assertThrows(RuntimeException.class, () -> gioHangController.deleteThuoc(productId));
    }
}

