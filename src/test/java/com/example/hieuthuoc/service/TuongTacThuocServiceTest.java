package com.example.hieuthuoc.service;

import com.example.hieuthuoc.dto.ResponseDTO;
import com.example.hieuthuoc.entity.TuongTacThuoc;
import com.example.hieuthuoc.repository.TuongTacThuocRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class TuongTacThuocServiceTest {

    @Autowired
    private TuongTacThuocService tuongTacThuocService;

    @Autowired
    private TuongTacThuocRepo tuongTacThuocRepo;

    private TuongTacThuoc sampleInteraction;
    private String hoatChat1;
    private String hoatChat2;
    private String hoatChat3;

    @BeforeEach
    void setUp() {
        // Clear database for clean state
        tuongTacThuocRepo.deleteAll();

        hoatChat1 = "Warfarin";
        hoatChat2 = "Amiodarone";
        hoatChat3 = "Paracetamol";

        // Create and save sample interaction
        sampleInteraction = new TuongTacThuoc();
        sampleInteraction.setHoatChat1(hoatChat1);
        sampleInteraction.setHoatChat2(hoatChat2);
        sampleInteraction.setHauQua("Tăng tác dụng chống đông của Warfarin, tăng nguy cơ chảy máu.");
        sampleInteraction.setCoChe("Amiodarone ức chế chuyển hóa Warfarin qua CYP2C9.");
        sampleInteraction.setXuTri("Giảm liều Warfarin, theo dõi INR chặt chẽ.");
        sampleInteraction = tuongTacThuocRepo.save(sampleInteraction);
    }

    @Test
    @DisplayName("Check Thanh Phan Thuoc Service - Found")
    void checkThanhPhanThuocService_Found() {
        // --- Execution ---
        ResponseDTO<TuongTacThuoc> response = tuongTacThuocService.checkThanhPhan(hoatChat1, hoatChat2);

        // --- Assertions ---
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Thành công.", response.getMsg());
        assertNotNull(response.getData());
        assertEquals(sampleInteraction.getId(), response.getData().getId());
        assertEquals(sampleInteraction.getHauQua(), response.getData().getHauQua());

        // --- Verification ---
        assertTrue(tuongTacThuocRepo.findById(sampleInteraction.getId()).isPresent());
    }

    @Test
    @DisplayName("Check Thanh Phan Thuoc Service - Not Found")
    void checkThanhPhanThuocService_NotFound() {
        // --- Execution ---
        ResponseDTO<TuongTacThuoc> response = tuongTacThuocService.checkThanhPhan(hoatChat1, hoatChat3);

        // --- Assertions ---
        assertNotNull(response);
        assertEquals(404, response.getStatus());
        assertEquals("Không tìm thấy tương tác giữa hai hoạt chất này.", response.getMsg());
        assertNull(response.getData());

        List<TuongTacThuoc> tuongTacThuocs = tuongTacThuocRepo.findAll();
        boolean ok = false;
        for(TuongTacThuoc tt : tuongTacThuocs) {
            if(tt.getHoatChat1().equals(hoatChat1) && tt.getHoatChat2().equals(hoatChat3)) {
                ok = true;
                break;
            }
        }
        assertFalse(ok);
    }

    @Test
    @DisplayName("Check Thanh Phan Thuoc Service - Null Input 1 Returns Not Found")
    void checkThanhPhanThuocService_NullInput1_ReturnsNotFound() {
        // --- Input ---
        String nullHoatChat = null;

        // --- Execution ---
        ResponseDTO<TuongTacThuoc> response = tuongTacThuocService.checkThanhPhan(nullHoatChat, hoatChat2);

        // --- Assertions ---
        assertNotNull(response);
        assertEquals(400, response.getStatus());
        assertEquals("Đầu vào không hợp lệ.", response.getMsg());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Check Thanh Phan Thuoc Service - Null Input 2 Returns Not Found")
    void checkThanhPhanThuocService_NullInput2_ReturnsNotFound() {
        // --- Input ---
        String nullHoatChat = null;

        // --- Execution ---
        ResponseDTO<TuongTacThuoc> response = tuongTacThuocService.checkThanhPhan(hoatChat1, nullHoatChat);

        // --- Assertions ---
        assertNotNull(response);
        assertEquals(400, response.getStatus()); // Adjusted to expect 400 based on service logic
        assertEquals("Đầu vào không hợp lệ.", response.getMsg());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Check Thanh Phan Thuoc Service - Empty Input Returns Not Found")
    void checkThanhPhanThuocService_EmptyInput_ReturnsNotFound() {
        // --- Input ---
        String emptyHoatChat = "";

        // --- Execution ---
        ResponseDTO<TuongTacThuoc> response = tuongTacThuocService.checkThanhPhan(emptyHoatChat, hoatChat2);

        // --- Assertions ---
        assertNotNull(response);
        assertEquals(404, response.getStatus()); // Adjusted to expect 400 based on service logic
        assertEquals("Không tìm thấy tương tác giữa hai hoạt chất này.", response.getMsg());
        assertNull(response.getData());

        List<TuongTacThuoc> tuongTacThuocs = tuongTacThuocRepo.findAll();
        boolean ok = false;
        for(TuongTacThuoc tt : tuongTacThuocs) {
            if(tt.getHoatChat1().equals(emptyHoatChat) && tt.getHoatChat2().equals(hoatChat2)) {
                ok = true;
                break;
            }
        }
        assertFalse(ok);
    }
}