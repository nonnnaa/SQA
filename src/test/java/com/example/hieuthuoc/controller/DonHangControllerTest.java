package com.example.hieuthuoc.controller;

import com.example.hieuthuoc.dto.*;
import com.example.hieuthuoc.entity.*;
import com.example.hieuthuoc.service.DonHangService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DonHangControllerTest {

    @InjectMocks
    private DonHangController donHangController;

    @Mock
    private DonHangService donHangService;

    @Test
    void testGetByTrangThaiGiaoHang_validInput() throws Exception {
        // Arrange
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setKeyWord("giao");
        searchDTO.setId(1);
        searchDTO.setCurrentPage(1);
        searchDTO.setSize(10);
        searchDTO.setSortedField("ngayLap");

        PageDTO<List<DonHang>> pageDTO = new PageDTO<>();
        pageDTO.setCurrentPage(1);
        pageDTO.setTotalPages(2);
        pageDTO.setTotalElements(5L);
        pageDTO.setData(List.of(new DonHang()));

        ResponseDTO<PageDTO<List<DonHang>>> responseDTO = new ResponseDTO<>();
        responseDTO.setData(pageDTO);

        when(donHangService.getByTrangThaiGiaoHang(any(SearchDTO.class)))
                .thenReturn(responseDTO);

        // Act
        ResponseDTO<PageDTO<List<DonHang>>> result = donHangController.getByTrangThaiGiaoHang(searchDTO);

        // Assert
        assertNotNull(result);
        assertEquals(5L, result.getData().getTotalElements());
        verify(donHangService, times(1)).getByTrangThaiGiaoHang(searchDTO);
    }
}
