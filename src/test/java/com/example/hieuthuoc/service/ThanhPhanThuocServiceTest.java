package com.example.hieuthuoc.service;

import com.example.hieuthuoc.dto.ThanhPhanThuocDTO;
import com.example.hieuthuoc.entity.ThanhPhanThuoc;
import com.example.hieuthuoc.entity.Thuoc;
import com.example.hieuthuoc.repository.ThanhPhanThuocRepo;
import com.example.hieuthuoc.repository.ThuocRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ThanhPhanThuocServiceTest {

    @Autowired
    private ThanhPhanThuocService thanhPhanThuocService;

    @Autowired
    private ThanhPhanThuocRepo thanhPhanThuocRepo;

    @Autowired
    private ThuocRepo thuocRepo;

    private ModelMapper modelMapper = new ModelMapper();

    private ThanhPhanThuoc thanhPhanThuoc1;
    private ThanhPhanThuoc thanhPhanThuoc2;
    private ThanhPhanThuocDTO thanhPhanThuocDTO;
    private Thuoc thuoc;

    @BeforeEach
    void setUp() {
        Thuoc thuoc = new Thuoc();
        thuoc.setTenThuoc("Thuốc test");
        thuoc.setMaThuoc("123987654");
        thuoc.setMaVach("TEST123456789");
        thuoc.setSoDangKy("PTIT123456");
        thuoc.setDoiTuongs(new ArrayList<>());
        thuoc = thuocRepo.save(thuoc);

        // Create test data
        thanhPhanThuoc1 = new ThanhPhanThuoc();
        thanhPhanThuoc1.setThuoc(thuoc);
        thanhPhanThuoc1.setTenThanhPhan("Paracetamol");
        thanhPhanThuoc1.setHamLuong("500");
        thanhPhanThuoc1.setDonVi("mg");
        thanhPhanThuoc1 = thanhPhanThuocRepo.save(thanhPhanThuoc1);

        thanhPhanThuoc2 = new ThanhPhanThuoc();
        thanhPhanThuoc2.setThuoc(thuoc);
        thanhPhanThuoc2.setTenThanhPhan("Caffeine");
        thanhPhanThuoc2.setHamLuong("50");
        thanhPhanThuoc2.setDonVi("mg");
        thanhPhanThuoc2 = thanhPhanThuocRepo.save(thanhPhanThuoc2);

        thanhPhanThuocDTO = new ThanhPhanThuocDTO();
        thanhPhanThuocDTO.setId(thanhPhanThuoc1.getId());
        thanhPhanThuocDTO.setThuocId(thuoc.getId());
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
    void getAllThanhPhanThuocsService_ReturnAllThanhPhanThuocs() {
        Long size = thanhPhanThuocRepo.count();
        // Act
        List<ThanhPhanThuoc> thanhPhanThuocs = thanhPhanThuocService.getAllThanhPhanThuocs();

        // Assert
        assertTrue(thanhPhanThuocs.stream().anyMatch(tp -> tp.getTenThanhPhan().equals("Paracetamol")));
        assertTrue(thanhPhanThuocs.stream().anyMatch(tp -> tp.getTenThanhPhan().equals("Caffeine")));
        assertEquals(size, thanhPhanThuocs.size());

    }

    @Test
    void getThanhPhanThuocByIdService_Found() {
        // Act
        Optional<ThanhPhanThuoc> result = thanhPhanThuocService.getThanhPhanThuocById(8);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(8, result.get().getId());
        assertTrue(thanhPhanThuocRepo.findById(8).isPresent());
    }

    @Test
    void getThanhPhanThuocByIdService_NotFound() {
        // Act
        Optional<ThanhPhanThuoc> result = thanhPhanThuocService.getThanhPhanThuocById(999);

        // Assert
        assertFalse(result.isPresent());
        assertFalse(thanhPhanThuocRepo.findById(999).isPresent());
    }

    @Test
    void createTpThuocService_Success() {
        Long size = thanhPhanThuocRepo.count();
        thanhPhanThuocDTO.setId(null); // New entity
        thanhPhanThuocDTO.setTenThanhPhan("Ibuprofen");
        thanhPhanThuocDTO.setHamLuong("200");
        thanhPhanThuocDTO.setDonVi("mg");

        // Act
        ThanhPhanThuoc createdThanhPhanThuoc = thanhPhanThuocService.create(thanhPhanThuocDTO);

        // Assert
        assertNotNull(createdThanhPhanThuoc);
        assertEquals(thanhPhanThuocDTO.getThuocId(), createdThanhPhanThuoc.getThuoc().getId());
        assertEquals(thanhPhanThuocDTO.getTenThanhPhan(), createdThanhPhanThuoc.getTenThanhPhan());
        assertEquals(thanhPhanThuocDTO.getHamLuong(), createdThanhPhanThuoc.getHamLuong());
        assertEquals(thanhPhanThuocDTO.getDonVi(), createdThanhPhanThuoc.getDonVi());
        assertEquals(size+1, thanhPhanThuocRepo.count());
    }

    @Test
    void updateTpThuocService_Found() {
        // Arrange
        thanhPhanThuocDTO.setTenThanhPhan("Paracetamol Updated");
        thanhPhanThuocDTO.setHamLuong("1000");
        thanhPhanThuocDTO.setDonVi("mg");

        // Act
        ThanhPhanThuoc updatedThanhPhanThuoc = thanhPhanThuocService.update(thanhPhanThuocDTO);

        // Assert
        assertNotNull(updatedThanhPhanThuoc);
        assertEquals(thanhPhanThuocDTO.getThuocId(), updatedThanhPhanThuoc.getThuoc().getId());
        assertEquals(thanhPhanThuocDTO.getTenThanhPhan(), updatedThanhPhanThuoc.getTenThanhPhan());
        assertEquals(thanhPhanThuocDTO.getHamLuong(), updatedThanhPhanThuoc.getHamLuong());
        assertEquals(thanhPhanThuocDTO.getDonVi(), updatedThanhPhanThuoc.getDonVi());

        ThanhPhanThuoc tp = thanhPhanThuocRepo.findById(thanhPhanThuocDTO.getId()).get();
        assertEquals(tp.getTenThanhPhan(), "Paracetamol Updated");
        assertEquals(tp.getHamLuong(), "1000");
        assertEquals(tp.getDonVi(), "mg");
    }

    @Test
    void updateTpThuocService_NotFound() {
        // Arrange
        ThanhPhanThuocDTO nonExistingDTO = new ThanhPhanThuocDTO();
        nonExistingDTO.setId(999);
        nonExistingDTO.setThuocId(8);
        nonExistingDTO.setTenThanhPhan("TpTest");
        nonExistingDTO.setHamLuong("2000");
        nonExistingDTO.setDonVi("mg");

        // Act
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            thanhPhanThuocService.update(nonExistingDTO);
        });

        assertEquals("Không tìm thấy thành phần thuốc", thrown.getMessage());
    }

    @Test
    void deleteTpThuocService_Success() {
        Long size = thanhPhanThuocRepo.count();
        // Act
        thanhPhanThuocService.delete(8);

        // Assert
        assertFalse(thanhPhanThuocRepo.existsById(8));
        assertEquals(size-1, thanhPhanThuocRepo.count());
    }

    @Test
    void deleteTpThuocService_NotFound() {
        Long size = thanhPhanThuocRepo.count();
        // Act
        thanhPhanThuocService.delete(999);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            thanhPhanThuocService.delete(999);
        });

        // Assert
        assertFalse(thanhPhanThuocRepo.existsById(999));
        assertEquals(size, thanhPhanThuocRepo.count());
        assertEquals("Không tìm thấy thành phần thuốc", thrown.getMessage());
    }
}