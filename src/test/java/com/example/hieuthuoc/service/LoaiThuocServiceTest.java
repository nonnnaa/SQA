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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class LoaiThuocServiceTest {

    @Autowired
    private LoaiThuocService loaiThuocService;

    @Autowired
    private LoaiThuocRepo loaiThuocRepo;

    @Autowired
    private DanhMucThuocRepo danhMucThuocRepo;

    private ModelMapper modelMapper = new ModelMapper();

    private LoaiThuoc loaiThuoc;
    private LoaiThuocDTO loaiThuocDTO;
    private List<LoaiThuoc> loaiThuocList;
    private DanhMucThuoc danhMucThuoc;

    @BeforeEach
    public void setup() {
        // Set up test data
        danhMucThuoc = new DanhMucThuoc();
        danhMucThuoc.setTenDanhMuc("Thuốc kháng sinh");
        danhMucThuoc.setMoTa("Các loại thuốc kháng sinh");
        danhMucThuoc = danhMucThuocRepo.save(danhMucThuoc);

        loaiThuoc = new LoaiThuoc();
        loaiThuoc.setTenLoai("Amoxicillin");
        loaiThuoc.setMoTa("Thuốc kháng sinh nhóm beta-lactam");
        loaiThuoc.setDanhMucThuoc(danhMucThuoc);
        loaiThuoc = loaiThuocRepo.save(loaiThuoc);

        loaiThuocDTO = new LoaiThuocDTO();
        loaiThuocDTO.setId(loaiThuoc.getId());
        loaiThuocDTO.setTenLoai("Amoxicillin");
        loaiThuocDTO.setMoTa("Thuốc kháng sinh nhóm beta-lactam");
        loaiThuocDTO.setDanhMucThuocId(danhMucThuoc.getId());

        loaiThuocList = Arrays.asList(loaiThuoc);
    }

    @Test
    @DisplayName("Test getAllLoaiThuocs method returns all loai thuoc")
    public void testGetAllLoaiThuocsServiceSuccess() {
        // When
        ResponseDTO<List<LoaiThuoc>> response = loaiThuocService.getAllLoaiThuocs();

        // Then
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertEquals(loaiThuocRepo.findAll().get(0).getTenLoai(), response.getData().get(0).getTenLoai());
        assertEquals(loaiThuocRepo.count(), response.getData().size());
    }

    @Test
    @DisplayName("Test searchByTenLoai returns matching loai thuoc")
    public void testSearchByTenLoaiServiceSuccess() {
        // Given
        String searchTerm = "Amoxicillin";

        // When
        ResponseDTO<List<LoaiThuoc>> response = loaiThuocService.searchByTenLoai(searchTerm);

        // Then
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertEquals(loaiThuocRepo.searchByTenLoai(searchTerm).size(), response.getData().size());
        assertEquals(loaiThuoc.getTenLoai(), response.getData().get(0).getTenLoai());
        assertEquals(loaiThuocRepo.searchByTenLoai(searchTerm).get(0).getTenLoai(), response.getData().get(0).getTenLoai());
        assertFalse(response.getData()==null);
        assertFalse(response.getData().isEmpty());

        boolean ok = false;
        List<LoaiThuoc> loaiThuocs = loaiThuocRepo.findAll();
        for(LoaiThuoc lt : loaiThuocs) {
            if(lt.getTenLoai().equalsIgnoreCase("Amoxicillin")) {
                ok = true;
                break;
            }
        }
        assertTrue(ok);
    }

    @Test
    @DisplayName("Test searchByTenLoai returns error when no match found")
    public void testSearchByTenLoaiServiceNoResults() {
        // Given
        String searchTerm = "không tồn tại";

        // When
        ResponseDTO<List<LoaiThuoc>> response = loaiThuocService.searchByTenLoai(searchTerm);

        // Then
        assertEquals(404, response.getStatus());
        assertEquals("Loại thuốc không tồn tại", response.getMsg());
        assertNull(response.getData());

        boolean ok = false;
        List<LoaiThuoc> loaiThuocs = loaiThuocRepo.findAll();
        for(LoaiThuoc lt : loaiThuocs) {
            if(lt.getTenLoai().equalsIgnoreCase("không tồn tại")) {
                ok = true;
                break;
            }
        }
        assertFalse(ok);
    }



    @Test
    @DisplayName("Test create method successfully creates loai thuoc")
    public void testCreateLoaiThuocServiceSuccess() {
        Long size= loaiThuocRepo.count();
        // Given
        loaiThuocDTO.setId(null);
        loaiThuocDTO.setTenLoai("Cefalexin");
        loaiThuocDTO.setDanhMucThuocId(danhMucThuoc.getId());

        // When
        ResponseDTO<LoaiThuoc> response = loaiThuocService.create(loaiThuocDTO);

        // Then
        assertEquals(201, response.getStatus());
        assertEquals("Tạo loại thuốc thành công", response.getMsg());
        assertNotNull(response.getData());
        assertEquals(loaiThuocDTO.getTenLoai(), response.getData().getTenLoai());
        assertTrue(danhMucThuocRepo.existsById(danhMucThuoc.getId()));
        assertEquals(size+1, loaiThuocRepo.count());
    }

    @Test
    @DisplayName("Test create method returns error when loai thuoc already exists")
    public void testCreateLoaiThuocServiceAlreadyExists() {
        // Given
        loaiThuocDTO.setId(null); // New entity
        loaiThuocDTO.setTenLoai("Amoxicillin");

        // When
        ResponseDTO<LoaiThuoc> response = loaiThuocService.create(loaiThuocDTO);

        // Then
        assertEquals(409, response.getStatus());
        assertEquals("Loại thuốc đã tồn tại", response.getMsg());
        assertNull(response.getData());
        assertFalse(loaiThuocRepo.searchByTenLoai("Amoxicillin").isEmpty());
    }

    @Test
    @DisplayName("Test create method throws exception when danh muc thuoc not found")
    public void testCreateLoaiThuocServiceWhenDanhMucNotFound() {
        // Given
        loaiThuocDTO.setId(null); // New entity
        loaiThuocDTO.setTenLoai("Cefalexin");
        loaiThuocDTO.setDanhMucThuocId(999); // Non-existent DanhMucThuoc

        // When/Then
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            loaiThuocService.create(loaiThuocDTO);
        });

        assertEquals("Danh mục thuốc không tồn tại", thrown.getMessage());
        assertFalse(danhMucThuocRepo.findById(999).isPresent());
    }

    @Test
    @DisplayName("Test update method successfully updates loai thuoc")
    public void testUpdateLoaiThuocServiceSuccess() {
        // Given
        loaiThuocDTO.setTenLoai("Amoxicillin Updated");
        loaiThuocDTO.setMoTa("Mô tả cập nhật");

        // When
        ResponseDTO<LoaiThuoc> response = loaiThuocService.update(loaiThuocDTO);

        // Then
        assertEquals(200, response.getStatus());
        assertEquals("Cập nhật loại thuốc thành công", response.getMsg());
        assertNotNull(response.getData());
        assertEquals(loaiThuocDTO.getTenLoai(), response.getData().getTenLoai());
        assertEquals(loaiThuocDTO.getMoTa(), response.getData().getMoTa());
        assertTrue(loaiThuocRepo.findById(loaiThuocDTO.getId()).get().getTenLoai().equals("Amoxicillin Updated"));
    }

    @Test
    @DisplayName("Test update method returns error when loai thuoc not found")
    public void testUpdateLoaiThuocServiceNotFound() {
        // Given
        loaiThuocDTO.setId(999);

        // When
        ResponseDTO<LoaiThuoc> response = loaiThuocService.update(loaiThuocDTO);

        // Then
        assertEquals(404, response.getStatus());
        assertEquals("Không tìm thấy loại thuốc", response.getMsg());
        assertNull(response.getData());
        assertFalse(loaiThuocRepo.existsById(999));
    }

    @Test
    @DisplayName("Test delete method successfully deletes loai thuoc")
    public void testDeleteLoaiThuocServiceSuccess() {
        // When
        ResponseDTO<Void> response = loaiThuocService.delete(loaiThuoc.getId());

        // Then
        assertEquals(200, response.getStatus());
        assertEquals("Xóa loại thuốc thành công", response.getMsg());
        assertNull(response.getData());
        assertFalse(loaiThuocRepo.existsById(loaiThuoc.getId()));
    }

    @Test
    @DisplayName("Test delete method returns error when loai thuoc not found")
    public void testDeleteLoaiThuocServiceNotFound() {
        // When
        ResponseDTO<Void> response = loaiThuocService.delete(999);

        // Then
        assertEquals(404, response.getStatus());
        assertEquals("Không tìm thấy loại thuốc để xóa", response.getMsg());
        assertNull(response.getData());
        assertFalse(loaiThuocRepo.existsById(999));
    }
}