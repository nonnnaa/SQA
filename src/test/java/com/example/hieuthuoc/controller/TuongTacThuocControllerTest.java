package com.example.hieuthuoc.controller;

import com.example.hieuthuoc.entity.TuongTacThuoc;
import com.example.hieuthuoc.repository.TuongTacThuocRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TuongTacThuocControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TuongTacThuocRepo tuongTacThuocRepository;

    private String hoatChat1;
    private String hoatChat2;
    private String hoatChat3;

    @BeforeEach
    public void setup() {
        // Clear database to ensure clean state
        tuongTacThuocRepository.deleteAll();

        // Initialize test data
        hoatChat1 = "Paracetamol";
        hoatChat2 = "Warfarin";
        hoatChat3 = "Ibuprofen";

        // Insert a sample interaction into the database
        TuongTacThuoc sampleInteraction = new TuongTacThuoc();
        sampleInteraction.setHoatChat1(hoatChat1);
        sampleInteraction.setHoatChat2(hoatChat2);
        sampleInteraction.setCoChe("Giảm chuyển hóa Warfarin");
        sampleInteraction.setHauQua("Tăng nguy cơ chảy máu");
        sampleInteraction.setXuTri("Theo dõi INR thường xuyên, điều chỉnh liều Warfarin nếu cần");
        tuongTacThuocRepository.save(sampleInteraction);
    }

    @Test
    public void testCheckThanhPhanController_Found() throws Exception{
        mockMvc.perform(get("/tuongtacthuoc/get")
                        .param("hoatChat1", hoatChat1)
                        .param("hoatChat2", hoatChat2)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // HTTP 200 OK
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("Thành công."))
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.data.hoatChat1").value(hoatChat1))
                .andExpect(jsonPath("$.data.hoatChat2").value(hoatChat2))
                .andExpect(jsonPath("$.data.hauQua").value("Tăng nguy cơ chảy máu"))
                .andDo(print());

        // Verify database state (optional, to confirm data was read)
        assertFalse(tuongTacThuocRepository.findAll().isEmpty());
    }

    @Test
    public void testCheckThanhPhanController_NotFound() throws Exception {
        mockMvc.perform(get("/tuongtacthuoc/get")
                        .param("hoatChat1", hoatChat1)
                        .param("hoatChat2", hoatChat3) // No interaction exists
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()) // HTTP 404 Not Found
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.msg").value("Không tìm thấy tương tác giữa hai hoạt chất này."))
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(print());

        assertTrue(tuongTacThuocRepository.findAll().isEmpty());
    }

    @Test
    public void testCheckThanhPhanController_EmptyParameters() throws Exception {
        mockMvc.perform(get("/tuongtacthuoc/get")
                        .param("hoatChat1", "")
                        .param("hoatChat2", "")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()) // HTTP 400 Bad Request
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("Cần nhập đủ thông tin hai hoạt chất."))
                .andDo(print());
    }
}
