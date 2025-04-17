package com.example.hieuthuoc;

import com.example.hieuthuoc.dto.NhaCungCapDTO;
import com.example.hieuthuoc.dto.ResponseDTO;
import com.example.hieuthuoc.entity.NhaCungCap;
import com.example.hieuthuoc.impl.NhaCungCapServiceImpl;
import com.example.hieuthuoc.repository.NhaCungCapRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NhaCungCapServiceImplTest {

    @InjectMocks
    private NhaCungCapServiceImpl nhaCungCapService;

    @Mock
    private NhaCungCapRepo nhaCungCapRepo;

    @Mock
    private ModelMapper modelMapper;

    private NhaCungCapDTO nhaCungCapDTO;
    private NhaCungCap nhaCungCap;

    @BeforeEach
    void thietLap() {
        // Khởi tạo NhaCungCapDTO
        nhaCungCapDTO = new NhaCungCapDTO();
        nhaCungCapDTO.setId(1);
        nhaCungCapDTO.setMaNCC("NCC001");
        nhaCungCapDTO.setTenNhaCungCap("Công ty A");

        // Khởi tạo NhaCungCap
        nhaCungCap = new NhaCungCap();
        nhaCungCap.setId(1);
        nhaCungCap.setMaNCC("NCC001");
        nhaCungCap.setTenNhaCungCap("Công ty A");
    }
    
 // TC29
    // Test Case 1: Lấy danh sách nhà cung cấp thành công
    // Mục tiêu: Kiểm tra rằng getAll trả về danh sách nhà cung cấp
    @Test
    void kiemTraGetAllThanhCong() {
        // Sắp xếp
        List<NhaCungCap> nhaCungCaps = Collections.singletonList(nhaCungCap);
        when(nhaCungCapRepo.findAll()).thenReturn(nhaCungCaps);

        // Thực hiện
        ResponseDTO<List<NhaCungCap>> ketQua = nhaCungCapService.getAll();

        // Kiểm tra
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(200, ketQua.getStatus(), "Trạng thái phải là 200");
        assertEquals("Thành công", ketQua.getMsg(), "Thông điệp phải là 'Thành công'");
        assertEquals(nhaCungCaps, ketQua.getData(), "Danh sách nhà cung cấp phải khớp");

        // Xác minh
        verify(nhaCungCapRepo, times(1)).findAll();
    }

 // TC30
    // Test Case 2: Lấy danh sách nhà cung cấp rỗng
    // Mục tiêu: Kiểm tra rằng getAll trả về danh sách rỗng
    @Test
    void kiemTraGetAllDanhSachRong() {
        // Sắp xếp
        when(nhaCungCapRepo.findAll()).thenReturn(Collections.emptyList());

        // Thực hiện
        ResponseDTO<List<NhaCungCap>> ketQua = nhaCungCapService.getAll();

        // Kiểm tra
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(200, ketQua.getStatus(), "Trạng thái phải là 200");
        assertEquals("Thành công", ketQua.getMsg(), "Thông điệp phải là 'Thành công'");
        assertTrue(ketQua.getData().isEmpty(), "Danh sách nhà cung cấp phải rỗng");

        // Xác minh
        verify(nhaCungCapRepo, times(1)).findAll();
    }

 // TC31
    // Test Case 3: Tìm kiếm nhà cung cấp thành công
    // Mục tiêu: Kiểm tra rằng searchByTenNhaCungCap trả về danh sách hợp lệ
    @Test
    void kiemTraSearchByTenNhaCungCapThanhCong() {
        // Sắp xếp
        List<NhaCungCap> nhaCungCaps = Collections.singletonList(nhaCungCap);
        when(nhaCungCapRepo.searchByTenNhaCungCap("Công ty A")).thenReturn(nhaCungCaps);

        // Thực hiện
        ResponseDTO<List<NhaCungCap>> ketQua = nhaCungCapService.searchByTenNhaCungCap("Công ty A");

        // Kiểm tra
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(200, ketQua.getStatus(), "Trạng thái phải là 200");
        assertEquals("Thành công", ketQua.getMsg(), "Thông điệp phải là 'Thành công'");
        assertEquals(nhaCungCaps, ketQua.getData(), "Danh sách nhà cung cấp phải khớp");

        // Xác minh
        verify(nhaCungCapRepo, times(1)).searchByTenNhaCungCap("Công ty A");
    }

 // TC32
    // Test Case 4: Tìm kiếm nhà cung cấp thất bại
    // Mục tiêu: Kiểm tra rằng searchByTenNhaCungCap trả về lỗi khi không tìm thấy
    @Test
    void kiemTraSearchByTenNhaCungCapThatBai() {
        // Sắp xếp
        when(nhaCungCapRepo.searchByTenNhaCungCap("Công ty X")).thenReturn(Collections.emptyList());

        // Thực hiện
        ResponseDTO<List<NhaCungCap>> ketQua = nhaCungCapService.searchByTenNhaCungCap("Công ty X");

        // Kiểm tra
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(409, ketQua.getStatus(), "Trạng thái phải là 409");
        assertEquals("Nhà sản xuất không tồn tại", ketQua.getMsg(), "Thông điệp phải là 'Nhà sản xuất không tồn tại'");
        assertNull(ketQua.getData(), "Dữ liệu trả về phải là null");

        // Xác minh
        verify(nhaCungCapRepo, times(1)).searchByTenNhaCungCap("Công ty X");
    }

 // TC33
    // Test Case 5: Tìm kiếm với tên nhà cung cấp null
    // Mục tiêu: Kiểm tra xử lý khi tên nhà cung cấp là null
    @Test
    void kiemTraSearchByTenNhaCungCapTenNull() {
        // Sắp xếp
        when(nhaCungCapRepo.searchByTenNhaCungCap(null)).thenReturn(Collections.emptyList());

        // Thực hiện
        ResponseDTO<List<NhaCungCap>> ketQua = nhaCungCapService.searchByTenNhaCungCap(null);

        // Kiểm tra
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(409, ketQua.getStatus(), "Trạng thái phải là 409");
        assertEquals("Nhà sản xuất không tồn tại", ketQua.getMsg(), "Thông điệp phải là 'Nhà sản xuất không tồn tại'");
        assertNull(ketQua.getData(), "Dữ liệu trả về phải là null");

        // Xác minh
        verify(nhaCungCapRepo, times(1)).searchByTenNhaCungCap(null);
    }

 // TC34
    // Test Case 6: Tạo nhà cung cấp thành công
    // Mục tiêu: Kiểm tra rằng nhà cung cấp được tạo khi mã NCC chưa tồn tại
    @Test
    void kiemTraCreateThanhCong() {
        // Sắp xếp
        when(modelMapper.map(nhaCungCapDTO, NhaCungCap.class)).thenReturn(nhaCungCap);
        when(nhaCungCapRepo.existsByMaNCC("NCC001")).thenReturn(false);
        when(nhaCungCapRepo.save(any(NhaCungCap.class))).thenReturn(nhaCungCap);

        // Thực hiện
        ResponseDTO<NhaCungCap> ketQua = nhaCungCapService.create(nhaCungCapDTO);

        // Kiểm tra
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(201, ketQua.getStatus(), "Trạng thái phải là 201");
        assertEquals("Thành công", ketQua.getMsg(), "Thông điệp phải là 'Thành công'");
        assertEquals(nhaCungCap, ketQua.getData(), "Dữ liệu nhà cung cấp phải khớp");

        // Xác minh
        verify(modelMapper, times(1)).map(nhaCungCapDTO, NhaCungCap.class);
        verify(nhaCungCapRepo, times(1)).existsByMaNCC("NCC001");
        verify(nhaCungCapRepo, times(1)).save(nhaCungCap);
    }

 // TC35
    // Test Case 7: Thất bại khi mã nhà cung cấp đã tồn tại
    // Mục tiêu: Kiểm tra xử lý khi mã NCC đã tồn tại
    @Test
    void kiemTraCreateThatBaiMaNCCDaTonTai() {
        // Sắp xếp
        when(modelMapper.map(nhaCungCapDTO, NhaCungCap.class)).thenReturn(nhaCungCap);
        when(nhaCungCapRepo.existsByMaNCC("NCC001")).thenReturn(true);

        // Thực hiện
        ResponseDTO<NhaCungCap> ketQua = nhaCungCapService.create(nhaCungCapDTO);

        // Kiểm tra
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(409, ketQua.getStatus(), "Trạng thái phải là 409");
        assertEquals("Nhà cung cấp đã tồn tại", ketQua.getMsg(), "Thông điệp phải là 'Nhà cung cấp đã tồn tại'");
        assertNull(ketQua.getData(), "Dữ liệu trả về phải là null");

        // Xác minh
        verify(modelMapper, times(1)).map(nhaCungCapDTO, NhaCungCap.class);
        verify(nhaCungCapRepo, times(1)).existsByMaNCC("NCC001");
        verify(nhaCungCapRepo, never()).save(any(NhaCungCap.class));
    }

 // TC36
    // Test Case 8: Tạo với đầu vào null
    // Mục tiêu: Kiểm tra xử lý khi nhaCungCapDTO là null
    @Test
    void kiemTraCreateDauVaoNull() {
        // Sắp xếp
        when(modelMapper.map(null, NhaCungCap.class)).thenThrow(new IllegalArgumentException("DTO không hợp lệ"));

        // Thực hiện
        ResponseDTO<NhaCungCap> ketQua = nhaCungCapService.create(null);

        // Kiểm tra
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(409, ketQua.getStatus(), "Trạng thái phải là 409");
        assertEquals("Nhà cung cấp đã tồn tại", ketQua.getMsg(), "Thông điệp phải là 'Nhà cung cấp đã tồn tại'");
        assertNull(ketQua.getData(), "Dữ liệu trả về phải là null");

        // Xác minh
        verify(modelMapper, times(1)).map(null, NhaCungCap.class);
        verify(nhaCungCapRepo, never()).existsByMaNCC(anyString());
        verify(nhaCungCapRepo, never()).save(any(NhaCungCap.class));
    }

 // TC37
    // Test Case 9: Cập nhật nhà cung cấp thành công (giữ nguyên maNCC)
    // Mục tiêu: Kiểm tra rằng nhà cung cấp được cập nhật khi giữ nguyên maNCC
    @Test
    void kiemTraUpdateThanhCongGiuNguyenMaNCC() {
        // Sắp xếp
        when(modelMapper.map(nhaCungCapDTO, NhaCungCap.class)).thenReturn(nhaCungCap);
        when(nhaCungCapRepo.findById(1)).thenReturn(Optional.of(nhaCungCap));
        when(nhaCungCapRepo.save(any(NhaCungCap.class))).thenReturn(nhaCungCap);

        // Thực hiện
        ResponseDTO<NhaCungCap> ketQua = nhaCungCapService.update(nhaCungCapDTO);

        // Kiểm tra
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(200, ketQua.getStatus(), "Trạng thái phải là 200");
        assertEquals("Thành công", ketQua.getMsg(), "Thông điệp phải là 'Thành công'");
        assertEquals(nhaCungCap, ketQua.getData(), "Dữ liệu nhà cung cấp phải khớp");

        // Xác minh
        verify(modelMapper, times(1)).map(nhaCungCapDTO, NhaCungCap.class);
        verify(nhaCungCapRepo, times(1)).findById(1);
        verify(nhaCungCapRepo, never()).existsByMaNCC(anyString());
        verify(nhaCungCapRepo, times(1)).save(nhaCungCap);
    }

 // TC38
    // Test Case 10: Cập nhật nhà cung cấp thành công (thay đổi maNCC không trùng)
    // Mục tiêu: Kiểm tra rằng nhà cung cấp được cập nhật khi thay đổi maNCC
    @Test
    void kiemTraUpdateThanhCongThayDoiMaNCC() {
        // Sắp xếp
        NhaCungCap nhaCungCapMoi = new NhaCungCap();
        nhaCungCapMoi.setId(1);
        nhaCungCapMoi.setMaNCC("NCC002");
        nhaCungCapMoi.setTenNhaCungCap("Công ty A");
        when(modelMapper.map(nhaCungCapDTO, NhaCungCap.class)).thenReturn(nhaCungCapMoi);
        when(nhaCungCapRepo.findById(1)).thenReturn(Optional.of(nhaCungCap));
        when(nhaCungCapRepo.existsByMaNCC("NCC002")).thenReturn(false);
        when(nhaCungCapRepo.save(any(NhaCungCap.class))).thenReturn(nhaCungCapMoi);

        // Thực hiện
        ResponseDTO<NhaCungCap> ketQua = nhaCungCapService.update(nhaCungCapDTO);

        // Kiểm tra
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(200, ketQua.getStatus(), "Trạng thái phải là 200");
        assertEquals("Thành công", ketQua.getMsg(), "Thông điệp phải là 'Thành công'");
        assertEquals(nhaCungCapMoi, ketQua.getData(), "Dữ liệu nhà cung cấp phải khớp");

        // Xác minh
        verify(modelMapper, times(1)).map(nhaCungCapDTO, NhaCungCap.class);
        verify(nhaCungCapRepo, times(1)).findById(1);
        verify(nhaCungCapRepo, times(1)).existsByMaNCC("NCC002");
        verify(nhaCungCapRepo, times(1)).save(nhaCungCapMoi);
    }

 // TC39
    // Test Case 11: Thất bại khi nhà cung cấp không tồn tại
    // Mục tiêu: Kiểm tra xử lý khi không tìm thấy nhà cung cấp
    @Test
    void kiemTraUpdateThatBaiKhongTonTai() {
        // Sắp xếp
        when(modelMapper.map(nhaCungCapDTO, NhaCungCap.class)).thenReturn(nhaCungCap);
        when(nhaCungCapRepo.findById(1)).thenReturn(Optional.empty());

        // Thực hiện
        ResponseDTO<NhaCungCap> ketQua = nhaCungCapService.update(nhaCungCapDTO);

        // Kiểm tra
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(404, ketQua.getStatus(), "Trạng thái phải là 404");
        assertEquals("Không tìm thấy nhà cung cấp", ketQua.getMsg(), "Thông điệp phải là 'Không tìm thấy nhà cung cấp'");
        assertNull(ketQua.getData(), "Dữ liệu trả về phải là null");

        // Xác minh
        verify(modelMapper, times(1)).map(nhaCungCapDTO, NhaCungCap.class);
        verify(nhaCungCapRepo, times(1)).findById(1);
        verify(nhaCungCapRepo, never()).existsByMaNCC(anyString());
        verify(nhaCungCapRepo, never()).save(any(NhaCungCap.class));
    }

 // TC40
    // Test Case 12: Thất bại khi mã nhà cung cấp mới đã tồn tại
    // Mục tiêu: Kiểm tra xử lý khi maNCC mới trùng với nhà cung cấp khác
    @Test
    void kiemTraUpdateThatBaiMaNCCDaTonTai() {
        // Sắp xếp
        NhaCungCap nhaCungCapMoi = new NhaCungCap();
        nhaCungCapMoi.setId(1);
        nhaCungCapMoi.setMaNCC("NCC002");
        nhaCungCapMoi.setTenNhaCungCap("Công ty A");
        when(modelMapper.map(nhaCungCapDTO, NhaCungCap.class)).thenReturn(nhaCungCapMoi);
        when(nhaCungCapRepo.findById(1)).thenReturn(Optional.of(nhaCungCap));
        when(nhaCungCapRepo.existsByMaNCC("NCC002")).thenReturn(true);

        // Thực hiện
        ResponseDTO<NhaCungCap> ketQua = nhaCungCapService.update(nhaCungCapDTO);

        // Kiểm tra
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(409, ketQua.getStatus(), "Trạng thái phải là 409");
        assertEquals("Mã Nhà cung cấp đã tồn tại", ketQua.getMsg(), "Thông điệp phải là 'Mã Nhà cung cấp đã tồn tại'");
        assertNull(ketQua.getData(), "Dữ liệu trả về phải là null");

        // Xác minh
        verify(modelMapper, times(1)).map(nhaCungCapDTO, NhaCungCap.class);
        verify(nhaCungCapRepo, times(1)).findById(1);
        verify(nhaCungCapRepo, times(1)).existsByMaNCC("NCC002");
        verify(nhaCungCapRepo, never()).save(any(NhaCungCap.class));
    }

 // TC41
    // Test Case 13: Cập nhật với đầu vào null
    // Mục tiêu: Kiểm tra xử lý khi nhaCungCapDTO là null
    @Test
    void kiemTraUpdateDauVaoNull() {
        // Sắp xếp
        when(modelMapper.map(null, NhaCungCap.class)).thenThrow(new IllegalArgumentException("DTO không hợp lệ"));

        // Thực hiện
        ResponseDTO<NhaCungCap> ketQua = nhaCungCapService.update(null);

        // Kiểm tra
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(404, ketQua.getStatus(), "Trạng thái phải là 404");
        assertEquals("Không tìm thấy nhà cung cấp", ketQua.getMsg(), "Thông điệp phải là 'Không tìm thấy nhà cung cấp'");
        assertNull(ketQua.getData(), "Dữ liệu trả về phải là null");

        // Xác minh
        verify(modelMapper, times(1)).map(null, NhaCungCap.class);
        verify(nhaCungCapRepo, never()).findById(anyInt());
        verify(nhaCungCapRepo, never()).existsByMaNCC(anyString());
        verify(nhaCungCapRepo, never()).save(any(NhaCungCap.class));
    }

 // TC42
    // Test Case 14: Xóa nhà cung cấp thành công
    // Mục tiêu: Kiểm tra rằng nhà cung cấp được xóa thành công
    @Test
    void kiemTraDeleteThanhCong() {
        // Sắp xếp
        doNothing().when(nhaCungCapRepo).deleteById(1);

        // Thực hiện
        ResponseDTO<Void> ketQua = nhaCungCapService.delete(1);

        // Kiểm tra
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(200, ketQua.getStatus(), "Trạng thái phải là 200");
        assertEquals("Thành công", ketQua.getMsg(), "Thông điệp phải là 'Thành công'");
        assertNull(ketQua.getData(), "Dữ liệu trả về phải là null");

        // Xác minh
        verify(nhaCungCapRepo, times(1)).deleteById(1);
    }

 // TC43
    // Test Case 15: Xóa với ID không tồn tại
    // Mục tiêu: Kiểm tra xử lý khi ID không tồn tại
    @Test
    void kiemTraDeleteIDKhongTonTai() {
        // Sắp xếp
        doThrow(new EmptyResultDataAccessException(1)).when(nhaCungCapRepo).deleteById(1);

        // Thực hiện
        ResponseDTO<Void> ketQua = nhaCungCapService.delete(1);

        // Kiểm tra
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(200, ketQua.getStatus(), "Trạng thái phải là 200");
        assertEquals("Thành công", ketQua.getMsg(), "Thông điệp phải là 'Thành công'");
        assertNull(ketQua.getData(), "Dữ liệu trả về phải là null");

        // Xác minh
        verify(nhaCungCapRepo, times(1)).deleteById(1);
    }
}