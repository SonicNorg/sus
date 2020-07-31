package com.tieto.core.sus.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageEntity implements Serializable {
    private String accountId;
    private String status;
    private String msisdn;
}
