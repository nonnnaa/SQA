package com.example.hieuthuoc.controller;

import com.example.hieuthuoc.dto.ResponseDTO;
import com.example.hieuthuoc.entity.TuongTacThuoc;
import com.example.hieuthuoc.service.TuongTacThuocService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class TestTuongTacThuocController {

    private MockMvc mockMvc;

    @Mock // Tạo mock cho service dependency
    private TuongTacThuocService tuongTacThuocService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private TuongTacThuocController tuongTacThuocController;

    private TuongTacThuoc sampleInteraction;
    private ResponseDTO<TuongTacThuoc> responseFound;
    private ResponseDTO<TuongTacThuoc> responseNotFound;
    private String hoatChat1;
    private String hoatChat2;
    private String hoatChat3;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(tuongTacThuocController).build();

        hoatChat1 = "Paracetamol";
        hoatChat2 = "Warfarin";
        hoatChat3 = "Ibuprofen";

        sampleInteraction = new TuongTacThuoc();
        sampleInteraction.setId(1);
        sampleInteraction.setHoatChat1(hoatChat1);
        sampleInteraction.setHoatChat2(hoatChat2);
        sampleInteraction.setCoChe("Giảm chuyển hóa Warfarin");
        sampleInteraction.setHauQua("Tăng nguy cơ chảy máu");
        sampleInteraction.setXuTri("Theo dõi INR thường xuyên, điều chỉnh liều Warfarin nếu cần");

        // Response mẫu khi tìm thấy tương tác
        responseFound = new ResponseDTO<>(200, "Thành công.", sampleInteraction);

        // Response mẫu khi không tìm thấy tương tác (service trả về data null)
        responseNotFound = new ResponseDTO<>(404, "Không tìm thấy tương tác giữa hai hoạt chất này.", null);
    }


    @Test
    public void testCheckThanhPhan_Found() throws Exception {
        when(tuongTacThuocService.checkThanhPhan(eq(hoatChat1), eq(hoatChat2)))
                .thenReturn(responseFound);

        mockMvc.perform(get("/tuongtacthuoc/get")
                        .param("hoatChat1", hoatChat1)
                        .param("hoatChat2", hoatChat2)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Mong đợi HTTP 200 OK
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("Thành công."))
                .andExpect(jsonPath("$.data", notNullValue())) // Đảm bảo data không null
                .andExpect(jsonPath("$.data.id").value(sampleInteraction.getId()))
                .andExpect(jsonPath("$.data.hoatChat1").value(sampleInteraction.getHoatChat1()))
                .andExpect(jsonPath("$.data.hoatChat2").value(sampleInteraction.getHoatChat2()))
                .andExpect(jsonPath("$.data.hauQua").value(sampleInteraction.getHauQua()));
    }

    @Test
    public void testCheckThanhPhan_NotFound() throws Exception {
        when(tuongTacThuocService.checkThanhPhan(eq(hoatChat1), eq(hoatChat3)))
                .thenReturn(responseNotFound);

        mockMvc.perform(get("/tuongtacthuoc/get")
                        .param("hoatChat1", hoatChat1)
                        .param("hoatChat2", hoatChat3) // Sử dụng hoatChat3
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.msg").value("Không tìm thấy tương tác giữa hai hoạt chất này."))
                .andDo(print())
        ;
    }

    @Test
    public void testCheckThanhPhan_EmptyParameters() throws Exception {
        when(tuongTacThuocService.checkThanhPhan(eq(""), eq("")))
                .thenReturn(responseNotFound);

        mockMvc.perform(get("/tuongtacthuoc/get")
                        .param("hoatChat1", "") // Truyền chuỗi rỗng
                        .param("hoatChat2", "") // Truyền chuỗi rỗng
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.msg").value("Không tìm thấy tương tác giữa hai hoạt chất này."))
                ;
    }

 }
