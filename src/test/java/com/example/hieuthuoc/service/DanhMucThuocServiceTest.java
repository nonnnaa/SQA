package com.example.hieuthuoc.service;

import com.example.hieuthuoc.dto.DanhMucThuocDTO;
import com.example.hieuthuoc.dto.ResponseDTO;
import com.example.hieuthuoc.entity.DanhMucThuoc;
import com.example.hieuthuoc.entity.LoaiThuoc;
import com.example.hieuthuoc.repository.DanhMucThuocRepo;
import com.example.hieuthuoc.repository.LoaiThuocRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class DanhMucThuocServiceTest {

    @Autowired
    private DanhMucThuocService danhMucThuocService;

    @Autowired
    private DanhMucThuocRepo danhMucThuocRepo;

    @Autowired
    private LoaiThuocRepo loaiThuocRepo;

    private ModelMapper modelMapper = new ModelMapper();

    private DanhMucThuoc danhMucThuoc;
    private DanhMucThuocDTO danhMucThuocDTO;
    private List<DanhMucThuoc> danhMucThuocList;

    @BeforeEach
    public void setup() {
        // test data
        danhMucThuoc = new DanhMucThuoc();
        danhMucThuoc.setTenDanhMuc("Thuốc kháng sinh");
        danhMucThuoc.setMoTa("Các loại thuốc kháng sinh");
        danhMucThuoc.setLoaiThuocs(new ArrayList<>());
        danhMucThuoc = danhMucThuocRepo.save(danhMucThuoc);

        danhMucThuocDTO = new DanhMucThuocDTO();
        danhMucThuocDTO.setId(danhMucThuoc.getId());
        danhMucThuocDTO.setTenDanhMuc("Thuốc kháng sinh");
        danhMucThuocDTO.setMoTa("Các loại thuốc kháng sinh");

        danhMucThuocList = Arrays.asList(danhMucThuoc);
    }

    @Test
    public void testGetAllDmtService() {
        Long size = danhMucThuocRepo.count();
        // When
        ResponseDTO<List<DanhMucThuoc>> response = danhMucThuocService.getAll();

        // Then
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertEquals(size, response.getData().size());
        assertEquals(danhMucThuocRepo.findAll().get(0).getTenDanhMuc(), response.getData().get(0).getTenDanhMuc());
        assertEquals(size, danhMucThuocRepo.count());
    }

    @Test
    public void testGetDanhMucAnhLoaiThuocService() {
        // When
        ResponseDTO<List<DanhMucThuoc>> response = danhMucThuocService.getDanhMucAnhLoaiThuoc();

        // Then
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertEquals(danhMucThuocRepo.findAllWithLoaiThuocs().size(), response.getData().size());
        assertFalse(response.getData().get(0).getLoaiThuocs().isEmpty());

    }

    @Test
    public void testSearchByTenDanhMucServiceSuccess() {
        // Given
        String searchTerm = "Dược mỹ phẩm";

        // When
        ResponseDTO<List<DanhMucThuoc>> response = danhMucThuocService.searchByTenDanhMuc(searchTerm);

        // Then
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertEquals(1, response.getData().size());
        assertEquals("Dược mỹ phẩm", response.getData().get(0).getTenDanhMuc());
        assertFalse(response.getData()==null);
        assertFalse(response.getData().isEmpty());

        boolean ok = false;
        List<DanhMucThuoc> danhMucThuocs = danhMucThuocRepo.findAll();
        for(DanhMucThuoc dmt : danhMucThuocs) {
            if(dmt.getTenDanhMuc().equalsIgnoreCase("Dược mỹ phẩm")) {
                ok = true;
                break;
            }
        }
        assertTrue(ok);
    }

    @Test
    public void testSearchByTenDanhMucServiceNoResults() {
        // Given
        String searchTerm = "không tồn tại";

        // When
        ResponseDTO<List<DanhMucThuoc>> response = danhMucThuocService.searchByTenDanhMuc(searchTerm);

        // Then
        assertEquals(409, response.getStatus());
        assertEquals("Danh mục thuốc không tồn tại", response.getMsg());
        assertNull(response.getData());
        assertTrue(danhMucThuocRepo.searchByTenDanhMuc(searchTerm).isEmpty());
    }

    @Test
    public void testCreateDmtServiceSuccess() {
        Long size = danhMucThuocRepo.count();
        // Given
        danhMucThuocDTO.setId(null); // New entity
        danhMucThuocDTO.setTenDanhMuc("Thuốc giảm đau");

        // When
        ResponseDTO<DanhMucThuoc> response = danhMucThuocService.create(danhMucThuocDTO);

        // Then
        assertEquals(201, response.getStatus());
        assertEquals("Tạo danh mục thuốc thành công", response.getMsg());
        assertNotNull(response.getData());
        assertEquals(danhMucThuocDTO.getTenDanhMuc(), response.getData().getTenDanhMuc());
        assertTrue(size+1 == danhMucThuocRepo.findAll().size());
    }

    @Test
    public void testCreateDmtServiceAlreadyExists() {
        // Given
        danhMucThuocDTO.setId(null); // New entity

        // When
        ResponseDTO<DanhMucThuoc> response = danhMucThuocService.create(danhMucThuocDTO);

        // Then
        assertEquals(409, response.getStatus());
        assertEquals("Danh mục thuốc đã tồn tại", response.getMsg());
        assertNull(response.getData());
        assertFalse(danhMucThuocRepo.searchByTenDanhMuc(danhMucThuocDTO.getTenDanhMuc()).isEmpty());
    }

    @Test
    public void testUpdateDmtServiceSuccess() {
        // Given
        danhMucThuocDTO.setTenDanhMuc("Thuốc kháng sinh cập nhật");

        // When
        ResponseDTO<DanhMucThuoc> response = danhMucThuocService.update(danhMucThuocDTO);

        // Then
        assertEquals(200, response.getStatus());
        assertEquals("Cập nhật danh mục thuốc thành công", response.getMsg());
        assertNotNull(response.getData());
        assertEquals(danhMucThuocDTO.getTenDanhMuc(), response.getData().getTenDanhMuc());

        assertTrue(danhMucThuocRepo.findById(danhMucThuocDTO.getId()).get().getTenDanhMuc().equals("Thuốc kháng sinh cập nhật"));
    }

    @Test
    public void testUpdateDmtServiceNotFound() {
        // Given
        danhMucThuocDTO.setId(999);

        // When
        ResponseDTO<DanhMucThuoc> response = danhMucThuocService.update(danhMucThuocDTO);

        // Then
        assertEquals(404, response.getStatus());
        assertEquals("Không tìm thấy danh mục thuốc", response.getMsg());
        assertNull(response.getData());
        assertFalse(danhMucThuocRepo.findById(999).isPresent());
    }

    @Test
    public void testDeleteDmtServiceSuccess() {
        // When
        ResponseDTO<Void> response = danhMucThuocService.delete(danhMucThuoc.getId());

        // Then
        assertEquals(200, response.getStatus());
        assertEquals("Xóa danh mục thuốc thành công", response.getMsg());
        assertNull(response.getData());
        assertFalse(danhMucThuocRepo.existsById(danhMucThuoc.getId()));
        assertFalse(danhMucThuocRepo.findById(danhMucThuocDTO.getId()).isPresent());
    }

    @Test
    public void testDeleteDmtServiceNotFound() {
        Long size = danhMucThuocRepo.count();
        // When
        ResponseDTO<Void> response = danhMucThuocService.delete(999);

        // Then
        assertEquals(404, response.getStatus());
        assertEquals("Không tìm thấy danh mục thuốc để xóa", response.getMsg());
        assertNull(response.getData());
        assertTrue(size == danhMucThuocRepo.count());
    }
}