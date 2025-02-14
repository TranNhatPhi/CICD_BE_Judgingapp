package com.project.judging.DTOs.AdminDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class JudgeAccountDTO {
    private String email;
    private String phoneNumber;
    private String account;
    private String password;
}