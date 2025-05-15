package com.example.hieuthuoc.service;

import com.example.hieuthuoc.dto.PageDTO;
import com.example.hieuthuoc.dto.ResponseDTO;
import com.example.hieuthuoc.dto.SearchDTO;
import com.example.hieuthuoc.dto.ThongBaoDTO;
import com.example.hieuthuoc.entity.NguoiDung;
import com.example.hieuthuoc.entity.ThongBao;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.example.hieuthuoc.repository.NguoiDungRepo;
import com.example.hieuthuoc.repository.ThongBaoRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

@ExtendWith(MockitoExtension.class)
public class ThongBaoServiceTest {

    @InjectMocks
    private ThongBaoServiceImpl thongBaoService;

    @Mock
    private NguoiDungRepo nguoiDungRepo;

    @Mock
    private ThongBaoRepo thongBaoRepo;

    @Test
    public void testGetByNguoiDungId_Success() {
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setId(1);
        searchDTO.setCurrentPage(0);
        searchDTO.setSize(10);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        List<ThongBao> mockList = List.of(new ThongBao(), new ThongBao());
        Page<ThongBao> mockPage = new PageImpl<>(mockList, pageable, mockList.size());

        when(thongBaoRepo.findByNguoiDungId(1, pageable)).thenReturn(mockPage);

        ResponseDTO<PageDTO<List<ThongBao>>> response = thongBaoService.getByNguoiDungId(searchDTO);

        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertNotNull(response.getData());
        assertEquals(2, response.getData().getData().size());
    }

    @Test
    public void testGetByNguoiDungId_InvalidId() {
        // Tạo SearchDTO với id không hợp lệ
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setId(-1);  // id không hợp lệ
        searchDTO.setCurrentPage(0);
        searchDTO.setSize(10);

        // Giả lập trả về null từ repository (mô phỏng trường hợp không có thông báo)
        when(thongBaoRepo.findByNguoiDungId(eq(-1), any(PageRequest.class))).thenReturn(Page.empty());

        // Gọi phương thức trong service
        ResponseDTO<PageDTO<List<ThongBao>>> response = thongBaoService.getByNguoiDungId(searchDTO);

        // Kiểm tra kết quả trả về
        assertNotNull(response);
        assertEquals(400, response.getStatus());  // Kiểm tra mã trạng thái lỗi
        assertEquals("Không tìm thấy thông báo cho người dùng với Id = -1", response.getMsg());  // Kiểm tra thông báo lỗi
    }

    @Test
    public void testGetByNguoiDungId_idNotFound() {
        // Tạo SearchDTO với id không hợp lệ
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setId(999);  // id không hợp lệ
        searchDTO.setCurrentPage(0);
        searchDTO.setSize(5);

        // Giả lập trả về một page rỗng từ repository
        Page<ThongBao> emptyPage = Page.empty();
        when(thongBaoRepo.findByNguoiDungId(eq(999), any(PageRequest.class))).thenReturn(emptyPage);

        // Gọi phương thức trong service
        ResponseDTO<PageDTO<List<ThongBao>>> response = thongBaoService.getByNguoiDungId(searchDTO);

        // Kiểm tra kết quả trả về
        assertEquals(200, response.getStatus());  // Kiểm tra mã status
        assertEquals("Không có thông báo", response.getMsg());  // Kiểm tra thông báo đã được thay đổi
        assertNull(response.getData());  // Kiểm tra data là null vì không có thông báo nào
    }


    @Test
    public void testGetByNguoiDungId_SortedFieldNotEmpty() {
        SearchDTO dto = new SearchDTO();
        dto.setKeyWord("");
        dto.setSortedField("createdAt"); // không rỗng
        dto.setCurrentPage(0);
        dto.setSize(10);

        Page<ThongBao> page = new PageImpl<>(List.of(new ThongBao()));
        when(thongBaoRepo.findByNguoiDungId(eq(0), any(Pageable.class))).thenReturn(page);

        ResponseDTO<PageDTO<List<ThongBao>>> res = thongBaoService.getByNguoiDungId(dto);

        assertEquals(200, res.getStatus());
        assertNotNull(res.getData());
    }

    @Test
    public void testGetByNguoiDungId_CurrentPageIsNull() {
        SearchDTO dto = new SearchDTO();
        dto.setKeyWord("");
        dto.setSortedField("createdAt");
        dto.setCurrentPage(null); // null để phủ nhánh
        dto.setSize(10);

        Page<ThongBao> page = new PageImpl<>(List.of(new ThongBao()));
        when(thongBaoRepo.findByNguoiDungId(eq(0), any(Pageable.class))).thenReturn(page);

        ResponseDTO<PageDTO<List<ThongBao>>> res = thongBaoService.getByNguoiDungId(dto);

        assertEquals(200, res.getStatus());
        assertNotNull(res.getData());
    }

    @Test
    public void testGetByNguoiDungId_SizeIsNull() {
        SearchDTO dto = new SearchDTO();
        dto.setKeyWord("");
        dto.setSortedField("createdAt");
        dto.setCurrentPage(0);
        dto.setSize(null); // null để phủ nhánh

        Page<ThongBao> page = new PageImpl<>(List.of(new ThongBao()));
        when(thongBaoRepo.findByNguoiDungId(eq(0), any(Pageable.class))).thenReturn(page);

        ResponseDTO<PageDTO<List<ThongBao>>> res = thongBaoService.getByNguoiDungId(dto);

        assertEquals(200, res.getStatus());
        assertNotNull(res.getData());
    }


    @Test
    public void testGetByLoaiThongBao_SortedFieldNotEmpty() {
        // Tạo đối tượng SearchDTO với sortedField không rỗng
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setKeyWord("CA_NHAN");  // Sử dụng keyWord hợp lệ
        searchDTO.setSortedField("createdAt");  // Trường sắp xếp không rỗng
        searchDTO.setCurrentPage(0);
        searchDTO.setSize(10);

        // Giả lập trả về một page với dữ liệu
        Page<ThongBao> page = new PageImpl<>(List.of(new ThongBao()));
        when(thongBaoRepo.findByLoaiThongBao(eq(ThongBao.LoaiThongBao.valueOf("CA_NHAN")), any(Pageable.class)))
                .thenReturn(page);

        // Gọi service để lấy kết quả
        ResponseDTO<PageDTO<List<ThongBao>>> response = thongBaoService.getByLoaiThongBao(searchDTO);

        // Kiểm tra kết quả trả về
        assertEquals(200, response.getStatus());  // Kiểm tra mã status
        assertEquals("Thành công", response.getMsg());  // Kiểm tra thông báo thành công
        assertNotNull(response.getData());  // Kiểm tra dữ liệu không null
        assertEquals(1, response.getData().getTotalElements());  // Kiểm tra số lượng phần tử
    }


    @Test
    public void testGetByLoaiThongBao_KeyWordIsNull() {
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setKeyWord(null);
        searchDTO.setCurrentPage(0);
        searchDTO.setSize(10);

        // Giả lập trả về một page với dữ liệu
        Page<ThongBao> page = new PageImpl<>(List.of(new ThongBao(), new ThongBao(), new ThongBao(), new ThongBao(), new ThongBao()));
        when(thongBaoRepo.findAll(any(Pageable.class))).thenReturn(page);

        ResponseDTO<PageDTO<List<ThongBao>>> response = thongBaoService.getByLoaiThongBao(searchDTO);

        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertNotNull(response.getData());
        assertEquals(5, response.getData().getTotalElements());
    }

    @Test
    public void testGetByLoaiThongBao_KeyWordIsEmpty() {
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setKeyWord("");
        searchDTO.setCurrentPage(0);
        searchDTO.setSize(10);

        // Giả lập trả về một page với dữ liệu
        Page<ThongBao> page = new PageImpl<>(List.of(new ThongBao(), new ThongBao(), new ThongBao(), new ThongBao(), new ThongBao()));
        when(thongBaoRepo.findAll(any(Pageable.class))).thenReturn(page);

        ResponseDTO<PageDTO<List<ThongBao>>> response = thongBaoService.getByLoaiThongBao(searchDTO);

        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertNotNull(response.getData());
        assertEquals(5, response.getData().getTotalElements());
    }

    @Test
    public void testGetByLoaiThongBao_CurrentPageNull() {
        // Tạo đối tượng SearchDTO với keyWord hợp lệ và currentPage là null
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setKeyWord("HE_THONG");  // Sử dụng keyWord hợp lệ từ enum
        searchDTO.setCurrentPage(null);  // currentPage là null để kiểm tra logic trong service
        searchDTO.setSize(10);

        // Giả lập trả về một page với dữ liệu
        Page<ThongBao> page = new PageImpl<>(List.of(new ThongBao()));
        when(thongBaoRepo.findByLoaiThongBao(eq(ThongBao.LoaiThongBao.valueOf("HE_THONG")), any(Pageable.class)))
                .thenReturn(page);

        // Gọi service để lấy kết quả
        ResponseDTO<PageDTO<List<ThongBao>>> response = thongBaoService.getByLoaiThongBao(searchDTO);

        // Kiểm tra kết quả trả về
        assertEquals(200, response.getStatus());  // Kiểm tra mã status là 200
        assertEquals("Thành công", response.getMsg());  // Kiểm tra thông báo thành công
        assertNotNull(response.getData());  // Kiểm tra dữ liệu không null
        assertEquals(1, response.getData().getTotalElements());  // Kiểm tra số lượng phần tử
    }

    @Test
    public void testGetByLoaiThongBao_SizeNull() {
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setKeyWord("KHUYEN_MAI");
        searchDTO.setCurrentPage(0);
        searchDTO.setSize(null);

        // Giả lập trả về một page với dữ liệu
        Page<ThongBao> page = new PageImpl<>(List.of(new ThongBao()));
        when(thongBaoRepo.findByLoaiThongBao(eq(ThongBao.LoaiThongBao.valueOf("KHUYEN_MAI")), any(Pageable.class))).thenReturn(page);

        ResponseDTO<PageDTO<List<ThongBao>>> response = thongBaoService.getByLoaiThongBao(searchDTO);

        assertEquals(200, response.getStatus());
        assertEquals("Thanh công", response.getMsg());
        assertNotNull(response.getData());
        assertEquals(1, response.getData().getTotalElements());
    }


    @Test
    void testGetById_ThongBaoTonTai() {
        ThongBao thongBao = new ThongBao();
        thongBao.setId(1);
        thongBao.setTrangThai(false);
        when(thongBaoRepo.findById(1)).thenReturn(Optional.of(thongBao));

        ResponseDTO<ThongBao> response = thongBaoService.getById(1);

        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertNotNull(response.getData());
        assertTrue(response.getData().getTrangThai());
    }

    @Test
    void testGetById_ThongBaoKhongTonTai() {
        when(thongBaoRepo.findById(9999)).thenReturn(Optional.empty());

        ResponseDTO<ThongBao> response = thongBaoService.getById(9999);

        assertEquals(409, response.getStatus());
        assertEquals("Không tìm thấy thông báo có Id = 9999", response.getMsg());
        assertNull(response.getData());
    }


    @Test
    void testCreate_ThanhCongVoiDanhSachNguoiDung() {
        ThongBaoDTO dto = new ThongBaoDTO();
        dto.setLoaiThongBao("CA_NHAN");
        dto.setNguoiDungId(List.of(1, 2));

        NguoiDung nguoi1 = new NguoiDung(); nguoi1.setId(1);
        NguoiDung nguoi2 = new NguoiDung(); nguoi2.setId(2);

        when(nguoiDungRepo.findById(1)).thenReturn(Optional.of(nguoi1));
        when(nguoiDungRepo.findById(2)).thenReturn(Optional.of(nguoi2));
        when(thongBaoRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseDTO<ThongBao> response = thongBaoService.create(dto);

        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertNotNull(response.getData());
        assertEquals(2, response.getData().getNguoiNhan().size());
    }

    @Test
    void testCreate_KhongCoNguoiDung() {
        ThongBaoDTO dto = new ThongBaoDTO();
        dto.setLoaiThongBao("CA_NHAN");
        dto.setNguoiDungId(null); // hoặc new ArrayList<>()

        when(thongBaoRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseDTO<ThongBao> response = thongBaoService.create(dto);

        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
    }

//    @Test
//    void testCreate_NguoiDungKhongTonTai() {
//        ThongBaoDTO dto = new ThongBaoDTO();
//        dto.setLoaiThongBao("CA_NHAN");
//        dto.setNguoiDungId(List.of(99));
//
//        when(nguoiDungRepo.findById(99)).thenReturn(Optional.empty());
//
//        RuntimeException ex = assertThrows(RuntimeException.class, () -> thongBaoService.create(dto));
//        assertEquals("Người dùng có Id = 99 không tồn tại", ex.getMessage());
//    }

//    @Test
//    void testCreate_LoaiThongBaoKhongHopLe() {
//        ThongBaoDTO dto = new ThongBaoDTO();
//        dto.setLoaiThongBao("INVALID_KEY");
//        dto.setNguoiDungId(null);
//
//        assertThrows(IllegalArgumentException.class, () -> thongBaoService.create(dto));
//    }

    @Test
    void testCreate_NguoiDungEmptyList() {
        ThongBaoDTO dto = new ThongBaoDTO();
        dto.setLoaiThongBao("CA_NHAN");
        dto.setNguoiDungId(new ArrayList<>()); // danh sách rỗng

        when(thongBaoRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseDTO<ThongBao> response = thongBaoService.create(dto);

        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertNotNull(response.getData());
        assertEquals(0, response.getData().getNguoiNhan().size());
    }

    @Test
    void testUpdate_success() {
        ThongBaoDTO dto = new ThongBaoDTO();
        dto.setId(1);
        dto.setLoaiThongBao("HE_THONG");
        dto.setNguoiDungId(List.of(1, 2));

        ThongBao currentThongBao = new ThongBao();
        currentThongBao.setId(1);

        NguoiDung nguoi1 = new NguoiDung(); nguoi1.setId(1);
        NguoiDung nguoi2 = new NguoiDung(); nguoi2.setId(2);

        when(thongBaoRepo.findById(1)).thenReturn(Optional.of(currentThongBao));
        when(nguoiDungRepo.findById(1)).thenReturn(Optional.of(nguoi1));
        when(nguoiDungRepo.findById(2)).thenReturn(Optional.of(nguoi2));
        when(thongBaoRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseDTO<ThongBao> response = thongBaoService.update(dto);

        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertEquals(2, response.getData().getNguoiNhan().size());
    }

    @Test
    void testUpdate_KhongCoNguoiDung() {
        ThongBaoDTO dto = new ThongBaoDTO();
        dto.setId(1);
        dto.setLoaiThongBao("HE_THONG");
        dto.setNguoiDungId(null); // hoặc new ArrayList<>()

        ThongBao currentThongBao = new ThongBao();
        currentThongBao.setId(1);

        when(thongBaoRepo.findById(1)).thenReturn(Optional.of(currentThongBao));
        when(thongBaoRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseDTO<ThongBao> response = thongBaoService.update(dto);

        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
    }

//    @Test
//    void testUpdate_NguoiDungKhongTonTai() {
//        ThongBaoDTO dto = new ThongBaoDTO();
//        dto.setId(1);
//        dto.setLoaiThongBao("HE_THONG");
//        dto.setNguoiDungId(List.of(99));
//
//        ThongBao currentThongBao = new ThongBao();
//        currentThongBao.setId(1);
//
//        when(thongBaoRepo.findById(1)).thenReturn(Optional.of(currentThongBao));
//        when(nguoiDungRepo.findById(99)).thenReturn(Optional.empty());
//
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> thongBaoService.update(dto));
//        assertEquals("Người dùng có Id = 99 không tồn tại", exception.getMessage());
//    }

//    @Test
//    void testUpdate_LoaiThongBaoKhongHopLe() {
//        ThongBaoDTO dto = new ThongBaoDTO();
//        dto.setId(1);
//        dto.setLoaiThongBao("INVALID_KEY");
//        dto.setNguoiDungId(null);
//
//        ThongBao currentThongBao = new ThongBao();
//        currentThongBao.setId(1);
//
//        when(thongBaoRepo.findById(1)).thenReturn(Optional.of(currentThongBao));
//
//        assertThrows(IllegalArgumentException.class, () -> thongBaoService.update(dto));
//    }

    @Test
    void testUpdate_ThongBaoKhongTonTai() {
        ThongBaoDTO dto = new ThongBaoDTO();
        dto.setId(999);
        dto.setLoaiThongBao("HE_THONG");

        when(thongBaoRepo.findById(999)).thenReturn(Optional.empty());

        ResponseDTO<ThongBao> response = thongBaoService.update(dto);

        assertEquals(200, response.getStatus());
        assertEquals("Không tìm thấy thông báo", response.getMsg());
    }

    @Test
    void testUpdate_NguoiDungId_Empty() {
        ThongBaoDTO dto = new ThongBaoDTO();
        dto.setId(1);
        dto.setLoaiThongBao("HE_THONG");
        dto.setNguoiDungId(Collections.emptyList());

        ThongBao currentThongBao = new ThongBao();
        currentThongBao.setId(1);

        when(thongBaoRepo.findById(1)).thenReturn(Optional.of(currentThongBao));
        when(thongBaoRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseDTO<ThongBao> response = thongBaoService.update(dto);

        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
    }


    @Test
    void testDelete_ThanhCong() {
        // Arrange
        Integer id = 1;

        doNothing().when(thongBaoRepo).deleteById(id);

        // Act
        ResponseDTO<Void> response = thongBaoService.delete(id);

        // Assert
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
    }

}


