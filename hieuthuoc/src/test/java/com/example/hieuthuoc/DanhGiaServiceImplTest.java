package com.example.hieuthuoc;

import com.example.hieuthuoc.dto.DanhGiaDTO;
import com.example.hieuthuoc.dto.PageDTO;
import com.example.hieuthuoc.dto.ResponseDTO;
import com.example.hieuthuoc.dto.SearchDTO;
import com.example.hieuthuoc.entity.DanhGia;
import com.example.hieuthuoc.entity.NguoiDung;
import com.example.hieuthuoc.entity.Thuoc;
import com.example.hieuthuoc.impl.DanhGiaServiceImpl;
import com.example.hieuthuoc.repository.DanhGiaRepo;
import com.example.hieuthuoc.repository.NguoiDungRepo;
import com.example.hieuthuoc.repository.ThuocRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DanhGiaServiceImplTest {

    @InjectMocks
    private DanhGiaServiceImpl danhGiaService;

    @Mock
    private DanhGiaRepo danhGiaRepo;

    @Mock
    private ThuocRepo thuocRepo;

    @Mock
    private NguoiDungRepo nguoiDungRepo;

    @Mock
    private ModelMapper modelMapper;

    private DanhGiaDTO danhGiaDTO;
    private DanhGia danhGia;
    private Thuoc thuoc;
    private NguoiDung nguoiDung;
    private SearchDTO searchDTO;

    @BeforeEach
    void thietLap() {
        // Khởi tạo DanhGiaDTO
        danhGiaDTO = new DanhGiaDTO();
        danhGiaDTO.setId(1);
        danhGiaDTO.setThuocId(1);
        danhGiaDTO.setNguoiDungId(1);
        danhGiaDTO.setDanhGia("Rất tốt");
        danhGiaDTO.setDiemSo(5);

        // Khởi tạo DanhGia
        danhGia = new DanhGia();
        danhGia.setId(1);
        danhGia.setDanhGia("Rất tốt");
        danhGia.setDiemSo(5);

        // Khởi tạo Thuoc
        thuoc = new Thuoc();
        thuoc.setId(1);
        thuoc.setTenThuoc("Paracetamol");

        // Khởi tạo NguoiDung
        nguoiDung = new NguoiDung();
        nguoiDung.setId(1);
        nguoiDung.setHoTen("Nguyen Van A");

        // Khởi tạo SearchDTO
        searchDTO = new SearchDTO();
        searchDTO.setId(1);
        searchDTO.setSortedField("id");
        searchDTO.setCurrentPage(0);
        searchDTO.setSize(20);
        searchDTO.setKeyWord("test");
    }

    // TC44
    // Test Case 1: Lấy danh sách đánh giá thành công với tham số đầy đủ
    // Mục tiêu: Kiểm tra rằng getAll trả về danh sách đánh giá hợp lệ
    @Test
    void kiemTraGetAllThanhCongThamSoDayDu() {
        // Sắp xếp
        Pageable pageable = PageRequest.of(0, 20, Sort.by("id").ascending());
        List<DanhGia> danhGiaList = Collections.singletonList(danhGia);
        Page<DanhGia> page = new PageImpl<>(danhGiaList, pageable, 1);
        when(danhGiaRepo.findByThuocId(1, pageable)).thenReturn(page);

        // Thực hiện
        ResponseDTO<PageDTO<List<DanhGia>>> ketQua = danhGiaService.getAll(searchDTO);

        // Kiểm tra
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(200, ketQua.getStatus(), "Trạng thái phải là 200");
        assertEquals("Thanh công", ketQua.getMsg(), "Thông điệp phải là 'Thanh công'");
        assertNotNull(ketQua.getData(), "Dữ liệu trả về không được null");
        assertEquals(1, ketQua.getData().getTotalElements(), "Tổng số phần tử phải là 1");
        assertEquals(1, ketQua.getData().getTotalPages(), "Tổng số trang phải là 1");
        assertEquals(danhGiaList, ketQua.getData().getData(), "Danh sách đánh giá phải khớp");

        // Xác minh
        verify(danhGiaRepo, times(1)).findByThuocId(1, pageable);
    }
    // TC45
    // Test Case 2: Lấy danh sách đánh giá với tham số mặc định
    // Mục tiêu: Kiểm tra rằng getAll sử dụng giá trị mặc định khi tham số null
    @Test
    void kiemTraGetAllThamSoMacDinh() {
        // Sắp xếp
        searchDTO.setCurrentPage(null);
        searchDTO.setSize(null);
        searchDTO.setKeyWord(null);
        Pageable pageable = PageRequest.of(0, 20, Sort.by("id").ascending());
        List<DanhGia> danhGiaList = Collections.singletonList(danhGia);
        Page<DanhGia> page = new PageImpl<>(danhGiaList, pageable, 1);
        when(danhGiaRepo.findByThuocId(1, pageable)).thenReturn(page);

        // Thực hiện
        ResponseDTO<PageDTO<List<DanhGia>>> ketQua = danhGiaService.getAll(searchDTO);

        // Kiểm tra
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(200, ketQua.getStatus(), "Trạng thái phải là 200");
        assertEquals("Thanh công", ketQua.getMsg(), "Thông điệp phải là 'Thanh công'");
        assertNotNull(ketQua.getData(), "Dữ liệu trả về không được null");
        assertEquals(1, ketQua.getData().getTotalElements(), "Tổng số phần tử phải là 1");
        assertEquals(1, ketQua.getData().getTotalPages(), "Tổng số trang phải là 1");
        assertEquals(danhGiaList, ketQua.getData().getData(), "Danh sách đánh giá phải khớp");

        // Xác minh
        verify(danhGiaRepo, times(1)).findByThuocId(1, pageable);
    }

    // TC46
    // Test Case 3: Lấy danh sách đánh giá rỗng
    // Mục tiêu: Kiểm tra rằng getAll trả về trang rỗng khi không có kết quả
    @Test
    void kiemTraGetAllKetQuaRong() {
        // Sắp xếp
        Pageable pageable = PageRequest.of(0, 20, Sort.by("id").ascending());
        Page<DanhGia> page = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(danhGiaRepo.findByThuocId(1, pageable)).thenReturn(page);

        // Thực hiện
        ResponseDTO<PageDTO<List<DanhGia>>> ketQua = danhGiaService.getAll(searchDTO);

        // Kiểm tra
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(200, ketQua.getStatus(), "Trạng thái phải là 200");
        assertEquals("Thanh công", ketQua.getMsg(), "Thông điệp phải là 'Thanh công'");
        assertNotNull(ketQua.getData(), "Dữ liệu trả về không được null");
        assertEquals(0, ketQua.getData().getTotalElements(), "Tổng số phần tử phải là 0");
        assertEquals(0, ketQua.getData().getTotalPages(), "Tổng số trang phải là 0");
        assertTrue(ketQua.getData().getData().isEmpty(), "Danh sách đánh giá phải rỗng");

        // Xác minh
        verify(danhGiaRepo, times(1)).findByThuocId(1, pageable);
    }

    // TC47
    // Test Case 4: Lấy danh sách với đầu vào searchDTO null
    // Mục tiêu: Kiểm tra xử lý khi searchDTO là null
//    @Test
//    void kiemTraGetAllDauVaoNull() {
//        // Sắp xếp
//        Pageable pageable = PageRequest.of(0, 20, Sort.by("id").ascending());
//        List<DanhGia> danhGiaList = Collections.singletonList(danhGia);
//        Page<DanhGia> page = new PageImpl<>(danhGiaList, pageable, 1);
//        when(danhGiaRepo.findByThuocId(null, pageable)).thenReturn(page);
//
//        // Thực hiện
//        ResponseDTO<PageDTO<List<DanhGia>>> ketQua = danhGiaService.getAll(null);
//
//        // Kiểm tra
//        assertNotNull(ketQua, "Kết quả không được null");
//        assertEquals(200, ketQua.getStatus(), "Trạng thái phải là 200");
//        assertEquals("Thanh công", ketQua.getMsg(), "Thông điệp phải là 'Thanh công'");
//        assertNotNull(ketQua.getData(), "Dữ liệu trả về không được null");
//        assertEquals(1, ketQua.getData().getTotalElements(), "Tổng số phần tử phải là 1");
//        assertEquals(1, ketQua.getData().getTotalPages(), "Tổng số trang phải là 1");
//        assertEquals(danhGiaList, ketQua.getData().getData(), "Danh sách đánh giá phải khớp");
//
//        // Xác minh
//        verify(danhGiaRepo, times(1)).findByThuocId(null, pageable);
//    }

    
    // TC48
    // Test Case 5: Tạo đánh giá thành công (không có danhGiaGoc)
    // Mục tiêu: Kiểm tra rằng đánh giá được tạo khi đầu vào hợp lệ
    @Test
    void kiemTraCreateThanhCongKhongCoDanhGiaGoc() {
        // Sắp xếp
        when(modelMapper.map(danhGiaDTO, DanhGia.class)).thenReturn(danhGia);
        when(thuocRepo.findById(1)).thenReturn(Optional.of(thuoc));
        when(nguoiDungRepo.findById(1)).thenReturn(Optional.of(nguoiDung));
        when(danhGiaRepo.save(any(DanhGia.class))).thenReturn(danhGia);

        // Thực hiện
        ResponseDTO<DanhGia> ketQua = danhGiaService.create(danhGiaDTO);

        // Kiểm tra
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(200, ketQua.getStatus(), "Trạng thái phải là 200");
        assertEquals("Thanh công", ketQua.getMsg(), "Thông điệp phải là 'Thanh công'");
        assertEquals(danhGia, ketQua.getData(), "Dữ liệu đánh giá phải khớp");
        assertEquals(thuoc, danhGia.getThuoc(), "Thuốc phải được gán đúng");
        assertEquals(nguoiDung, danhGia.getNguoiDung(), "Người dùng phải được gán đúng");
        assertNull(danhGia.getDanhGiaGoc(), "Đánh giá gốc phải là null");

        // Xác minh
        verify(modelMapper, times(1)).map(danhGiaDTO, DanhGia.class);
        verify(thuocRepo, times(1)).findById(1);
        verify(nguoiDungRepo, times(1)).findById(1);
        verify(danhGiaRepo, never()).findById(anyInt());
        verify(danhGiaRepo, times(1)).save(danhGia);
    }

    
    // TC49
    // Test Case 6: Tạo đánh giá thành công (có danhGiaGoc)
    // Mục tiêu: Kiểm tra rằng đánh giá được tạo khi có đánh giá gốc
    @Test
    void kiemTraCreateThanhCongCoDanhGiaGoc() {
        // Sắp xếp
        danhGiaDTO.setDanhGiaGocId(2);
        DanhGia danhGiaGoc = new DanhGia();
        danhGiaGoc.setId(2);
        when(modelMapper.map(danhGiaDTO, DanhGia.class)).thenReturn(danhGia);
        when(thuocRepo.findById(1)).thenReturn(Optional.of(thuoc));
        when(nguoiDungRepo.findById(1)).thenReturn(Optional.of(nguoiDung));
        when(danhGiaRepo.findById(2)).thenReturn(Optional.of(danhGiaGoc));
        when(danhGiaRepo.save(any(DanhGia.class))).thenReturn(danhGia);

        // Thực hiện
        ResponseDTO<DanhGia> ketQua = danhGiaService.create(danhGiaDTO);

        // Kiểm tra
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(200, ketQua.getStatus(), "Trạng thái phải là 200");
        assertEquals("Thanh công", ketQua.getMsg(), "Thông điệp phải là 'Thanh công'");
        assertEquals(danhGia, ketQua.getData(), "Dữ liệu đánh giá phải khớp");
        assertEquals(thuoc, danhGia.getThuoc(), "Thuốc phải được gán đúng");
        assertEquals(nguoiDung, danhGia.getNguoiDung(), "Người dùng phải được gán đúng");
        assertEquals(danhGiaGoc, danhGia.getDanhGiaGoc(), "Đánh giá gốc phải được gán đúng");

        // Xác minh
        verify(modelMapper, times(1)).map(danhGiaDTO, DanhGia.class);
        verify(thuocRepo, times(1)).findById(1);
        verify(nguoiDungRepo, times(1)).findById(1);
        verify(danhGiaRepo, times(1)).findById(2);
        verify(danhGiaRepo, times(1)).save(danhGia);
    }

    
    // TC50
    // Test Case 7: Thất bại khi thuốc không tồn tại
    // Mục tiêu: Kiểm tra xử lý khi không tìm thấy thuốc
    @Test
    void kiemTraCreateThatBaiThuocKhongTonTai() {
        // Sắp xếp
        when(modelMapper.map(danhGiaDTO, DanhGia.class)).thenReturn(danhGia);
        when(thuocRepo.findById(1)).thenReturn(Optional.empty());

        // Thực hiện & Kiểm tra
        assertThrows(RuntimeException.class,
                () -> danhGiaService.create(danhGiaDTO),
                "Mong đợi RuntimeException khi thuốc không tồn tại");

        // Xác minh
        verify(modelMapper, times(1)).map(danhGiaDTO, DanhGia.class);
        verify(thuocRepo, times(1)).findById(1);
        verify(nguoiDungRepo, never()).findById(anyInt());
        verify(danhGiaRepo, never()).findById(anyInt());
        verify(danhGiaRepo, never()).save(any(DanhGia.class));
    }

    
    // TC51
    // Test Case 8: Thất bại khi người dùng không tồn tại
    // Mục tiêu: Kiểm tra xử lý khi không tìm thấy người dùng
    @Test
    void kiemTraCreateThatBaiNguoiDungKhongTonTai() {
        // Sắp xếp
        when(modelMapper.map(danhGiaDTO, DanhGia.class)).thenReturn(danhGia);
        when(thuocRepo.findById(1)).thenReturn(Optional.of(thuoc));
        when(nguoiDungRepo.findById(1)).thenReturn(Optional.empty());

        // Thực hiện & Kiểm tra
        assertThrows(RuntimeException.class,
                () -> danhGiaService.create(danhGiaDTO),
                "Mong đợi RuntimeException khi người dùng không tồn tại");

        // Xác minh
        verify(modelMapper, times(1)).map(danhGiaDTO, DanhGia.class);
        verify(thuocRepo, times(1)).findById(1);
        verify(nguoiDungRepo, times(1)).findById(1);
        verify(danhGiaRepo, never()).findById(anyInt());
        verify(danhGiaRepo, never()).save(any(DanhGia.class));
    }

    // TC52
    // Test Case 9: Thất bại khi đánh giá gốc không tồn tại
    // Mục tiêu: Kiểm tra xử lý khi không tìm thấy đánh giá gốc
    @Test
    void kiemTraCreateThatBaiDanhGiaGocKhongTonTai() {
        // Sắp xếp
        danhGiaDTO.setDanhGiaGocId(2);
        when(modelMapper.map(danhGiaDTO, DanhGia.class)).thenReturn(danhGia);
        when(thuocRepo.findById(1)).thenReturn(Optional.of(thuoc));
        when(nguoiDungRepo.findById(1)).thenReturn(Optional.of(nguoiDung));
        when(danhGiaRepo.findById(2)).thenReturn(Optional.empty());

        // Thực hiện & Kiểm tra
        assertThrows(RuntimeException.class,
                () -> danhGiaService.create(danhGiaDTO),
                "Mong đợi RuntimeException khi đánh giá gốc không tồn tại");

        // Xác minh
        verify(modelMapper, times(1)).map(danhGiaDTO, DanhGia.class);
        verify(thuocRepo, times(1)).findById(1);
        verify(nguoiDungRepo, times(1)).findById(1);
        verify(danhGiaRepo, times(1)).findById(2);
        verify(danhGiaRepo, never()).save(any(DanhGia.class));
    }

    
    // TC53
    // Test Case 10: Tạo với đầu vào null
    // Mục tiêu: Kiểm tra xử lý khi danhGiaDTO là null
    @Test
    void kiemTraCreateDauVaoNull() {
        // Sắp xếp
        when(modelMapper.map(null, DanhGia.class)).thenThrow(new IllegalArgumentException("DTO không hợp lệ"));

        // Thực hiện & Kiểm tra
        assertThrows(IllegalArgumentException.class,
                () -> danhGiaService.create(null),
                "Mong đợi IllegalArgumentException khi DTO null");

        // Xác minh
        verify(modelMapper, times(1)).map(null, DanhGia.class);
        verify(thuocRepo, never()).findById(anyInt());
        verify(nguoiDungRepo, never()).findById(anyInt());
        verify(danhGiaRepo, never()).findById(anyInt());
        verify(danhGiaRepo, never()).save(any(DanhGia.class));
    }

    // TC54
    // Test Case 11: Cập nhật đánh giá thành công
    // Mục tiêu: Kiểm tra rằng đánh giá được cập nhật thành công
    @Test
    void kiemTraUpdateThanhCong() {
        // Sắp xếp
        when(danhGiaRepo.findById(1)).thenReturn(Optional.of(danhGia));
        when(danhGiaRepo.save(any(DanhGia.class))).thenReturn(danhGia);

        // Thực hiện
        ResponseDTO<DanhGia> ketQua = danhGiaService.update(danhGiaDTO);

        // Kiểm tra
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(200, ketQua.getStatus(), "Trạng thái phải là 200");
        assertEquals("Thanh công", ketQua.getMsg(), "Thông điệp phải là 'Thanh công'");
        assertEquals(danhGia, ketQua.getData(), "Dữ liệu đánh giá phải khớp");
        assertEquals("Rất tốt", danhGia.getDanhGia(), "Nội dung đánh giá phải được cập nhật");
        assertEquals(5, danhGia.getDiemSo(), "Điểm số phải được cập nhật");

        // Xác minh
        verify(danhGiaRepo, times(1)).findById(1);
        verify(danhGiaRepo, times(1)).save(danhGia);
    }

    
    // TC55
    // Test Case 12: Thất bại khi đánh giá không tồn tại
    // Mục tiêu: Kiểm tra xử lý khi không tìm thấy đánh giá
    @Test
    void kiemTraUpdateThatBaiKhongTonTai() {
        // Sắp xếp
        when(danhGiaRepo.findById(1)).thenReturn(Optional.empty());

        // Thực hiện
        ResponseDTO<DanhGia> ketQua = danhGiaService.update(danhGiaDTO);

        // Kiểm tra
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(409, ketQua.getStatus(), "Trạng thái phải là 409");
        assertEquals("Không tồn tại đánh giá", ketQua.getMsg(), "Thông điệp phải là 'Không tồn tại đánh giá'");
        assertNull(ketQua.getData(), "Dữ liệu trả về phải là null");

        // Xác minh
        verify(danhGiaRepo, times(1)).findById(1);
        verify(danhGiaRepo, never()).save(any(DanhGia.class));
    }

    
    // TC56
    // Test Case 13: Cập nhật với đầu vào null
    // Mục tiêu: Kiểm tra xử lý khi danhGiaDTO là null
    @Test
    void kiemTraUpdateDauVaoNull() {
        // Thực hiện
        ResponseDTO<DanhGia> ketQua = danhGiaService.update(null);

        // Kiểm tra
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(409, ketQua.getStatus(), "Trạng thái phải là 409");
        assertEquals("Không tồn tại đánh giá", ketQua.getMsg(), "Thông điệp phải là 'Không tồn tại đánh giá'");
        assertNull(ketQua.getData(), "Dữ liệu trả về phải là null");

        // Xác minh
        verify(danhGiaRepo, never()).findById(anyInt());
        verify(danhGiaRepo, never()).save(any(DanhGia.class));
    }

    
    // TC57
    // Test Case 14: Xóa đánh giá thành công
    // Mục tiêu: Kiểm tra rằng đánh giá được xóa thành công
    @Test
    void kiemTraDeleteThanhCong() {
        // Sắp xếp
        doNothing().when(danhGiaRepo).deleteById(1);

        // Thực hiện
        ResponseDTO<Void> ketQua = danhGiaService.delete(1);

        // Kiểm tra
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(200, ketQua.getStatus(), "Trạng thái phải là 200");
        assertEquals("Thanh công", ketQua.getMsg(), "Thông điệp phải là 'Thanh công'");
        assertNull(ketQua.getData(), "Dữ liệu trả về phải là null");

        // Xác minh
        verify(danhGiaRepo, times(1)).deleteById(1);
    }

    
    // TC58
    // Test Case 15: Xóa với ID không tồn tại
    // Mục tiêu: Kiểm tra xử lý khi ID không tồn tại
    @Test
    void kiemTraDeleteIDKhongTonTai() {
        // Sắp xếp
        int idKhongTonTai = 1;
        doThrow(new EmptyResultDataAccessException(1)).when(danhGiaRepo).deleteById(idKhongTonTai);

        // Thực hiện
        ResponseDTO<Void> ketQua = danhGiaService.delete(idKhongTonTai);

        // Kiểm tra
        assertNotNull(ketQua, "Kết quả không được null");
        assertEquals(404, ketQua.getStatus(), "Trạng thái phải là 404 khi ID không tồn tại");
        assertEquals("Không tìm thấy đánh giá cần xoá", ketQua.getMsg(), "Thông điệp phù hợp");
        assertNull(ketQua.getData(), "Dữ liệu trả về phải là null");

        // Xác minh
        verify(danhGiaRepo, times(1)).deleteById(idKhongTonTai);
    }
}