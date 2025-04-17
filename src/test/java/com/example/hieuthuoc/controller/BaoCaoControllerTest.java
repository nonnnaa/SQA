package com.example.hieuthuoc.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.hieuthuoc.dto.DoanhThuDTO;
import com.example.hieuthuoc.repository.DonHangRepo;

@SpringBootTest
@AutoConfigureMockMvc
class BaoCaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DonHangRepo donHangRepo;

    private List<DoanhThuDTO> mockDoanhThuDTOs;

    @BeforeEach
    void setUp() {
        DoanhThuDTO doanhThuDTO = new DoanhThuDTO(1, 1000.0, 5L, 1L);
        mockDoanhThuDTOs = Arrays.asList(doanhThuDTO);
    }

    /**
     * TC01 - Mục tiêu: Test API /baocao/doanhthutheongay với ngày hợp lệ
     * Input: "2024-01-01"
     * Output: 200 OK, msg = "Thành công.", data đúng mock
     * Ghi chú: Dữ liệu được mock từ repo
     */
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void doanhThuTheoNgay_ShouldReturnDoanhThuList() throws Exception {
        when(donHangRepo.doanhThuTheoNgay(any(Date.class))).thenReturn(mockDoanhThuDTOs);

        mockMvc.perform(get("/baocao/doanhthutheongay")
                        .param("ngay", "2024-01-01")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("Thành công."))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].tongDoanhThu").value(1000.0))
                .andExpect(jsonPath("$.data[0].tongDonHang").value(5))
                .andExpect(jsonPath("$.data[0].tongDonHangTraLai").value(1));
    }

    /**
     * TC02 - Mục tiêu: Test API /baocao/doanhthutheothang với tháng/năm hợp lệ
     * Input: năm = 2024, tháng = 1
     * Output: 200 OK, msg = "Thành công.", data đúng mock
     * Ghi chú: Dữ liệu mock tương tự TC01
     */
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void doanhThuTheoThang_ShouldReturnDoanhThuList() throws Exception {
        when(donHangRepo.doanhThuTheoThang(anyInt(), anyInt())).thenReturn(mockDoanhThuDTOs);

        mockMvc.perform(get("/baocao/doanhthutheothang")
                        .param("nam", "2024")
                        .param("thang", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("Thành công."))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].tongDoanhThu").value(1000.0))
                .andExpect(jsonPath("$.data[0].tongDonHang").value(5))
                .andExpect(jsonPath("$.data[0].tongDonHangTraLai").value(1));
    }

    /**
     * TC03 - Mục tiêu: Test API /baocao/doanhthutheonam với năm hợp lệ
     * Input: năm = 2024
     * Output: 200 OK, msg = "Thành công.", data đúng mock
     * Ghi chú: Kiểm tra phản hồi năm
     */
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void doanhThuTheoNam_ShouldReturnDoanhThuList() throws Exception {
        when(donHangRepo.doanhThuTheoNam(anyInt())).thenReturn(mockDoanhThuDTOs);

        mockMvc.perform(get("/baocao/doanhthutheonam")
                        .param("nam", "2024")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("Thành công."))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].tongDoanhThu").value(1000.0))
                .andExpect(jsonPath("$.data[0].tongDonHang").value(5))
                .andExpect(jsonPath("$.data[0].tongDonHangTraLai").value(1));
    }

    /**
     * TC04 - Mục tiêu: Test API /baocao/doanhthutheongay với ngày không hợp lệ
     * Input: "invalid-date"
     * Output: 400 Bad Request
     * Ghi chú: Không cần mock repo
     */
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void doanhThuTheoNgay_WithInvalidDate_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/baocao/doanhthutheongay")
                        .param("ngay", "invalid-date")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * TC05 - Mục tiêu: Test API /baocao/doanhthutheothang với tháng không hợp lệ
     * Input: năm = 2024, tháng = 13
     * Output: 400 Bad Request
     * Ghi chú: Kiểm tra validation logic
     */
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void doanhThuTheoThang_WithInvalidMonth_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/baocao/doanhthutheothang")
                        .param("nam", "2024")
                        .param("thang", "13")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    /**
     * TC06 - Mục tiêu: Test API /baocao/doanhthutheongay không truyền param "ngay"
     * Input: không truyền
     * Output: 400 Bad Request
     * Ghi chú: Thiếu param là lỗi phổ biến, cần xử lý rõ ràng
     */
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void doanhThuTheoNgay_MissingDateParam_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/baocao/doanhthutheongay")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * TC07 - Mục tiêu: Test API /baocao/doanhthutheongay trả về null từ repo
     * Input: ngày hợp lệ
     * Output: 200 OK, data rỗng
     * Ghi chú: Nếu repo trả null thì hệ thống cần xử lý an toàn (trả về mảng rỗng)
     */
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void doanhThuTheoNgay_NullFromRepo_ShouldReturnEmptyData() throws Exception {
        when(donHangRepo.doanhThuTheoNgay(any(Date.class))).thenReturn(null);

        mockMvc.perform(get("/baocao/doanhthutheongay")
                        .param("ngay", "2024-01-01")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    /**
     * TC08 - Mục tiêu: Test API /baocao/doanhthutheothang thiếu param "thang"
     * Input: chỉ truyền năm = 2024
     * Output: 400 Bad Request
     * Ghi chú: Kiểm tra thiếu param bắt buộc
     */
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void doanhThuTheoThang_MissingMonth_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/baocao/doanhthutheothang")
                        .param("nam", "2024")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * TC09 - Mục tiêu: Test API /baocao/doanhthutheothang trả về null từ repo
     * Input: năm = 2024, tháng = 1
     * Output: 200 OK, data rỗng
     * Ghi chú: Bảo vệ trường hợp repo trả null
     */
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void doanhThuTheoThang_NullFromRepo_ShouldReturnEmptyData() throws Exception {
        when(donHangRepo.doanhThuTheoThang(anyInt(), anyInt())).thenReturn(null);

        mockMvc.perform(get("/baocao/doanhthutheothang")
                        .param("nam", "2024")
                        .param("thang", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    /**
     * TC10 - Mục tiêu: Test API /baocao/doanhthutheonam thiếu param "nam"
     * Input: không truyền param
     * Output: 400 Bad Request
     * Ghi chú: Trường hợp rất dễ bị bỏ sót
     */
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void doanhThuTheoNam_MissingYear_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/baocao/doanhthutheonam")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * TC11 - Mục tiêu: Test API /baocao/doanhthutheonam repo trả về null
     * Input: năm = 2024
     * Output: 200 OK, data rỗng
     * Ghi chú: Đảm bảo controller không lỗi khi repo trả null
     */
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void doanhThuTheoNam_NullFromRepo_ShouldReturnEmptyData() throws Exception {
        when(donHangRepo.doanhThuTheoNam(anyInt())).thenReturn(null);

        mockMvc.perform(get("/baocao/doanhthutheonam")
                        .param("nam", "2024")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
