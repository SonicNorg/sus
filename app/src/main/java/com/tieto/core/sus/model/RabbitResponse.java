package com.tieto.core.sus.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RabbitResponse {
    private MessageEntity messageEntity;
    private boolean success;
    private ErrorCode errorCode;
    private String message;
}
