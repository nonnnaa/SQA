package com.example.hieuthuoc.service;

import com.example.hieuthuoc.dto.*;
import com.example.hieuthuoc.entity.*;
import com.example.hieuthuoc.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DonHangServiceImplTest {

    @InjectMocks
    private DonHangServiceImpl donHangService;

    @Mock
    private DonHangRepo donHangRepo;

    @Mock
    private NguoiDungRepo nguoiDungRepo;

    @Mock
    private ThuocRepo thuocRepo;

    @Mock
    private ThongBaoRepo thongBaoRepo;

    @Mock
    private TonKhoRepo tonKhoRepo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        donHangService.modelMapper = new ModelMapper();
    }

    /**
     * TC48 - Mục tiêu: Lấy đơn hàng theo ID thành công
     * Input: id = 1
     * Expected Output: ResponseDTO status = 200, msg = "Thành công", data != null
     * Ghi chú: Trường hợp tìm thấy đơn hàng theo ID.
     */
    @Test
    void getById_ShouldReturnDonHang_WhenFound() {
        DonHang donHang = new DonHang();
        donHang.setId(1);
        when(donHangRepo.findById(1)).thenReturn(Optional.of(donHang));
        ResponseDTO<DonHang> response = donHangService.getById(1);
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertNotNull(response.getData());
    }

    /**
     * TC49 - Mục tiêu: Lấy đơn hàng không tồn tại
     * Input: id = 99
     * Expected Output: ResponseDTO status = 200, msg = "Không tìm thấy đơn hàng", data = null
     * Ghi chú: Trường hợp không tìm thấy đơn hàng theo ID.
     */
    @Test
    void getById_ShouldReturnNotFound_WhenMissing() {
        when(donHangRepo.findById(99)).thenReturn(Optional.empty());
        ResponseDTO<DonHang> response = donHangService.getById(99);
        assertEquals(200, response.getStatus());
        assertEquals("Không tìm thấy đơn hàng", response.getMsg());
        assertNull(response.getData());
    }

    /**
     * TC50 - Mục tiêu: Lấy đơn hàng với ID âm
     * Input: id = -1
     * Expected Output: 200 OK, msg = "Không tìm thấy đơn hàng", data = null
     * Ghi chú: Trường hợp ID hợp lệ về kiểu nhưng không tồn tại.
     */
    @Test
    void getById_ShouldHandleNegativeId() {
        when(donHangRepo.findById(-1)).thenReturn(Optional.empty());
        ResponseDTO<DonHang> response = donHangService.getById(-1);
        assertEquals(200, response.getStatus());
        assertEquals("Không tìm thấy đơn hàng", response.getMsg());
        assertNull(response.getData());
    }

    /**
     * TC51 - Mục tiêu: Lấy đơn hàng với ID null gây lỗi NullPointerException
     * Input: id = null
     * Expected Output: NullPointerException
     * Ghi chú: Test defensive programming nếu cần validate null.
     */
    @Test
    void getById_ShouldThrowException_WhenIdIsNull() {
        assertThrows(NullPointerException.class, () -> donHangService.getById(null));
    }
    /**
     * TC52 - Mục tiêu: Xóa đơn hàng theo ID
     * Input: id = 3
     * Expected Output: ResponseDTO status = 200, msg = "Thành công."
     * Ghi chú: Kiểm tra logic xóa đơn hàng.
     */
    @Test
    void delete_ShouldReturnSuccess() {
        doNothing().when(donHangRepo).deleteById(3);
        ResponseDTO<Void> response = donHangService.delete(3);
        assertEquals(200, response.getStatus());
        assertEquals("Thành công.", response.getMsg());
    }
    /**
     * TC53 - Mục tiêu: Gọi xóa với ID null gây lỗi NullPointerException
     * Input: id = null
     * Expected Output: NullPointerException
     * Ghi chú: Kiểm tra xử lý null nếu chưa có validate.
     */
    @Test
    void delete_ShouldThrowException_WhenIdIsNull() {
        assertThrows(NullPointerException.class, () -> donHangService.delete(null));
    }

    /**
     * TC54 - Mục tiêu: Gọi xóa với ID âm gây lỗi IllegalArgumentException
     * Input: id = -1
     * Expected Output: IllegalArgumentException
     * Ghi chú: Kiểm tra hệ thống có ném exception khi nhận ID âm.
     */
    @Test
    void delete_ShouldThrowException_WhenIdIsNegative() {
        // Tạo test case giả định hệ thống không xử lý ID âm
        assertThrows(IllegalArgumentException.class, () -> donHangService.delete(-1));  // Mong đợi một exception
    }

    /**
     * TC55 - Mục tiêu: Tạo đơn hàng nhưng thiếu người tạo (cả KhachHangId và NguoiDungId đều null)
     * Input: donHangDTO không có người tạo, có danh sách chi tiết rỗng
     * Expected Output: ResponseDTO status = 409, msg = "Không có người tạo đơn"
     * Ghi chú: Validate bắt buộc người tạo đơn hàng.
     */
    @Test
    void create_ShouldReturnConflict_WhenNoNguoiDungOrKhachHang() {
        DonHangDTO dto = new DonHangDTO();
        dto.setPhuongThucThanhToan("TIEN_MAT");
        dto.setChiTietDonHangs(new ArrayList<>());
        ResponseDTO<DonHang> response = donHangService.create(dto);
        assertEquals(409, response.getStatus());
        assertEquals("Không có người tạo đơn", response.getMsg());
    }
    /**
     * TC56 - Mục tiêu: Tạo đơn hàng hợp lệ với NguoiDung và chi tiết đơn hàng
     * Input: donHangDTO với NguoiDungId = 1, 1 chi tiết đơn hàng (100 * 2)
     * Expected Output: tổng tiền = 200.0, status = 200
     * Ghi chú: Kiểm tra đúng tổng tiền và liên kết đối tượng.
     */
    @Test
    void create_ShouldCalculateTotalAmount() {
        DonHangDTO dto = new DonHangDTO();
        dto.setNguoiDungId(1);
        dto.setPhuongThucThanhToan("TIEN_MAT");
        ChiTietDonHangDTO ct = new ChiTietDonHangDTO();
        ct.setDonGia(100.0);
        ct.setSoLuong(2);
        ct.setThuocId(1);
        dto.setChiTietDonHangs(List.of(ct));
        NguoiDung user = new NguoiDung();
        Thuoc thuoc = new Thuoc();
        when(nguoiDungRepo.findById(1)).thenReturn(Optional.of(user));
        when(thuocRepo.findById(1)).thenReturn(Optional.of(thuoc));
        when(donHangRepo.save(any(DonHang.class))).thenAnswer(inv -> inv.getArgument(0));
        ResponseDTO<DonHang> response = donHangService.create(dto);
        assertEquals(200, response.getStatus());
        assertEquals("ok", response.getMsg());
        assertEquals(200.0, response.getData().getTongTien());
    }
    /**
     * TC57 - Mục tiêu: Gọi create với phương thức thanh toán không hợp lệ
     * Input: donHangDTO có nguoiDungId, phương thức thanh toán = "INVALID"
     * Expected Output: IllegalArgumentException
     * Ghi chú: Test nhánh kiểm tra throw khi phương thức thanh toán sai
     */
    @Test
    void create_ShouldThrowException_WhenPhuongThucThanhToanInvalid() {
        DonHangDTO dto = new DonHangDTO();
        dto.setNguoiDungId(1);
        dto.setPhuongThucThanhToan("INVALID");
        dto.setChiTietDonHangs(new ArrayList<>());

        when(nguoiDungRepo.findById(1)).thenReturn(Optional.of(new NguoiDung()));

        assertThrows(IllegalArgumentException.class, () -> donHangService.create(dto));
    }

    /**
     * TC58 - Mục tiêu: Tạo đơn hàng với KhachHang và kiểm tra tạo thông báo thành công
     * Input: donHangDTO có KhachHangId = 1, có 1 ChiTietDonHang
     * Expected Output: Thông báo được lưu, status 200
     * Ghi chú: Phủ đoạn tạo thông báo nếu có KhachHang
     */
    @Test
    void create_WithKhachHang_ShouldSendThongBao() {
        DonHangDTO dto = new DonHangDTO();
        dto.setKhachHangId(1);
        dto.setPhuongThucThanhToan("TIEN_MAT");
        ChiTietDonHangDTO ct = new ChiTietDonHangDTO();
        ct.setDonGia(100.0);
        ct.setSoLuong(1);
        ct.setThuocId(1);
        dto.setChiTietDonHangs(List.of(ct));

        NguoiDung kh = new NguoiDung();
        Thuoc thuoc = new Thuoc();

        when(nguoiDungRepo.findById(1)).thenReturn(Optional.of(kh));
        when(thuocRepo.findById(1)).thenReturn(Optional.of(thuoc));
        when(donHangRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ResponseDTO<DonHang> response = donHangService.create(dto);

        assertEquals(200, response.getStatus());
        verify(thongBaoRepo).save(any());
    }
    /**
     * TC59 - Mục tiêu: Tạo đơn hàng với KhachHangId không tồn tại trong DB
     * Input: donHangDTO với KhachHangId = 999 (không tồn tại)
     * Expected Output: RuntimeException, msg = "Khách hàng không tồn tại"
     * Ghi chú: Kiểm tra khi KhachHangId không tồn tại trong DB.
     * Note: Test này fail nếu hệ thống không ném exception khi khách hàng không tồn tại.
     */
    @Test
    void create_ShouldThrowException_WhenKhachHangNotFound() {
        DonHangDTO dto = new DonHangDTO();
        dto.setKhachHangId(999);  // Khách hàng không tồn tại
        dto.setPhuongThucThanhToan("TIEN_MAT");
        ChiTietDonHangDTO chiTiet = new ChiTietDonHangDTO();
        chiTiet.setThuocId(1);
        chiTiet.setDonGia(100.0);
        chiTiet.setSoLuong(2);
        dto.setChiTietDonHangs(List.of(chiTiet));

        when(nguoiDungRepo.findById(999)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> donHangService.create(dto));
    }

    /**
     * TC60 - Mục tiêu: Thay đổi trạng thái thanh toán thành công
     * Input: donHangDTO có id = 1, trạng thái = DA_THANH_TOAN
     * Expected Output: ResponseDTO status = 200, msg = "Thành công", data != null
     * Ghi chú: Trường hợp cập nhật trạng thái thanh toán thành công.
     */
    @Test
    void changTrangThaiThanhToan_ShouldUpdateSuccessfully() {
        DonHang donHang = new DonHang();
        DonHangDTO dto = new DonHangDTO();
        dto.setId(1);
        dto.setTrangThaiThanhToan("DA_THANH_TOAN");
        when(donHangRepo.findById(1)).thenReturn(Optional.of(donHang));
        when(donHangRepo.save(any(DonHang.class))).thenReturn(donHang);
        ResponseDTO<DonHang> response = donHangService.changTrangThaiThanhToan(dto);
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        assertNotNull(response.getData());
    }

    /**
     * TC61 - Mục tiêu: Thay đổi trạng thái thanh toán nhưng đơn hàng không tồn tại
     * Input: donHangDTO có id = 99
     * Expected Output: ResponseDTO status = 200, msg = "Không tìm thấy đơn hàng", data = null
     * Ghi chú: Kiểm tra xử lý khi không tìm thấy đơn cần cập nhật trạng thái thanh toán.
     */
    @Test
    void changTrangThaiThanhToan_ShouldReturnNotFound() {
        DonHangDTO dto = new DonHangDTO();
        dto.setId(99);
        dto.setTrangThaiThanhToan("DA_THANH_TOAN");
        when(donHangRepo.findById(99)).thenReturn(Optional.empty());
        ResponseDTO<DonHang> response = donHangService.changTrangThaiThanhToan(dto);
        assertEquals(200, response.getStatus());
        assertEquals("Không tìm thấy đơn hàng", response.getMsg());
    }
    /**
     * TC62 - Mục tiêu: Đổi trạng thái giao hàng thành DA_GIAO sẽ gọi updateSoLuongKho
     * Input: donHangDTO.id = 1, trạng thái = DA_GIAO
     * Expected Output: gọi updateSoLuongKho, status = 200
     * Ghi chú: Đảm bảo logic gọi update tồn kho khi giao hàng
     */
    @Test
    void changTrangThaiGiaoHang_ShouldUpdateTonKho_WhenDelivered() {
        DonHang donHang = new DonHang();
        donHang.setId(1);
        donHang.setKhachHang(new NguoiDung());
        donHang.setChiTietDonHangs(List.of(new ChiTietDonHang()));
        DonHangDTO dto = new DonHangDTO();
        dto.setId(1);
        dto.setTrangThaiGiaoHang("DA_GIAO");

        when(donHangRepo.findById(1)).thenReturn(Optional.of(donHang));
        when(donHangRepo.save(any())).thenReturn(donHang);

        ResponseDTO<DonHang> response = donHangService.changTrangThaiGiaoHang(dto);

        assertEquals(200, response.getStatus());
        verify(thongBaoRepo).save(any());
    }

    /**
     * TC63 - Mục tiêu: Cập nhật tồn kho khi giao hàng thành công
     * Input: id = 1 ChiTietDonHang với số lượng = 2, tồn kho ban đầu = 10
     * Expected Output: tồn kho sau khi cập nhật = 8
     * Ghi chú: Test trực tiếp hàm updateSoLuongKho.
     */
    @Test
    void updateSoLuongKho_ShouldReduceStock() {
        Thuoc thuoc = new Thuoc();
        thuoc.setId(1);
        ChiTietDonHang chiTiet = new ChiTietDonHang();
        chiTiet.setSoLuong(2);
        chiTiet.setThuoc(thuoc);
        TonKho tonKho = new TonKho();
        tonKho.setSoLuong(10);
        when(tonKhoRepo.findNearestTonKhoByThuocIdAndHanSuDungBefore(anyInt(), any(Date.class))).thenReturn(tonKho);
        donHangService.updateSoLuongKho(List.of(chiTiet));
        verify(tonKhoRepo).save(tonKho);
        assertEquals(8, tonKho.getSoLuong());
    }



    /**
     * TC64 - Mục tiêu: Cập nhật đơn hàng nhưng không tồn tại trong DB
     * Input: donHangDTO.id = 99
     * Expected Output:
     * Ghi chú: Đảm bảo xử lý đúng khi không tìm thấy đơn hàng cần update.
     */
    @Test
    void update_ShouldReturnError_WhenNotFound() {
        DonHangDTO dto = new DonHangDTO();
        dto.setId(99);
        when(donHangRepo.findById(99)).thenReturn(Optional.empty());
        ResponseDTO<DonHang> response = donHangService.update(dto);
        assertEquals(409, response.getStatus());
        assertEquals("Đơn hàng không tồn tài", response.getMsg());
    }

    /**
     * TC65 - Mục tiêu: Cập nhật đơn hàng nhưng thiếu thông tin người tạo
     * Input: donHangDTO.id = 1, không có KhachHangId/NguoiDungId
     * Expected Output: ResponseDTO status = 409, msg = "Không có người tạo đơn"
     * Ghi chú: Validate update yêu cầu có người tạo.
     */
    @Test
    void update_ShouldReturnError_WhenNoNguoiDungOrKhachHang() {
        DonHangDTO dto = new DonHangDTO();
        dto.setId(1);
        dto.setChiTietDonHangs(new ArrayList<>());
        when(donHangRepo.findById(1)).thenReturn(Optional.of(new DonHang()));
        ResponseDTO<DonHang> response = donHangService.update(dto);
        assertEquals(409, response.getStatus());
        assertEquals("Không có người tạo đơn", response.getMsg());
    }
    /**
     * TC66 - Mục tiêu: Cập nhật đơn hàng thành công với thông tin đầy đủ
     * Input: donHangDTO.id = 1, donHangDTO.getKhachHangId() = 2, donHangDTO.getNguoiDungId() = 3, chiTietDonHangs có giá trị hợp lệ
     * Expected Output: ResponseDTO status = 200, msg = "Thành công"
     * Ghi chú: Đảm bảo rằng đơn hàng đã được cập nhật với thông tin mới và tổng tiền được tính đúng.
     */
    @Test
    void update_ShouldReturnSuccess_WhenValidData() {
        DonHangDTO dto = new DonHangDTO();
        dto.setId(1);
        dto.setKhachHangId(2);  // Đảm bảo có Khách Hàng
        dto.setNguoiDungId(3);  // Đảm bảo có Người Dùng
        dto.setChiTietDonHangs(new ArrayList<>());

        // Giả lập DonHang tồn tại trong DB
        DonHang donHang = new DonHang();
        donHang.setId(1);
        when(donHangRepo.findById(1)).thenReturn(Optional.of(donHang));

        // Giả lập Khách Hàng và Người Dùng
        NguoiDung khachHang = new NguoiDung();
        when(nguoiDungRepo.findById(2)).thenReturn(Optional.of(khachHang));

        NguoiDung nguoiDung = new NguoiDung();
        when(nguoiDungRepo.findById(3)).thenReturn(Optional.of(nguoiDung));

        // Giả lập các Chi Tiết Đơn Hàng hợp lệ
        ChiTietDonHangDTO chiTietDTO = new ChiTietDonHangDTO();
        chiTietDTO.setThuocId(1);
        chiTietDTO.setDonGia(100.0);  // Đảm bảo donGia không null
        chiTietDTO.setSoLuong(2);
        dto.setChiTietDonHangs(List.of(chiTietDTO));

        Thuoc thuoc = new Thuoc();
        when(thuocRepo.findById(1)).thenReturn(Optional.of(thuoc));

        ResponseDTO<DonHang> response = donHangService.update(dto);

        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
    }



    /**
     * TC67 - Mục tiêu: Lấy danh sách đơn hàng khi không có keyword và id = 0
     * Input: searchDTO(keyword = null, id = 0)
     * Expected Output: Gọi donHangRepo.findAll(), trả về status = 200
     * Ghi chú: Test lại với PageImpl thay vì mock null.
     */
    @Test
    void getByTrangThaiGiaoHang_WithNoKeywordAndZeroId_ShouldCallFindAll() {
        // Tạo SearchDTO
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setCurrentPage(0);
        searchDTO.setSize(10);
        searchDTO.setId(0);

        // Tạo dữ liệu mẫu
        DonHang donHang = new DonHang();
        donHang.setId(1);
        donHang.setTenKhachHang("Nguyễn Văn A");
        donHang.setSoDienThoai("0901234567");
        donHang.setDiaChi("123 Đường ABC, Hà Nội");
        donHang.setEmail("a@example.com");
        donHang.setTongTien(500000.0);
        donHang.setTrangThaiGiaoHang(DonHang.TrangThaiGiaoHang.DANG_XU_LY);
        donHang.setPhuongThucThanhToan(DonHang.PhuongThucThanhToan.TIEN_MAT);
        donHang.setTrangThaiThanhToan(DonHang.TrangThaiThanhToan.CHUA_THANH_TOAN);

        List<DonHang> donHangList = List.of(donHang);
        Page<DonHang> page = new PageImpl<>(donHangList);
        when(donHangRepo.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(page);


        // Gọi hàm
        ResponseDTO<PageDTO<List<DonHang>>> response = donHangService.getByTrangThaiGiaoHang(searchDTO);

        // Kiểm tra kết quả
        assertEquals(200, response.getStatus());
        assertEquals("ok", response.getMsg());
        assertNotNull(response.getData());
        assertEquals(1, response.getData().getTotalElements());
        assertEquals(1, response.getData().getData().size());
    }

    /**
     * TC68 - Mục tiêu: Lấy danh sách đơn hàng theo ID người dùng
     * Input: searchDTO(keyword = null, id = 2)
     * Expected Output: Gọi donHangRepo.findByNguoiDungId(2), status = 200
     * Ghi chú: Kiểm tra nhánh lọc theo người dùng.
     */
    @Test
    void getByTrangThaiGiaoHang_WithNoKeywordAndValidId_ShouldCallFindByNguoiDungId() {
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setCurrentPage(0);
        searchDTO.setSize(10);
        searchDTO.setId(2);

        Page<DonHang> page = mock(Page.class);
        when(donHangRepo.findByNguoiDungId(eq(2), any())).thenReturn(page);
        when(page.getContent()).thenReturn(List.of());
        when(page.getTotalElements()).thenReturn(0L);
        when(page.getTotalPages()).thenReturn(1);

        ResponseDTO<PageDTO<List<DonHang>>> response = donHangService.getByTrangThaiGiaoHang(searchDTO);

        assertEquals(200, response.getStatus());
        assertEquals("ok", response.getMsg());
        assertNotNull(response.getData());
    }

    /**
     * TC69 - Mục tiêu: Lấy danh sách đơn hàng theo trạng thái giao hàng cụ thể
     * Input: searchDTO(keyword = "DANG_XU_LY", id = 1)
     * Expected Output: Gọi donHangRepo.findByTrangThaiGiaoHang(...), status = 200
     * Ghi chú: Đảm bảo mapping enum và lọc đúng.
     */
    @Test
    void getByTrangThaiGiaoHang_WithKeyword_ShouldCallFindByTrangThaiGiaoHang() {
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setCurrentPage(0);
        searchDTO.setSize(10);
        searchDTO.setId(1);
        searchDTO.setKeyWord("DANG_XU_LY");

        Page<DonHang> page = mock(Page.class);
        when(donHangRepo.findByTrangThaiGiaoHang(eq(DonHang.TrangThaiGiaoHang.DANG_XU_LY), eq(1), any())).thenReturn(page);
        when(page.getContent()).thenReturn(List.of());
        when(page.getTotalElements()).thenReturn(0L);
        when(page.getTotalPages()).thenReturn(1);

        ResponseDTO<PageDTO<List<DonHang>>> response = donHangService.getByTrangThaiGiaoHang(searchDTO);

        assertEquals(200, response.getStatus());
        assertEquals("ok", response.getMsg());
        assertNotNull(response.getData());
    }
    /**
     * TC70 - Mục tiêu: getByTrangThaiGiaoHang có sortedField hợp lệ
     * Input: searchDTO.setSortedField("tenKhachHang")
     * Expected Output: Không lỗi, gọi thành công
     * Ghi chú: Đảm bảo nhánh sortBy được gọi đúng
     */
    @Test
    void getByTrangThaiGiaoHang_WithSortedField_ShouldSortCorrectly() {
        SearchDTO dto = new SearchDTO();
        dto.setSortedField("tenKhachHang");
        dto.setId(0);

        Page<DonHang> page = mock(Page.class);
        when(donHangRepo.findAll(any(Pageable.class))).thenReturn(page);
        when(page.getContent()).thenReturn(List.of());
        when(page.getTotalElements()).thenReturn(0L);
        when(page.getTotalPages()).thenReturn(1);

        ResponseDTO<PageDTO<List<DonHang>>> response = donHangService.getByTrangThaiGiaoHang(dto);

        assertEquals(200, response.getStatus());
        assertEquals("ok", response.getMsg());
        assertNotNull(response.getData());
    }
    /**
     * TC71 - Mục tiêu: Đổi trạng thái giao hàng nhưng đơn hàng không tồn tại
     * Input: donHangDTO.id = 404
     * Expected Output: ResponseDTO status = 200, msg = "Không tìm thấy đơn hàng"
     * Ghi chú: Trường hợp không tìm thấy đơn hàng cần cập nhật trạng thái.
     */
    @Test
    void changTrangThaiGiaoHang_ShouldReturnNotFound() {
        DonHangDTO dto = new DonHangDTO();
        dto.setId(404);
        when(donHangRepo.findById(404)).thenReturn(Optional.empty());
        ResponseDTO<DonHang> response = donHangService.changTrangThaiGiaoHang(dto);
        assertEquals(200, response.getStatus());
        assertEquals("Không tìm thấy đơn hàng", response.getMsg());
        assertNull(response.getData());
    }

    /**
     * TC72 - Mục tiêu: Đổi trạng thái giao hàng thành DA_HUY và tạo thông báo
     * Input: donHangDTO.id = 1, trạng thái = DA_HUY
     * Expected Output: Gửi thông báo, status = 200
     * Ghi chú: Kiểm tra logic tạo ThongBao khi huỷ đơn hàng.
     */
    @Test
    void changTrangThaiGiaoHang_ShouldCreateThongBaoWhenCancelled() {
        DonHang donHang = new DonHang();
        donHang.setId(1);
        NguoiDung kh = new NguoiDung();
        donHang.setKhachHang(kh);
        donHang.setChiTietDonHangs(new ArrayList<>());

        DonHangDTO dto = new DonHangDTO();
        dto.setId(1);
        dto.setTrangThaiGiaoHang("DA_HUY");

        when(donHangRepo.findById(1)).thenReturn(Optional.of(donHang));
        when(donHangRepo.save(any())).thenReturn(donHang);

        ResponseDTO<DonHang> response = donHangService.changTrangThaiGiaoHang(dto);

        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());
        verify(thongBaoRepo).save(any());
    }
    /**
     * TC73 - Mục tiêu: Kiểm tra cập nhật trạng thái giao hàng thành công và đảm bảo gọi updateSoLuongKho.
     * Input: donHangDTO.id = 1, trạng thái = DA_GIAO
     * Expected Output: status = 200, gọi updateSoLuongKho
     * Ghi chú: Đảm bảo rằng khi trạng thái giao hàng là DA_GIAO, hệ thống gọi phương thức cập nhật kho.
     */
    @Test
    void changTrangThaiGiaoHang_ShouldCallUpdateSoLuongKho_WhenDelivered() {
        DonHang donHang = new DonHang();
        donHang.setId(1);
        donHang.setTrangThaiGiaoHang(DonHang.TrangThaiGiaoHang.DA_GIAO);  // Đảm bảo trạng thái là DA_GIAO

        DonHangDTO dto = new DonHangDTO();
        dto.setId(1);
        dto.setTrangThaiGiaoHang("DA_GIAO");

        // Mock donHangRepo và khi tìm thấy đơn hàng thì trả về đối tượng donHang
        when(donHangRepo.findById(1)).thenReturn(Optional.of(donHang));
        when(donHangRepo.save(any())).thenReturn(donHang);

        // Spy đối tượng donHangService để mock updateSoLuongKho
        DonHangServiceImpl donHangServiceMock = spy(donHangService); // Sử dụng spy thay vì mock
        doNothing().when(donHangServiceMock).updateSoLuongKho(any());

        // Gọi phương thức changTrangThaiGiaoHang
        ResponseDTO<DonHang> response = donHangServiceMock.changTrangThaiGiaoHang(dto);

        // Kiểm tra kết quả
        assertEquals(200, response.getStatus());
        assertEquals("Thành công", response.getMsg());

        // Kiểm tra phương thức updateSoLuongKho đã được gọi
        verify(donHangServiceMock).updateSoLuongKho(any());
    }


}
