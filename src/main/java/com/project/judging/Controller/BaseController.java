package com.project.judging.Controller;

import com.project.judging.DTOs.ResponseDTO.ResponseDTO;
import com.project.judging.Factories.IResponseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class BaseController {
    @Autowired
    @Qualifier("successResponse")
    private IResponseFactory successFactory;

    @Autowired
    @Qualifier("errorResponse")
    private IResponseFactory errorFactory;

    public <T> ResponseEntity<ResponseDTO<T>> success(T data){
        return successFactory.build(data);
    }

    public <T> ResponseEntity<ResponseDTO<T>> success(int status, String message, T data){
        return successFactory.custom(status, message, data);
    }
    public <T> ResponseEntity<ResponseDTO<T>> success(String message, T data){
        return successFactory.custom(message, data);
    }
    public <T> ResponseEntity<ResponseDTO<T>> error(T data, String message){
        return errorFactory.custom(message, data);
    }
}
