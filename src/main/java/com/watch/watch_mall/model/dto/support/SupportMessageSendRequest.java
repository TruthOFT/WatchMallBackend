package com.watch.watch_mall.model.dto.support;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class SupportMessageSendRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long ticketId;

    private String content;
}
