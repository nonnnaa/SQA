package com.example.hieuthuoc.controller;

import com.example.hieuthuoc.configuration.VNPayConfig;
import com.example.hieuthuoc.dto.ResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import javax.servlet.http.HttpServletRequest;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.http.ResponseEntity;

import java.io.UnsupportedEncodingException;

@ExtendWith(MockitoExtension.class)
public class VNPayControllerTest {

    @Mock
    private VNPayController controller;

    @Mock
    private jakarta.servlet.http.HttpServletRequest request;

    @Mock
    private VNPayConfig vnPayConfig;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        controller = new VNPayController();
        request = mock(jakarta.servlet.http.HttpServletRequest.class);

        // Mock request IP
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        // Mock static fields (có thể cần PowerMockito nếu dùng static mạnh)
        VNPayConfig.vnp_TmnCode = "2QXUI4J4";
        VNPayConfig.vnp_ReturnUrl = "http://localhost:8080/payment-return";
        VNPayConfig.vnp_PayUrl = "http://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
        VNPayConfig.vnp_Command = "pay";
        VNPayConfig.vnp_Version = "2.1.0";
        VNPayConfig.secretKey = "TESTSECRET";
    }

    @Test
    public void testCreatePayment_NormalAmount() throws UnsupportedEncodingException {
        ResponseDTO<String> response = controller.createPayment(request, 1000);
        assertEquals(200, response.getStatus());
        assertTrue(response.getData().contains("vnp_Amount=100000")); // 1000 * 100
        assertTrue(response.getData().contains("vnp_SecureHash"));
    }


    @Test
    public void testCreatePayment_ZeroAmount() throws UnsupportedEncodingException {
        ResponseDTO<String> response = controller.createPayment(request, 0);
        assertEquals(400, response.getStatus());
        assertEquals("Số tiền phải lớn hơn 0.", response.getMsg());
        assertNull(response.getData());
    }

    @Test
    public void testCreatePayment_NegativeAmount() throws UnsupportedEncodingException {
        ResponseDTO<String> response = controller.createPayment(request, -500);
        assertEquals(400, response.getStatus());
        assertEquals("Số tiền phải lớn hơn 0.", response.getMsg());
        assertNull(response.getData());
    }

    @Test
    public void testCreatePayment_NullIpAddress() throws UnsupportedEncodingException {
        when(request.getRemoteAddr()).thenReturn(null); // IP null
        ResponseDTO<String> response = controller.createPayment(request, 500);
        assertEquals(200, response.getStatus());
        assertNotNull(response.getData());
    }

}
