package com.example.hieuthuoc.service;

import com.example.hieuthuoc.dto.ThanhPhanThuocDTO;
import com.example.hieuthuoc.entity.ThanhPhanThuoc;
import com.example.hieuthuoc.entity.Thuoc;
import com.example.hieuthuoc.repository.ThanhPhanThuocRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestThanhPhanThuocService {

    @Mock
    private ThanhPhanThuocRepo thanhPhanThuocRepo;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private ThanhPhanThuocServiceImpl thanhPhanThuocService;

    private ThanhPhanThuoc thanhPhanThuoc1;
    private ThanhPhanThuoc thanhPhanThuoc2;
    private ThanhPhanThuocDTO thanhPhanThuocDTO;
    private Thuoc thuoc;

    @BeforeEach
    void setUp() {
        // Create Thuoc entity
        thuoc = new Thuoc();
        thuoc.setId(10);

        // Create test data
        thanhPhanThuoc1 = new ThanhPhanThuoc();
        thanhPhanThuoc1.setId(1);
        thanhPhanThuoc1.setThuoc(thuoc);
        thanhPhanThuoc1.setTenThanhPhan("Paracetamol");
        thanhPhanThuoc1.setHamLuong("500");
        thanhPhanThuoc1.setDonVi("mg");

        thanhPhanThuoc2 = new ThanhPhanThuoc();
        thanhPhanThuoc2.setId(2);
        thanhPhanThuoc2.setThuoc(thuoc);
        thanhPhanThuoc2.setTenThanhPhan("Caffeine");
        thanhPhanThuoc2.setHamLuong("50");
        thanhPhanThuoc2.setDonVi("mg");

        thanhPhanThuocDTO = new ThanhPhanThuocDTO();
        thanhPhanThuocDTO.setId(1);
        thanhPhanThuocDTO.setThuocId(10);
        thanhPhanThuocDTO.setTenThanhPhan("Paracetamol");
        thanhPhanThuocDTO.setHamLuong("500");
        thanhPhanThuocDTO.setDonVi("mg");

        // Configure ModelMapper for proper mapping between DTO and Entity
        modelMapper.createTypeMap(ThanhPhanThuocDTO.class, ThanhPhanThuoc.class)
                .addMappings(mapper -> mapper.skip(ThanhPhanThuoc::setThuoc))
                .setPostConverter(context -> {
                    ThanhPhanThuocDTO source = context.getSource();
                    ThanhPhanThuoc destination = context.getDestination();

                    if (source.getThuocId() != null) {
                        Thuoc mappedThuoc = new Thuoc();
                        mappedThuoc.setId(source.getThuocId());
                        destination.setThuoc(mappedThuoc);
                    }

                    return destination;
                });
    }

    @Test
    void getAllThanhPhanThuocs_ShouldReturnAllThanhPhanThuocs() {
        // Arrange
        List<ThanhPhanThuoc> expectedThanhPhanThuocs = Arrays.asList(thanhPhanThuoc1, thanhPhanThuoc2);
        when(thanhPhanThuocRepo.findAll()).thenReturn(expectedThanhPhanThuocs);

        // Act
        List<ThanhPhanThuoc> actualThanhPhanThuocs = thanhPhanThuocService.getAllThanhPhanThuocs();

        // Assert
        assertEquals(expectedThanhPhanThuocs, actualThanhPhanThuocs);
        verify(thanhPhanThuocRepo, times(1)).findAll();
    }

    @Test
    void getThanhPhanThuocById_WhenIdExists_ShouldReturnThanhPhanThuoc() {
        // Arrange
        Integer id = 1;
        when(thanhPhanThuocRepo.findById(id)).thenReturn(Optional.of(thanhPhanThuoc1));

        // Act
        Optional<ThanhPhanThuoc> result = thanhPhanThuocService.getThanhPhanThuocById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(thanhPhanThuoc1, result.get());
        verify(thanhPhanThuocRepo, times(1)).findById(id);
    }

    @Test
    void getThanhPhanThuocById_WhenIdDoesNotExist_ShouldReturnEmptyOptional() {
        // Arrange
        Integer id = 999;
        when(thanhPhanThuocRepo.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<ThanhPhanThuoc> result = thanhPhanThuocService.getThanhPhanThuocById(id);

        // Assert
        assertFalse(result.isPresent());
        verify(thanhPhanThuocRepo, times(1)).findById(id);
    }

    @Test
    void create_ShouldSaveAndReturnNewThanhPhanThuoc() {
        // Arrange
        ThanhPhanThuoc mappedThanhPhanThuoc = new ThanhPhanThuoc();
        mappedThanhPhanThuoc.setId(1);
        mappedThanhPhanThuoc.setThuoc(thuoc);
        mappedThanhPhanThuoc.setTenThanhPhan("Paracetamol");
        mappedThanhPhanThuoc.setHamLuong("500");
        mappedThanhPhanThuoc.setDonVi("mg");

        when(thanhPhanThuocRepo.save(any(ThanhPhanThuoc.class))).thenReturn(mappedThanhPhanThuoc);

        // Act
        ThanhPhanThuoc createdThanhPhanThuoc = thanhPhanThuocService.create(thanhPhanThuocDTO);

        // Assert
        assertNotNull(createdThanhPhanThuoc);
        assertEquals(mappedThanhPhanThuoc.getId(), createdThanhPhanThuoc.getId());
        assertEquals(mappedThanhPhanThuoc.getThuoc().getId(), createdThanhPhanThuoc.getThuoc().getId());
        assertEquals(mappedThanhPhanThuoc.getTenThanhPhan(), createdThanhPhanThuoc.getTenThanhPhan());
        assertEquals(mappedThanhPhanThuoc.getHamLuong(), createdThanhPhanThuoc.getHamLuong());
        assertEquals(mappedThanhPhanThuoc.getDonVi(), createdThanhPhanThuoc.getDonVi());

        verify(thanhPhanThuocRepo, times(1)).save(any(ThanhPhanThuoc.class));
    }

    @Test
    void update_WhenIdExists_ShouldUpdateAndReturnThanhPhanThuoc() {
        // Arrange
        ThanhPhanThuoc mappedThanhPhanThuoc = new ThanhPhanThuoc();
        mappedThanhPhanThuoc.setId(1);
        mappedThanhPhanThuoc.setThuoc(thuoc);
        mappedThanhPhanThuoc.setTenThanhPhan("Paracetamol");
        mappedThanhPhanThuoc.setHamLuong("500");
        mappedThanhPhanThuoc.setDonVi("mg");

        when(thanhPhanThuocRepo.findById(1)).thenReturn(Optional.of(thanhPhanThuoc1));
        when(thanhPhanThuocRepo.save(any(ThanhPhanThuoc.class))).thenReturn(mappedThanhPhanThuoc);

        // Act
        ThanhPhanThuoc updatedThanhPhanThuoc = thanhPhanThuocService.update(thanhPhanThuocDTO);

        // Assert
        assertNotNull(updatedThanhPhanThuoc);
        assertEquals(mappedThanhPhanThuoc.getId(), updatedThanhPhanThuoc.getId());
        assertEquals(mappedThanhPhanThuoc.getThuoc().getId(), updatedThanhPhanThuoc.getThuoc().getId());
        assertEquals(mappedThanhPhanThuoc.getTenThanhPhan(), updatedThanhPhanThuoc.getTenThanhPhan());
        assertEquals(mappedThanhPhanThuoc.getHamLuong(), updatedThanhPhanThuoc.getHamLuong());
        assertEquals(mappedThanhPhanThuoc.getDonVi(), updatedThanhPhanThuoc.getDonVi());

        verify(thanhPhanThuocRepo, times(1)).findById(1);
        verify(thanhPhanThuocRepo, times(1)).save(any(ThanhPhanThuoc.class));
    }

    @Test
    void update_WhenIdDoesNotExist_ShouldReturnNull() {
        // Arrange
        ThanhPhanThuocDTO nonExistingDTO = new ThanhPhanThuocDTO();
        nonExistingDTO.setId(999);
        nonExistingDTO.setThuocId(10);
        nonExistingDTO.setTenThanhPhan("Paracetamol");
        nonExistingDTO.setHamLuong("500");
        nonExistingDTO.setDonVi("mg");

        // Create a Thuoc entity for the nonExisting ThanhPhanThuoc
        Thuoc nonExistingThuoc = new Thuoc();
        nonExistingThuoc.setId(10);

        when(thanhPhanThuocRepo.findById(999)).thenReturn(Optional.empty());

        // Act
        ThanhPhanThuoc result = thanhPhanThuocService.update(nonExistingDTO);

        // Assert
        assertNull(result);
        verify(thanhPhanThuocRepo, times(1)).findById(999);
        verify(thanhPhanThuocRepo, never()).save(any(ThanhPhanThuoc.class));
    }

    @Test
    void delete_ShouldDeleteThanhPhanThuoc() {
        // Arrange
        Integer id = 1;
        doNothing().when(thanhPhanThuocRepo).deleteById(id);

        // Act
        thanhPhanThuocService.delete(id);

        // Assert
        verify(thanhPhanThuocRepo, times(1)).deleteById(id);
    }

}
