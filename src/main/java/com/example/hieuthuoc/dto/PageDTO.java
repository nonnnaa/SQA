package com.example.hieuthuoc.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class PageDTO<T> {
	private int totalPages;
	private long totalElements;
	private int currentPage;
	private	T data;
	
}