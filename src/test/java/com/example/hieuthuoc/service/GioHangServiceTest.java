package com.example.hieuthuoc.service;


import com.example.hieuthuoc.dto.ChiTietGioHangDTO;
import com.example.hieuthuoc.dto.ResponseDTO;
import com.example.hieuthuoc.entity.ChiTietGioHang;
import com.example.hieuthuoc.entity.GioHang;
import com.example.hieuthuoc.entity.Thuoc;
import com.example.hieuthuoc.repository.ChiTietGioHangRepo;
import com.example.hieuthuoc.repository.GioHangRepo;
import com.example.hieuthuoc.repository.ThongBaoRepo;
import com.example.hieuthuoc.repository.ThuocRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GioHangServiceTest {

    @InjectMocks
    private GioHangServiceImpl  gioHangService;

    @Mock
    private GioHangRepo gioHangRepo;

    @Mock
    private ChiTietGioHangRepo chiTietGioHangRepo;

    @Mock
    private ThuocRepo thuocRepo;

    @Test
    void testGetByNguoiDungId_success() {
        // Arrange
        int nguoiDungId = 1;
        GioHang gioHang = new GioHang();
        gioHang.setId(1); // Giả sử giỏ hàng có id là 1
        Optional<GioHang> gioHangOpt = Optional.of(gioHang);

        when(gioHangRepo.findByKhachHangId(nguoiDungId)).thenReturn(gioHangOpt);

        // Act
        ResponseDTO<GioHang> response = gioHangService.getByNguoiDungId(nguoiDungId);

        // Assert
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertNotNull(response.getData());
        assertEquals(gioHang.getId(), response.getData().getId());
    }

    @Test
    void testGetByNguoiDungId_notFound() {
        // Arrange
        int nguoiDungId = 999;
        Optional<GioHang> gioHangOpt = Optional.empty();

        when(gioHangRepo.findByKhachHangId(nguoiDungId)).thenReturn(gioHangOpt);

        // Act
        ResponseDTO<GioHang> response = gioHangService.getByNguoiDungId(nguoiDungId);

        // Assert
        assertEquals(404, response.getStatus());
        assertEquals("Không tìm thấy giỏ hàng cho người dùng ID: " + nguoiDungId, response.getMsg());
    }

    @Test
    void testCreateThuoc_success() {
        ChiTietGioHangDTO dto = new ChiTietGioHangDTO();
        dto.setGioHangId(1);  // Giỏ hàng ID hợp lệ
        dto.setThuocId(20);  // Thuốc ID hợp lệ
        dto.setSoLuong(2);    // Số lượng thuốc cần thêm vào giỏ  // Giỏ hàng ID 1, Thuốc ID 101, Số lượng 2
        GioHang gioHang = new GioHang(); gioHang.setId(1);
        Thuoc thuoc = new Thuoc(); thuoc.setId(101);

        when(gioHangRepo.findById(1)).thenReturn(Optional.of(gioHang));
        when(thuocRepo.findById(20)).thenReturn(Optional.of(thuoc));
        when(chiTietGioHangRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseDTO<ChiTietGioHang> response = gioHangService.createThuoc(dto);

        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertNotNull(response.getData());
        assertEquals(2, response.getData().getSoLuong());
    }

    @Test
    void testCreateThuoc_UpdateQuantity() {
        ChiTietGioHangDTO dto = new ChiTietGioHangDTO();
        dto.setGioHangId(1);  // Giỏ hàng ID hợp lệ
        dto.setThuocId(20);  // Thuốc ID hợp lệ
        dto.setSoLuong(3); // Giỏ hàng ID 1, Thuốc ID 101, Số lượng 3
        GioHang gioHang = new GioHang(); gioHang.setId(1);
        Thuoc thuoc = new Thuoc(); thuoc.setId(101);
        ChiTietGioHang existingItem = new ChiTietGioHang();
        existingItem.setSoLuong(2); // Thuốc đã có trong giỏ hàng, với số lượng 2

        when(gioHangRepo.findById(1)).thenReturn(Optional.of(gioHang));
        when(thuocRepo.findById(20)).thenReturn(Optional.of(thuoc));
        when(chiTietGioHangRepo.existsByGioHangIdAndThuocId(1, 20)).thenReturn(true);
        when(chiTietGioHangRepo.findByThuocId(20)).thenReturn(existingItem);
        when(chiTietGioHangRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseDTO<ChiTietGioHang> response = gioHangService.createThuoc(dto);

        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertNotNull(response.getData());
        assertEquals(5, response.getData().getSoLuong());  // Kiểm tra số lượng đã được cập nhật (2 + 3)
    }

    @Test
    void testCreateThuoc_GioHangNotFound() {
        ChiTietGioHangDTO dto = new ChiTietGioHangDTO();  // Giỏ hàng ID 999 không tồn tại
        dto.setGioHangId(999);  // Giỏ hàng ID hợp lệ
        dto.setThuocId(20);  // Thuốc ID hợp lệ
        dto.setSoLuong(2);    // Số lượng thuốc cần thêm vào giỏ

        when(gioHangRepo.findById(999)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> gioHangService.createThuoc(dto));
        assertEquals("Giỏ hàng với ID = 999 không tồn tại", ex.getMessage());
    }

    @Test
    void testCreateThuoc_ThuocNotFound() {
        ChiTietGioHangDTO dto = new ChiTietGioHangDTO();  // Thuốc ID 999 không tồn tại
        dto.setGioHangId(1);
        dto.setThuocId(999);
        dto.setSoLuong(2);

        when(gioHangRepo.findById(1)).thenReturn(Optional.of(new GioHang()));
        when(thuocRepo.findById(999)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> gioHangService.createThuoc(dto));
        assertEquals("Thuốc với ID = 999 không tồn tại", ex.getMessage());
    }

    @Test
    void testUpdateThuoc_Success() {
        ChiTietGioHangDTO dto = new ChiTietGioHangDTO();
        dto.setId(1);  // Sản phẩm tồn tại với ID = 1
        dto.setSoLuong(5);  // Số lượng mới cần cập nhật

        // Mock chiTietGioHangRepo.findById trả về sản phẩm tồn tại
        ChiTietGioHang existingProduct = new ChiTietGioHang();
        existingProduct.setId(1);
        existingProduct.setSoLuong(3);  // Số lượng cũ là 3

        when(chiTietGioHangRepo.findById(1)).thenReturn(Optional.of(existingProduct));
        when(chiTietGioHangRepo.save(any(ChiTietGioHang.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Giả lập việc lưu sản phẩm

        // Gọi phương thức updateThuoc
        ResponseDTO<ChiTietGioHang> response = gioHangService.updateThuoc(dto);

        // Kiểm tra mã trạng thái, thông báo và dữ liệu trả về
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertNotNull(response.getData());
        assertEquals(5, response.getData().getSoLuong());  // Kiểm tra số lượng được cập nhật
    }

    @Test
    void testUpdateThuoc_ProductNotFound() {
        ChiTietGioHangDTO dto = new ChiTietGioHangDTO();
        dto.setId(1);  // Sản phẩm với ID = 1 không tồn tại
        dto.setSoLuong(5);  // Số lượng cần cập nhật

        // Mock chiTietGioHangRepo.findById trả về Optional.empty() (sản phẩm không tồn tại)
        when(chiTietGioHangRepo.findById(1)).thenReturn(Optional.empty());

        // Gọi phương thức updateThuoc
        ResponseDTO<ChiTietGioHang> response = gioHangService.updateThuoc(dto);

        // Kiểm tra mã trạng thái và thông báo lỗi
        assertEquals(404, response.getStatus());
        assertEquals("Không tìm thấy sản phẩm trong giỏ hàng", response.getMsg());
    }

    @Test
    void testDeleteThuoc_Success() {
        int id = 5;

        // Không cần mock deleteById vì void (nếu không throw exception)
        doNothing().when(chiTietGioHangRepo).deleteById(id);

        ResponseDTO<Void> response = gioHangService.deleteThuoc(id);

        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
    }

}
