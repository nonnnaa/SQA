package com.example.hieuthuoc.controller;

import com.example.hieuthuoc.dto.ResponseDTO;
import com.example.hieuthuoc.entity.NhaCungCap;
import com.example.hieuthuoc.service.NhaCungCapService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
public class TestNhaCungCapController {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NhaCungCapService nhaCungCapService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String path = "/nhacungcap";

    private ResponseDTO responseDTO;

    @Test
    public void testValid_whenGetAll_thenStatus200() {
        try {
            //Given
            List<NhaCungCap> listNcc = new ArrayList<>();
            NhaCungCap ncc = new NhaCungCap();
            ncc.setId(1);
            ncc.setDiaChi("ht");
            ncc.setEmail("tmq21012002@gmail.com");
            ncc.setMaNCC("ncc1");
            ncc.setTenNhaCungCap("quang");
            listNcc.add(ncc);
            responseDTO = new ResponseDTO<List<NhaCungCap>>();
            responseDTO.setStatus(200);
            responseDTO.setData(listNcc);
            responseDTO.setMsg("Thành công");


            when(nhaCungCapService.getAll()).thenReturn(responseDTO);

            //when, then
            mockMvc.perform(get(path+"/list")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.msg").value("Thành công"))
                    .andExpect(jsonPath("$.data", notNullValue()))
                    .andExpect(jsonPath("$.data", hasSize(1)))
                    .andExpect(jsonPath("$.data[0].id").value(1))
                    .andExpect(jsonPath("$.data[0].diaChi").value("ht"))
                    .andExpect(jsonPath("$.data[0].tenNhaCungCap").value("quang"))
                    .andExpect(jsonPath("$.data[0].email").value("tmq21012002@gmail.com"))
                    .andExpect(jsonPath("$.data[0].maNCC").value("ncc1"))
                    .andExpect(jsonPath("$.status").value(200))
                    .andDo(print());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
