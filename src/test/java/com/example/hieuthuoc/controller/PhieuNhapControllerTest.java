package com.example.hieuthuoc.controller;

import com.example.hieuthuoc.dto.*;
import com.example.hieuthuoc.entity.ChiTietPhieuNhap;
import com.example.hieuthuoc.entity.NguoiDung;
import com.example.hieuthuoc.entity.NhaCungCap;
import com.example.hieuthuoc.entity.PhieuNhap;
import com.example.hieuthuoc.service.PhieuNhapService;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
class PhieuNhapControllerTest {

    @InjectMocks
    private PhieuNhapController phieuNhapController;

    @Mock
    private PhieuNhapService phieuNhapService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void search_success() {
        // Arrange
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setKeyWord("ncc");
        searchDTO.setCurrentPage(1);
        searchDTO.setSize(10);

        List<PhieuNhap> content = new ArrayList<>();
        content.add(new PhieuNhap()); //

        PageDTO<List<PhieuNhap>> pageDTO = new PageDTO<>();
        pageDTO.setCurrentPage(1);
        pageDTO.setTotalElements(1);
        pageDTO.setTotalPages(1);
        pageDTO.setData(content);

        ResponseDTO<PageDTO<List<PhieuNhap>>> response = new ResponseDTO<>(200, "OK", pageDTO);

        // Act
        when(phieuNhapService.search(searchDTO)).thenReturn(response);
        ResponseEntity<ResponseDTO<PageDTO<List<PhieuNhap>>>> result = phieuNhapController.search(searchDTO);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.getBody().getStatus());
        assertEquals(1, result.getBody().getData().getTotalElements());

        // Verify gọi đúng hàm
        verify(phieuNhapService, times(1)).search(searchDTO);
    }

    @Test
    void search_emptyResult() {
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setKeyWord("KhôngTồnTại");

        PageDTO<List<PhieuNhap>> pageDTO = new PageDTO<>();
        pageDTO.setCurrentPage(1);
        pageDTO.setTotalPages(0);
        pageDTO.setTotalElements(0);
        pageDTO.setData(Collections.emptyList());

        ResponseDTO<PageDTO<List<PhieuNhap>>> expected = new ResponseDTO<>(200, "Không tìm thấy dữ liệu", pageDTO);

        when(phieuNhapService.search(searchDTO)).thenReturn(expected);

        ResponseEntity<ResponseDTO<PageDTO<List<PhieuNhap>>>> response = phieuNhapController.search(searchDTO);

        System.out.println("Expected Output: " + expected);
        System.out.println("Actual Output: " + response.getBody());

        assertEquals(200, response.getBody().getStatus());
        assertTrue(response.getBody().getData().getData().isEmpty());
    }

    @Test
    void getById_success() {
        // Arrange
        Integer id = 1;
        PhieuNhap phieuNhap = new PhieuNhap();
        phieuNhap.setId(id);
        phieuNhap.setTongTien(500000.0);

        NhaCungCap nhaCungCap = new NhaCungCap();
        nhaCungCap.setId(10);
        phieuNhap.setNhaCungCap(nhaCungCap);

        NguoiDung nguoiDung = new NguoiDung();
        nguoiDung.setId(20);
        phieuNhap.setNguoiDung(nguoiDung);

        ChiTietPhieuNhap chiTiet = new ChiTietPhieuNhap();
        chiTiet.setId(100);
        phieuNhap.setChiTietPhieuNhaps(Collections.singletonList(chiTiet));

        ResponseDTO<PhieuNhap> mockResponse = new ResponseDTO<>(200, "OK", phieuNhap);
        when(phieuNhapService.getById(id)).thenReturn(mockResponse);

        // Act
        ResponseEntity<ResponseDTO<PhieuNhap>> response = phieuNhapController.getById(id);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getBody().getStatus());
        assertEquals(500000.0, response.getBody().getData().getTongTien());
        assertEquals(10, response.getBody().getData().getNhaCungCap().getId());
        assertEquals(20, response.getBody().getData().getNguoiDung().getId());
        assertEquals(1, response.getBody().getData().getChiTietPhieuNhaps().size());

        verify(phieuNhapService, times(1)).getById(id);
    }

    @Test
    void getById_NotFound() {
        // Arrange
        Integer id = 999;
        ResponseDTO<PhieuNhap> mockResponse = new ResponseDTO<>(404, "Not Found", null);
        when(phieuNhapService.getById(id)).thenReturn(mockResponse);

        // Act
        ResponseEntity<ResponseDTO<PhieuNhap>> response = phieuNhapController.getById(id);

        // Assert
        assertNotNull(response);
        assertEquals(404, response.getBody().getStatus());
        assertNull(response.getBody().getData());

        verify(phieuNhapService, times(1)).getById(id);
    }

    @Test
    void create_success() {
        // Arrange
        PhieuNhapDTO inputDto = new PhieuNhapDTO();
        inputDto.setNhaCungCapId(10);
        inputDto.setNguoiDungId(20);
        inputDto.setTongTien(100000.0);

        ChiTietPhieuNhapDTO chiTiet = new ChiTietPhieuNhapDTO();
        chiTiet.setId(1);
        chiTiet.setSoLuong(5);
        chiTiet.setDonGia(20000.0);
        inputDto.setChiTietPhieuNhaps(List.of(chiTiet));

        // Tạo PhieuNhap giả lập trả về
        PhieuNhap createdPhieuNhap = new PhieuNhap();
        createdPhieuNhap.setId(1);
        createdPhieuNhap.setTongTien(100000.0);

        NhaCungCap nhaCungCap = new NhaCungCap();
        nhaCungCap.setId(10);
        createdPhieuNhap.setNhaCungCap(nhaCungCap);

        NguoiDung nguoiDung = new NguoiDung();
        nguoiDung.setId(20);
        createdPhieuNhap.setNguoiDung(nguoiDung);

        ResponseDTO<PhieuNhap> mockResponse = new ResponseDTO<>(200, "Thành công", createdPhieuNhap);
        when(phieuNhapService.create(inputDto)).thenReturn(mockResponse);

        // Act
        ResponseEntity<ResponseDTO<PhieuNhap>> response = phieuNhapController.create(inputDto);

        // Assert
        assertEquals(200, response.getBody().getStatus());
        assertNotNull(response.getBody().getData());
        assertEquals(100000.0, response.getBody().getData().getTongTien());
        assertEquals(10, response.getBody().getData().getNhaCungCap().getId());
        assertEquals(20, response.getBody().getData().getNguoiDung().getId());

        verify(phieuNhapService, times(1)).create(inputDto);
    }

    @Test
    void create_missingData() {
        // Arrange
        PhieuNhapDTO inputDto = new PhieuNhapDTO(); // bỏ trống

        ResponseDTO<PhieuNhap> mockResponse = new ResponseDTO<>(400, "Thiếu thông tin", null);
        when(phieuNhapService.create(inputDto)).thenReturn(mockResponse);

        // Act
        ResponseEntity<ResponseDTO<PhieuNhap>> response = phieuNhapController.create(inputDto);

        // Assert
        assertEquals(400, response.getBody().getStatus());
        assertNull(response.getBody().getData());

        verify(phieuNhapService, times(1)).create(inputDto);
    }

    @Test
    void update_success() {
        // Arrange - INPUT
        PhieuNhapDTO updateDTO = new PhieuNhapDTO();
        updateDTO.setId(5);
        updateDTO.setNhaCungCapId(2);
        updateDTO.setNguoiDungId(4);
        updateDTO.setTongTien(500000.0);

        // Expected OUTPUT
        PhieuNhap updated = new PhieuNhap();
        updated.setId(5);
        updated.setTongTien(500000.0);

        ResponseDTO<PhieuNhap> responseDTO = new ResponseDTO<>(200, "Cập nhật thành công", updated);
        when(phieuNhapService.update(updateDTO)).thenReturn(responseDTO);

        // Act
        ResponseEntity<ResponseDTO<PhieuNhap>> response = phieuNhapController.update(updateDTO);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getBody().getStatus());
        assertEquals(5, response.getBody().getData().getId());
        assertEquals(500000.0, response.getBody().getData().getTongTien());

        verify(phieuNhapService, times(1)).update(updateDTO);
    }

    @Test
    void update_invalidId() {
        // Arrange - INPUT
        PhieuNhapDTO invalidDTO = new PhieuNhapDTO();
        invalidDTO.setId(9999); // không tồn tại
        invalidDTO.setTongTien(200000.0);

        // Expected OUTPUT
        ResponseDTO<PhieuNhap> responseDTO = new ResponseDTO<>(404, "Không tìm thấy phiếu nhập", null);
        when(phieuNhapService.update(invalidDTO)).thenReturn(responseDTO);

        // Act
        ResponseEntity<ResponseDTO<PhieuNhap>> response = phieuNhapController.update(invalidDTO);

        // Assert
        assertNotNull(response);
        assertEquals(404, response.getBody().getStatus());
        assertNull(response.getBody().getData());

        verify(phieuNhapService, times(1)).update(invalidDTO);
    }

    @Test
    void delete_success() {
        // Arrange
        int id = 5;
        ResponseDTO<Void> responseDTO = new ResponseDTO<>(200, "Xóa thành công", null);
        when(phieuNhapService.delete(id)).thenReturn(responseDTO);

        // Act
        ResponseEntity<ResponseDTO<Void>> response = phieuNhapController.delete(id);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Xóa thành công", response.getBody().getMsg());
        assertNull(response.getBody().getData());

        verify(phieuNhapService, times(1)).delete(id);
    }

    @Test
    void delete_NotFound() {
        // Arrange
        int id = 9999;
        ResponseDTO<Void> responseDTO = new ResponseDTO<>(404, "Không tìm thấy phiếu nhập", null);
        when(phieuNhapService.delete(id)).thenReturn(responseDTO);

        // Act
        ResponseEntity<ResponseDTO<Void>> response = phieuNhapController.delete(id);

        // Assert
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Không tìm thấy phiếu nhập", response.getBody().getMsg());
        assertNull(response.getBody().getData());

        verify(phieuNhapService, times(1)).delete(id);
    }

    @Test
    void delete_Exception() {
        // Given
        int id = 3;
        when(phieuNhapService.delete(id)).thenThrow(new RuntimeException("Unexpected error"));

        // When + Then
        assertThrows(RuntimeException.class, () -> phieuNhapController.delete(id));
        verify(phieuNhapService).delete(id);
    }
}


