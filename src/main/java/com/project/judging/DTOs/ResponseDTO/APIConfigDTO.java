package com.project.judging.DTOs.ResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class APIConfigDTO {
    // Project field
    private boolean isRound1Closed;
    private boolean isRound2Closed;
    private String eventName;
    private String description;
}
