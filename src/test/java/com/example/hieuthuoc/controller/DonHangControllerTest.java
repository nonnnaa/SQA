package com.example.hieuthuoc.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;

import com.example.hieuthuoc.dto.*;
import com.example.hieuthuoc.entity.DonHang;
import com.example.hieuthuoc.service.DonHangService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class DonHangControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DonHangService donHangService;

    @Autowired
    private ObjectMapper objectMapper;

    private DonHangDTO mockDonHangDTO;
    private DonHang mockDonHang;
    private ResponseDTO<DonHang> mockResponse;
    private ResponseDTO<Void> mockVoidResponse;

    @BeforeEach
    void setup() {
        mockDonHangDTO = new DonHangDTO(); // có thể setup thêm field nếu cần
        mockDonHang = new DonHang();
        mockResponse = new ResponseDTO<>(200, "OK", mockDonHang);
        mockVoidResponse = new ResponseDTO<>(200, "Deleted", null);
    }

    /**
     * TC01 - Mục tiêu: Test /donhang/get?id=1 trả về đơn hàng đúng
     * Input: id = 1
     * Output: 200 OK, status = 200, data.id = 1
     * Ghi chú: Happy case, mock donHangService.getById()
     */
    @Test
    @WithMockUser
    void getById_ShouldReturnDonHang() throws Exception {
        DonHang donHang = new DonHang();
        donHang.setId(1);
        ResponseDTO<DonHang> mockResponse = ResponseDTO.<DonHang>builder()
                .status(200)
                .msg("Thành công")
                .data(donHang)
                .build();

        Mockito.when(donHangService.getById(eq(1))).thenReturn(mockResponse);

        mockMvc.perform(get("/donhang/get").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.id").value(1));
    }
    /**
     * TC02 - Mục tiêu: Test /donhang/get?id=999 với ID không tồn tại
     * Input: id = 999
     * Expected output: Không trả về đơn hàng nào (data không tồn tại)
     * Output: 200 OK, status = 200, không có trường data trong response
     * Ghi chú: Nếu vẫn xuất hiện trường data trong response thì là FAIL
     */
    @Test
    @WithMockUser
    void getById_ShouldFail_WhenNonExistingIdReturnsData() throws Exception {
        Mockito.when(donHangService.getById(eq(999))).thenReturn(
                ResponseDTO.<DonHang>builder()
                        .status(200)
                        .msg("Không tìm thấy đơn hàng")
                        .data(null)
                        .build());

        mockMvc.perform(get("/donhang/get").param("id", "999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist()); // <-- Nếu data tồn tại => FAIL
    }

    /**
     * TC03 - Mục tiêu: Test /donhang/get?id=-1 với ID âm
     * Input: id = -1
     * Expected output: Không trả về đơn hàng nào (data == null hoặc không tồn tại)
     * Output: 200 OK, status = 200, không có trường data trong response
     * Ghi chú: Trường hợp ID âm không hợp lệ nên không được trả ra đơn hàng nào
     */
    @Test
    @WithMockUser
    void getById_ShouldReturnNull_WhenNegativeId() throws Exception {
        Mockito.when(donHangService.getById(eq(-1))).thenReturn(
                ResponseDTO.<DonHang>builder()
                        .status(200)
                        .msg("Không tìm thấy đơn hàng")
                        .data(null)
                        .build());

        mockMvc.perform(get("/donhang/get").param("id", "-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    /**
     * TC04 - Mục tiêu: Test /donhang/get?id=abc với ID sai định dạng
     * Input: id = \"abc\"
     * Expected output: 400 Bad Request
     * Output: 400 Bad Request
     * Ghi chú: Spring Boot tự động bắt lỗi parse int cho @RequestParam
     */
    @Test
    @WithMockUser
    void getById_ShouldReturnBadRequest_WhenInvalidFormat() throws Exception {
        mockMvc.perform(get("/donhang/get").param("id", "abc"))
                .andExpect(status().isBadRequest());
    }



    /**
     * TC05 - Mục tiêu: Truy cập đơn hàng không thuộc user hiện tại
     * Input: id = 7
     * Expected output: 403 Forbidden nếu phân quyền chặt
     * Output: 200 OK, status = 200, data.id = 7
     * Ghi chú: Thiếu kiểm tra quyền sở hữu đơn hàng trong controller/service
     */
    @Test
    @WithMockUser
    void getById_ShouldReturnOrder_WhenOrderOfOtherUser() throws Exception {
        DonHang dh = new DonHang(); dh.setId(7);
        ResponseDTO<DonHang> response = ResponseDTO.<DonHang>builder()
                .status(200).msg("Thành công").data(dh).build();

        Mockito.when(donHangService.getById(eq(7))).thenReturn(response);

        mockMvc.perform(get("/donhang/get").param("id", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.id").value(7));
    }



    /**
     * TC06 - Mục tiêu: Test trả về danh sách đơn hàng theo trạng thái (đúng định dạng đầu vào)
     * Input: SearchDTO với từ khóa hợp lệ, id = 1
     * Expected output: 200 OK, có danh sách đơn hàng trả về
     * Output: 200 OK, data.data là list rỗng (đúng định dạng)
     * Ghi chú: Mock trả về danh sách rỗng nhưng đúng cấu trúc PageDTO
     */
    @Test
    @WithMockUser
    void getByTrangThaiGiaoHang_ShouldReturnList() throws Exception {
        SearchDTO dto = new SearchDTO();
        dto.setKeyWord("DANG_XU_LY");
        dto.setId(1);

        PageDTO<List<DonHang>> pageDTO = new PageDTO<>();
        pageDTO.setData(Collections.emptyList());
        pageDTO.setTotalPages(1);
        pageDTO.setTotalElements(0L);

        Mockito.when(donHangService.getByTrangThaiGiaoHang(any())).thenReturn(
                ResponseDTO.<PageDTO<List<DonHang>>>builder()
                        .status(200).msg("ok").data(pageDTO).build());

        mockMvc.perform(post("/donhang/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.data").isArray());
    }

    /**
     * TC07 - Mục tiêu: Test với SearchDTO không có keyword (trả về toàn bộ đơn hàng)
     * Input: SearchDTO với keyword rỗng, id = 0
     * Expected output: 200 OK, danh sách đơn hàng trả về
     * Output: 200 OK, data.data là list rỗng (nếu không có đơn)
     * Ghi chú: Hệ thống xử lý đúng khi không có từ khóa lọc
     */
    @Test
    @WithMockUser
    void getByTrangThaiGiaoHang_ShouldReturnAll_WhenNoKeyword() throws Exception {
        SearchDTO dto = new SearchDTO();
        dto.setKeyWord("");
        dto.setId(0);

        PageDTO<List<DonHang>> pageDTO = new PageDTO<>();
        pageDTO.setData(Collections.emptyList());
        pageDTO.setTotalPages(1);
        pageDTO.setTotalElements(0L);

        Mockito.when(donHangService.getByTrangThaiGiaoHang(any())).thenReturn(
                ResponseDTO.<PageDTO<List<DonHang>>>builder()
                        .status(200).msg("ok").data(pageDTO).build());

        mockMvc.perform(post("/donhang/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.data").isArray());
    }

    /**
     * TC08 - Mục tiêu: Test với keyword không hợp lệ (không map được enum)
     * Input: SearchDTO với keyword = "SAI_TRANG_THAI"
     * Expected output: 400 Bad Request hoặc 500 Internal Server Error nếu không handle
     * Output: 500 Internal Server Error
     * Ghi chú: Hiện tại hệ thống không kiểm soát lỗi enum, nên trả lỗi hệ thống
     */
    @Test
    @WithMockUser
    void getByTrangThaiGiaoHang_ShouldFail_WhenInvalidEnum() throws Exception {
        SearchDTO dto = new SearchDTO();
        dto.setKeyWord("SAI_TRANG_THAI");
        dto.setId(1);

        mockMvc.perform(post("/donhang/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError()); // hoặc .isBadRequest() nếu handle tốt
    }

    /**
     * TC09 - Mục tiêu: Test với SearchDTO thiếu id, keyword null
     * Input: SearchDTO thiếu id, keyword null
     * Expected output: 200 OK, danh sách toàn bộ đơn hàng trả về
     * Output: 200 OK, data.data là list rỗng (nếu không có đơn)
     * Ghi chú: Hệ thống xử lý đúng khi thiếu cả từ khóa và id người dùng
     */
    @Test
    @WithMockUser
    void getByTrangThaiGiaoHang_ShouldReturnAll_WhenKeywordNull() throws Exception {
        SearchDTO dto = new SearchDTO();
        dto.setKeyWord(null);

        PageDTO<List<DonHang>> pageDTO = new PageDTO<>();
        pageDTO.setData(Collections.emptyList());
        pageDTO.setTotalPages(1);
        pageDTO.setTotalElements(0L);

        Mockito.when(donHangService.getByTrangThaiGiaoHang(any())).thenReturn(
                ResponseDTO.<PageDTO<List<DonHang>>>builder()
                        .status(200).msg("ok").data(pageDTO).build());

        mockMvc.perform(post("/donhang/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.data").isArray());
    }
    /**
     * TC10 - Mục tiêu: Test với currentPage và size âm
     * Input: SearchDTO với currentPage = -1, size = -5
     * Expected output: 400 Bad Request hoặc lỗi hệ thống nếu không validate
     * Output: Phụ thuộc vào xử lý trong service (có thể 500 Internal Server Error)
     * Ghi chú: Kiểm tra xem hệ thống có kiểm soát phân trang âm hay không
     */
    @Test
    @WithMockUser
    void getByTrangThaiGiaoHang_ShouldFail_WhenPagingNegative() throws Exception {
        SearchDTO dto = new SearchDTO();
        dto.setKeyWord("DANG_XU_LY");
        dto.setId(1);
        dto.setCurrentPage(-1);
        dto.setSize(-5);

        mockMvc.perform(post("/donhang/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError()); // nếu có xử lý tốt có thể là isBadRequest()
    }


    /**
     * TC11 - Mục tiêu: Test với sortedField không hợp lệ (không tồn tại trong entity)
     * Input: SearchDTO với sortedField = "khong_ton_tai"
     * Expected output: 400 Bad Request hoặc lỗi runtime nếu không validate
     * Output: Có thể là 500 Internal Server Error nếu Sort.by(...) ném exception
     * Ghi chú: Kiểm tra hệ thống có kiểm soát sắp xếp theo trường không hợp lệ không
     */
    @Test
    @WithMockUser
    void getByTrangThaiGiaoHang_ShouldFail_WhenInvalidSortField() throws Exception {
        SearchDTO dto = new SearchDTO();
        dto.setKeyWord("DA_GIAO");
        dto.setId(1);
        dto.setSortedField("khong_ton_tai");

        mockMvc.perform(post("/donhang/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());
    }





    /**
     * TC12 - Mục tiêu: Chuyển trạng thái giao hàng hợp lệ (DANG_GIAO → DA_GIAO)
     * Input: donHangDTO.id = 1, trangThaiGiaoHang = "DA_GIAO"
     * Expected output: 200 OK, data.id = 1
     * Output: 200 OK, trạng thái cập nhật thành công
     * Ghi chú: Test happy case
     */
    @Test
    @WithMockUser
    void changTrangThaiGiaoHang_ShouldSucceed_WhenValidUpdate() throws Exception {
        DonHangDTO dto = new DonHangDTO();
        dto.setId(1);
        dto.setTrangThaiGiaoHang("DA_GIAO");

        DonHang dh = new DonHang();
        dh.setId(1);

        Mockito.when(donHangService.changTrangThaiGiaoHang(any())).thenReturn(
                ResponseDTO.<DonHang>builder().status(200).msg("Thành công").data(dh).build());

        mockMvc.perform(post("/donhang/changTrangThaiGiaoHang")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    /**
     * TC13 - Mục tiêu: Hủy đơn hàng thành công
     * Input: id = 2, trangThaiGiaoHang = "DA_HUY"
     * Expected output: 200 OK, data.id = 2
     * Output: 200 OK
     * Ghi chú: Hệ thống cho phép hủy đơn bình thường
     */
    @Test
    @WithMockUser
    void changTrangThaiGiaoHang_ShouldSucceed_WhenHuyDonHang() throws Exception {
        DonHangDTO dto = new DonHangDTO();
        dto.setId(2);
        dto.setTrangThaiGiaoHang("DA_HUY");

        DonHang dh = new DonHang();
        dh.setId(2);

        Mockito.when(donHangService.changTrangThaiGiaoHang(any())).thenReturn(
                ResponseDTO.<DonHang>builder().status(200).msg("Đã hủy thành công").data(dh).build());

        mockMvc.perform(post("/donhang/changTrangThaiGiaoHang")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(2));
    }

    /**
     * TC14 - Mục tiêu: Chuyển trạng thái với ID không tồn tại
     * Input: id = 999, trangThaiGiaoHang = "DA_GIAO"
     * Expected output: 200 OK, data = null, msg = "Không tìm thấy đơn hàng"
     * Output: 200 OK
     * Ghi chú: Kiểm tra xử lý trường hợp không tìm thấy đơn hàng
     */
    @Test
    @WithMockUser
    void changTrangThaiGiaoHang_ShouldReturnNull_WhenIdNotFound() throws Exception {
        DonHangDTO dto = new DonHangDTO();
        dto.setId(999);
        dto.setTrangThaiGiaoHang("DA_GIAO");

        Mockito.when(donHangService.changTrangThaiGiaoHang(any())).thenReturn(
                ResponseDTO.<DonHang>builder().status(200).msg("Không tìm thấy đơn hàng").data(null).build());

        mockMvc.perform(post("/donhang/changTrangThaiGiaoHang")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    /**
     * TC15 - Mục tiêu: Truyền trạng thái không hợp lệ (không thuộc enum)
     * Input: id = 3, trangThaiGiaoHang = "LOI_GIAO"
     * Expected output: 500 Internal Server Error (nếu không handle)
     * Output: 500 Internal Server Error
     * Ghi chú: Enum.valueOf sẽ ném exception nếu không map được
     */
    @Test
    @WithMockUser
    void changTrangThaiGiaoHang_ShouldFail_WhenTrangThaiInvalid() throws Exception {
        DonHangDTO dto = new DonHangDTO();
        dto.setId(3);
        dto.setTrangThaiGiaoHang("LOI_GIAO");

        mockMvc.perform(post("/donhang/changTrangThaiGiaoHang")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());
    }

    /**
     * TC16 - Mục tiêu: Không truyền trangThaiGiaoHang
     * Input: id = 4, trangThaiGiaoHang = null
     * Expected output: 400 Bad Request hoặc lỗi runtime tùy validate
     * Output: 500 Internal Server Error nếu không validate @NotNull
     * Ghi chú: Kiểm tra bắt buộc trường trạng thái khi cập nhật
     */
    @Test
    @WithMockUser
    void changTrangThaiGiaoHang_ShouldFail_WhenTrangThaiNull() throws Exception {
        DonHangDTO dto = new DonHangDTO();
        dto.setId(4);
        dto.setTrangThaiGiaoHang(null);

        mockMvc.perform(post("/donhang/changTrangThaiGiaoHang")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());
    }

    /**
     * TC17 - Mục tiêu: Truyền ID âm (không hợp lệ)
     * Input: id = -5, trangThaiGiaoHang = "DA_GIAO"
     * Expected output: 200 OK hoặc lỗi validate tùy cách xử lý
     * Output: 200 OK nếu không validate
     * Ghi chú: Nên bổ sung validate ID > 0 trong service/controller
     */
    @Test
    @WithMockUser
    void changTrangThaiGiaoHang_ShouldSucceed_WhenIdIsNegative() throws Exception {
        DonHangDTO dto = new DonHangDTO();
        dto.setId(-5);
        dto.setTrangThaiGiaoHang("DA_GIAO");

        DonHang dh = new DonHang();
        dh.setId(-5);

        Mockito.when(donHangService.changTrangThaiGiaoHang(any())).thenReturn(
                ResponseDTO.<DonHang>builder().status(200).msg("OK").data(dh).build());

        mockMvc.perform(post("/donhang/changTrangThaiGiaoHang")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(-5));
    }



    /**
     * TC18 - Mục tiêu: Chuyển trạng thái thanh toán hợp lệ (CHUA_THANH_TOAN → DA_THANH_TOAN)
     * Input: id = 1, trangThaiThanhToan = "DA_THANH_TOAN"
     * Expected output: 200 OK, data.id = 1
     * Output: 200 OK, trạng thái thanh toán cập nhật thành công
     * Ghi chú: Trường hợp hợp lệ, mock service trả đơn hàng đúng
     */
    @Test
    @WithMockUser
    void changTrangThaiThanhToan_ShouldSucceed_WhenValid() throws Exception {
        DonHangDTO dto = new DonHangDTO();
        dto.setId(1);
        dto.setTrangThaiThanhToan("DA_THANH_TOAN");

        DonHang dh = new DonHang();
        dh.setId(1);

        Mockito.when(donHangService.changTrangThaiThanhToan(any())).thenReturn(
                ResponseDTO.<DonHang>builder().status(200).msg("Thành công").data(dh).build());

        mockMvc.perform(post("/donhang/changTrangThaiThanhToan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    /**
     * TC19 - Mục tiêu: Truyền ID không tồn tại khi cập nhật trạng thái thanh toán
     * Input: id = 999, trangThaiThanhToan = "DA_THANH_TOAN"
     * Expected output: 200 OK, data = null
     * Output: 200 OK, data = null, msg = "Không tìm thấy đơn hàng"
     * Ghi chú: Kiểm tra xử lý ID không tồn tại
     */
    @Test
    @WithMockUser
    void changTrangThaiThanhToan_ShouldReturnNull_WhenIdNotFound() throws Exception {
        DonHangDTO dto = new DonHangDTO();
        dto.setId(999);
        dto.setTrangThaiThanhToan("DA_THANH_TOAN");

        Mockito.when(donHangService.changTrangThaiThanhToan(any())).thenReturn(
                ResponseDTO.<DonHang>builder().status(200).msg("Không tìm thấy đơn hàng").data(null).build());

        mockMvc.perform(post("/donhang/changTrangThaiThanhToan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    /**
     * TC20 - Mục tiêu: Truyền trạng thái không hợp lệ (không thuộc enum)
     * Input: id = 2, trangThaiThanhToan = "KHONG_HOP_LE"
     * Expected output: 500 Internal Server Error (do Enum.valueOf ném lỗi)
     * Output: 500 Internal Server Error
     * Ghi chú: Kiểm tra hệ thống không bị crash do enum sai
     */
    @Test
    @WithMockUser
    void changTrangThaiThanhToan_ShouldFail_WhenTrangThaiInvalid() throws Exception {
        DonHangDTO dto = new DonHangDTO();
        dto.setId(2);
        dto.setTrangThaiThanhToan("KHONG_HOP_LE");

        mockMvc.perform(post("/donhang/changTrangThaiThanhToan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());
    }

    /**
     * TC21 - Mục tiêu: Không truyền trangThaiThanhToan
     * Input: id = 3, trangThaiThanhToan = null
     * Expected output: 400 Bad Request hoặc lỗi runtime
     * Output: 500 Internal Server Error nếu không validate @NotNull
     * Ghi chú: Kiểm tra xử lý null cho trường bắt buộc
     */
    @Test
    @WithMockUser
    void changTrangThaiThanhToan_ShouldFail_WhenTrangThaiNull() throws Exception {
        DonHangDTO dto = new DonHangDTO();
        dto.setId(3);
        dto.setTrangThaiThanhToan(null);

        mockMvc.perform(post("/donhang/changTrangThaiThanhToan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());
    }

    /**
     * TC22 - Mục tiêu: Truyền ID âm khi cập nhật thanh toán
     * Input: id = -10, trangThaiThanhToan = "DA_THANH_TOAN"
     * Expected output: 200 OK hoặc lỗi validate tùy xử lý
     * Output: 200 OK nếu không có validate
     * Ghi chú: Hệ thống nên kiểm tra ID > 0
     */
    @Test
    @WithMockUser
    void changTrangThaiThanhToan_ShouldSucceed_WhenIdIsNegative() throws Exception {
        DonHangDTO dto = new DonHangDTO();
        dto.setId(-10);
        dto.setTrangThaiThanhToan("DA_THANH_TOAN");

        DonHang dh = new DonHang();
        dh.setId(-10);

        Mockito.when(donHangService.changTrangThaiThanhToan(any())).thenReturn(
                ResponseDTO.<DonHang>builder().status(200).msg("OK").data(dh).build());

        mockMvc.perform(post("/donhang/changTrangThaiThanhToan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(-10));
    }
    /**
     * TC23 - Mục tiêu: Tạo đơn hàng mới với dữ liệu hợp lệ
     * Input: DonHangDTO hợp lệ với chi tiết đơn hàng
     * Expected output: 200 OK, data.id = 1
     * Output: 200 OK, đơn hàng được tạo thành công
     * Ghi chú: Happy case, mock trả về đơn hàng mới
     */
    @Test
    @WithMockUser
    void create_ShouldSucceed_WhenValid() throws Exception {
        DonHangDTO dto = new DonHangDTO();
        dto.setNguoiDungId(1);
        dto.setPhuongThucThanhToan("TIEN_MAT");

        DonHang dh = new DonHang();
        dh.setId(1);

        Mockito.when(donHangService.create(any())).thenReturn(
                ResponseDTO.<DonHang>builder().status(200).msg("OK").data(dh).build());

        mockMvc.perform(post("/donhang/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    /**
     * TC24 - Mục tiêu: Tạo đơn với phương thức thanh toán không hợp lệ
     * Input: phuongThucThanhToan = "KHONG_HOP_LE"
     * Expected output: 500 Internal Server Error
     * Output: 500 Internal Server Error (Enum.valueOf thất bại)
     * Ghi chú: Kiểm tra validation enum thanh toán
     */
    @Test
    @WithMockUser
    void create_ShouldFail_WhenInvalidPaymentMethod() throws Exception {
        DonHangDTO dto = new DonHangDTO();
        dto.setNguoiDungId(1);
        dto.setPhuongThucThanhToan("KHONG_HOP_LE");

        mockMvc.perform(post("/donhang/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());
    }

    /**
     * TC25 - Mục tiêu: Không có người dùng tạo đơn hàng
     * Input: Khách hàng ID = null, Người dùng ID = null
     * Expected output: 409 Conflict, msg = "Không có người tạo đơn"
     * Output: 409 Conflict
     * Ghi chú: Hệ thống bắt buộc có người tạo đơn
     */
    @Test
    @WithMockUser
    void create_ShouldFail_WhenMissingNguoiDungId() throws Exception {
        DonHangDTO dto = new DonHangDTO();
        dto.setPhuongThucThanhToan("TIEN_MAT");

        Mockito.when(donHangService.create(any())).thenReturn(
                ResponseDTO.<DonHang>builder().status(409).msg("Không có người tạo đơn").build());

        mockMvc.perform(post("/donhang/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.msg").value("Không có người tạo đơn"));
    }

    /**
     * TC26 - Mục tiêu: Truyền ID thuốc không tồn tại trong chi tiết đơn
     * Input: ChiTietDonHang chứa thuốc không tồn tại
     * Expected output: 500 Internal Server Error (RuntimeException)
     * Output: 500 Internal Server Error
     * Ghi chú: Thuốc không tồn tại sẽ gây lỗi trong service
     */
    @Test
    @WithMockUser
    void create_ShouldFail_WhenThuocNotFound() throws Exception {
        DonHangDTO dto = new DonHangDTO();
        dto.setNguoiDungId(1);
        dto.setPhuongThucThanhToan("TIEN_MAT");

        ChiTietDonHangDTO ct = new ChiTietDonHangDTO();
        ct.setThuocId(999); // thuốc không tồn tại
        ct.setSoLuong(1);
        ct.setDonGia(100.0);
        dto.setChiTietDonHangs(List.of(ct));

        mockMvc.perform(post("/donhang/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());
    }

    /**
     * TC27 - Mục tiêu: Tạo đơn hàng với ID âm
     * Input: NguoiDungId = -1
     * Expected output: 200 OK hoặc lỗi nếu validate có
     * Output: 200 OK nếu không validate
     * Ghi chú: Hệ thống nên validate ID > 0
     */
    @Test
    @WithMockUser
    void create_ShouldSucceed_WhenIdNegative() throws Exception {
        DonHangDTO dto = new DonHangDTO();
        dto.setNguoiDungId(-1);
        dto.setPhuongThucThanhToan("TIEN_MAT");

        DonHang dh = new DonHang();
        dh.setId(100);

        Mockito.when(donHangService.create(any())).thenReturn(
                ResponseDTO.<DonHang>builder().status(200).msg("OK").data(dh).build());

        mockMvc.perform(post("/donhang/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(100));
    }


    /**
     * TC28 - Mục tiêu: Cập nhật đơn hàng hợp lệ
     * Input: DonHangDTO có ID = 1 và thông tin cập nhật hợp lệ
     * Expected output: 200 OK, data.id = 1
     * Output: 200 OK
     * Ghi chú: Happy case cập nhật đơn hàng thành công
     */
    @Test
    @WithMockUser
    void update_ShouldSucceed_WhenValid() throws Exception {
        DonHangDTO dto = new DonHangDTO();
        dto.setId(1);
        dto.setPhuongThucThanhToan("TIEN_MAT");

        DonHang dh = new DonHang();
        dh.setId(1);

        Mockito.when(donHangService.update(any())).thenReturn(
                ResponseDTO.<DonHang>builder().status(200).msg("OK").data(dh).build());

        mockMvc.perform(put("/donhang/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    /**
     * TC29 - Mục tiêu: Cập nhật đơn hàng không tồn tại
     * Input: DonHangDTO có ID = 999 (không tồn tại)
     * Expected output: 409 Conflict, msg = "Đơn hàng không tồn tại"
     * Output: 409 Conflict
     * Ghi chú: Hệ thống phải xử lý hợp lý khi đơn hàng không tồn tại
     */
    @Test
    @WithMockUser
    void update_ShouldFail_WhenIdNotFound() throws Exception {
        DonHangDTO dto = new DonHangDTO();
        dto.setId(999);
        dto.setPhuongThucThanhToan("TIEN_MAT");

        Mockito.when(donHangService.update(any())).thenReturn(
                ResponseDTO.<DonHang>builder().status(409).msg("Đơn hàng không tồn tại").data(null).build());

        mockMvc.perform(put("/donhang/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.msg").value("Đơn hàng không tồn tại"));
    }

    /**
     * TC30 - Mục tiêu: Cập nhật đơn hàng với ID âm
     * Input: DonHangDTO có ID = -1
     * Expected output: 200 OK hoặc lỗi nếu có validate
     * Output: 200 OK nếu không có validate ID âm
     * Ghi chú: Nên validate ID > 0 trong thực tế
     */
    @Test
    @WithMockUser
    void update_ShouldSucceed_WhenIdIsNegative() throws Exception {
        DonHangDTO dto = new DonHangDTO();
        dto.setId(-1);
        dto.setPhuongThucThanhToan("CHUYEN_KHOAN");

        DonHang dh = new DonHang();
        dh.setId(-1);

        Mockito.when(donHangService.update(any())).thenReturn(
                ResponseDTO.<DonHang>builder().status(200).msg("OK").data(dh).build());

        mockMvc.perform(put("/donhang/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(-1));
    }

    /**
     * TC31 - Mục tiêu: Xóa đơn hàng hợp lệ
     * Input: id = 1
     * Expected output: 200 OK, msg = "Thành công."
     * Output: 200 OK
     * Ghi chú: Happy case xóa thành công
     */
    @Test
    @WithMockUser
    void delete_ShouldSucceed_WhenValidId() throws Exception {
        Mockito.when(donHangService.delete(eq(1))).thenReturn(
                ResponseDTO.<Void>builder().status(200).msg("Thành công.").build());

        mockMvc.perform(delete("/donhang/delete")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Thành công."));
    }

    /**
     * TC32 - Mục tiêu: Xóa đơn hàng không tồn tại
     * Input: id = 999
     * Expected output: 200 OK hoặc lỗi tùy xử lý
     * Output: 200 OK nếu không kiểm tra tồn tại
     * Ghi chú: Cần logic rõ ràng hơn về kiểm tra tồn tại trước khi xóa
     */
    @Test
    @WithMockUser
    void delete_ShouldSucceed_WhenIdNotFound() throws Exception {
        Mockito.when(donHangService.delete(eq(999))).thenReturn(
                ResponseDTO.<Void>builder().status(200).msg("Thành công.").build());

        mockMvc.perform(delete("/donhang/delete")
                        .param("id", "999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Thành công."));
    }

    /**
     * TC33 - Mục tiêu: Xóa đơn hàng với ID âm
     * Input: id = -5
     * Expected output: 200 OK hoặc lỗi nếu có validate
     * Output: 200 OK nếu không kiểm tra ID âm
     * Ghi chú: Nên validate ID > 0
     */
    @Test
    @WithMockUser
    void delete_ShouldSucceed_WhenIdIsNegative() throws Exception {
        Mockito.when(donHangService.delete(eq(-5))).thenReturn(
                ResponseDTO.<Void>builder().status(200).msg("Thành công.").build());

        mockMvc.perform(delete("/donhang/delete")
                        .param("id", "-5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Thành công."));
    }
}


