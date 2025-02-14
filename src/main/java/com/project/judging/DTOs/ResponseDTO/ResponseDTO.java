package com.project.judging.DTOs.ResponseDTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseDTO<T> {
    private int status;
    private String message;
    private T data;



}