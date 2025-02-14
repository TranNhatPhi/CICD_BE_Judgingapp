package com.project.judging.Factories;


import com.project.judging.Constant.ResponseConstant;
import com.project.judging.DTOs.ResponseDTO.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("successResponse")
public class SuccessFactory implements IResponseFactory {
    @Override
    public <T> ResponseEntity<ResponseDTO<T>> build(T data) {
        return ResponseEntity.ok(
                ResponseDTO.<T>builder()
                        .status(HttpStatus.OK.value())
                        .message(ResponseConstant.SUCCESS_MESSAGE)
                        .data(data)
                        .build()
        );
    }

    @Override
    public <T> ResponseEntity<ResponseDTO<T>> custom(int status, String message, T data) {
        return ResponseEntity.ok(
                ResponseDTO.<T>builder()
                        .status(status)
                        .message(message)
                        .data(data)
                        .build()
        );
    }

    @Override
    public <T> ResponseEntity<ResponseDTO<T>> custom(String message, T data) {
        return ResponseEntity.ok(
                ResponseDTO.<T>builder()
                        .status(HttpStatus.OK.value())
                        .message(message)
                        .data(data)
                        .build()
        );
    }
}
