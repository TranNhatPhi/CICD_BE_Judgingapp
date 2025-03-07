package com.project.judging.Controller;


import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@WebMvcTest(controllers = AdminController.class)
public class AdminControllerTest {

    @BeforeEach
    void initDate(){

    }
}
