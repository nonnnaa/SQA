package com.example.hieuthuoc.service;

import com.example.hieuthuoc.dto.DanhMucThuocDTO;
import com.example.hieuthuoc.dto.ResponseDTO;
import com.example.hieuthuoc.entity.DanhMucThuoc;
import com.example.hieuthuoc.entity.LoaiThuoc;
import com.example.hieuthuoc.repository.DanhMucThuocRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestDanhMucThuocService {

    @Mock
    private DanhMucThuocRepo danhMucThuocRepo;

    @InjectMocks
    private DanhMucThuocServiceImpl danhMucThuocService;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    private DanhMucThuoc danhMucThuoc;
    private DanhMucThuocDTO danhMucThuocDTO;
    private List<DanhMucThuoc> danhMucThuocList;

    @BeforeEach
    public void setup() {
        // Set up test data
        danhMucThuoc = new DanhMucThuoc();
        danhMucThuoc.setId(1);
        danhMucThuoc.setTenDanhMuc("Thuốc kháng sinh");
        danhMucThuoc.setMoTa("Các loại thuốc kháng sinh");
        danhMucThuoc.setLoaiThuocs(new ArrayList<>());

        danhMucThuocDTO = new DanhMucThuocDTO();
        danhMucThuocDTO.setId(1);
        danhMucThuocDTO.setTenDanhMuc("Thuốc kháng sinh");
        danhMucThuocDTO.setMoTa("Các loại thuốc kháng sinh");

        danhMucThuocList = Arrays.asList(danhMucThuoc);
    }

    @Test
    public void testGetAll() {
        // Given
        when(danhMucThuocRepo.findAll()).thenReturn(danhMucThuocList);

        // When
        ResponseDTO<List<DanhMucThuoc>> response = danhMucThuocService.getAll();

        // Then
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertEquals(danhMucThuocList, response.getData());
        verify(danhMucThuocRepo, times(1)).findAll();
    }

    @Test
    public void testGetDanhMucAnhLoaiThuoc() {
        // Given
        DanhMucThuoc danhMucWithLoaiThuoc = new DanhMucThuoc();
        danhMucWithLoaiThuoc.setId(1);
        danhMucWithLoaiThuoc.setTenDanhMuc("Thuốc kháng sinh");

        List<LoaiThuoc> loaiThuocList = new ArrayList<>();
        LoaiThuoc loaiThuoc = new LoaiThuoc();
        loaiThuoc.setId(1);
        loaiThuocList.add(loaiThuoc);

        danhMucWithLoaiThuoc.setLoaiThuocs(loaiThuocList);

        List<DanhMucThuoc> danhMucWithLoaiThuocList = Arrays.asList(danhMucWithLoaiThuoc);

        when(danhMucThuocRepo.findAllWithLoaiThuocs()).thenReturn(danhMucWithLoaiThuocList);

        // When
        ResponseDTO<List<DanhMucThuoc>> response = danhMucThuocService.getDanhMucAnhLoaiThuoc();

        // Then
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertEquals(danhMucWithLoaiThuocList, response.getData());
        verify(danhMucThuocRepo, times(1)).findAllWithLoaiThuocs();
    }

    @Test
    public void testSearchByTenDanhMucSuccess() {
        // Given
        String searchTerm = "kháng sinh";
        when(danhMucThuocRepo.searchByTenDanhMuc(searchTerm)).thenReturn(danhMucThuocList);

        // When
        ResponseDTO<List<DanhMucThuoc>> response = danhMucThuocService.searchByTenDanhMuc(searchTerm);

        // Then
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertEquals(danhMucThuocList, response.getData());
        verify(danhMucThuocRepo, times(1)).searchByTenDanhMuc(searchTerm);
    }

    @Test
    public void testSearchByTenDanhMucNoResults() {
        // Given
        String searchTerm = "không tồn tại";
        when(danhMucThuocRepo.searchByTenDanhMuc(searchTerm)).thenReturn(new ArrayList<>());

        // When
        ResponseDTO<List<DanhMucThuoc>> response = danhMucThuocService.searchByTenDanhMuc(searchTerm);

        // Then
        assertEquals(409, response.getStatus());
        assertEquals("Danh mục thuốc không tồn tại", response.getMsg());
        assertNull(response.getData());
        verify(danhMucThuocRepo, times(1)).searchByTenDanhMuc(searchTerm);
    }

    @Test
    public void testCreateSuccess() {
        // Given
        when(danhMucThuocRepo.existsByTenDanhMuc(anyString())).thenReturn(false);
        when(danhMucThuocRepo.save(any(DanhMucThuoc.class))).thenReturn(danhMucThuoc);

        // When
        ResponseDTO<DanhMucThuoc> response = danhMucThuocService.create(danhMucThuocDTO);

        // Then
        assertEquals(201, response.getStatus());
        assertEquals("Tạo danh mục thuốc thành công", response.getMsg());
        assertEquals(danhMucThuoc, response.getData());
        verify(danhMucThuocRepo, times(1)).existsByTenDanhMuc(anyString());
        verify(danhMucThuocRepo, times(1)).save(any(DanhMucThuoc.class));
    }

    @Test
    public void testCreateAlreadyExists() {
        // Given
        when(danhMucThuocRepo.existsByTenDanhMuc(anyString())).thenReturn(true);

        // When
        ResponseDTO<DanhMucThuoc> response = danhMucThuocService.create(danhMucThuocDTO);

        // Then
        assertEquals(409, response.getStatus());
        assertEquals("Danh mục thuốc đã tồn tại", response.getMsg());
        assertNull(response.getData());
        verify(danhMucThuocRepo, times(1)).existsByTenDanhMuc(anyString());
        verify(danhMucThuocRepo, never()).save(any(DanhMucThuoc.class));
    }

    @Test
    public void testUpdateSuccess() {
        // Given
        when(danhMucThuocRepo.findById(anyInt())).thenReturn(Optional.of(danhMucThuoc));
        when(danhMucThuocRepo.save(any(DanhMucThuoc.class))).thenReturn(danhMucThuoc);

        // When
        ResponseDTO<DanhMucThuoc> response = danhMucThuocService.update(danhMucThuocDTO);

        // Then
        assertEquals(200, response.getStatus());
        assertEquals("Cập nhật danh mục thuốc thành công", response.getMsg());
        assertEquals(danhMucThuoc, response.getData());
        verify(danhMucThuocRepo, times(1)).findById(anyInt());
        verify(danhMucThuocRepo, times(1)).save(any(DanhMucThuoc.class));
    }

    @Test
    public void testUpdateNotFound() {
        // Given
        when(danhMucThuocRepo.findById(anyInt())).thenReturn(Optional.empty());

        danhMucThuocDTO.setId(999);
        ResponseDTO<DanhMucThuoc> response = danhMucThuocService.update(danhMucThuocDTO);

        // Then
        assertEquals(404, response.getStatus());
        assertEquals("Không tìm thấy danh mục thuốc", response.getMsg());
        assertNull(response.getData());
        verify(danhMucThuocRepo, times(1)).findById(anyInt());
        verify(danhMucThuocRepo, never()).save(any(DanhMucThuoc.class));
    }

    @Test
    public void testDeleteSuccess() {
        // Given
        when(danhMucThuocRepo.existsById(anyInt())).thenReturn(true);
        doNothing().when(danhMucThuocRepo).deleteById(anyInt());

        // When
        ResponseDTO<Void> response = danhMucThuocService.delete(1);

        // Then
        assertEquals(200, response.getStatus());
        assertEquals("Xóa danh mục thuốc thành công", response.getMsg());
        assertNull(response.getData());
        verify(danhMucThuocRepo, times(1)).existsById(anyInt());
        verify(danhMucThuocRepo, times(1)).deleteById(anyInt());
    }

    @Test
    public void testDeleteNotFound() {
        // Given
        when(danhMucThuocRepo.existsById(anyInt())).thenReturn(false);

        // When
        ResponseDTO<Void> response = danhMucThuocService.delete(999);

        // Then
        assertEquals(404, response.getStatus());
        assertEquals("Không tìm thấy danh mục thuốc để xóa", response.getMsg());
        assertNull(response.getData());
        verify(danhMucThuocRepo, times(1)).existsById(anyInt());
        verify(danhMucThuocRepo, never()).deleteById(anyInt());
    }

}
