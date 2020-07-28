package com.tieto.core.sus.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageEntity {
    private String accountId;
    private String status;
    private String msisdn;
}
