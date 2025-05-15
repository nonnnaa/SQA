package com.example.hieuthuoc.service;

import com.example.hieuthuoc.dto.*;
import com.example.hieuthuoc.entity.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;

import com.example.hieuthuoc.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import java.util.Arrays;
import java.util.Optional;

import org.mockito.InjectMocks;
import org.mockito.Mock;

@ExtendWith(MockitoExtension.class)
public class PhieuNhapServiceTest {

    @InjectMocks
    private PhieuNhapServiceImpl phieuNhapService;

    @Mock
    private PhieuNhapRepo phieuNhapRepo;

    @Mock
    private NguoiDungRepo nguoiDungRepo;

    @Mock
    private NhaCungCapRepo nhaCungCapRepo;

    @Mock
    private TonKhoRepo tonKhoRepo;

    @Mock
    private ThuocRepo thuocRepo;

    @Mock
    private ModelMapper modelMapper;


    @Test
    void testSearchPhieuNhap_success() {
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setKeyWord("Công ty A");
        searchDTO.setCurrentPage(0);
        searchDTO.setSize(2);

        List<PhieuNhap> mockList = Arrays.asList(new PhieuNhap(), new PhieuNhap());
        Page<PhieuNhap> mockPage = new PageImpl<>(mockList, PageRequest.of(0, 2), 2);

        when(phieuNhapRepo.findByNhaCungCapTen(eq("Công ty A"), any(PageRequest.class))).thenReturn(mockPage);

        ResponseDTO<PageDTO<List<PhieuNhap>>> response = phieuNhapService.search(searchDTO);

        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertEquals(2, response.getData().getData().size());
        assertEquals(2, response.getData().getTotalElements());
    }

    @Test
    void testSearchPhieuNhap_EmptyKeyword() {
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setKeyWord(""); // Empty keyword
        searchDTO.setCurrentPage(0);
        searchDTO.setSize(2);
        searchDTO.setSortedField("createdAt");

        List<PhieuNhap> mockList = Arrays.asList(new PhieuNhap(), new PhieuNhap());
        Page<PhieuNhap> mockPage = new PageImpl<>(mockList, PageRequest.of(0, 2), 2);

        when(phieuNhapRepo.findAll(any(PageRequest.class))).thenReturn(mockPage);

        ResponseDTO<PageDTO<List<PhieuNhap>>> response = phieuNhapService.search(searchDTO);

        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertEquals(2, response.getData().getData().size());
        assertEquals(2, response.getData().getTotalElements());
    }

    @Test
    void testSearchPhieuNhap_DefaultPaging() {
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setKeyWord(null); // Không có keyword
        searchDTO.setCurrentPage(null); // Mặc định
        searchDTO.setSize(null);        // Mặc định
        searchDTO.setSortedField(null); // Mặc định

        List<PhieuNhap> mockList = Arrays.asList(new PhieuNhap(), new PhieuNhap());
        Page<PhieuNhap> mockPage = new PageImpl<>(mockList, PageRequest.of(0, 20), 2);

        when(phieuNhapRepo.findAll(any(PageRequest.class))).thenReturn(mockPage);

        ResponseDTO<PageDTO<List<PhieuNhap>>> response = phieuNhapService.search(searchDTO);

        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertEquals(2, response.getData().getData().size());
        assertEquals(2, response.getData().getTotalElements());
    }


    @Test
    void testGetById_success() {
        // Arrange
        PhieuNhap phieuNhap = new PhieuNhap();
        phieuNhap.setId(10);

        when(phieuNhapRepo.findById(10)).thenReturn(Optional.of(phieuNhap));

        // Act
        ResponseDTO<PhieuNhap> response = phieuNhapService.getById(10);

        // Assert
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertNotNull(response.getData());
        assertEquals(10, response.getData().getId());
    }

    @Test
    void testGetById_NotFound() {
        // Arrange
        when(phieuNhapRepo.findById(999)).thenReturn(Optional.empty());

        // Act
        ResponseDTO<PhieuNhap> response = phieuNhapService.getById(999);

        // Assert
        assertEquals(409, response.getStatus());
        assertEquals("Không tìm thấy phiếu nhập", response.getMsg());
        assertNull(response.getData());
    }

    @Test
    void testCreatePhieuNhap_Success() {
        // Setup DTO
        PhieuNhapDTO dto = new PhieuNhapDTO();
        dto.setNguoiDungId(1);
        dto.setNhaCungCapId(2);

        ChiTietPhieuNhapDTO ct = new ChiTietPhieuNhapDTO();
        ct.setThuocId(5);
        ct.setSoLuong(10);
        ct.setDonGia(5000.0);
        dto.setChiTietPhieuNhaps(List.of(ct));

        // Mocks cho ModelMapper
        PhieuNhap phieuNhap = new PhieuNhap();
        when(modelMapper.map(dto, PhieuNhap.class)).thenReturn(phieuNhap);

        ChiTietPhieuNhap ctEntity = new ChiTietPhieuNhap();
        ctEntity.setSoLuong(10);
        ctEntity.setDonGia(5000.0);
        when(modelMapper.map(ct, ChiTietPhieuNhap.class)).thenReturn(ctEntity);

        // Mocks cho repo tìm kiếm NguoiDung & NhaCungCap
        when(nguoiDungRepo.findById(1)).thenReturn(Optional.of(new NguoiDung()));
        when(nhaCungCapRepo.findById(2)).thenReturn(Optional.of(new NhaCungCap()));

        // Mocks cho repo tìm Thuoc, bao gồm soLuongTon để tránh NPE
        Thuoc thuoc = new Thuoc();
        thuoc.setId(5);
        thuoc.setSoLuongTon(100);                          // <-- Khởi gán tồn kho ban đầu
        when(thuocRepo.findById(5)).thenReturn(Optional.of(thuoc));

        // Mocks cho TonKhoRepo.save(...) trong createTonKhoFromPhieuNhap(...)
        when(tonKhoRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Mocks cho lưu PhieuNhap
        when(phieuNhapRepo.save(any())).thenAnswer(inv -> {
            PhieuNhap pn = inv.getArgument(0);
            pn.setId(123);
            return pn;
        });

        // Act
        ResponseDTO<PhieuNhap> response = phieuNhapService.create(dto);

        // Assert
        assertEquals(200, response.getStatus());
        assertEquals("ok", response.getMsg());
        assertNotNull(response.getData());
        // Tổng tiền = soLuong * donGia = 10 * 5000 = 50000
        assertEquals(50000.0, response.getData().getTongTien());
    }

    @Test
    void testCreatePhieuNhap_ThuocNotFound() {
        PhieuNhapDTO dto = new PhieuNhapDTO();
        ChiTietPhieuNhapDTO ct = new ChiTietPhieuNhapDTO();
        ct.setThuocId(999);
        ct.setSoLuong(1);
        ct.setDonGia(100.0);
        dto.setChiTietPhieuNhaps(List.of(ct));

        when(modelMapper.map(dto, PhieuNhap.class)).thenReturn(new PhieuNhap());
        when(thuocRepo.findById(999)).thenReturn(Optional.empty());

        // Assert exception
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            phieuNhapService.create(dto);
        });
        assertEquals("Thuốc không tồn tại", thrown.getMessage());
    }

    @Test
    void testCreatePhieuNhap_NguoiDungIdNotFound() {
        // Setup DTO
        PhieuNhapDTO dto = new PhieuNhapDTO();
        dto.setNguoiDungId(100);  // Không tồn tại
        dto.setNhaCungCapId(2);

        ChiTietPhieuNhapDTO ct = new ChiTietPhieuNhapDTO();
        ct.setThuocId(5);
        ct.setSoLuong(10);
        ct.setDonGia(5000.0);
        dto.setChiTietPhieuNhaps(List.of(ct));

        // Mock repo: người dùng không tồn tại
        when(nguoiDungRepo.findById(100)).thenReturn(Optional.empty());
        when(nhaCungCapRepo.findById(2)).thenReturn(Optional.of(new NhaCungCap()));

        // Mock thuoc
        Thuoc thuoc = new Thuoc();
        thuoc.setSoLuongTon(50);
        when(thuocRepo.findById(5)).thenReturn(Optional.of(thuoc));

        // ✅ Mock modelMapper
        PhieuNhap phieuNhap = new PhieuNhap();
        when(modelMapper.map(dto, PhieuNhap.class)).thenReturn(phieuNhap);

        ChiTietPhieuNhap chiTietPhieuNhap = new ChiTietPhieuNhap();
        chiTietPhieuNhap.setSoLuong(10);
        chiTietPhieuNhap.setDonGia(5000.0);
        when(modelMapper.map(ct, ChiTietPhieuNhap.class)).thenReturn(chiTietPhieuNhap);

        // Mock save
        when(phieuNhapRepo.save(any())).thenAnswer(inv -> {
            PhieuNhap p = inv.getArgument(0);
            p.setId(123);
            return p;
        });
        when(tonKhoRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        ResponseDTO<PhieuNhap> response = phieuNhapService.create(dto);

        // Assert
        assertEquals(404, response.getStatus());
        assertEquals("Người dùng không tồn tại", response.getMsg());
        assertNull(response.getData());
    }

    @Test
    void testCreatePhieuNhap_NhaCungCapIdNotFound() {
        // Setup DTO
        PhieuNhapDTO dto = new PhieuNhapDTO();
        dto.setNguoiDungId(1);      // ID người dùng hợp lệ
        dto.setNhaCungCapId(99);    // ID nhà cung cấp không tồn tại

        ChiTietPhieuNhapDTO ct = new ChiTietPhieuNhapDTO();
        ct.setThuocId(5);
        ct.setSoLuong(5);
        ct.setDonGia(1000.0);
        dto.setChiTietPhieuNhaps(List.of(ct));

        // Mock dữ liệu
        when(nguoiDungRepo.findById(1)).thenReturn(Optional.of(new NguoiDung()));
        when(nhaCungCapRepo.findById(99)).thenReturn(Optional.empty()); // Không tìm thấy nhà cung cấp

        Thuoc thuoc = new Thuoc();
        thuoc.setSoLuongTon(10);
        when(thuocRepo.findById(5)).thenReturn(Optional.of(thuoc));

        // ✅ Mock modelMapper.map(dto, PhieuNhap.class)
        PhieuNhap phieuNhap = new PhieuNhap();
        when(modelMapper.map(dto, PhieuNhap.class)).thenReturn(phieuNhap);

        // ✅ Mock modelMapper.map(ct, ChiTietPhieuNhap.class)
        ChiTietPhieuNhap chiTietPhieuNhap = new ChiTietPhieuNhap();
        chiTietPhieuNhap.setSoLuong(5);
        chiTietPhieuNhap.setDonGia(1000.0);
        when(modelMapper.map(ct, ChiTietPhieuNhap.class)).thenReturn(chiTietPhieuNhap);

        // Act
        ResponseDTO<PhieuNhap> response = phieuNhapService.create(dto);

        // Assert
        assertEquals(404, response.getStatus());
        assertEquals("Nhà cung cấp không tồn tại", response.getMsg());
        assertNull(response.getData());
    }

    @Test
    void testUpdatePhieuNhap_Success() {
        // Setup DTO
        PhieuNhapDTO dto = new PhieuNhapDTO();
        dto.setId(10);

        ChiTietPhieuNhapDTO ctDTO = new ChiTietPhieuNhapDTO();
        ctDTO.setThuocId(5);
        ctDTO.setSoLuong(3);
        ctDTO.setDonGia(10000.0);
        dto.setChiTietPhieuNhaps(List.of(ctDTO));

        PhieuNhap mappedPhieuNhap = new PhieuNhap();
        mappedPhieuNhap.setId(10);

        PhieuNhap existingPhieuNhap = new PhieuNhap();
        existingPhieuNhap.setId(10);

        ChiTietPhieuNhap mappedChiTiet = new ChiTietPhieuNhap();
        mappedChiTiet.setDonGia(10000.0);
        mappedChiTiet.setSoLuong(3);

        Thuoc thuoc = new Thuoc();
        thuoc.setId(5);
        thuoc.setSoLuongTon(20);  // Đảm bảo có số lượng tồn

        // Mocking
        when(modelMapper.map(dto, PhieuNhap.class)).thenReturn(mappedPhieuNhap);
        when(phieuNhapRepo.findById(10)).thenReturn(Optional.of(existingPhieuNhap));
        when(modelMapper.map(ctDTO, ChiTietPhieuNhap.class)).thenReturn(mappedChiTiet);
        when(thuocRepo.findById(5)).thenReturn(Optional.of(thuoc));
        when(phieuNhapRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ResponseDTO<PhieuNhap> response = phieuNhapService.update(dto);

        // Assert
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertNotNull(response.getData());
        assertEquals(10, response.getData().getId());
        assertEquals(30000.0, response.getData().getTongTien());  // Đảm bảo tính tổng tiền chính xác
    }

    @Test
    void testUpdatePhieuNhap_ThuocNotFound() {
        // Arrange
        int thuocId = 999; // ID mà bạn mong muốn không có trong DB
        int phieuNhapId = 1;

        PhieuNhapDTO phieuNhapDTO = new PhieuNhapDTO();
        phieuNhapDTO.setId(phieuNhapId);

        ChiTietPhieuNhapDTO chiTietPhieuNhapDTO = new ChiTietPhieuNhapDTO();
        chiTietPhieuNhapDTO.setThuocId(thuocId); // Thuốc không tồn tại
        chiTietPhieuNhapDTO.setSoLuong(10);
        chiTietPhieuNhapDTO.setDonGia(10000.0);
        phieuNhapDTO.setChiTietPhieuNhaps(List.of(chiTietPhieuNhapDTO));

        PhieuNhap mappedPhieuNhap = new PhieuNhap();
        mappedPhieuNhap.setId(phieuNhapId);

        // Mocking
        when(modelMapper.map(phieuNhapDTO, PhieuNhap.class)).thenReturn(mappedPhieuNhap);
        when(phieuNhapRepo.findById(phieuNhapId)).thenReturn(Optional.of(mappedPhieuNhap));

        // Mock ThuocRepo trả về Optional.empty() (Thuốc không tồn tại)
        when(thuocRepo.findById(thuocId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            phieuNhapService.update(phieuNhapDTO);
        });

        // Kiểm tra thông báo lỗi
        assertTrue(exception.getMessage().contains("Thuốc có Id = " + thuocId + " không tồn tại"));
    }


    @Test
    void testupdatePhieuNhap_ChiTietPhieuNhapIsNull() {
        // Arrange
        int id = 1;
        PhieuNhapDTO phieuNhapDTO = new PhieuNhapDTO();
        phieuNhapDTO.setId(id);
        phieuNhapDTO.setChiTietPhieuNhaps(null); // Trường hợp đang test

        PhieuNhap mappedPhieuNhap = new PhieuNhap();
        mappedPhieuNhap.setId(id);

        when(modelMapper.map(phieuNhapDTO, PhieuNhap.class)).thenReturn(mappedPhieuNhap);
        when(phieuNhapRepo.findById(id)).thenReturn(Optional.empty());

        // Act
        ResponseDTO<PhieuNhap> response = phieuNhapService.update(phieuNhapDTO);

        // Assert
        assertEquals(400, response.getStatus());
        assertEquals("Phiếu nhập không có dữ liệu", response.getMsg());
        assertNull(response.getData());
    }

    @Test
    void testUpdatePhieuNhap_NotFound() {
        // Given: DTO đầu vào có ID
        PhieuNhapDTO dto = new PhieuNhapDTO();
        dto.setId(10);

        // Mock để tránh NullPointerException
        PhieuNhap mappedPhieuNhap = new PhieuNhap();
        mappedPhieuNhap.setId(10); // rất quan trọng, để tránh lỗi khi gọi getId()

        // Mock mapping từ DTO -> Entity
        when(modelMapper.map(dto, PhieuNhap.class)).thenReturn(mappedPhieuNhap);

        // Mô phỏng không tìm thấy phiếu nhập trong DB
        when(phieuNhapRepo.findById(10)).thenReturn(Optional.empty());

        // When
        ResponseDTO<PhieuNhap> response = phieuNhapService.update(dto);

        // Then
        assertEquals(200, response.getStatus()); // Vì logic code trả về status 200 dù không tìm thấy
        assertEquals("Không tìm thấy phiếu nhập", response.getMsg());
        assertNull(response.getData()); // Không có data vì không tìm thấy
    }

    @Test
    void testDeletePhieuNhap_Success() {
        // Arrange
        int id = 10;

        // Mocking repository để giả lập dữ liệu
        doNothing().when(phieuNhapRepo).deleteById(id);  // Không làm gì khi gọi deleteById

        // Act
        ResponseDTO<Void> response = phieuNhapService.delete(id);

        // Assert
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
    }
}
