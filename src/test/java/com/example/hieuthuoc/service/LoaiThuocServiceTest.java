package com.example.hieuthuoc.service;

import com.example.hieuthuoc.dto.*;
import com.example.hieuthuoc.entity.*;
import com.example.hieuthuoc.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
public class ThuocServiceTest {

    @Autowired
    private ThuocService thuocService;

    @Autowired
    private ThuocRepo thuocRepo;

    @Autowired
    private LoaiThuocRepo loaiThuocRepo;

    @Autowired
    private NhaSanXuatRepo nhaSanXuatRepo;

    @Autowired
    private DoiTuongRepo doiTuongRepo;

    @Autowired
    private ChiTietDonHangRepo chiTietDonHangRepo;

    @MockBean
    private UploadImageService uploadImageService;

    @MockBean
    private MultipartFile mockFile;

//    @Autowired
    private ModelMapper modelMapper = new ModelMapper();

    // --- Dữ liệu Test Mẫu ---
    private Thuoc thuoc1, thuoc2;
    private ThuocDTO thuocDTO;
    private LoaiThuoc loaiThuoc;
    private NhaSanXuat nhaSanXuat;
    private DoiTuong doiTuong1;
    private SearchDTO searchDTO;
    private SearchThuocDTO searchThuocDTO;

    @BeforeEach
    void setUp() {
        // Create LoaiThuoc
        loaiThuoc = new LoaiThuoc();
        loaiThuoc.setTenLoai("Thuốc giảm đau");
        loaiThuoc = loaiThuocRepo.save(loaiThuoc);

        // Create NhaSanXuat
        nhaSanXuat = new NhaSanXuat();
        nhaSanXuat.setTenNhaSanXuat("NSX ABC");
        nhaSanXuat = nhaSanXuatRepo.save(nhaSanXuat);

        // Create DoiTuong
        doiTuong1 = new DoiTuong();
        doiTuong1.setTenDoiTuong("Người lớn");
        doiTuong1 = doiTuongRepo.save(doiTuong1);

        // Create Thuoc 1
        thuoc1 = new Thuoc();
        thuoc1.setTenThuoc("Paracetamol 500mg");
        thuoc1.setMaThuoc("PARA500");
        thuoc1.setLoaiThuoc(loaiThuoc);
        thuoc1.setNhaSanXuat(nhaSanXuat);
        thuoc1.setGiaBan(10000.0);
        thuoc1.setTrangThai(true);
        thuoc1.setAvatar("old_avatar_url.jpg");
        thuoc1.setDoiTuongs(new ArrayList<>());
        thuoc1.getDoiTuongs().add(doiTuong1);
        thuoc1.setThanhPhanThuocs(new ArrayList<>());
        thuoc1 = thuocRepo.save(thuoc1);

        // Create Thuoc 2
        thuoc2 = new Thuoc();
        thuoc2.setTenThuoc("Panadel Extra");
        thuoc2.setMaThuoc("PANAEXTRA");
        thuoc2.setLoaiThuoc(loaiThuoc);
        thuoc2.setNhaSanXuat(nhaSanXuat);
        thuoc2.setGiaBan(15000.0);
        thuoc2.setTrangThai(true);
        thuoc2.setAvatar("default.jpg");
        thuoc2.setDoiTuongs(Collections.singletonList(doiTuong1));
        thuoc2 = thuocRepo.save(thuoc2);

        // Create ChiTietDonHang for thuocBanChay
        ChiTietDonHang ctdh1 = new ChiTietDonHang();
        ctdh1.setThuoc(thuoc1);
        ctdh1.setSoLuong(10);
        chiTietDonHangRepo.save(ctdh1);

        ChiTietDonHang ctdh2 = new ChiTietDonHang();
        ctdh2.setThuoc(thuoc2);
        ctdh2.setSoLuong(5);
        chiTietDonHangRepo.save(ctdh2);

        // ThuocDTO for create/update
        DoiTuongDTO doiTuongDTO = new DoiTuongDTO();
        doiTuongDTO.setId(doiTuong1.getId());
        doiTuongDTO.setTenDoiTuong("Người lớn");

        thuocDTO = new ThuocDTO();
        thuocDTO.setId(thuoc1.getId()); // For update
        thuocDTO.setTenThuoc("Paracetamol 500mg");
        thuocDTO.setMaThuoc("PARA500");
        thuocDTO.setLoaiThuocId(loaiThuoc.getId());
        thuocDTO.setNhaSanXuatId(nhaSanXuat.getId());
        thuocDTO.setDoiTuongs(Collections.singletonList(doiTuongDTO));
        thuocDTO.setThanhPhanThuocs(Collections.emptyList());

        // SearchDTO
        searchDTO = new SearchDTO();
        searchDTO.setCurrentPage(0);
        searchDTO.setSize(10);

        // SearchThuocDTO
        searchThuocDTO = new SearchThuocDTO();
        searchThuocDTO.setCurrentPage(0);
        searchThuocDTO.setSize(10);
        searchThuocDTO.setKeyWord("Zaromax");
//        searchThuocDTO.setLoaiThuoc("Thuốc giảm đau");
//        searchThuocDTO.setNhaSanXuat("NSX ABC");
        searchThuocDTO.setTrangThai(true);
        searchThuocDTO.setSortedField("tenThuoc");
    }

    @Test
    void getThuocBanChayService_Success_DefaultPagingSort() {
        SearchDTO defaultSearchDTO = new SearchDTO();

        ResponseDTO<PageDTO<List<Thuoc>>> response = thuocService.getThuocBanChay(defaultSearchDTO);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertNotNull(response.getData());
        assertEquals(10, response.getData().getTotalElements());
        assertEquals(1, response.getData().getTotalPages());
        assertFalse(response.getData().getData().isEmpty());

    }

    @Test
    void getThuocBanChayService_Success_CustomPagingSort() {
        searchDTO.setSortedField("giaBan");
        searchDTO.setCurrentPage(0);
        searchDTO.setSize(5);

        ResponseDTO<PageDTO<List<Thuoc>>> response = thuocService.getThuocBanChay(searchDTO);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertNotNull(response.getData());
        assertEquals(10, response.getData().getTotalElements());
        assertEquals(2, response.getData().getTotalPages());
        assertFalse(response.getData().getData().isEmpty());
        assertTrue(response.getData().getData().size()==5);

        Sort sortBy = Sort.by("giaBan").ascending();
        PageRequest pageRequest = PageRequest.of(searchDTO.getCurrentPage(), searchDTO.getSize(), sortBy);
        List<Thuoc> thuocs = chiTietDonHangRepo.findAllThuocBanChay(pageRequest).getContent();
        assertTrue(response.getData().getData().size() == thuocs.size());
    }

    @Test
    void getThuocBanChayService_Success_NullKeyWord() {
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setCurrentPage(0);
        searchDTO.setSize(10);
        searchDTO.setKeyWord(null);

        ResponseDTO<PageDTO<List<Thuoc>>> response = thuocService.getThuocBanChay(searchDTO);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertNotNull(response.getData());
        assertEquals(10, response.getData().getTotalElements());
        assertEquals(1, response.getData().getTotalPages());
        assertFalse(response.getData().getData().isEmpty());
        assertEquals("", searchDTO.getKeyWord());

        Sort sortBy = Sort.by("id").ascending();
        PageRequest pageRequest = PageRequest.of(searchDTO.getCurrentPage(), searchDTO.getSize(), sortBy);
        List<Thuoc> thuocs = chiTietDonHangRepo.findAllThuocBanChay(pageRequest).getContent();
        assertTrue(response.getData().getData().size() == thuocs.size());
    }

    @Test
    void searchThuocService_Success_WithCriteria() {
        ResponseDTO<PageDTO<List<Thuoc>>> response = thuocService.search(searchThuocDTO);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertNotNull(response.getData());
        assertEquals(1, response.getData().getTotalElements());
        assertTrue(response.getData().getData().size() == 1);

    }

    @Test
    void searchThuocService_Success_WithDefaultsAndNullKeyword() {
        SearchThuocDTO defaultSearchDTO = new SearchThuocDTO();
        defaultSearchDTO.setLoaiThuoc(null);
        defaultSearchDTO.setNhaSanXuat(null);
        defaultSearchDTO.setDanhMucThuoc(null);
        defaultSearchDTO.setMinGiaBan(null);
        defaultSearchDTO.setMaxGiaBan(null);
        defaultSearchDTO.setTenDoiTuong(null);
        defaultSearchDTO.setTrangThai(null);

        ResponseDTO<PageDTO<List<Thuoc>>> response = thuocService.search(defaultSearchDTO);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertNotNull(response.getData());
        assertFalse(response.getData().getData().isEmpty());
        assertEquals("", defaultSearchDTO.getKeyWord());

    }

    @Test
    void searchThuocService_Success_DefaultPagingSizeAndKeywordNull() {
        SearchThuocDTO searchDTOWithDefaults = new SearchThuocDTO();
        searchDTOWithDefaults.setSortedField("tenThuoc");
        searchDTOWithDefaults.setLoaiThuoc("Thuốc da liễu");
        searchDTOWithDefaults.setTrangThai(true);

        ResponseDTO<PageDTO<List<Thuoc>>> response = thuocService.search(searchDTOWithDefaults);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertNotNull(response.getData());
        assertEquals(3, response.getData().getTotalElements());
        assertEquals(1, response.getData().getTotalPages());
        assertFalse(response.getData().getData().isEmpty());
        assertEquals("", searchDTOWithDefaults.getKeyWord());
        assertFalse(searchDTOWithDefaults.getKeyWord() == null);

    }

    @Test
    void searchThuocService_Success_DefaultSortAndKeywordNull() {
        SearchThuocDTO searchDTOWithDefaults = new SearchThuocDTO();
        searchDTOWithDefaults.setCurrentPage(1);
        searchDTOWithDefaults.setSize(5);
        searchDTOWithDefaults.setNhaSanXuat("STADA");

        ResponseDTO<PageDTO<List<Thuoc>>> response = thuocService.search(searchDTOWithDefaults);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertNotNull(response.getData());
        assertEquals(10, response.getData().getTotalElements());
        assertEquals(2, response.getData().getTotalPages());
        assertEquals(5, response.getData().getData().size());
        assertFalse(response.getData().getData().isEmpty());
        assertEquals("", searchDTOWithDefaults.getKeyWord());
    }

    @Test
    void searchThuocService_Success_EmptySortedField() {
        SearchThuocDTO searchDTOWithEmptySort = new SearchThuocDTO();
        searchDTOWithEmptySort.setSortedField("");
        searchDTOWithEmptySort.setCurrentPage(0);
        searchDTOWithEmptySort.setSize(10);
        searchDTOWithEmptySort.setKeyWord("test1111");
        searchDTOWithEmptySort.setMinGiaBan(100.0);

        ResponseDTO<PageDTO<List<Thuoc>>> response = thuocService.search(searchDTOWithEmptySort);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertNotNull(response.getData());
        assertEquals(0, response.getData().getTotalElements());
        assertEquals(0, response.getData().getTotalPages());
        assertTrue(response.getData().getData().isEmpty());
        assertEquals("test1111", searchDTOWithEmptySort.getKeyWord());
    }

    @Test
    void searchThuocService_Success_EmptyKeyword() {
        SearchThuocDTO searchDTOWithEmptyKeyword = new SearchThuocDTO();
        searchDTOWithEmptyKeyword.setSortedField("tenThuoc");
        searchDTOWithEmptyKeyword.setCurrentPage(0);
        searchDTOWithEmptyKeyword.setSize(10);
        searchDTOWithEmptyKeyword.setKeyWord("");
        searchDTOWithEmptyKeyword.setLoaiThuoc("Thuốc da liễu");

        ResponseDTO<PageDTO<List<Thuoc>>> response = thuocService.search(searchDTOWithEmptyKeyword);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertNotNull(response.getData());
        assertEquals(3, response.getData().getTotalElements());
        assertEquals(1, response.getData().getTotalPages());
        assertFalse(response.getData().getData().isEmpty());
        assertEquals("", searchDTOWithEmptyKeyword.getKeyWord());

        List<Thuoc> thuocs = thuocRepo.findAll();

    }

    @Test
    void getThuocByIdService_Found() {
        Integer id = thuoc1.getId();

        ResponseDTO<Thuoc> response = thuocService.getById(id);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertNotNull(response.getData());
        assertEquals(thuoc1.getId(), response.getData().getId());

        assertTrue(thuocRepo.findById(id).isPresent());
    }

    @Test
    void getThuocByIdService_NotFound() {
        Integer id = 99;

        ResponseDTO<Thuoc> response = thuocService.getById(id);

        assertNotNull(response);
        assertEquals(404, response.getStatus());
        assertEquals("Không tìm thấy thuốc", response.getMsg());
        assertNull(response.getData());
    }

    @Test
    void createThuocService_Success_NoFile() {
        Long size = thuocRepo.count();
        thuocDTO.setId(null);
        thuocDTO.setFile(null);
        thuocDTO.setMaThuoc("NEWPARA");
        thuocDTO.setTenThuoc("New Paracetamol");
        thuocDTO.setNhaSanXuatId(1);
        thuocDTO.setLoaiThuocId(1);
        thuocDTO.setDoiTuongs(new ArrayList<>());
        thuocDTO.setThanhPhanThuocs(new ArrayList<>());

        ResponseDTO<Thuoc> response = thuocService.create(thuocDTO);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertNotNull(response.getData());
        assertNotNull(response.getData().getId());
        assertEquals(thuocDTO.getTenThuoc(), response.getData().getTenThuoc());
        assertNull(response.getData().getAvatar());

        verify(uploadImageService, never()).uploadImage(any(), anyString());
        assertEquals(size+1, thuocRepo.count());
    }

    @Test
    void createThuocService_Success_WithFile() {
        Long size = thuocRepo.count();
        thuocDTO.setId(null);
        thuocDTO.setFile(mockFile);
        thuocDTO.setMaThuoc("NEWPARA");
        thuocDTO.setTenThuoc("New Paracetamol");
        String expectedAvatarUrl = "http://cloudinary.com/image.jpg";

        when(mockFile.isEmpty()).thenReturn(false);
        when(uploadImageService.uploadImage(eq(mockFile), anyString())).thenReturn(expectedAvatarUrl);

        ResponseDTO<Thuoc> response = thuocService.create(thuocDTO);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertNotNull(response.getData());
        assertNotNull(response.getData().getId());
        assertEquals(expectedAvatarUrl, response.getData().getAvatar());

        verify(mockFile).isEmpty();
        verify(uploadImageService).uploadImage(eq(mockFile), anyString());
        assertEquals(size+1, thuocRepo.count());
    }

    @Test
    void createThuocService_Fail_MaThuocExists() {
        Long size = thuocRepo.count();
        thuocDTO.setId(null);
        thuocDTO.setMaThuoc("PARA500");

        ResponseDTO<Thuoc> response = thuocService.create(thuocDTO);

        assertNotNull(response);
        assertEquals(409, response.getStatus());
        assertEquals("Mã thuốc đã tồn tại", response.getMsg());
        assertNull(response.getData());

        assertEquals(size, thuocRepo.count());
    }

    @Test
    void createThuocService_Fail_DoiTuongNotFound() {
        Long size = thuocRepo.count();
        DoiTuongDTO nonExistingDoiTuongDTO = new DoiTuongDTO();
        nonExistingDoiTuongDTO.setId(999);

        thuocDTO.setId(null);
        thuocDTO.setMaThuoc("NEWPARA");
        thuocDTO.setTenThuoc("New Paracetamol");
        thuocDTO.setDoiTuongs(Arrays.asList(nonExistingDoiTuongDTO));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            thuocService.create(thuocDTO);
        });

        assertEquals("Đối tượng không tồn tại: ID 999", exception.getMessage());
        assertEquals(size, thuocRepo.count());
    }

    @Test
    void createThuocService_Fail_NhaSanXuatNotFound() {
        Long size = thuocRepo.count();
        thuocDTO.setId(null);
        thuocDTO.setMaThuoc("NEWPARA");
        thuocDTO.setTenThuoc("New Paracetamol");
        thuocDTO.setNhaSanXuatId(999);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            thuocService.create(thuocDTO);
        });

        assertEquals("Nhà sản xuất không tồn tại", exception.getMessage());
        assertEquals(size, thuocRepo.count());
    }

    @Test
    void createThuocService_Fail_TenThuocExists() {
        Long size = thuocRepo.count();
        ThuocDTO inputDto = new ThuocDTO();
        inputDto.setTenThuoc("Paracetamol 500mg");
        inputDto.setMaThuoc("NEW_CODE_123");
        inputDto.setLoaiThuocId(loaiThuoc.getId());
        inputDto.setNhaSanXuatId(nhaSanXuat.getId());
        inputDto.setDoiTuongs(Collections.emptyList());
        inputDto.setThanhPhanThuocs(Collections.emptyList());
        inputDto.setFile(null);

        ResponseDTO<Thuoc> response = thuocService.create(inputDto);

        assertNotNull(response);
        assertEquals(409, response.getStatus());
        assertEquals("Tên thuốc đã tồn tại", response.getMsg());
        assertNull(response.getData());

        assertEquals(size, thuocRepo.count());
    }

    @Test
    void createThuocService_Success_WithThanhPhanThuocs() {
        Long size = thuocRepo.count();
        ThanhPhanThuocDTO tpDto1 = new ThanhPhanThuocDTO();
        tpDto1.setTenThanhPhan("Paracetamol");
        tpDto1.setHamLuong("500mg");
        ThanhPhanThuocDTO tpDto2 = new ThanhPhanThuocDTO();
        tpDto2.setTenThanhPhan("Caffeine");
        tpDto2.setHamLuong("65mg");
        List<ThanhPhanThuocDTO> thanhPhanDtoList = Arrays.asList(tpDto1, tpDto2);

        ThuocDTO inputDto = new ThuocDTO();
        inputDto.setTenThuoc("Panadol Extra plus");
        inputDto.setMaThuoc("PANE01");
        inputDto.setLoaiThuocId(loaiThuoc.getId());
        inputDto.setNhaSanXuatId(nhaSanXuat.getId());
        inputDto.setThanhPhanThuocs(thanhPhanDtoList);
        inputDto.setDoiTuongs(Collections.emptyList());
        inputDto.setFile(null);

        ResponseDTO<Thuoc> response = thuocService.create(inputDto);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertNotNull(response.getData());
        assertNotNull(response.getData().getId());

        Thuoc savedThuoc = thuocRepo.findById(response.getData().getId()).orElseThrow();
        assertNotNull(savedThuoc.getThanhPhanThuocs());
        assertFalse(savedThuoc.getThanhPhanThuocs().isEmpty());
        assertEquals(thanhPhanDtoList.size(), savedThuoc.getThanhPhanThuocs().size());

        ThanhPhanThuoc savedTp1 = savedThuoc.getThanhPhanThuocs().get(0);
        assertEquals(tpDto1.getTenThanhPhan(), savedTp1.getTenThanhPhan());
        assertEquals(tpDto1.getHamLuong(), savedTp1.getHamLuong());
        assertNotNull(savedTp1.getThuoc());
        assertEquals(savedThuoc.getId(), savedTp1.getThuoc().getId());

        ThanhPhanThuoc savedTp2 = savedThuoc.getThanhPhanThuocs().get(1);
        assertEquals(tpDto2.getTenThanhPhan(), savedTp2.getTenThanhPhan());
        assertEquals(tpDto2.getHamLuong(), savedTp2.getHamLuong());
        assertNotNull(savedTp2.getThuoc());
        assertEquals(savedThuoc.getId(), savedTp2.getThuoc().getId());

        assertEquals(size+1, thuocRepo.count());
    }

    @Test
    void createThuocService_Fail_WithEmptyThanhPhanThuocs() {
        Long size = thuocRepo.count();
        ThuocDTO inputDto = new ThuocDTO();
        inputDto.setTenThuoc("Test Thuoc No TP");
        inputDto.setMaThuoc("TESTNOTP");
        inputDto.setLoaiThuocId(loaiThuoc.getId());
        inputDto.setNhaSanXuatId(nhaSanXuat.getId());
        inputDto.setThanhPhanThuocs(Collections.emptyList());
        inputDto.setDoiTuongs(Collections.emptyList());
        inputDto.setFile(null);

        ResponseDTO<Thuoc> response = thuocService.create(inputDto);

        assertNotNull(response);
        assertEquals(400, response.getStatus());
        assertEquals("Thành phần thuốc không được bỏ trống", response.getMsg());
        assertEquals(size, thuocRepo.count());
    }

    @Test
    void createThuocService_Fail_LoaiThuocNotFound() {
        Long size = thuocRepo.count();
        thuocDTO.setId(null);
        thuocDTO.setMaThuoc("NEWPARA");
        thuocDTO.setTenThuoc("New Paracetamol");
        thuocDTO.setLoaiThuocId(999);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            thuocService.create(thuocDTO);
        });
        assertEquals("Loại thuốc không tồn tại", exception.getMessage());

        assertEquals(size, thuocRepo.count());
    }

    @Test
    void updateThuocService_Fail_MaThuocConflict() {
        ThuocDTO inputDto = new ThuocDTO();
        inputDto.setId(thuoc1.getId());
        inputDto.setMaThuoc("PANAEXTRA");
        inputDto.setTenThuoc("Thuoc Cu");
        inputDto.setLoaiThuocId(loaiThuoc.getId());
        inputDto.setNhaSanXuatId(nhaSanXuat.getId());

        ResponseDTO<Thuoc> response = thuocService.update(inputDto);

        assertNotNull(response);
        assertEquals(409, response.getStatus());
        assertEquals("Mã thuốc đã tồn tại", response.getMsg());
        assertNull(response.getData());

        assertTrue(thuocRepo.existsByMaThuoc("PANAEXTRA"));
    }

    @Test
    void updateThuocService_Fail_LoaiThuocNotFound() {
        ThuocDTO inputDto = new ThuocDTO();
        inputDto.setId(thuoc1.getId());
        inputDto.setMaThuoc("MA001");
        inputDto.setTenThuoc("Thuoc Moi");
        inputDto.setLoaiThuocId(999);
        inputDto.setNhaSanXuatId(nhaSanXuat.getId());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            thuocService.update(inputDto);
        });
        assertEquals("Loại thuốc không tồn tại", exception.getMessage());

        assertFalse(loaiThuocRepo.existsById(999));
    }

    @Test
    void updateThuocService_Fail_NhaSanXuatNotFound() {
        ThuocDTO inputDto = new ThuocDTO();
        inputDto.setId(thuoc1.getId());
        inputDto.setMaThuoc("MA001");
        inputDto.setTenThuoc("Thuoc Moi");
        inputDto.setLoaiThuocId(loaiThuoc.getId());
        inputDto.setNhaSanXuatId(999);
        inputDto.setDoiTuongs(Collections.emptyList());
        inputDto.setThanhPhanThuocs(Collections.emptyList());
        inputDto.setFile(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            thuocService.update(inputDto);
        });
        assertEquals("Nhà sản xuất không tồn tại", exception.getMessage());

        assertFalse(nhaSanXuatRepo.existsById(999));
    }

    @Test
    void updateThuocService_Success_WithFileChangeNoExistingAvatar() {
        thuoc1.setAvatar(null);
        thuocRepo.save(thuoc1);

        ThuocDTO inputDto = new ThuocDTO();
        inputDto.setId(thuoc1.getId());
        inputDto.setMaThuoc("PARA500");
        inputDto.setTenThuoc("Thuoc Moi");
        inputDto.setLoaiThuocId(loaiThuoc.getId());
        inputDto.setNhaSanXuatId(nhaSanXuat.getId());
        inputDto.setFile(mockFile);

        when(mockFile.isEmpty()).thenReturn(false);
        when(uploadImageService.uploadImage(eq(mockFile), anyString())).thenReturn("http://cloudinary.com/new_image.jpg");

        ResponseDTO<Thuoc> response = thuocService.update(inputDto);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertNotNull(response.getData());
        assertEquals(inputDto.getTenThuoc(), response.getData().getTenThuoc());
        assertEquals("http://cloudinary.com/new_image.jpg", response.getData().getAvatar());

        verify(uploadImageService, never()).deleteImage(anyString());
        verify(uploadImageService).uploadImage(eq(mockFile), anyString());
        assertTrue(thuocRepo.findById(thuoc1.getId()).get().getAvatar().equals("http://cloudinary.com/new_image.jpg"));
    }

    @Test
    void updateThuocService_Success_WithFileChangeAndExistingAvatar() {
        thuoc1.setAvatar("http://cloudinary.com/old_image.jpg");
        ThuocDTO inputDto = new ThuocDTO();
        inputDto.setId(thuoc1.getId());
        inputDto.setMaThuoc("PARA500");
        inputDto.setTenThuoc("Thuoc Moi");
        inputDto.setLoaiThuocId(loaiThuoc.getId());
        inputDto.setNhaSanXuatId(nhaSanXuat.getId());
        inputDto.setAvatar("http://cloudinary.com/old_image.jpg");
        inputDto.setFile(mockFile);

        when(mockFile.isEmpty()).thenReturn(false);
        doNothing().when(uploadImageService).deleteImage(eq("http://cloudinary.com/old_image.jpg"));
        when(uploadImageService.uploadImage(eq(mockFile), anyString())).thenReturn("http://cloudinary.com/new_image.jpg");

        ResponseDTO<Thuoc> response = thuocService.update(inputDto);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertNotNull(response.getData());
        assertEquals(inputDto.getTenThuoc(), response.getData().getTenThuoc());
        assertEquals("http://cloudinary.com/new_image.jpg", response.getData().getAvatar());

        verify(uploadImageService).deleteImage(eq("http://cloudinary.com/old_image.jpg"));
        verify(uploadImageService).uploadImage(eq(mockFile), anyString());

        assertTrue(thuocRepo.findById(thuoc1.getId()).get().getAvatar().equals("http://cloudinary.com/new_image.jpg"));
    }

    @Test
    void updateThuocService_Success_WithThanhPhanThuocUpdate() {
        int size =  thuocRepo.findById(thuoc1.getId()).get().getThanhPhanThuocs().size();
        ThanhPhanThuocDTO tpDtoUpdate1 = new ThanhPhanThuocDTO();
        tpDtoUpdate1.setTenThanhPhan("Acetylsalicylic Acid");
        tpDtoUpdate1.setHamLuong("81mg");
        ThanhPhanThuocDTO tpDtoUpdate2 = new ThanhPhanThuocDTO();
        tpDtoUpdate2.setTenThanhPhan("Clopidogrel");
        tpDtoUpdate2.setHamLuong("75mg");
        ThanhPhanThuocDTO tpDtoUpdate3 = new ThanhPhanThuocDTO();
        tpDtoUpdate3.setTenThanhPhan("Paracetamol");
        tpDtoUpdate3.setHamLuong("50mg");
        List<ThanhPhanThuocDTO> updatedTpListDto = Arrays.asList(tpDtoUpdate1, tpDtoUpdate2, tpDtoUpdate3);

        ThuocDTO inputDto = new ThuocDTO();
        inputDto.setId(thuoc1.getId());
        inputDto.setMaThuoc("PARA500");
        inputDto.setTenThuoc("Thuoc Moi Update TP");
        inputDto.setLoaiThuocId(loaiThuoc.getId());
        inputDto.setNhaSanXuatId(nhaSanXuat.getId());
        inputDto.setThanhPhanThuocs(updatedTpListDto);
        inputDto.setDoiTuongs(Collections.emptyList());
        inputDto.setFile(null);

        ResponseDTO<Thuoc> response = thuocService.update(inputDto);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertNotNull(response.getData());
        assertEquals(inputDto.getId(), response.getData().getId());
        assertEquals(inputDto.getTenThuoc(), response.getData().getTenThuoc());

        Thuoc savedThuoc = thuocRepo.findById(response.getData().getId()).orElseThrow();
        assertNotNull(savedThuoc.getThanhPhanThuocs());
        assertFalse(savedThuoc.getThanhPhanThuocs().isEmpty());
        assertEquals(updatedTpListDto.size(), savedThuoc.getThanhPhanThuocs().size());

        ThanhPhanThuoc savedTp1 = savedThuoc.getThanhPhanThuocs().stream()
                .filter(tp -> tp.getTenThanhPhan().equals(tpDtoUpdate1.getTenThanhPhan()))
                .findFirst().orElseThrow();
        assertEquals(tpDtoUpdate1.getHamLuong(), savedTp1.getHamLuong());
        assertNotNull(savedTp1.getThuoc());
        assertEquals(savedThuoc.getId(), savedTp1.getThuoc().getId());

        ThanhPhanThuoc savedTp2 = savedThuoc.getThanhPhanThuocs().stream()
                .filter(tp -> tp.getTenThanhPhan().equals(tpDtoUpdate2.getTenThanhPhan()))
                .findFirst().orElseThrow();
        assertEquals(tpDtoUpdate2.getHamLuong(), savedTp2.getHamLuong());
        assertNotNull(savedTp2.getThuoc());
        assertEquals(savedThuoc.getId(), savedTp2.getThuoc().getId());
        System.out.println(thuocRepo.findById(thuoc1.getId()).get().getThanhPhanThuocs().size());
        assertFalse(size == thuocRepo.findById(thuoc1.getId()).get().getThanhPhanThuocs().size());
    }

    @Test
    void updateThuocService_Fail_WithNullOrEmptyThanhPhanThuocs() {
        int size = thuocRepo.findById(thuoc1.getId()).get().getThanhPhanThuocs().size();
        ThanhPhanThuoc oldTp = new ThanhPhanThuoc();
        oldTp.setTenThanhPhan("Old Component");
        oldTp.setHamLuong("10mg");
        oldTp.setThuoc(thuoc1);
        thuoc1.setThanhPhanThuocs(new ArrayList<>(Arrays.asList(oldTp)));
        thuocRepo.save(thuoc1);

        ThuocDTO inputDto = new ThuocDTO();
        inputDto.setId(thuoc1.getId());
        inputDto.setMaThuoc("PARA500");
        inputDto.setTenThuoc("Thuoc Update TP Rong Same");
        inputDto.setLoaiThuocId(loaiThuoc.getId());
        inputDto.setNhaSanXuatId(nhaSanXuat.getId());
        inputDto.setThanhPhanThuocs(null);
        inputDto.setDoiTuongs(Collections.emptyList());
        inputDto.setFile(null);

        ResponseDTO<Thuoc> response = thuocService.update(inputDto);

        assertNotNull(response);
        assertEquals(400, response.getStatus());
        assertEquals("Danh sách thành phần thuốc phải khác rỗng", response.getMsg());

        assertTrue(size == thuocRepo.findById(thuoc1.getId()).get().getThanhPhanThuocs().size());
    }

    @Test
    void updateThuocService_Success_NoFileChange() {
        thuoc1.setAvatar("Avarta");
        String url = thuoc1.getAvatar();
        thuocDTO.setFile(null);
        thuocDTO.setTenThuoc("Paracetamol 500mg Updated");

        ResponseDTO<Thuoc> response = thuocService.update(thuocDTO);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertNotNull(response.getData());
        assertEquals(thuocDTO.getTenThuoc(), response.getData().getTenThuoc());
        assertEquals(thuoc1.getAvatar(), response.getData().getAvatar());

        verify(uploadImageService, never()).deleteImage(anyString());
        verify(uploadImageService, never()).uploadImage(any(), anyString());

    }

    @Test
    void updateThuocService_Fail_NotFound() {
        thuocDTO.setId(99);

        ResponseDTO<Thuoc> response = thuocService.update(thuocDTO);

        assertNotNull(response);
        assertEquals(404, response.getStatus());
        assertEquals("Không tìm thấy thuốc", response.getMsg());
        assertNull(response.getData());

        assertFalse(thuocRepo.findById(99).isPresent());
    }

    @Test
    void updateThuocService_Fail_TenThuocConflict() {
        thuocDTO.setTenThuoc("Panadel Extra");

        ResponseDTO<Thuoc> response = thuocService.update(thuocDTO);

        assertNotNull(response);
        assertEquals(409, response.getStatus());
        assertEquals("Tên thuốc đã tồn tại", response.getMsg());
        assertNull(response.getData());

        assertTrue(thuocRepo.existsByTenThuoc("Panadel Extra"));
    }

    @Test
    void updateThuocService_Fail_DoiTuongNotFound() {
        DoiTuongDTO nonExistingDoiTuongDTO = new DoiTuongDTO();
        nonExistingDoiTuongDTO.setId(999);

        thuocDTO.setDoiTuongs(Arrays.asList(nonExistingDoiTuongDTO));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            thuocService.update(thuocDTO);
        });

        assertEquals("Đối tượng không tồn tại: ID 999", exception.getMessage());
        assertFalse(doiTuongRepo.findById(999).isPresent());
    }

    @Test
    void deleteThuocService_Success() {
        Integer id = thuoc1.getId();

        ResponseDTO<Void> response = thuocService.delete(id);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertNull(response.getData());

        assertFalse(thuocRepo.findById(id).isPresent());
    }

    @Test
    void deleteThuocService_Fail_ThrowsException() {
        Integer id = 99;

        assertThrows(Exception.class, () -> {
            thuocService.delete(id);
        });

        assertFalse(thuocRepo.findById(id).isPresent());
    }
}
