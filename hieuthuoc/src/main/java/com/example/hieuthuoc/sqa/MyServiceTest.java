package com.example.hieuthuoc.sqa;

import static org.junit.Assert.assertEquals;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


import com.example.hieuthuoc.service.CalculatorService;

public class MyServiceTest {
	
	private final CalculatorService calculatorService = new CalculatorService();

    @Test
    void testAdd() {
        int result = calculatorService.add(2, 3);
        assertEquals(5, result, "2 + 3 phải bằng 5");
    }

    @Test
    void testAddWithNegative() {
        int result = calculatorService.add(-1, 4);
        assertEquals(3, result, "-1 + 4 phải bằng 3");
    }
}
