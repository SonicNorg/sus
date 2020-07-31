package com.tieto.core.sus.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RabbitResponse implements Serializable {
    private MessageEntity messageEntity;
    private boolean success;
    private ErrorCode errorCode;
    private String message;
}
