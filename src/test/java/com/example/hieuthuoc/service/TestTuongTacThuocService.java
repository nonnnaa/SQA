package com.example.hieuthuoc.service;

import com.example.hieuthuoc.dto.ResponseDTO;
import com.example.hieuthuoc.entity.TuongTacThuoc;
import com.example.hieuthuoc.repository.TuongTacThuocRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestTuongTacThuocService {

    @Mock // Tạo đối tượng giả cho dependency
    private TuongTacThuocRepo tuongTacThuocRepo;

    @InjectMocks // Tạo instance của lớp cần test và inject mock vào
    private TuongTacThuocImpl tuongTacThuocService;

    private TuongTacThuoc sampleInteraction;
    private String hoatChat1;
    private String hoatChat2;
    private String hoatChat3;

    @BeforeEach
    void setUp() {
        hoatChat1 = "Warfarin";
        hoatChat2 = "Amiodarone";
        hoatChat3 = "Paracetamol"; // Giả định không tương tác với Warfarin

        sampleInteraction = new TuongTacThuoc();
        sampleInteraction.setId(1);
        sampleInteraction.setHoatChat1(hoatChat1); // Hoặc hoatChat2 tùy cách lưu trong DB
        sampleInteraction.setHoatChat2(hoatChat2); // Hoặc hoatChat1
        sampleInteraction.setHauQua("Tăng tác dụng chống đông của Warfarin, tăng nguy cơ chảy máu.");
        sampleInteraction.setCoChe("Amiodarone ức chế chuyển hóa Warfarin qua CYP2C9.");
        sampleInteraction.setXuTri("Giảm liều Warfarin, theo dõi INR chặt chẽ.");
    }

    @Test
    void checkThanhPhan_InteractionFound() {
        // --- Input ---
        // hoatChat1 và hoatChat2 từ setUp

        // --- Mocking ---
        // Giả lập repository trả về đối tượng tương tác khi tìm kiếm
        when(tuongTacThuocRepo.findByHoatChatCombination(eq(hoatChat1), eq(hoatChat2)))
                .thenReturn(sampleInteraction);

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
        // Xác minh rằng phương thức repository đã được gọi đúng 1 lần với đúng tham số
        verify(tuongTacThuocRepo, times(1)).findByHoatChatCombination(eq(hoatChat1), eq(hoatChat2));
        // Hoặc chỉ cần verify(tuongTacThuocRepo).findByHoatChatCombination(eq(hoatChat1), eq(hoatChat2));
    }

    @Test
    void checkThanhPhan_NoInteractionFound() {
        // --- Input ---
        // Sử dụng hoatChat1 và hoatChat3 (giả định không tương tác)

        // --- Mocking ---
        // Giả lập repository trả về null khi không tìm thấy
        when(tuongTacThuocRepo.findByHoatChatCombination(eq(hoatChat1), eq(hoatChat3)))
                .thenReturn(null);

        // --- Execution ---
        ResponseDTO<TuongTacThuoc> response = tuongTacThuocService.checkThanhPhan(hoatChat1, hoatChat3);

        // --- Assertions ---
        assertNotNull(response);
        assertEquals(404, response.getStatus());
        assertEquals("Không tìm thấy tương tác giữa hai hoạt chất này.", response.getMsg());
        assertNull(response.getData());

        // --- Verification ---
        verify(tuongTacThuocRepo).findByHoatChatCombination(eq(hoatChat1), eq(hoatChat3));
    }

    @Test
    void checkThanhPhan_NullInput1_ReturnsNotFound() {
        // --- Input ---
        String nullHoatChat = null;

        // --- Mocking ---
        // Giả lập repository trả về null khi một trong các input là null
        when(tuongTacThuocRepo.findByHoatChatCombination(isNull(), eq(hoatChat2)))
                .thenReturn(null);

        // --- Execution ---
        ResponseDTO<TuongTacThuoc> response = tuongTacThuocService.checkThanhPhan(nullHoatChat, hoatChat2);

        // --- Assertions ---
        // Dựa vào logic hiện tại, service sẽ trả về 404 nếu repo trả về null
        assertNotNull(response);
        assertEquals(400, response.getStatus());
        assertEquals("Đầu vào không hợp lệ.", response.getMsg());
        assertNull(response.getData());

        // --- Verification ---
        // Xác minh repo được gọi với tham số null
        verify(tuongTacThuocRepo).findByHoatChatCombination(isNull(), eq(hoatChat2));
    }

    @Test
    void checkThanhPhan_NullInput2_ReturnsNotFound() {
        // --- Input ---
        String nullHoatChat = null;

        // --- Mocking ---
        when(tuongTacThuocRepo.findByHoatChatCombination(eq(hoatChat1), isNull()))
                .thenReturn(null);

        // --- Execution ---
        ResponseDTO<TuongTacThuoc> response = tuongTacThuocService.checkThanhPhan(hoatChat1, nullHoatChat);

        // --- Assertions ---
        assertNotNull(response);
        assertEquals(404, response.getStatus());
        assertEquals("Không tìm thấy tương tác giữa hai hoạt chất này.", response.getMsg());
        assertNull(response.getData());

        // --- Verification ---
        verify(tuongTacThuocRepo).findByHoatChatCombination(eq(hoatChat1), isNull());
    }

    @Test
    void checkThanhPhan_EmptyInput_ReturnsNotFound() {
        // --- Input ---
        String emptyHoatChat = "";

        // --- Mocking ---
        // Giả lập repo trả về null khi input là chuỗi rỗng
        when(tuongTacThuocRepo.findByHoatChatCombination(eq(emptyHoatChat), eq(hoatChat2)))
                .thenReturn(null);

        // --- Execution ---
        ResponseDTO<TuongTacThuoc> response = tuongTacThuocService.checkThanhPhan(emptyHoatChat, hoatChat2);

        // --- Assertions ---
        assertNotNull(response);
        assertEquals(404, response.getStatus());
        assertEquals("Không tìm thấy tương tác giữa hai hoạt chất này.", response.getMsg());
        assertNull(response.getData());

        // --- Verification ---
        verify(tuongTacThuocRepo).findByHoatChatCombination(eq(emptyHoatChat), eq(hoatChat2));
    }

}
