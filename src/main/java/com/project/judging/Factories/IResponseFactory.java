package com.project.judging.Factories;

import com.project.judging.DTOs.ResponseDTO.ResponseDTO;
import org.springframework.http.ResponseEntity;

public interface IResponseFactory {
    <T> ResponseEntity<ResponseDTO<T>> build(T data);
    <T> ResponseEntity<ResponseDTO<T>> custom(int status, String message, T data);
    <T> ResponseEntity<ResponseDTO<T>> custom( String message, T data);
}
