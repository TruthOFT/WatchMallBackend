package com.watch.watch_mall.model.dto.support;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class SupportTicketCreateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String subject;

    private String content;

    private String contactName;

    private String contactPhone;

    private String contactEmail;
}
