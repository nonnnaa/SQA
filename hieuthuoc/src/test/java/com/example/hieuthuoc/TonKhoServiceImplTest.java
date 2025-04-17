package com.example.hieuthuoc;

import com.example.hieuthuoc.dto.PageDTO;
import com.example.hieuthuoc.dto.ResponseDTO;
import com.example.hieuthuoc.dto.SearchTonKhoDTO;
import com.example.hieuthuoc.dto.TonKhoDTO;
import com.example.hieuthuoc.entity.Thuoc;
import com.example.hieuthuoc.entity.TonKho;
import com.example.hieuthuoc.impl.TonKhoServiceImpl;
import com.example.hieuthuoc.repository.ThuocRepo;
import com.example.hieuthuoc.repository.TonKhoRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TonKhoServiceImplTest {

    @InjectMocks
    private TonKhoServiceImpl tonKhoService;

    @Mock
    private TonKhoRepo tonKhoRepo;

    @Mock
    private ThuocRepo thuocRepo;

    @Mock
    private ModelMapper modelMapper;

    private TonKhoDTO tonKhoDTO;
    private TonKho tonKho;
    private Thuoc thuoc;
    private SearchTonKhoDTO searchTonKhoDTO;

    @BeforeEach
    void thietLap() {
        // Khởi tạo TonKhoDTO
        tonKhoDTO = new TonKhoDTO();
        tonKhoDTO.setId(1);
        tonKhoDTO.setThuocId(1);

        // Khởi tạo TonKho
        tonKho = new TonKho();
        tonKho.setId(1);

        // Khởi tạo Thuoc
        thuoc = new Thuoc();
        thuoc.setId(1);
        thuoc.setTenThuoc("Paracetamol");

        // Khởi tạo SearchTonKhoDTO
        searchTonKhoDTO = new SearchTonKhoDTO();
        searchTonKhoDTO.setTenThuoc("Paracetamol");
        searchTonKhoDTO.setSoLo("LOT123");
        searchTonKhoDTO.setTenNhaSanXuat("Pharma");
        searchTonKhoDTO.setSortedField("tenThuoc");
        searchTonKhoDTO.setCurrentPage(0);
        searchTonKhoDTO.setSize(20);
    }

    // TC21
    // Test Case 1: Cập nhật tồn kho thành công
    // Mục tiêu: Kiểm tra rằng tồn kho được cập nhật khi đầu vào hợp lệ
    @Test
    void kiemTraUpdateThanhCong() {
        // Sắp xếp
        when(tonKhoRepo.findById(1)).thenReturn(Optional.of(tonKho));
        when(thuocRepo.findById(1)).thenReturn(Optional.of(thuoc));
        when(modelMapper.map(tonKhoDTO, TonKho.class)).thenReturn(tonKho);
        when(tonKhoRepo.save(any(TonKho.class))).thenReturn(tonKho);

        // Thực hiện
        ResponseDTO<TonKho> ketQua = tonKhoService.update(tonKhoDTO);

        // Kiểm tra
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(200, ketQua.getStatus(), "Trạng thái phải là 200");
        assertEquals("Thành công.", ketQua.getMsg(), "Thông điệp phải là 'Thành công.'");
        assertEquals(tonKho, ketQua.getData(), "Dữ liệu tồn kho phải khớp");
        assertEquals(thuoc, tonKho.getThuoc(), "Thuốc phải được gán đúng");

        // Xác minh
        verify(tonKhoRepo, times(1)).findById(1);
        verify(thuocRepo, times(1)).findById(1);
        verify(modelMapper, times(1)).map(tonKhoDTO, TonKho.class);
        verify(tonKhoRepo, times(1)).save(tonKho);
    }
    
    // TC22
    // Test Case 2: Thất bại khi tồn kho không tồn tại
    // Mục tiêu: Kiểm tra xử lý khi không tìm thấy tồn kho
    @Test
    void kiemTraUpdateThatBaiTonKhoKhongTonTai() {
        // Sắp xếp
        when(tonKhoRepo.findById(1)).thenReturn(Optional.empty());

        // Thực hiện
        ResponseDTO<TonKho> ketQua = tonKhoService.update(tonKhoDTO);

        // Kiểm tra
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(404, ketQua.getStatus(), "Trạng thái phải là 404");
        assertEquals("Tồn kho không tồn tại.", ketQua.getMsg(), "Thông điệp phải là 'Tồn kho không tồn tại.'");
        assertNull(ketQua.getData(), "Dữ liệu trả về phải là null");

        // Xác minh
        verify(tonKhoRepo, times(1)).findById(1);
        verify(thuocRepo, never()).findById(anyInt());
        verify(modelMapper, never()).map(any(), any());
        verify(tonKhoRepo, never()).save(any(TonKho.class));
    }
    
    // TC23
    // Test Case 3: Thất bại khi thuốc không tồn tại
    // Mục tiêu: Kiểm tra xử lý khi không tìm thấy thuốc
    @Test
    void kiemTraUpdateThatBaiThuocKhongTonTai() {
        // Sắp xếp
        when(tonKhoRepo.findById(1)).thenReturn(Optional.of(tonKho));
        when(thuocRepo.findById(1)).thenReturn(Optional.empty());

        // Thực hiện & Kiểm tra
        assertThrows(NoSuchElementException.class,
                () -> tonKhoService.update(tonKhoDTO),
                "Mong đợi NoSuchElementException khi thuốc không tồn tại");

        // Xác minh
        verify(tonKhoRepo, times(1)).findById(1);
        verify(thuocRepo, times(1)).findById(1);
        verify(modelMapper, never()).map(any(), any());
        verify(tonKhoRepo, never()).save(any(TonKho.class));
    }

 // TC24
    // Test Case 4: Đầu vào tonKhoDTO null
    // Mục tiêu: Kiểm tra xử lý khi tonKhoDTO là null
    @Test
    void kiemTraUpdateDauVaoNull() {
        // Thực hiện
        ResponseDTO<TonKho> ketQua = tonKhoService.update(null);

        // Kiểm tra
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(404, ketQua.getStatus(), "Trạng thái phải là 404");
        assertEquals("Tồn kho không tồn tại.", ketQua.getMsg(), "Thông điệp phải là 'Tồn kho không tồn tại.'");
        assertNull(ketQua.getData(), "Dữ liệu trả về phải là null");

        // Xác minh
        verify(tonKhoRepo, never()).findById(anyInt());
        verify(thuocRepo, never()).findById(anyInt());
;
    }

 // TC25
    // Test Case 5: Tìm kiếm thành công với tham số đầy đủ
    // Mục tiêu: Kiểm tra rằng tìm kiếm trả về kết quả đúng với tham số đầy đủ
    @Test
    void kiemTraSearchThanhCongThamSoDayDu() {
        // Sắp xếp
        Pageable pageable = PageRequest.of(0, 20, Sort.by("tenThuoc").ascending());
        List<TonKho> tonKhoList = Collections.singletonList(tonKho);
        Page<TonKho> page = new PageImpl<>(tonKhoList, pageable, 1);
        when(tonKhoRepo.search("Paracetamol", "LOT123", "Pharma", pageable)).thenReturn(page);

        // Thực hiện
        ResponseDTO<PageDTO<List<TonKho>>> ketQua = tonKhoService.search(searchTonKhoDTO);

        // Kiểm tra
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(200, ketQua.getStatus(), "Trạng thái phải là 200");
        assertEquals("Thanh công", ketQua.getMsg(), "Thông điệp phải là 'Thanh công'");
        assertNotNull(ketQua.getData(), "Dữ liệu trả về không được null");
        assertEquals(1, ketQua.getData().getTotalElements(), "Tổng số phần tử phải là 1");
        assertEquals(1, ketQua.getData().getTotalPages(), "Tổng số trang phải là 1");
        assertEquals(tonKhoList, ketQua.getData().getData(), "Dữ liệu tồn kho phải khớp");

        // Xác minh
        verify(tonKhoRepo, times(1)).search("Paracetamol", "LOT123", "Pharma", pageable);
    }

 // TC26
    // Test Case 6: Tìm kiếm thành công với tham số mặc định
    // Mục tiêu: Kiểm tra rằng tìm kiếm sử dụng giá trị mặc định khi tham số null
    @Test
    void kiemTraSearchThanhCongThamSoMacDinh() {
        // Tạo DTO với tất cả các trường null
        SearchTonKhoDTO searchTonKhoDTO = new SearchTonKhoDTO();
        searchTonKhoDTO.setTenThuoc(null);
        searchTonKhoDTO.setSoLo(null);
        searchTonKhoDTO.setTenNhaSanXuat(null);
        searchTonKhoDTO.setCurrentPage(null);
        searchTonKhoDTO.setSize(null);
        searchTonKhoDTO.setSortedField(null);

        // Giá trị mặc định sẽ được áp dụng trong service:
        String expectedTenThuoc = "";
        String expectedSoLo = null;
        String expectedNhaSanXuat = null;
        Pageable expectedPageable = PageRequest.of(0, 20, Sort.by("createdAt").ascending());

        // Giả lập dữ liệu trả về từ repository
        List<TonKho> tonKhoList = Collections.singletonList(tonKho);
        Page<TonKho> page = new PageImpl<>(tonKhoList, expectedPageable, 1);

        // Mock behavior
        when(tonKhoRepo.search(expectedTenThuoc, expectedSoLo, expectedNhaSanXuat, expectedPageable))
            .thenReturn(page);

        // Gọi hàm search
        ResponseDTO<PageDTO<List<TonKho>>> ketQua = tonKhoService.search(searchTonKhoDTO);

        // Kiểm tra kết quả
        assertNotNull(ketQua);
        assertEquals(200, ketQua.getStatus());
        assertEquals("Thanh công", ketQua.getMsg());
        assertNotNull(ketQua.getData());
        assertEquals(1, ketQua.getData().getTotalElements());
        assertEquals(1, ketQua.getData().getTotalPages());
        assertEquals(tonKhoList, ketQua.getData().getData());

        // Kiểm tra gọi repository đúng 1 lần với giá trị mong đợi
        verify(tonKhoRepo, times(1)).search(expectedTenThuoc, expectedSoLo, expectedNhaSanXuat, expectedPageable);
    }

 // TC27
    // Test Case 7: Tìm kiếm với kết quả rỗng
    // Mục tiêu: Kiểm tra rằng tìm kiếm trả về trang rỗng khi không có kết quả
    @Test
    void kiemTraSearchKetQuaRong() {
        // Sắp xếp
        Pageable pageable = PageRequest.of(0, 20, Sort.by("tenThuoc").ascending());
        Page<TonKho> page = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(tonKhoRepo.search("Paracetamol", "LOT123", "Pharma", pageable)).thenReturn(page);

        // Thực hiện
        ResponseDTO<PageDTO<List<TonKho>>> ketQua = tonKhoService.search(searchTonKhoDTO);

        // Kiểm tra
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(200, ketQua.getStatus(), "Trạng thái phải là 200");
        assertEquals("Thanh công", ketQua.getMsg(), "Thông điệp phải là 'Thanh công'");
        assertNotNull(ketQua.getData(), "Dữ liệu trả về không được null");
        assertEquals(0, ketQua.getData().getTotalElements(), "Tổng số phần tử phải là 0");
        assertEquals(0, ketQua.getData().getTotalPages(), "Tổng số trang phải là 0");
        assertTrue(ketQua.getData().getData().isEmpty(), "Dữ liệu tồn kho phải rỗng");

        // Xác minh
        verify(tonKhoRepo, times(1)).search("Paracetamol", "LOT123", "Pharma", pageable);
    }

 // TC28
    // Test Case 8: Đầu vào searchTonKhoDTO null
    // Mục tiêu: Kiểm tra xử lý khi searchTonKhoDTO là null
    @Test
    void kiemTraSearchDauVaoNull() {
        // Sắp xếp
        Pageable pageable = PageRequest.of(0, 20, Sort.by("createdAt").ascending());
        List<TonKho> tonKhoList = Collections.singletonList(tonKho);
        Page<TonKho> page = new PageImpl<>(tonKhoList, pageable, 1);
        when(tonKhoRepo.search("", null, null, pageable)).thenReturn(page);

        // Thực hiện
        ResponseDTO<PageDTO<List<TonKho>>> ketQua = tonKhoService.search(null);

        // Kiểm tra
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(200, ketQua.getStatus(), "Trạng thái phải là 200");
        assertEquals("Thanh công", ketQua.getMsg(), "Thông điệp phải là 'Thanh công'");
        assertNotNull(ketQua.getData(), "Dữ liệu trả về không được null");
        assertEquals(1, ketQua.getData().getTotalElements(), "Tổng số phần tử phải là 1");
        assertEquals(1, ketQua.getData().getTotalPages(), "Tổng số trang phải là 1");
        assertEquals(tonKhoList, ketQua.getData().getData(), "Dữ liệu tồn kho phải khớp");

        // Xác minh
        verify(tonKhoRepo, times(1)).search("", null, null, pageable);
    }
}