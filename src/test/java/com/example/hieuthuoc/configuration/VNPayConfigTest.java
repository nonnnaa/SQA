package com.example.hieuthuoc.configuration;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;


import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VNPayConfigTest {

    @Test
    void testSha256_validInput() {
        String input = "test";
        String output = VNPayConfig.Sha256(input);

        assertNotNull(output);
        assertEquals(64, output.length());
        assertTrue(output.matches("[a-f0-9]+"));
    }

    @Test
    void testSha256_emptyInput() {
        String input = "";
        String output = VNPayConfig.Sha256(input);

        assertNotNull(output);
        assertEquals(64, output.length());
    }

    @Test
    void testSha256_nullInput() {
        assertThrows(NullPointerException.class, () -> {
            VNPayConfig.Sha256(null);
        });
    }

    @Test
    void testHmacSHA512_keyNull() {
        assertEquals("", VNPayConfig.hmacSHA512(null, "data"));
    }

    @Test
    void testHmacSHA512_dataNull() {
        assertEquals("", VNPayConfig.hmacSHA512("key", null));
    }

    @Test
    void testHmacSHA512_keyAndDataNull() {
        assertEquals("", VNPayConfig.hmacSHA512(null, null));
    }

    @Test
    void testHmacSHA512_validInput() {
        String expected = "bfe9db7e4bd5f5ab3d18dbfdaab5ba5a040ab9b70362e02f30262dbf970f99b7c72e1a399de6e44f8d478089dc813e84c978a9b57ca84033870f2d7c1b9715cf";
        assertEquals(expected, VNPayConfig.hmacSHA512("key", "data"));
    }


    @Test
    void testHmacSHA512_emptyData() {
        String expected = "b26d3b0d27b6d0be508d09753b3d3d99b0be8bfb65d99706260314b04e3b88521511cb0abbe1a900bbf99300da071078ec50ab5dcb82460e97ec95a313db7a2";
        assertEquals(expected, VNPayConfig.hmacSHA512("key", ""));
    }

    @Test
    void testHashAllFields_validFields() {
        // Mocking secretKey and hmacSHA512 method if necessary
        Map<String, String> fields = new HashMap<>();
        fields.put("name", "John");
        fields.put("age", "30");
        fields.put("city", "New York");

        String expectedResult = "expectedHMAC";  // Replace with actual expected HMAC value
        String actualResult = VNPayConfig.hashAllFields(fields);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testHashAllFields_emptyFields() {
        Map<String, String> fields = new HashMap<>();

        String expectedResult = "expectedHMAC";  // Replace with actual expected HMAC value
        String actualResult = VNPayConfig.hashAllFields(fields);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testHashAllFields_nullFieldValue() {
        Map<String, String> fields = new HashMap<>();
        fields.put("name", "John");
        fields.put("city", null);

        String expectedResult = "expectedHMAC";  // Replace with actual expected HMAC value
        String actualResult = VNPayConfig.hashAllFields(fields);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testHashAllFields_emptyFieldValue() {
        Map<String, String> fields = new HashMap<>();
        fields.put("name", "John");
        fields.put("city", "");

        String expectedResult = "expectedHMAC";  // Replace with actual expected HMAC value
        String actualResult = VNPayConfig.hashAllFields(fields);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testHashAllFields_singleField() {
        Map<String, String> fields = new HashMap<>();
        fields.put("name", "John");

        String expectedResult = "expectedHMAC";  // Replace with actual expected HMAC value
        String actualResult = VNPayConfig.hashAllFields(fields);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testGetIpAddress_UsingXForwardedFor() {
        // Mock HttpServletRequest
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-FORWARDED-FOR")).thenReturn("192.168.1.1");

        // Kiểm tra kết quả
        String ipAddress = VNPayConfig.getIpAddress(request);
        assertEquals("192.168.1.1", ipAddress);
    }

    @Test
    void testGetIpAddress_UsingRemoteAddr() {
        // Mock HttpServletRequest
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-FORWARDED-FOR")).thenReturn(null);  // Không có header này
        when(request.getRemoteAddr()).thenReturn("192.168.1.2");

        // Kiểm tra kết quả
        String ipAddress = VNPayConfig.getIpAddress(request);
        assertEquals("192.168.1.2", ipAddress);
    }

    @Test
    void testGetIpAddress_Exception() {
        // Mock HttpServletRequest
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-FORWARDED-FOR")).thenThrow(new RuntimeException("Test exception"));

        // Kiểm tra kết quả
        String ipAddress = VNPayConfig.getIpAddress(request);
        assertTrue(ipAddress.startsWith("Invalid IP:"));  // Kiểm tra xem chuỗi trả về có chứa "Invalid IP:"
    }

    @Test
    void testGetRandomNumber_ValidLength() {
        int len = 6;
        String result = VNPayConfig.getRandomNumber(len);
        assertEquals(len, result.length());  // Kiểm tra độ dài của chuỗi
    }

    @Test
    void testGetRandomNumber_ValidChars() {
        int len = 10;
        String result = VNPayConfig.getRandomNumber(len);
        assertTrue(result.matches("[0-9]{10}"));  // Kiểm tra chuỗi chỉ chứa số
    }

    @Test
    void testGetRandomNumber_OneChar() {
        int len = 1;
        String result = VNPayConfig.getRandomNumber(len);
        assertEquals(1, result.length());  // Kiểm tra độ dài là 1
        assertTrue(result.matches("[0-9]"));  // Kiểm tra chuỗi chỉ chứa một ký tự số
    }

    @Test
    void testGetRandomNumber_ZeroLength() {
        int len = 0;
        String result = VNPayConfig.getRandomNumber(len);
        assertEquals("", result);  // Kiểm tra chuỗi trả về là rỗng
    }
}
