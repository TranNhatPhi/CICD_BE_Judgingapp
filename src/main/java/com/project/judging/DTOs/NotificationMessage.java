package com.project.judging.DTOs;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NotificationMessage {

    private String content;

    public NotificationMessage() {}

    public NotificationMessage(String content) {
        this.content = content;
    }
}
