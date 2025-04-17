package com.example.hieuthuoc.service;

import com.example.hieuthuoc.dto.*;
import com.example.hieuthuoc.entity.*;
import com.example.hieuthuoc.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*; // Import static Mockito methods

@ExtendWith(MockitoExtension.class) // Tích hợp Mockito với JUnit 5
class TestThuocService {

    @Mock // Tạo mock cho các repository và service phụ thuộc
    private ThuocRepo thuocRepo;
    @Mock
    private LoaiThuocRepo loaiThuocRepo;
    @Mock
    private NhaSanXuatRepo nhaSanXuatRepo;
    @Mock
    private DoiTuongRepo doiTuongRepo;
    // @Mock DoiTuongSdThuocRepo doiTuongSdThuocRepo; // Mock nếu cần dùng trực tiếp
    // @Mock ThanhPhanThuocRepo thanhPhanThuocRepo; // Mock nếu cần dùng trực tiếp
    @Mock
    private ChiTietDonHangRepo chiTietDonHangRepo;
    @Mock
    private UploadImageService uploadImageService;
    @Mock // Mock cả MultipartFile nếu cần kiểm tra sâu hơn
    private MultipartFile mockFile;



    @InjectMocks // Tạo instance của ThuocServiceImpl và inject các mock ở trên vào
    private ThuocServiceImpl thuocService;

    // Dùng @Spy cho ModelMapper vì nó được khởi tạo bằng `new` trong Service
    // Nếu bạn muốn hành vi mapping mặc định thì dùng Spy hoặc khởi tạo trực tiếp
    // Nếu bạn muốn kiểm soát chính xác việc mapping thì dùng @Mock
    // Ở đây, ta dùng Spy để giống với việc sử dụng instance thật trong service
    @Spy
    private ModelMapper modelMapper = new ModelMapper();


    // --- Dữ liệu Test Mẫu ---
    private Thuoc thuoc1, thuoc2;
    private ThuocDTO thuocDTO;
    private LoaiThuoc loaiThuoc;
    private NhaSanXuat nhaSanXuat;
    private DoiTuong doiTuong1;
    private Page<Thuoc> thuocPage, thuocPageSearch;
    private SearchDTO searchDTO;
    private SearchThuocDTO searchThuocDTO;


    @BeforeEach
    void setUp() {
        // Khởi tạo dữ liệu dùng chung cho các test case
        loaiThuoc = new LoaiThuoc();
        loaiThuoc.setId(1);
        loaiThuoc.setTenLoai("Thuốc giảm đau");

        nhaSanXuat = new NhaSanXuat();
        nhaSanXuat.setId(1);
        nhaSanXuat.setTenNhaSanXuat("NSX ABC");

        doiTuong1 = new DoiTuong();
        doiTuong1.setId(1);
        doiTuong1.setTenDoiTuong("Người lớn");

        thuoc1 = new Thuoc();
        thuoc1.setId(1);
        thuoc1.setTenThuoc("Paracetamol 500mg");
        thuoc1.setMaThuoc("PARA500");
        thuoc1.setLoaiThuoc(loaiThuoc);
        thuoc1.setNhaSanXuat(nhaSanXuat);
        thuoc1.setGiaBan(10000.0);
        thuoc1.setTrangThai(true);
        thuoc1.setAvatar("old_avatar_url.jpg");

        thuoc2 = new Thuoc();
        thuoc2.setId(2);
        thuoc2.setTenThuoc("Panadel Extra");
        thuoc2.setMaThuoc("PANAEXTRA");
        thuoc2.setLoaiThuoc(loaiThuoc);
        thuoc2.setNhaSanXuat(nhaSanXuat);
        thuoc2.setGiaBan(15000.0);
        thuoc2.setTrangThai(true);
        thuoc2.setAvatar("default.jpg");

        DoiTuongDTO doiTuongDTO = new DoiTuongDTO();
        doiTuongDTO.setId(1);
        doiTuongDTO.setTenDoiTuong("Người lớn");

        thuocDTO = new ThuocDTO();
        thuocDTO.setId(1); // Cho update
        thuocDTO.setTenThuoc("Paracetamol 500mg");
        thuocDTO.setMaThuoc("PARA500");
        thuocDTO.setLoaiThuocId(1);
        thuocDTO.setNhaSanXuatId(1);
        thuocDTO.setDoiTuongs(Collections.singletonList(doiTuongDTO));
        thuocDTO.setThanhPhanThuocs(Collections.emptyList());


        // Dữ liệu phân trang mẫu
        List<Thuoc> thuocList = Arrays.asList(thuoc1, thuoc2);
        thuocPage = new PageImpl<>(thuocList, PageRequest.of(0, 10), 2L); // Page chứa 1 thuốc
        thuocPageSearch = new PageImpl<>(Arrays.asList(thuoc1), PageRequest.of(0, 10), 1L);

        // SearchDTO mẫu
        searchDTO = new SearchDTO();
        searchDTO.setCurrentPage(0);
        searchDTO.setSize(10);

        // SearchThuocDTO mẫu
        searchThuocDTO = new SearchThuocDTO();
        searchThuocDTO.setCurrentPage(0);
        searchThuocDTO.setSize(10);
        searchThuocDTO.setKeyWord("Para");
        searchThuocDTO.setLoaiThuoc("Thuốc giảm đau");
        searchThuocDTO.setNhaSanXuat("NSX ABC");
        searchThuocDTO.setTrangThai(true);
        searchThuocDTO.setSortedField("tenThuoc");
    }


    @Test
    void getThuocBanChay_Success_DefaultPagingSort() {
        // Input
        SearchDTO defaultSearchDTO = new SearchDTO(); // Không set page, size, sort

        // Mocking
        PageRequest expectedPageRequest = PageRequest.of(0, 20, Sort.by("tenThuoc").ascending()); // Defaults
        when(chiTietDonHangRepo.findAllThuocBanChay(eq(expectedPageRequest))).thenReturn(thuocPage);

        // Execution
        ResponseDTO<PageDTO<List<Thuoc>>> response = thuocService.getThuocBanChay(defaultSearchDTO);

        // Assertions
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertNotNull(response.getData());
        assertEquals(2, response.getData().getTotalElements());
        assertEquals(1, response.getData().getTotalPages());
        assertFalse(response.getData().getData().isEmpty());
        assertEquals(thuoc1.getTenThuoc(), response.getData().getData().get(0).getTenThuoc());
        assertEquals(thuoc2.getTenThuoc(), response.getData().getData().get(1).getTenThuoc());
        verify(chiTietDonHangRepo).findAllThuocBanChay(eq(expectedPageRequest));
    }

    @Test
    void getThuocBanChay_Success_CustomPagingSort() {
        // Input
        searchDTO.setSortedField("giaBan"); // Custom sort
        searchDTO.setCurrentPage(1);
        searchDTO.setSize(5);

        // Mocking
        PageRequest expectedPageRequest = PageRequest.of(1, 5, Sort.by("giaBan").ascending());
        when(chiTietDonHangRepo.findAllThuocBanChay(eq(expectedPageRequest))).thenReturn(thuocPage); // Trả về cùng page data mẫu

        // Execution
        ResponseDTO<PageDTO<List<Thuoc>>> response = thuocService.getThuocBanChay(searchDTO);

        // Assertions
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertEquals(2, response.getData().getTotalElements()); // Vẫn là total elements của page mock
        assertNotNull(response.getData());
        assertEquals(2, response.getData().getTotalElements());
        assertEquals(1, response.getData().getTotalPages());
        assertFalse(response.getData().getData().isEmpty());
        assertEquals(thuoc1.getMaThuoc(), response.getData().getData().get(0).getMaThuoc());
        assertEquals(thuoc2.getMaThuoc(), response.getData().getData().get(1).getMaThuoc());
        verify(chiTietDonHangRepo).findAllThuocBanChay(eq(expectedPageRequest));
    }

    @Test
    void getThuocBanChay_Success_NullKeyWord() {
        // Input
        SearchDTO searchDTOWithNullKeyword = new SearchDTO();
        searchDTOWithNullKeyword.setCurrentPage(0);
        searchDTOWithNullKeyword.setSize(10);
        searchDTOWithNullKeyword.setKeyWord(null);

        // Mocking
        PageRequest expectedPageRequest = PageRequest.of(0, 10, Sort.by("tenThuoc").ascending());
        when(chiTietDonHangRepo.findAllThuocBanChay(eq(expectedPageRequest))).thenReturn(thuocPage);

        // Execution
        ResponseDTO<PageDTO<List<Thuoc>>> response = thuocService.getThuocBanChay(searchDTOWithNullKeyword);

        // Assertions
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertNotNull(response.getData());
        assertEquals(2, response.getData().getTotalElements());
        assertEquals(1, response.getData().getTotalPages());
        assertFalse(response.getData().getData().isEmpty());
        verify(chiTietDonHangRepo).findAllThuocBanChay(eq(expectedPageRequest));
    }

    @Test
    void search_Success_WithCriteria() {
        Sort expectedSort = Sort.by("tenThuoc").ascending();
        PageRequest expectedPageRequest = PageRequest.of(0, 10, expectedSort);
        when(thuocRepo.search(
                eq(searchThuocDTO.getKeyWord()), eq(searchThuocDTO.getLoaiThuoc()),
                eq(searchThuocDTO.getNhaSanXuat()), eq(searchThuocDTO.getDanhMucThuoc()),
                eq(searchThuocDTO.getMinGiaBan()), eq(searchThuocDTO.getMaxGiaBan()),
                eq(searchThuocDTO.getTenDoiTuong()), eq(searchThuocDTO.getTrangThai()),
                eq(expectedPageRequest))
        ).thenReturn(thuocPageSearch);

        ResponseDTO<PageDTO<List<Thuoc>>> response = thuocService.search(searchThuocDTO);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertNotNull(response.getData());
        assertEquals(1, response.getData().getTotalElements());
        assertFalse(response.getData().getData().isEmpty());
        verify(thuocRepo).search(
                eq(searchThuocDTO.getKeyWord()), eq(searchThuocDTO.getLoaiThuoc()),
                eq(searchThuocDTO.getNhaSanXuat()), eq(searchThuocDTO.getDanhMucThuoc()),
                eq(searchThuocDTO.getMinGiaBan()), eq(searchThuocDTO.getMaxGiaBan()),
                eq(searchThuocDTO.getTenDoiTuong()), eq(searchThuocDTO.getTrangThai()),
                eq(expectedPageRequest));
    }

    @Test
    void getById_Found() {
        Integer id = 1;
        when(thuocRepo.findById(eq(id))).thenReturn(Optional.of(thuoc1));

        // Execution
        ResponseDTO<Thuoc> response = thuocService.getById(id);

        // Assertions
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertNotNull(response.getData());
        assertEquals(thuoc1.getId(), response.getData().getId());
        verify(thuocRepo).findById(eq(id));
    }

    @Test
    void getById_NotFound() {
        Integer id = 99;
        when(thuocRepo.findById(eq(id))).thenReturn(Optional.empty());

        ResponseDTO<Thuoc> response = thuocService.getById(id);

        assertNotNull(response);
        assertEquals(404, response.getStatus());
        assertEquals("Không tìm thấy thuốc", response.getMsg());
        assertNull(response.getData());
        verify(thuocRepo).findById(eq(id));
    }

    @Test
    void create_Success_NoFile() {
        thuocDTO.setId(null); // ID null khi tạo mới
        thuocDTO.setFile(null);

        when(thuocRepo.existsByMaThuoc(anyString())).thenReturn(false);
        when(thuocRepo.existsByTenThuoc(anyString())).thenReturn(false);
        when(loaiThuocRepo.findById(eq(thuocDTO.getLoaiThuocId()))).thenReturn(Optional.of(loaiThuoc));
        when(nhaSanXuatRepo.findById(eq(thuocDTO.getNhaSanXuatId()))).thenReturn(Optional.of(nhaSanXuat));
        when(doiTuongRepo.findById(anyInt())).thenReturn(Optional.of(doiTuong1)); // Giả sử tìm thấy đối tượng
        // Mock save trả về đối tượng đã được gán ID
        when(thuocRepo.save(any(Thuoc.class))).thenAnswer(invocation -> {
            Thuoc savedThuoc = invocation.getArgument(0);
            savedThuoc.setId(100); // Gán ID giả định sau khi lưu
            return savedThuoc;
        });

        ResponseDTO<Thuoc> response = thuocService.create(thuocDTO);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertNotNull(response.getData());
        assertEquals(100, response.getData().getId()); // Kiểm tra ID đã được gán
        assertEquals(thuocDTO.getTenThuoc(), response.getData().getTenThuoc());
        assertNull(response.getData().getAvatar()); // Không có file nên avatar null

        verify(thuocRepo).existsByMaThuoc(eq(thuocDTO.getMaThuoc()));
        verify(thuocRepo).existsByTenThuoc(eq(thuocDTO.getTenThuoc()));
        verify(loaiThuocRepo).findById(eq(thuocDTO.getLoaiThuocId()));
        verify(nhaSanXuatRepo).findById(eq(thuocDTO.getNhaSanXuatId()));
        verify(doiTuongRepo, times(thuocDTO.getDoiTuongs().size())).findById(anyInt());
        verify(uploadImageService, never()).uploadImage(any(), anyString()); // Không gọi upload
        verify(thuocRepo).save(any(Thuoc.class));
    }

    @Test
    void create_Success_WithFile() {
        thuocDTO.setId(null);
        thuocDTO.setFile(mockFile);
        String expectedAvatarUrl = "http://cloudinary.com/image.jpg";

        when(mockFile.isEmpty()).thenReturn(false); // File không rỗng
        when(thuocRepo.existsByMaThuoc(anyString())).thenReturn(false);
        when(thuocRepo.existsByTenThuoc(anyString())).thenReturn(false);
        when(loaiThuocRepo.findById(anyInt())).thenReturn(Optional.of(loaiThuoc));
        when(nhaSanXuatRepo.findById(anyInt())).thenReturn(Optional.of(nhaSanXuat));
        when(doiTuongRepo.findById(anyInt())).thenReturn(Optional.of(doiTuong1));
        when(uploadImageService.uploadImage(eq(mockFile), anyString())).thenReturn(expectedAvatarUrl); // Mock upload trả về URL
        when(thuocRepo.save(any(Thuoc.class))).thenAnswer(invocation -> {
            Thuoc savedThuoc = invocation.getArgument(0);
            savedThuoc.setId(101);
            // Quan trọng: Kiểm tra xem avatar đã được set trước khi save chưa
            assertEquals(expectedAvatarUrl, savedThuoc.getAvatar());
            return savedThuoc;
        });

        // Execution
        ResponseDTO<Thuoc> response = thuocService.create(thuocDTO);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertNotNull(response.getData());
        assertEquals(101, response.getData().getId());
        assertEquals(expectedAvatarUrl, response.getData().getAvatar()); // Kiểm tra avatar URL

        verify(uploadImageService).uploadImage(eq(mockFile), anyString()); // Verify upload được gọi
        verify(thuocRepo).save(any(Thuoc.class));
    }

    @Test
    void create_Fail_MaThuocExists() {
        thuocDTO.setId(null);

        when(thuocRepo.existsByMaThuoc(eq(thuocDTO.getMaThuoc()))).thenReturn(true); // Mã đã tồn tại
        ResponseDTO<Thuoc> response = thuocService.create(thuocDTO);

        assertNotNull(response);
        assertEquals(409, response.getStatus());
        assertEquals("Mã thuốc đã tồn tại", response.getMsg());
        assertNull(response.getData());

        verify(thuocRepo).existsByMaThuoc(eq(thuocDTO.getMaThuoc()));
        verify(thuocRepo, never()).existsByTenThuoc(anyString());
        verify(thuocRepo, never()).save(any(Thuoc.class)); // Không lưu
    }

    @Test
    void create_Fail_DoiTuongNotFound() {
        // Input
        DoiTuongDTO existingDoiTuongDTO = new DoiTuongDTO();
        existingDoiTuongDTO.setId(1);

        DoiTuongDTO nonExistingDoiTuongDTO = new DoiTuongDTO();
        nonExistingDoiTuongDTO.setId(999); // ID that will not exist

        thuocDTO.setDoiTuongs(Arrays.asList(existingDoiTuongDTO, nonExistingDoiTuongDTO));
        thuocDTO.setId(null); // Ensure it's a create scenario

        // Mocking
        when(thuocRepo.existsByMaThuoc(anyString())).thenReturn(false);
        when(thuocRepo.existsByTenThuoc(anyString())).thenReturn(false);
        when(loaiThuocRepo.findById(eq(thuocDTO.getLoaiThuocId()))).thenReturn(Optional.of(loaiThuoc));
        when(nhaSanXuatRepo.findById(eq(thuocDTO.getNhaSanXuatId()))).thenReturn(Optional.of(nhaSanXuat));
        when(doiTuongRepo.findById(eq(1))).thenReturn(Optional.of(doiTuong1)); // Existing DoiTuong
        when(doiTuongRepo.findById(eq(999))).thenReturn(Optional.empty()); // Non-existing DoiTuong

        // Execution and Assertions
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            thuocService.create(thuocDTO);
        });

        assertEquals("Đối tượng không tồn tại: ID 999", exception.getMessage());

        // Verify that save was not called
        verify(thuocRepo, never()).save(any());
    }

    @Test
    void create_Fail_NhaSanXuatNotFound() {
        // Input
        thuocDTO.setNhaSanXuatId(999); // Set an ID that will not exist

        // Mocking
        when(loaiThuocRepo.findById(any())).thenReturn(Optional.of(loaiThuoc)); // Mock LoaiThuoc to exist
        when(nhaSanXuatRepo.findById(eq(999))).thenReturn(Optional.empty()); // Mock NhaSanXuat to NOT exist
        when(thuocRepo.existsByMaThuoc(anyString())).thenReturn(false);
        when(thuocRepo.existsByTenThuoc(anyString())).thenReturn(false);

        // Execution and Assertions
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            thuocService.create(thuocDTO);
        });

        assertEquals("Nhà sản xuất không tồn tại", exception.getMessage());

        // Verify that save was not called
        verify(thuocRepo, never()).save(any());
    }

    @Test
    void create_Fail_TenThuocExists() {
        ThuocDTO inputDto = new ThuocDTO();
        inputDto.setTenThuoc("Paracetamol 500mg");
        inputDto.setMaThuoc("NEW_CODE_123");
        inputDto.setLoaiThuocId(1);
        inputDto.setNhaSanXuatId(1);
        inputDto.setDoiTuongs(Collections.emptyList());
        inputDto.setThanhPhanThuocs(Collections.emptyList());
        inputDto.setFile(null); // Không cần file cho test này

        when(thuocRepo.existsByMaThuoc(eq(inputDto.getMaThuoc()))).thenReturn(false);
        when(thuocRepo.existsByTenThuoc(eq(inputDto.getTenThuoc()))).thenReturn(true);
        ResponseDTO<Thuoc> response = thuocService.create(inputDto);

        assertNotNull(response);
        assertEquals(409, response.getStatus()); // Mong đợi status 409 Conflict
        assertEquals("Tên thuốc đã tồn tại", response.getMsg()); // Mong đợi đúng message lỗi
        assertNull(response.getData()); // Không có dữ liệu trả về khi lỗi

        verify(thuocRepo, times(1)).existsByMaThuoc(eq(inputDto.getMaThuoc()));
        verify(thuocRepo, times(1)).existsByTenThuoc(eq(inputDto.getTenThuoc()));

        verify(loaiThuocRepo, never()).findById(anyInt());
        verify(nhaSanXuatRepo, never()).findById(anyInt());
        verify(doiTuongRepo, never()).findById(anyInt());
        verify(uploadImageService, never()).uploadImage(any(), anyString());
        verify(thuocRepo, never()).save(any(Thuoc.class));
    }

    @Test
    void create_Success_WithThanhPhanThuocs() {
        // 1. Tạo các ThanhPhanThuocDTO mẫu
        ThanhPhanThuocDTO tpDto1 = new ThanhPhanThuocDTO();
        tpDto1.setTenThanhPhan("Paracetamol");
        tpDto1.setHamLuong("500mg");
        ThanhPhanThuocDTO tpDto2 = new ThanhPhanThuocDTO();
        tpDto2.setTenThanhPhan("Caffeine");
        tpDto2.setHamLuong("65mg");
        List<ThanhPhanThuocDTO> thanhPhanDtoList = Arrays.asList(tpDto1, tpDto2);

        // 2. Tạo ThuocDTO đầu vào, set list thành phần
        ThuocDTO inputDto = new ThuocDTO();
        inputDto.setTenThuoc("Panadol Extra");
        inputDto.setMaThuoc("PANE01");
        inputDto.setLoaiThuocId(1);
        inputDto.setNhaSanXuatId(1);
        inputDto.setThanhPhanThuocs(thanhPhanDtoList);
        inputDto.setDoiTuongs(Collections.emptyList());
        inputDto.setFile(null);

        when(thuocRepo.existsByMaThuoc(anyString())).thenReturn(false);
        when(thuocRepo.existsByTenThuoc(anyString())).thenReturn(false);
        when(loaiThuocRepo.findById(eq(inputDto.getLoaiThuocId()))).thenReturn(Optional.of(loaiThuoc)); // Sử dụng loaiThuoc từ setUp
        when(nhaSanXuatRepo.findById(eq(inputDto.getNhaSanXuatId()))).thenReturn(Optional.of(nhaSanXuat)); // Sử dụng nhaSanXuat từ setUp

        ArgumentCaptor<Thuoc> thuocCaptor = ArgumentCaptor.forClass(Thuoc.class);

        when(thuocRepo.save(thuocCaptor.capture())).thenAnswer(invocation -> {
            Thuoc capturedThuoc = thuocCaptor.getValue();
            capturedThuoc.setId(150); // Gán ID giả lập sau khi lưu
            return capturedThuoc;
        });

        ResponseDTO<Thuoc> response = thuocService.create(inputDto);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertNotNull(response.getData());
        assertEquals(150, response.getData().getId()); // ID giả lập đã gán

        // 2. Lấy đối tượng Thuoc đã được truyền vào save thông qua Captor
        Thuoc savedThuoc = thuocCaptor.getValue();

        // 3. Kiểm tra List<ThanhPhanThuoc> trên đối tượng đã lưu
        assertNotNull(savedThuoc.getThanhPhanThuocs()); // List không null
        assertFalse(savedThuoc.getThanhPhanThuocs().isEmpty()); // List không rỗng
        assertEquals(thanhPhanDtoList.size(), savedThuoc.getThanhPhanThuocs().size()); // Kiểm tra số lượng phần tử

        // 4. Kiểm tra từng ThanhPhanThuoc trong list đã lưu
        ThanhPhanThuoc savedTp1 = savedThuoc.getThanhPhanThuocs().get(0);
        assertNotNull(savedTp1);
        assertEquals(tpDto1.getTenThanhPhan(), savedTp1.getTenThanhPhan()); // Kiểm tra mapping tên
        assertEquals(tpDto1.getHamLuong(), savedTp1.getHamLuong()); // Kiểm tra mapping hàm lượng
        assertNotNull(savedTp1.getThuoc()); // Kiểm tra liên kết ngược không null
        assertSame(savedThuoc, savedTp1.getThuoc()); // Kiểm tra liên kết ngược trỏ đúng đến đối tượng Thuoc cha

        ThanhPhanThuoc savedTp2 = savedThuoc.getThanhPhanThuocs().get(1);
        assertNotNull(savedTp2);
        assertEquals(tpDto2.getTenThanhPhan(), savedTp2.getTenThanhPhan());
        assertEquals(tpDto2.getHamLuong(), savedTp2.getHamLuong());
        assertNotNull(savedTp2.getThuoc());
        assertSame(savedThuoc, savedTp2.getThuoc());

        // --- Verification ---
        // Xác minh các lời gọi cần thiết đã xảy ra
        verify(thuocRepo).existsByMaThuoc(eq(inputDto.getMaThuoc()));
        verify(thuocRepo).existsByTenThuoc(eq(inputDto.getTenThuoc()));
        verify(loaiThuocRepo).findById(eq(inputDto.getLoaiThuocId()));
        verify(nhaSanXuatRepo).findById(eq(inputDto.getNhaSanXuatId()));
        verify(thuocRepo).save(any(Thuoc.class)); // Xác minh save được gọi
        // Xác minh DoiTuong không được xử lý (vì list rỗng trong DTO)
        verify(doiTuongRepo, never()).findById(anyInt());
        // Xác minh upload ảnh không được gọi (vì file null)
        verify(uploadImageService, never()).uploadImage(any(), anyString());
    }

    @Test
    void create_Success_WithEmptyThanhPhanThuocs() {
        // --- Input ---
        ThuocDTO inputDto = new ThuocDTO();
        inputDto.setTenThuoc("Test Thuoc No TP");
        inputDto.setMaThuoc("TESTNOTP");
        inputDto.setLoaiThuocId(1);
        inputDto.setNhaSanXuatId(1);
        inputDto.setThanhPhanThuocs(Collections.emptyList()); // *** List rỗng ***
        inputDto.setDoiTuongs(Collections.emptyList());
        inputDto.setFile(null);

        // --- Mocking ---
        when(thuocRepo.existsByMaThuoc(anyString())).thenReturn(false);
        when(thuocRepo.existsByTenThuoc(anyString())).thenReturn(false);
        when(loaiThuocRepo.findById(anyInt())).thenReturn(Optional.of(loaiThuoc));
        when(nhaSanXuatRepo.findById(anyInt())).thenReturn(Optional.of(nhaSanXuat));

        ArgumentCaptor<Thuoc> thuocCaptor = ArgumentCaptor.forClass(Thuoc.class);
        when(thuocRepo.save(thuocCaptor.capture())).thenAnswer(invocation -> {
            Thuoc capturedThuoc = thuocCaptor.getValue();
            capturedThuoc.setId(151);
            return capturedThuoc;
        });

        // --- Execution ---
        ResponseDTO<Thuoc> response = thuocService.create(inputDto);

        // --- Assertions ---
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertNotNull(response.getData());

        Thuoc savedThuoc = thuocCaptor.getValue();
        // Kiểm tra list thành phần: có thể là null hoặc list rỗng tùy cách ModelMapper/JPA xử lý
        // Giả sử nó là null hoặc rỗng khi không được set
        assertTrue(savedThuoc.getThanhPhanThuocs() == null || savedThuoc.getThanhPhanThuocs().isEmpty());


        // --- Verification ---
        verify(thuocRepo).save(any(Thuoc.class));
    }

    @Test
    void create_Fail_LoaiThuocNotFound() {
        // Input: thuocDTO
        thuocDTO.setId(null);

        // Mocking
        when(thuocRepo.existsByMaThuoc(anyString())).thenReturn(false);
        when(thuocRepo.existsByTenThuoc(anyString())).thenReturn(false);
        when(loaiThuocRepo.findById(eq(thuocDTO.getLoaiThuocId()))).thenReturn(Optional.empty()); // Không tìm thấy

        // Execution & Assertion
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            thuocService.create(thuocDTO);
        });
        assertEquals("Loại thuốc không tồn tại", exception.getMessage());

        verify(loaiThuocRepo).findById(eq(thuocDTO.getLoaiThuocId()));
        verify(thuocRepo, never()).save(any(Thuoc.class));
    }

    @Test
    void update_Fail_MaThuocConflict() {
        Thuoc curentThuoc = new Thuoc();
        curentThuoc.setId(1);
        curentThuoc.setMaThuoc("OLD_MA001"); // Mã thuốc cũ
        curentThuoc.setTenThuoc("Thuoc Cu");
        // Set các trường khác nếu cần cho logic sau này (dù test này không cần)
        curentThuoc.setLoaiThuoc(loaiThuoc);
        curentThuoc.setNhaSanXuat(nhaSanXuat);

        // 2. Tạo ThuocDTO đầu vào để update
        ThuocDTO inputDto = new ThuocDTO();
        inputDto.setId(1); // ID của thuốc cần update
        inputDto.setMaThuoc("CONFLICT_MA002"); // *** Mã thuốc MỚI, khác mã cũ ***
        inputDto.setTenThuoc("Ten Thuoc Update"); // Tên thuốc có thể giống hoặc khác
        inputDto.setLoaiThuocId(1);
        inputDto.setNhaSanXuatId(1);
        inputDto.setDoiTuongs(Collections.emptyList());
        inputDto.setThanhPhanThuocs(Collections.emptyList());
        inputDto.setFile(null);

        when(thuocRepo.findById(eq(inputDto.getId()))).thenReturn(Optional.of(curentThuoc));

        when(thuocRepo.existsByMaThuoc(eq(inputDto.getMaThuoc()))).thenReturn(true);

        ResponseDTO<Thuoc> response = thuocService.update(inputDto);

        assertNotNull(response);
        assertEquals(409, response.getStatus()); // Mong đợi status 409 Conflict
        assertEquals("Mã thuốc đã tồn tại", response.getMsg()); // Mong đợi đúng message lỗi
        assertNull(response.getData()); // Không có dữ liệu trả về khi lỗi

        // --- Verification ---
        // Xác minh findById đã được gọi 1 lần
        verify(thuocRepo, times(1)).findById(eq(inputDto.getId()));
        // Xác minh existsByMaThuoc đã được gọi 1 lần với mã thuốc MỚI từ DTO
        verify(thuocRepo, times(1)).existsByMaThuoc(eq(inputDto.getMaThuoc()));

        // Xác minh rằng các bước sau đó (kiểm tra tên, lookup, save) KHÔNG được gọi
        verify(thuocRepo, never()).existsByTenThuoc(anyString());
        verify(loaiThuocRepo, never()).findById(anyInt());
        verify(nhaSanXuatRepo, never()).findById(anyInt());
        verify(doiTuongRepo, never()).findById(anyInt());
        verify(uploadImageService, never()).deleteImage(anyString());
        verify(uploadImageService, never()).uploadImage(any(), anyString());
        verify(thuocRepo, never()).save(any(Thuoc.class));
    }

    @Test
    void update_Success_MaThuocUnchanged() {
        // --- Input ---
        Thuoc curentThuoc = new Thuoc();
        curentThuoc.setId(1);
        curentThuoc.setMaThuoc("SAME_MA001"); // Mã thuốc cũ
        curentThuoc.setTenThuoc("Thuoc Cu");
        curentThuoc.setLoaiThuoc(loaiThuoc);
        curentThuoc.setNhaSanXuat(nhaSanXuat);

        ThuocDTO inputDto = new ThuocDTO();
        inputDto.setId(1);
        inputDto.setMaThuoc("SAME_MA001");
        inputDto.setTenThuoc("Ten Thuoc Update Khong Trung");
        inputDto.setLoaiThuocId(1);
        inputDto.setNhaSanXuatId(1);
        inputDto.setDoiTuongs(Collections.emptyList());
        inputDto.setThanhPhanThuocs(Collections.emptyList());
        inputDto.setFile(null);

        // --- Mocking ---
        when(thuocRepo.findById(eq(inputDto.getId()))).thenReturn(Optional.of(curentThuoc));
        when(thuocRepo.existsByTenThuoc(eq(inputDto.getTenThuoc()))).thenReturn(false);
        when(loaiThuocRepo.findById(anyInt())).thenReturn(Optional.of(loaiThuoc));
        when(nhaSanXuatRepo.findById(anyInt())).thenReturn(Optional.of(nhaSanXuat));
        // Mock save
        when(thuocRepo.save(any(Thuoc.class))).thenAnswer(inv -> inv.getArgument(0));

        // --- Execution ---
        ResponseDTO<Thuoc> response = thuocService.update(inputDto);

        // --- Assertions ---
        assertNotNull(response);
        assertEquals(200, response.getStatus()); // Update thành công
        assertEquals("Thành công", response.getMsg());
        assertNotNull(response.getData());
        assertEquals(inputDto.getTenThuoc(), response.getData().getTenThuoc());

        verify(thuocRepo).findById(eq(inputDto.getId()));
        verify(thuocRepo, never()).existsByMaThuoc(anyString());
        verify(thuocRepo).existsByTenThuoc(eq(inputDto.getTenThuoc()));
        verify(thuocRepo).save(any(Thuoc.class)); // Save được gọi
    }
    // --- update ---
    @Test
    void update_Success_WithFileChange() {
        // Input: thuocDTO (id=1) có file mới
        thuocDTO.setFile(mockFile);
        thuocDTO.setTenThuoc("Paracetamol 500mg Updated"); // Thay đổi tên
        String newAvatarUrl = "http://cloudinary.com/new_image.jpg";

        // Mocking
        when(mockFile.isEmpty()).thenReturn(false);
        when(thuocRepo.findById(eq(thuocDTO.getId()))).thenReturn(Optional.of(thuoc1)); // Tìm thấy thuốc cũ (có avatar cũ)
        when(thuocRepo.existsByTenThuoc(eq(thuocDTO.getTenThuoc()))).thenReturn(false); // Tên mới chưa tồn tại
        // Giả sử mã thuốc không đổi, không cần mock existsByMaThuoc
        when(loaiThuocRepo.findById(anyInt())).thenReturn(Optional.of(loaiThuoc));
        when(nhaSanXuatRepo.findById(anyInt())).thenReturn(Optional.of(nhaSanXuat));
        when(doiTuongRepo.findById(anyInt())).thenReturn(Optional.of(doiTuong1));
        // Mock việc xóa ảnh cũ và upload ảnh mới
        doNothing().when(uploadImageService).deleteImage(eq(thuoc1.getAvatar())); // Giả lập xóa thành công
        when(uploadImageService.uploadImage(eq(mockFile), eq("Thuoc_1"))).thenReturn(newAvatarUrl); // Mock upload ảnh mới, use eq for name
        // Mock save and capture the saved Thuoc object
        ArgumentCaptor<Thuoc> thuocCaptor = ArgumentCaptor.forClass(Thuoc.class);
        when(thuocRepo.save(thuocCaptor.capture())).thenAnswer(invocation -> thuocCaptor.getValue());

        // Execution
        ResponseDTO<Thuoc> response = thuocService.update(thuocDTO);

        // Assertions
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertNotNull(response.getData());
        assertEquals(thuocDTO.getId(), response.getData().getId());
        assertEquals(thuocDTO.getTenThuoc(), response.getData().getTenThuoc()); // Tên đã update
        assertEquals(newAvatarUrl, response.getData().getAvatar()); // Avatar mới

        verify(thuocRepo).findById(eq(thuocDTO.getId()));
        verify(uploadImageService).deleteImage(eq(thuoc1.getAvatar())); // Verify xóa ảnh cũ
        verify(uploadImageService).uploadImage(eq(mockFile), eq("Thuoc_1")); // Verify upload ảnh mới, use eq for name
        verify(thuocRepo).save(any(Thuoc.class));

        Thuoc savedThuoc = thuocCaptor.getValue();
        assertEquals(newAvatarUrl, savedThuoc.getAvatar());
    }

    @Test
    void update_Success_WithThanhPhanThuocUpdate() {
        // --- Input ---
        // 1. Tạo đối tượng Thuoc hiện tại trong DB
        Thuoc curentThuoc = new Thuoc();
        curentThuoc.setId(1);
        curentThuoc.setMaThuoc("TP_UPDATE_MA"); // Mã thuốc cũ (sẽ không đổi)
        curentThuoc.setTenThuoc("Thuoc Cu Can Update TP"); // Tên thuốc cũ (sẽ đổi)
        curentThuoc.setLoaiThuoc(loaiThuoc);
        curentThuoc.setNhaSanXuat(nhaSanXuat);

        ThanhPhanThuocDTO tpDtoUpdate1 = new ThanhPhanThuocDTO();
        tpDtoUpdate1.setTenThanhPhan("Acetylsalicylic Acid");
        tpDtoUpdate1.setHamLuong("81mg");
        ThanhPhanThuocDTO tpDtoUpdate2 = new ThanhPhanThuocDTO();
        tpDtoUpdate2.setTenThanhPhan("Clopidogrel");
        tpDtoUpdate2.setHamLuong("75mg");
        List<ThanhPhanThuocDTO> updatedTpListDto = Arrays.asList(tpDtoUpdate1, tpDtoUpdate2);

        // 3. Tạo ThuocDTO đầu vào
        ThuocDTO inputDto = new ThuocDTO();
        inputDto.setId(1);
        inputDto.setMaThuoc("TP_UPDATE_MA"); // *** Giữ nguyên Mã thuốc ***
        inputDto.setTenThuoc("Thuoc Moi Update TP"); // *** Đổi Tên thuốc ***
        inputDto.setLoaiThuocId(1);
        inputDto.setNhaSanXuatId(1);
        inputDto.setThanhPhanThuocs(updatedTpListDto); // *** Set list mới ***
        inputDto.setDoiTuongs(Collections.emptyList()); // Giữ các list khác rỗng
        inputDto.setFile(null); // Không có file


        // --- Mocking ---
        // Mock các bước cần thiết cho luồng thực thi này
        when(thuocRepo.findById(eq(inputDto.getId()))).thenReturn(Optional.of(curentThuoc));
        // Do Mã thuốc không đổi -> KHÔNG cần mock existsByMaThuoc

        // Do Tên thuốc đổi -> CẦN mock existsByTenThuoc trả về false (không trùng)
        when(thuocRepo.existsByTenThuoc(eq(inputDto.getTenThuoc()))).thenReturn(false);

        // Mock các lookup Repo cần thiết
        when(loaiThuocRepo.findById(eq(inputDto.getLoaiThuocId()))).thenReturn(Optional.of(loaiThuoc));
        when(nhaSanXuatRepo.findById(eq(inputDto.getNhaSanXuatId()))).thenReturn(Optional.of(nhaSanXuat));

        // Sử dụng ArgumentCaptor để bắt đối tượng Thuoc được truyền vào hàm save
        ArgumentCaptor<Thuoc> thuocCaptor = ArgumentCaptor.forClass(Thuoc.class);
        when(thuocRepo.save(thuocCaptor.capture())).thenAnswer(invocation -> {
            // Trả về chính đối tượng đã được capture để kiểm tra
            // Không cần gán ID vì nó đã có ID=1
            return thuocCaptor.getValue();
        });

        // --- Execution ---
        ResponseDTO<Thuoc> response = thuocService.update(inputDto);

        // --- Assertions ---
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertNotNull(response.getData());
        assertEquals(inputDto.getId(), response.getData().getId()); // ID không đổi
        assertEquals(inputDto.getTenThuoc(), response.getData().getTenThuoc()); // Tên đã update

        // Lấy đối tượng Thuoc đã được truyền vào save để kiểm tra list TP
        Thuoc savedThuoc = thuocCaptor.getValue();
        assertNotNull(savedThuoc.getThanhPhanThuocs());
        assertFalse(savedThuoc.getThanhPhanThuocs().isEmpty());
        assertEquals(updatedTpListDto.size(), savedThuoc.getThanhPhanThuocs().size());

        // Kiểm tra nội dung và liên kết của từng ThanhPhanThuoc
        ThanhPhanThuoc savedTp1 = savedThuoc.getThanhPhanThuocs().stream()
                .filter(tp -> tp.getTenThanhPhan().equals(tpDtoUpdate1.getTenThanhPhan())) // Dùng getTenHoatChat()
                .findFirst().orElseThrow(); // Dùng orElseThrow để test rõ hơn nếu không tìm thấy
        assertEquals(tpDtoUpdate1.getHamLuong(), savedTp1.getHamLuong());
        assertNotNull(savedTp1.getThuoc());
        assertSame(savedThuoc, savedTp1.getThuoc()); // Kiểm tra liên kết cha

        ThanhPhanThuoc savedTp2 = savedThuoc.getThanhPhanThuocs().stream()
                .filter(tp -> tp.getTenThanhPhan().equals(tpDtoUpdate2.getTenThanhPhan())) // Dùng getTenHoatChat()
                .findFirst().orElseThrow();
        assertEquals(tpDtoUpdate2.getHamLuong(), savedTp2.getHamLuong());
        assertNotNull(savedTp2.getThuoc());
        assertSame(savedThuoc, savedTp2.getThuoc());


        // --- Verification ---
        // Xác minh các lời gọi ĐÃ xảy ra
        verify(thuocRepo).findById(eq(inputDto.getId()));
        verify(thuocRepo).existsByTenThuoc(eq(inputDto.getTenThuoc())); // Được gọi vì tên đổi
        verify(loaiThuocRepo).findById(eq(inputDto.getLoaiThuocId()));
        verify(nhaSanXuatRepo).findById(eq(inputDto.getNhaSanXuatId()));
        verify(thuocRepo).save(any(Thuoc.class)); // Save được gọi

        // Xác minh các lời gọi KHÔNG xảy ra
        verify(thuocRepo, never()).existsByMaThuoc(anyString()); // Không gọi vì mã không đổi
        verify(doiTuongRepo, never()).findById(anyInt()); // Không gọi vì list DoiTuong rỗng
        verify(uploadImageService, never()).deleteImage(anyString()); // Không gọi vì file null
        verify(uploadImageService, never()).uploadImage(any(), anyString()); // Không gọi vì file null
    }

    @Test
    void update_Success_WithNullOrEmptyThanhPhanThuocs() {
        // --- Input ---
        Thuoc curentThuoc = new Thuoc(); // Thuốc cũ
        curentThuoc.setId(1);
        // *** Đặt mã và tên giống nhau trong cả curentThuoc và inputDto để không trigger check exists ***
        curentThuoc.setMaThuoc("TP_UPDATE_EMPTY_SAME");
        curentThuoc.setTenThuoc("Thuoc Update TP Rong Same");
        curentThuoc.setLoaiThuoc(loaiThuoc);
        curentThuoc.setNhaSanXuat(nhaSanXuat);
        // Giả sử thuốc cũ có thành phần, để kiểm tra xem nó có bị ảnh hưởng không (tùy logic service)
        ThanhPhanThuoc oldTp = new ThanhPhanThuoc();
        oldTp.setId(10); oldTp.setTenThanhPhan("Old Component"); oldTp.setHamLuong("10mg");
        curentThuoc.setThanhPhanThuocs(new ArrayList<>(Arrays.asList(oldTp))); // Dùng ArrayList để có thể thay đổi nếu logic service cần

        ThuocDTO inputDto = new ThuocDTO();
        inputDto.setId(1);
        // *** Đặt mã và tên giống curentThuoc ***
        inputDto.setMaThuoc("TP_UPDATE_EMPTY_SAME");
        inputDto.setTenThuoc("Thuoc Update TP Rong Same");
        inputDto.setLoaiThuocId(1);
        inputDto.setNhaSanXuatId(1);
        // *** Đặt list thành phần là null hoặc rỗng ***
        inputDto.setThanhPhanThuocs(null);
        // hoặc inputDto.setThanhPhanThuocs(Collections.emptyList());
        inputDto.setDoiTuongs(Collections.emptyList());
        inputDto.setFile(null);


        // --- Mocking ---
        when(thuocRepo.findById(eq(inputDto.getId()))).thenReturn(Optional.of(curentThuoc));

        // Mock các lookup Repo CẦN THIẾT vì chúng được gọi sau các check exists
        when(loaiThuocRepo.findById(eq(inputDto.getLoaiThuocId()))).thenReturn(Optional.of(loaiThuoc));
        when(nhaSanXuatRepo.findById(eq(inputDto.getNhaSanXuatId()))).thenReturn(Optional.of(nhaSanXuat));

        ArgumentCaptor<Thuoc> thuocCaptor = ArgumentCaptor.forClass(Thuoc.class);
        when(thuocRepo.save(thuocCaptor.capture())).thenAnswer(inv -> inv.getArgument(0));

        // --- Execution ---
        ResponseDTO<Thuoc> response = thuocService.update(inputDto);

        // --- Assertions ---
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertNotNull(response.getData());

        Thuoc savedThuoc = thuocCaptor.getValue();
        assertTrue(savedThuoc.getThanhPhanThuocs() == null || savedThuoc.getThanhPhanThuocs().isEmpty(),
                "Danh sách Thành phần thuốc trên đối tượng lưu phải là null hoặc rỗng");

        verify(thuocRepo).findById(eq(inputDto.getId()));
        verify(loaiThuocRepo).findById(eq(inputDto.getLoaiThuocId())); // Vẫn được gọi
        verify(nhaSanXuatRepo).findById(eq(inputDto.getNhaSanXuatId())); // Vẫn được gọi
        verify(thuocRepo).save(any(Thuoc.class));

        // Xác minh các lời gọi KHÔNG xảy ra
        verify(thuocRepo, never()).existsByMaThuoc(anyString()); // Không gọi vì mã không đổi
        verify(thuocRepo, never()).existsByTenThuoc(anyString()); // Không gọi vì tên không đổi
        verify(doiTuongRepo, never()).findById(anyInt()); // Không gọi vì list DoiTuong rỗng
        verify(uploadImageService, never()).deleteImage(anyString()); // Không gọi vì file null
        verify(uploadImageService, never()).uploadImage(any(), anyString()); // Không gọi vì file null
    }

    @Test
    void update_Success_NoFileChange() {
        thuocDTO.setFile(null);
        thuocDTO.setTenThuoc("Paracetamol 500mg Updated");

        // Mocking
        when(thuocRepo.findById(eq(thuocDTO.getId()))).thenReturn(Optional.of(thuoc1)); // Tìm thấy thuốc cũ
        when(thuocRepo.existsByTenThuoc(eq(thuocDTO.getTenThuoc()))).thenReturn(false);
        when(loaiThuocRepo.findById(anyInt())).thenReturn(Optional.of(loaiThuoc));
        when(nhaSanXuatRepo.findById(anyInt())).thenReturn(Optional.of(nhaSanXuat));
        when(doiTuongRepo.findById(anyInt())).thenReturn(Optional.of(doiTuong1));
        when(thuocRepo.save(any(Thuoc.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Execution
        ResponseDTO<Thuoc> response = thuocService.update(thuocDTO);

        // Assertions
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertNotNull(response.getData());
        assertEquals(thuocDTO.getTenThuoc(), response.getData().getTenThuoc());
        assertEquals(thuoc1.getAvatar(), response.getData().getAvatar()); // Avatar giữ nguyên

        verify(uploadImageService, never()).deleteImage(anyString()); // Không xóa
        verify(uploadImageService, never()).uploadImage(any(), anyString()); // Không upload
        verify(thuocRepo).save(any(Thuoc.class));
    }

    @Test
    void update_Fail_NotFound() {
        thuocDTO.setId(99);

        when(thuocRepo.findById(eq(99))).thenReturn(Optional.empty()); // Không tìm thấy
        ResponseDTO<Thuoc> response = thuocService.update(thuocDTO);

        // Assertions
        assertNotNull(response);
        assertEquals(404, response.getStatus());
        assertEquals("Không tìm thấy thuốc", response.getMsg());
        assertNull(response.getData());

        verify(thuocRepo).findById(eq(99));
        verify(thuocRepo, never()).save(any(Thuoc.class)); // Không save
    }

    @Test
    void update_Fail_TenThuocConflict() {
        // Input: thuocDTO (id=1) có tên mới bị trùng
        thuocDTO.setTenThuoc("Thuoc Khac Da Co");

        // Mocking
        when(thuocRepo.findById(eq(thuocDTO.getId()))).thenReturn(Optional.of(thuoc1)); // Tìm thấy thuốc cũ
        // Tên mới khác tên cũ (thuoc1.getTenThuoc() != "Thuoc Khac Da Co")
        // Và tên mới này đã tồn tại trong DB
        when(thuocRepo.existsByTenThuoc(eq("Thuoc Khac Da Co"))).thenReturn(true);

        // Execution
        ResponseDTO<Thuoc> response = thuocService.update(thuocDTO);

        // Assertions
        assertNotNull(response);
        assertEquals(409, response.getStatus());
        assertEquals("Tên thuốc đã tồn tại", response.getMsg());
        assertNull(response.getData());

        verify(thuocRepo).findById(eq(thuocDTO.getId()));
        verify(thuocRepo).existsByTenThuoc(eq("Thuoc Khac Da Co"));
        verify(thuocRepo, never()).save(any(Thuoc.class));
    }

    @Test
    void updateThuoc_Fail_DoiTuongNotFound() {
        // Input
        Thuoc curentThuoc = new Thuoc();
        curentThuoc.setId(1);
        curentThuoc.setMaThuoc("CODE");
        curentThuoc.setTenThuoc("Name");

        DoiTuongDTO existingDoiTuongDTO = new DoiTuongDTO();
        existingDoiTuongDTO.setId(1);

        DoiTuongDTO nonExistingDoiTuongDTO = new DoiTuongDTO();
        nonExistingDoiTuongDTO.setId(999); // ID that will not exist

        thuocDTO.setId(1);
        thuocDTO.setMaThuoc("CODE");
        thuocDTO.setTenThuoc("Updated Name");
        thuocDTO.setDoiTuongs(Arrays.asList(existingDoiTuongDTO, nonExistingDoiTuongDTO));

        // Mocking
        when(thuocRepo.findById(eq(1))).thenReturn(Optional.of(curentThuoc));
        when(loaiThuocRepo.findById(anyInt())).thenReturn(Optional.of(loaiThuoc));
        when(nhaSanXuatRepo.findById(anyInt())).thenReturn(Optional.of(nhaSanXuat));
        when(doiTuongRepo.findById(eq(1))).thenReturn(Optional.of(doiTuong1)); // Existing DoiTuong
        when(doiTuongRepo.findById(eq(999))).thenReturn(Optional.empty()); // Non-existing DoiTuong

        // Execution and Assertions
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            thuocService.update(thuocDTO);
        });

        assertEquals("Đối tượng không tồn tại: ID 999", exception.getMessage());

        verify(thuocRepo).findById(eq(1));
        verify(doiTuongRepo).findById(eq(1));
        verify(doiTuongRepo).findById(eq(999));
        verify(thuocRepo, never()).save(any());
    }

    @Test
    void delete_Success() {
        // Input
        Integer id = 1;

        // Mocking
        // Giả lập hành vi không làm gì khi gọi deleteById (vì nó là void)
        doNothing().when(thuocRepo).deleteById(eq(id));

        // Execution
        ResponseDTO<Void> response = thuocService.delete(id);

        // Assertions
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertNull(response.getData());

        // Xác minh rằng deleteById đã được gọi đúng 1 lần với id=1
        verify(thuocRepo, times(1)).deleteById(eq(id));
    }

    @Test
    void delete_Fail_ThrowsException() {
        // Input
        Integer id = 1;

        // Mocking
        // Giả lập deleteById ném ra một DataAccessException
        doThrow(new org.springframework.dao.EmptyResultDataAccessException(1)) // Ví dụ một exception cụ thể
                .when(thuocRepo).deleteById(eq(id));

        // Execution & Assertion
        assertThrows(org.springframework.dao.EmptyResultDataAccessException.class, () -> {
            thuocService.delete(id);
        });
        // Xác minh rằng deleteById đã được gọi
        verify(thuocRepo).deleteById(eq(id));
    }
}