package com.example.hieuthuoc.service;

import com.example.hieuthuoc.dto.LoaiThuocDTO;
import com.example.hieuthuoc.dto.ResponseDTO;
import com.example.hieuthuoc.entity.DanhMucThuoc;
import com.example.hieuthuoc.entity.LoaiThuoc;
import com.example.hieuthuoc.repository.DanhMucThuocRepo;
import com.example.hieuthuoc.repository.LoaiThuocRepo;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestLoaiThuocService {

    @Mock
    private LoaiThuocRepo loaiThuocRepo;

    @Mock
    private DanhMucThuocRepo danhMucThuocRepo;

    @InjectMocks
    private LoaiThuocServiceImpl loaiThuocService;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    private LoaiThuoc loaiThuoc;
    private LoaiThuocDTO loaiThuocDTO;
    private List<LoaiThuoc> loaiThuocList;
    private DanhMucThuoc danhMucThuoc;

    @BeforeEach
    public void setup() {
        // Set up test data
        danhMucThuoc = new DanhMucThuoc();
        danhMucThuoc.setId(1);
        danhMucThuoc.setTenDanhMuc("Thuốc kháng sinh");
        danhMucThuoc.setMoTa("Các loại thuốc kháng sinh");

        loaiThuoc = new LoaiThuoc();
        loaiThuoc.setId(1);
        loaiThuoc.setTenLoai("Amoxicillin");
        loaiThuoc.setMoTa("Thuốc kháng sinh nhóm beta-lactam");
        loaiThuoc.setDanhMucThuoc(danhMucThuoc);

        loaiThuocDTO = new LoaiThuocDTO();
        loaiThuocDTO.setId(1);
        loaiThuocDTO.setTenLoai("Amoxicillin");
        loaiThuocDTO.setMoTa("Thuốc kháng sinh nhóm beta-lactam");
        loaiThuocDTO.setDanhMucThuocId(1);

        loaiThuocList = Arrays.asList(loaiThuoc);
    }

    @Test
    @DisplayName("Test getAllLoaiThuocs method returns all loai thuoc")
    public void testGetAllLoaiThuocs() {
        // Given
        when(loaiThuocRepo.findAll()).thenReturn(loaiThuocList);

        // When
        ResponseDTO<List<LoaiThuoc>> response = loaiThuocService.getAllLoaiThuocs();

        // Then
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertEquals(loaiThuocList, response.getData());
        verify(loaiThuocRepo, times(1)).findAll();
    }

    @Test
    @DisplayName("Test searchByTenLoai returns matching loai thuoc")
    public void testSearchByTenLoaiSuccess() {
        // Given
        String searchTerm = "Amoxicillin";
        when(loaiThuocRepo.searchByTenLoai(searchTerm)).thenReturn(loaiThuocList);

        // When
        ResponseDTO<List<LoaiThuoc>> response = loaiThuocService.searchByTenLoai(searchTerm);

        // Then
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertEquals(loaiThuocList, response.getData());
        verify(loaiThuocRepo, times(1)).searchByTenLoai(searchTerm);
    }

    @Test
    @DisplayName("Test searchByTenLoai returns error when no match found")
    public void testSearchByTenLoaiNoResults() {
        // Given
        String searchTerm = "không tồn tại";
        when(loaiThuocRepo.searchByTenLoai(searchTerm)).thenReturn(new ArrayList<>());

        // When
        ResponseDTO<List<LoaiThuoc>> response = loaiThuocService.searchByTenLoai(searchTerm);

        // Then
        assertEquals(409, response.getStatus());
        assertEquals("Loại thuốc không tồn tại", response.getMsg());
        assertNull(response.getData());
        verify(loaiThuocRepo, times(1)).searchByTenLoai(searchTerm);
    }

    @Test
    @DisplayName("Test create method successfully creates loai thuoc")
    public void testCreateSuccess() {
        // Given
        when(loaiThuocRepo.existsByTenLoai(anyString())).thenReturn(false);
        when(danhMucThuocRepo.findById(anyInt())).thenReturn(Optional.of(danhMucThuoc));
        when(loaiThuocRepo.save(any(LoaiThuoc.class))).thenReturn(loaiThuoc);

        // When
        ResponseDTO<LoaiThuoc> response = loaiThuocService.create(loaiThuocDTO);

        // Then
        assertEquals(201, response.getStatus());
        assertEquals("Tạo loại thuốc thành công", response.getMsg());
        assertEquals(loaiThuoc, response.getData());
        verify(loaiThuocRepo, times(1)).existsByTenLoai(anyString());
        verify(danhMucThuocRepo, times(1)).findById(anyInt());
        verify(loaiThuocRepo, times(1)).save(any(LoaiThuoc.class));
    }

    @Test
    @DisplayName("Test create method returns error when loai thuoc already exists")
    public void testCreateAlreadyExists() {
        // Given
        when(loaiThuocRepo.existsByTenLoai(anyString())).thenReturn(true);

        // When
        ResponseDTO<LoaiThuoc> response = loaiThuocService.create(loaiThuocDTO);

        // Then
        assertEquals(409, response.getStatus());
        assertEquals("Loại thuốc đã tồn tại", response.getMsg());
        assertNull(response.getData());
        verify(loaiThuocRepo, times(1)).existsByTenLoai(anyString());
        verify(danhMucThuocRepo, never()).findById(anyInt());
        verify(loaiThuocRepo, never()).save(any(LoaiThuoc.class));
    }

    @Test
    @DisplayName("Test create method throws exception when danh muc thuoc not found")
    public void testCreateDanhMucNotFound() {
        // Given
        when(loaiThuocRepo.existsByTenLoai(anyString())).thenReturn(false);
        when(danhMucThuocRepo.findById(anyInt())).thenReturn(Optional.empty());

        // When/Then
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            loaiThuocService.create(loaiThuocDTO);
        });

        assertEquals("Danh mục thuốc không tồn tại", thrown.getMessage());
        verify(loaiThuocRepo, times(1)).existsByTenLoai(anyString());
        verify(danhMucThuocRepo, times(1)).findById(anyInt());
        verify(loaiThuocRepo, never()).save(any(LoaiThuoc.class));
    }

    @Test
    @DisplayName("Test update method successfully updates loai thuoc")
    public void testUpdateSuccess() {
        // Given
        LoaiThuoc updatedLoaiThuoc = new LoaiThuoc();
        updatedLoaiThuoc.setId(1);
        updatedLoaiThuoc.setTenLoai("Amoxicillin Updated");
        updatedLoaiThuoc.setMoTa("Mô tả cập nhật");

        when(loaiThuocRepo.findById(anyInt())).thenReturn(Optional.of(loaiThuoc));
        when(loaiThuocRepo.save(any(LoaiThuoc.class))).thenReturn(updatedLoaiThuoc);

        // When
        ResponseDTO<LoaiThuoc> response = loaiThuocService.update(loaiThuocDTO);

        // Then
        assertEquals(200, response.getStatus());
        assertEquals("Cập nhật loại thuốc thành công", response.getMsg());
        assertEquals(updatedLoaiThuoc, response.getData());
        verify(loaiThuocRepo, times(1)).findById(anyInt());
        verify(loaiThuocRepo, times(1)).save(any(LoaiThuoc.class));
    }

    @Test
    @DisplayName("Test update method returns error when loai thuoc not found")
    public void testUpdateNotFound() {
        // Given
        when(loaiThuocRepo.findById(anyInt())).thenReturn(Optional.empty());

        loaiThuocDTO.setId(99);
        ResponseDTO<LoaiThuoc> response = loaiThuocService.update(loaiThuocDTO);

        // Then
        assertEquals(404, response.getStatus());
        assertEquals("Không tìm thấy loại thuốc", response.getMsg());
        assertNull(response.getData());
        verify(loaiThuocRepo, times(1)).findById(anyInt());
        verify(loaiThuocRepo, never()).save(any(LoaiThuoc.class));
    }

    @Test
    @DisplayName("Test delete method successfully deletes loai thuoc")
    public void testDeleteSuccess() {
        // Given
        when(loaiThuocRepo.existsById(anyInt())).thenReturn(true);
        doNothing().when(loaiThuocRepo).deleteById(anyInt());

        // When
        ResponseDTO<Void> response = loaiThuocService.delete(1);

        // Then
        assertEquals(200, response.getStatus());
        assertEquals("Xóa loại thuốc thành công", response.getMsg());
        assertNull(response.getData());
        verify(loaiThuocRepo, times(1)).existsById(anyInt());
        verify(loaiThuocRepo, times(1)).deleteById(anyInt());
    }

    @Test
    @DisplayName("Test delete method returns error when loai thuoc not found")
    public void testDeleteNotFound() {
        // Given
        when(loaiThuocRepo.existsById(anyInt())).thenReturn(false);

        // When
        ResponseDTO<Void> response = loaiThuocService.delete(1);

        // Then
        assertEquals(404, response.getStatus());
        assertEquals("Không tìm thấy loại thuốc để xóa", response.getMsg());
        assertNull(response.getData());
        verify(loaiThuocRepo, times(1)).existsById(anyInt());
        verify(loaiThuocRepo, never()).deleteById(anyInt());
    }

}
