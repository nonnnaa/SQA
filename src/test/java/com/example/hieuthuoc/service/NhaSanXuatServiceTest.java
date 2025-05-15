package com.example.hieuthuoc.service;

import com.example.hieuthuoc.dto.NhaSanXuatDTO;
import com.example.hieuthuoc.dto.ResponseDTO;
import com.example.hieuthuoc.entity.NhaSanXuat;
import com.example.hieuthuoc.repository.NhaSanXuatRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NhaSanXuatServiceImplTest {

    @InjectMocks
    private NhaSanXuatServiceImpl nhaSanXuatService;

    @Mock
    private NhaSanXuatRepo nhaSanXuatRepo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        nhaSanXuatService.modelMapper = new ModelMapper();
    }

    /**
     * TC74 - Mục tiêu: Lấy tất cả nhà sản xuất thành công
     * Input: không có
     * Expected Output: status = 200, data != null
     * Ghi chú: kiểm tra gọi findAll()
     */
    @Test
    void getAll_ShouldReturnList() {
        List<NhaSanXuat> list = List.of(new NhaSanXuat());
        when(nhaSanXuatRepo.findAll()).thenReturn(list);

        ResponseDTO<List<NhaSanXuat>> response = nhaSanXuatService.getAll();

        assertEquals(200, response.getStatus());
        assertNotNull(response.getData());
        verify(nhaSanXuatRepo).findAll();
    }

    /**
     * TC75 - Mục tiêu: Tìm theo tên nhà sản xuất thành công
     * Input: tên nhà sản xuất = ABC
     * Expected Output: status = 200
     * Ghi chú: nhà sản xuất tồn tại
     */
    @Test
    void searchByTenNhaSanXuat_ShouldReturnResults() {
        when(nhaSanXuatRepo.searchByTenNhaSanXuat("ABC"))
                .thenReturn(List.of(new NhaSanXuat()));

        ResponseDTO<List<NhaSanXuat>> response = nhaSanXuatService.searchByTenNhaSanXuat("ABC");

        assertEquals(200, response.getStatus());
        assertNotNull(response.getData());
    }

    /**
     * TC76 - Mục tiêu: Tìm theo tên không có kết quả
     * Input: tên không khớp
     * Expected Output: status = 409
     * Ghi chú: danh sách rỗng
     */
    @Test
    void searchByTenNhaSanXuat_ShouldReturnNotFound() {
        when(nhaSanXuatRepo.searchByTenNhaSanXuat("XYZ")).thenReturn(List.of());

        ResponseDTO<List<NhaSanXuat>> response = nhaSanXuatService.searchByTenNhaSanXuat("XYZ");

        assertEquals(409, response.getStatus());
        assertNull(response.getData());
    }


    /**
     * TC77 - Mục tiêu: Kiểm tra trường hợp không tìm thấy nhà sản xuất khi danh sách là null.
     * Input: tenNhaSanXuat = "XYZ"
     * Expected Output: ResponseDTO status = 409, msg = "Nhà sản xuất không tồn tại", data = null
     * Ghi chú: Đảm bảo phương thức xử lý đúng khi không có dữ liệu.
     */
    @Test
    void searchByTenNhaSanXuat_ShouldReturnNotFound_WhenNoData() {
        String tenNhaSanXuat = "XYZ";

        when(nhaSanXuatRepo.searchByTenNhaSanXuat(tenNhaSanXuat)).thenReturn(null);

        ResponseDTO<List<NhaSanXuat>> response = nhaSanXuatService.searchByTenNhaSanXuat(tenNhaSanXuat);

        assertEquals(409, response.getStatus());
        assertEquals("Nhà sản xuất không tồn tại", response.getMsg());
        assertNull(response.getData());
    }

    /**
     * TC78 - Mục tiêu: Tạo nhà sản xuất mới thành công
     * Input: mã chưa tồn tại
     * Expected Output: status = 201
     * Ghi chú: validate mã NSX mới
     */
    @Test
    void create_ShouldSucceed_WhenMaNSXIsNew() {
        NhaSanXuatDTO dto = new NhaSanXuatDTO();
        dto.setMaNSX("NSX01");

        when(nhaSanXuatRepo.existsByMaNSX("NSX01")).thenReturn(false);
        when(nhaSanXuatRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ResponseDTO<NhaSanXuat> response = nhaSanXuatService.create(dto);

        assertEquals(201, response.getStatus());
        assertEquals("Thành công", response.getMsg());
    }

    /**
     * TC79 - Mục tiêu: Tạo thất bại do trùng mã NSX
     * Input: mã đã tồn tại
     * Expected Output: status = 409
     * Ghi chú: kiểm tra validate trùng mã
     */
    @Test
    void create_ShouldFail_WhenMaNSXExists() {
        NhaSanXuatDTO dto = new NhaSanXuatDTO();
        dto.setMaNSX("DUPLICATE");

        when(nhaSanXuatRepo.existsByMaNSX("DUPLICATE")).thenReturn(true);

        ResponseDTO<NhaSanXuat> response = nhaSanXuatService.create(dto);

        assertEquals(409, response.getStatus());
        assertEquals("Nhà sản xuất đã tồn tại", response.getMsg());
    }

    /**
     * TC80 - Mục tiêu: Cập nhật thành công khi có dữ liệu
     * Input: NhaSanXuatDTO.id hợp lệ
     * Expected Output: status = 200
     * Ghi chú: kiểm tra save
     */
    @Test
    void update_ShouldSucceed_WhenExists() {
        NhaSanXuatDTO dto = new NhaSanXuatDTO();
        dto.setId(1);

        when(nhaSanXuatRepo.findById(1)).thenReturn(Optional.of(new NhaSanXuat()));
        when(nhaSanXuatRepo.save(any())).thenReturn(new NhaSanXuat());

        ResponseDTO<NhaSanXuat> response = nhaSanXuatService.update(dto);

        assertEquals(200, response.getStatus());
    }

    /**
     * TC81 - Mục tiêu: Cập nhật thất bại nếu không tìm thấy
     * Input: NhaSanXuatDTO.id không tồn tại
     * Expected Output: status = 404
     * Ghi chú: kiểm tra phản hồi khi dữ liệu không tồn tại
     */
    @Test
    void update_ShouldFail_WhenNotExists() {
        NhaSanXuatDTO dto = new NhaSanXuatDTO();
        dto.setId(999);

        when(nhaSanXuatRepo.findById(999)).thenReturn(Optional.empty());

        ResponseDTO<NhaSanXuat> response = nhaSanXuatService.update(dto);

        assertEquals(404, response.getStatus());
        assertEquals("Không tìm thấy nhà sản xuất", response.getMsg());
    }

    /**
     * TC82 - Mục tiêu: Xóa nhà sản xuất thành công
     * Input: id = 10
     * Expected Output: status = 200
     * Ghi chú: đơn giản test xóa
     */
    @Test
    void delete_ShouldReturnSuccess() {
        doNothing().when(nhaSanXuatRepo).deleteById(10);

        ResponseDTO<Void> response = nhaSanXuatService.delete(10);

        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
    }
    /**
     * TC83 - Mục tiêu: Xóa nhà sản xuất thất bại khi không tìm thấy nhà sản xuất
     * Input: id = 99 (nhà sản xuất không tồn tại)
         * Expected Output: status = 404, msg = "Không tìm thấy nhà sản xuất"
     * Ghi chú: Kiểm tra trường hợp không tìm thấy nhà sản xuất khi xóa.
     */
    @Test
    void delete_ShouldReturnError_WhenNotFound() {
        // Giả lập không tìm thấy nhà sản xuất với ID = 99
        when(nhaSanXuatRepo.findById(99)).thenReturn(Optional.empty());

        // Thực hiện xóa nhà sản xuất
        ResponseDTO<Void> response = nhaSanXuatService.delete(99);

        // Kiểm tra kết quả trả về
        assertEquals(404, response.getStatus());
        assertEquals("Không tìm thấy nhà sản xuất", response.getMsg());
    }

}
